package no.nav.oebs.nom.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.nom.db.entity.Livshendelse;
import no.nav.oebs.nom.db.repository.LivshendelseRepository;
import no.nav.oebs.nom.exception.HendelseBehandlingException;
import no.nav.oebs.nom.exception.RollbackHendelseException;
import no.nav.oebs.nom.kafka.nomshendelse.model.LivshendelseDto;
import no.nav.oebs.nom.logging.LoggingUtils;
import no.nav.oebs.nom.mdc.MdcOperations;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviceklasse som behandler mottatte livshendelser fra Kafka.
 */
@Slf4j
@Service
public class LivshendelseService extends HendelseServiceBase {

	private LivshendelseRepository livshendelseRepository;

	private ObjectMapper objectMapper;

	public LivshendelseService(ServiceConfig serviceConfig, LivshendelseRepository livshendelseRepository, ObjectMapper objectMapper) {
		super(serviceConfig);
		this.livshendelseRepository = livshendelseRepository;
		this.objectMapper = objectMapper;
	}

	/**
	 * Behandler mottatt livshendelse.
	 * <p>
	 * Status som lagres i livshendelse-tabellen:
	 * <ul>
	 * <li>BEHANDLET - ved normal fullført behandling av hendelsen.</li>
	 * <li>RETRY - dersom det oppstår feil under behandling av hendelsen.</li>
	 * <li>DUPLIKAT - dersom samme hendelseId allerede er mottatt.</li>
	 * </ul>
	 * <p>
	 * Kaster HendelseBehandlingException dersom behandlingen feiler og hendelsen skal gå til retry. Øvrige exception som kastes
	 * skal gi tilbakerulling av hendelsen til topic.
	 *
	 * @param mottattHendelse
	 *            hendelsen som er mottatt fra topicen.
	 */
	@Transactional(noRollbackFor = HendelseBehandlingException.class)
	public void behandleHendelse(LivshendelseDto mottattHendelse) {
		// Persisterer først en basishendelse. Formålet er å sjekke at databasen er tilgjengelig, ellers skal hendelsen rulles
		// tilbake på topicen. Må gjøres før try-catchen.
		Livshendelse livshendelse = createAndSaveBasicLivshendelseEntity(mottattHendelse);

		try {
			addMottattLivshendelseToEntity(livshendelse, mottattHendelse);
			if (isNyHendelseDuplikat(livshendelse)) {
				livshendelse.setStatus(Livshendelse.STATUS_DUPLIKAT);
			} else {

				livshendelse.setStatus(Livshendelse.STATUS_BEHANDLET);
			}
		} catch (Exception e) {
			livshendelse.setStatus(Livshendelse.STATUS_RETRY);
			livshendelse.setRetryTeller(serviceConfig.getRetryMaxAttempts());
			livshendelse.setRetryTidspunkt(getNextRetryTidspunkt(livshendelse));
			livshendelse.setFeilinformasjon(LoggingUtils.formatExceptionAsString(e));

			log.warn(String.format("Feilet under behandling av livshendelse; id=%d, retrytidspunkt=%s, cause=%s",
					livshendelse.getId(), livshendelse.getRetryTidspunkt(), e.getMessage()), e);

			throw new HendelseBehandlingException(e);
		}
	}

	/**
	 * Oppretter og persisterer en basishendelse med påkrevde felt, samt selve hendelsen som en JSON. Returnerer entiteten som
	 * er persistert.
	 */
	private Livshendelse createAndSaveBasicLivshendelseEntity(LivshendelseDto mottattHendelse) {
		try {
			String hendelseAsJson = objectMapper.writeValueAsString(mottattHendelse);
			Livshendelse hendelse = Livshendelse.builder() //
					.korrelasjonId(MdcOperations.get(MdcOperations.MDC_CORRELATION_ID)) //
					.status(Livshendelse.STATUS_NY) //
					.hendelse(hendelseAsJson) //
					.build();

			return livshendelseRepository.save(hendelse);
		} catch (JsonProcessingException e) {
			throw new RollbackHendelseException(e);
		}
	}

	/**
	 * Legger til feltene fra mottatt hendelse på entiteten.
	 */
	private void addMottattLivshendelseToEntity(Livshendelse entity, LivshendelseDto mottattHendelse) {
		entity.setHendelseId(getStringValue(mottattHendelse.getHendelseId(), Livshendelse.MAX_ID_LEN));
		entity.setHendelsePersonidenter(
				getPersonidenterAsCommaSeparatedString(mottattHendelse.getPersonidenter(), Livshendelse.MAX_PERSONIDENTER_LEN));
		entity.setHendelseMaster((getStringValue(mottattHendelse.getMaster(), Livshendelse.MAX_MASTER_LEN)));
		entity.setHendelseOpprettet(mottattHendelse.getOpprettet().toLocalDateTime());
		entity.setHendelseOpplysningstype(
				getStringValue(mottattHendelse.getOpplysningstype(), Livshendelse.MAX_OPPLYSNINGSTYPE_LEN));
		entity.setHendelseEndringstype(getStringValue(mottattHendelse.getEndringstype(), Livshendelse.MAX_ENDRINGSTYPE_LEN));
		entity.setHendelseTidligereHendelseId(
				getStringValue(mottattHendelse.getTidligereHendelseId(), Livshendelse.MAX_ID_LEN));
	}

	/**
	 * Returner stringverdien trunkert til spesifisert maxlengde; null dersom input er null.
	 */
	private String getStringValue(String value, int maxLength) {
		return value != null ? value.substring(0, Math.min(value.length(), maxLength)) : null;
	}

	/**
	 * Returnerer listen av identer som en komma-separert string trunkert til spesifisert maxlengde. Identene er organisert slik
	 * at alle fnr/dnr kommer først, deretter følger alle aktørId/NPid. Delmengdene av fnr/dnr og aktørId/NPid er sortert
	 * innbyrdes.
	 */
	private String getPersonidenterAsCommaSeparatedString(List<String> list, int maxLength) {
		String result = String.join(",", //
				list.stream().filter(e -> e.length() == 11).sorted().collect(Collectors.joining(",")),
				list.stream().filter(e -> e.length() != 11).sorted().collect(Collectors.joining(",")));

		// Ledende eller avsluttende komma kan forekomme dersom vi kun mottar fnr eller aktørId, men ikke begge.
		result = StringUtils.stripStart(result, ",");
		result = StringUtils.stripEnd(result, ",");

		return result.substring(0, Math.min(result.length(), maxLength));
	}

	/**
	 * Sjekker om en ny livshendelse er duplikat av tidligere mottatte hendelser. Sjekken gjøres på hendelse ID og
	 * opplysningstype, samt at status ikke må være NY. (Hendelse ID er kun unik pr. opplysningstype.)
	 */
	private boolean isNyHendelseDuplikat(Livshendelse livshendelse) {
		return !livshendelseRepository.findByHendelseIdAndHendelseOpplysningstypeAndStatusNotIn(livshendelse.getHendelseId(),
				livshendelse.getHendelseOpplysningstype(), List.of(Livshendelse.STATUS_NY)).isEmpty();
	}
}
