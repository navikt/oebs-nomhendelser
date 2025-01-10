package no.nav.oebs.nom.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Entitetsklasse som representerer en rad i LIVSHENDELSE-tabellen.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@Table(name = "XXRTV_NOMSHENDELSE")
public class NomsHendelse extends BaseHendelse {

	@Id
	@SequenceGenerator(name = "xxrtv_nom_seq", sequenceName = "xxrtv_nom_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "xxrtv_nom_seq")
	@Column(name = "NOMSHENDELSE_ID")
	private Long id;

	@Column(name = "HENDELSE_ID")
	private String hendelseId;

	@Column(name = "HENDELSE_FODSELSNR")
	private String hendelseFodselsnr;

	@Column(name = "HENDELSE_OPPRETTET")
	private LocalDateTime hendelseOpprettet;

	@Column(name = "HENDELSE")
	private String hendelse;

	/*@Column(name = "HENDELSE_OEBS")
	private String hendelseOebs;*/
}
