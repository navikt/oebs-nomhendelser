package no.nav.oebs.nom.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Klasse som representerer JSON-dataene i en skjermingshendelse som sendes til Arena.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class NomshendelseOebs {

	private String fodselsnr;

	private boolean status;
}
