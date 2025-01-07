package no.nav.oebs.nom.kafka.nomshendelse.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Klasse for en intern representasjon av en mottatt skjermingshendelse-melding fra Kafka.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class NomshendelseDto {
	
	private String hendelseId;
	
	private LocalDateTime hendelseTimestamp;

	private String fodselsnr;

	private String hendelseAsJson;
}
