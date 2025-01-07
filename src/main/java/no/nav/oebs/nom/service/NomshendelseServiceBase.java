package no.nav.oebs.nom.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.oebs.nom.db.entity.NomsHendelse;
import no.nav.oebs.nom.service.model.NomshendelseOebs;

/**
 * Basisklasse som arves av skjermingshendelsetjenestene for bruk av felles funksjonalitet.
 */
public class NomshendelseServiceBase extends HendelseServiceBase {

	private ObjectMapper objectMapper;

	public NomshendelseServiceBase(ServiceConfig serviceConfig, ObjectMapper objectMapper) {
		super(serviceConfig);
		this.objectMapper = objectMapper;
	}

	/**
	 * Legger til hendelseArena-feltet på entiteten. Dette er hendelsen på JSON-format som overføres til Arena.
	 */
	protected void addHendelseArenaToEntity(NomsHendelse entity) throws JsonProcessingException {
		boolean status = Boolean.parseBoolean(entity.getHendelse());

		NomshendelseOebs hendelseOebs = NomshendelseOebs.builder() //
				.fodselsnr(entity.getHendelseFodselsnr()) //
				.status(status) //
				.build();

		entity.setHendelse(objectMapper.writeValueAsString(hendelseOebs));
	}
}
