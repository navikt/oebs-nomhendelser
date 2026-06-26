package no.nav.oebs.nom.service;

import no.nav.oebs.nom.db.entity.NomsHendelse;
import no.nav.oebs.nom.service.model.NomshendelseOebs;
import tools.jackson.databind.json.JsonMapper;

/**
 * Basisklasse som arves av nomshendelsetjenestene for bruk av felles funksjonalitet.
 */
public class NomshendelseServiceBase extends HendelseServiceBase {

	private final JsonMapper objectMapper;

	public NomshendelseServiceBase(ServiceConfig serviceConfig, JsonMapper objectMapper) {
		super(serviceConfig);
		this.objectMapper = objectMapper;
	}

	/**
	 * Legger til hendelseOebs-feltet på entiteten. Dette er hendelsen på JSON-format som overføres til Oebs.
	 */
	protected void addHendelseOebsToEntity(NomsHendelse entity) {
		boolean status = Boolean.parseBoolean(entity.getHendelse());

		NomshendelseOebs nomshendelseOebs = NomshendelseOebs.builder() //
				.fodselsnr(entity.getHendelseFodselsnr()) //
				.status(status) //
				.build();

		entity.setHendelse(objectMapper.writeValueAsString(nomshendelseOebs));
	}
}
