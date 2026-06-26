package no.nav.oebs.nom.kafka.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;
import no.nav.person.pdl.leesah.Personhendelse;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Klasse for en intern representasjon av en mottatt livshendelse-melding fra Kafka. Brukes primært for å lage et gyldig
 * JSON-objekt.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonInclude(Include.NON_NULL)
public class LivshendelseDto {

	private String hendelseId;

	private List<String> personidenter;

	private String master;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
	private ZonedDateTime opprettet;

	private String opplysningstype;

	private String endringstype;

	private String tidligereHendelseId;

	private AdressebeskyttelseDto adressebeskyttelse;

	private DoedsfallDto doedsfall;

	private FoedselDto foedsel;

	private FoedselsdatoDto foedselsdato;

	private UtflyttingFraNorgeDto utflyttingFraNorge;

	private InnflyttingTilNorgeDto innflyttingTilNorge;

	private FolkeregisteridentifikatorDto folkeregisteridentifikator;

	private NavnDto navn;

	private KontaktadresseDto kontaktadresse;

	private BostedsadresseDto bostedsadresse;

	/**
	 * Mapper fra Avro- til Java-objekt.
	 */
	public static LivshendelseDto map(Personhendelse personhendelse) {
		return LivshendelseDto.builder() //
				.hendelseId(personhendelse.getHendelseId().toString()) //
				.personidenter(
						personhendelse.getPersonidenter().stream().map(CharSequence::toString).toList()) //
				.master(personhendelse.getMaster().toString()) //
				.opprettet(personhendelse.getOpprettet().atZone(ZoneId.systemDefault())) //
				.opplysningstype(personhendelse.getOpplysningstype().toString()) //
				.endringstype(personhendelse.getEndringstype().name()) //
				.tidligereHendelseId(ModelUtils.getAsString(personhendelse.getTidligereHendelseId())) //
				.adressebeskyttelse(AdressebeskyttelseDto.map(personhendelse.getAdressebeskyttelse())) //
				.doedsfall(DoedsfallDto.map(personhendelse.getDoedsfall())) //
				.foedselsdato(FoedselsdatoDto.map(personhendelse.getFoedselsdato())) //
				.utflyttingFraNorge(UtflyttingFraNorgeDto.map(personhendelse.getUtflyttingFraNorge())) //
				.innflyttingTilNorge(InnflyttingTilNorgeDto.map(personhendelse.getInnflyttingTilNorge())) //
				.folkeregisteridentifikator(FolkeregisteridentifikatorDto.map(personhendelse.getFolkeregisteridentifikator())) //
				.navn(NavnDto.map(personhendelse.getNavn())) //
				.kontaktadresse(KontaktadresseDto.map(personhendelse.getKontaktadresse())) //
				.bostedsadresse(BostedsadresseDto.map(personhendelse.getBostedsadresse())) //
				.build();
	}
}
