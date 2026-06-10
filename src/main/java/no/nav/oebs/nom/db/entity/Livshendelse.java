package no.nav.oebs.nom.db.entity;

import jakarta.persistence.*;
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
@Table(name = "XXRTV_PDL_LIVSHENDELSE")
public class Livshendelse extends BaseHendelse {

	// Maxlengder i databasetabell
	public static final int MAX_ID_LEN = 36;
	public static final int MAX_PERSONIDENTER_LEN = 250;
	public static final int MAX_MASTER_LEN = 20;
	public static final int MAX_OPPLYSNINGSTYPE_LEN = 50;
	public static final int MAX_ENDRINGSTYPE_LEN = 20;

	@Id
	@SequenceGenerator(name = "xxrtv_lihe_seq", sequenceName = "xxrtv_lihe_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "xxrtv_lihe_seq")
	@Column(name = "LIVSHENDELSE_ID")
	private Long id;

	@Column(name = "HENDELSE_ID")
	private String hendelseId;

	@Column(name = "HENDELSE_PERSONIDENTER")
	private String hendelsePersonidenter;

	@Column(name = "HENDELSE_MASTER")
	private String hendelseMaster;

	@Column(name = "HENDELSE_OPPRETTET")
	private LocalDateTime hendelseOpprettet;

	@Column(name = "HENDELSE_OPPLYSNINGSTYPE")
	private String hendelseOpplysningstype;

	@Column(name = "HENDELSE_ENDRINGSTYPE")
	private String hendelseEndringstype;

	@Column(name = "HENDELSE_TIDLIGERE_HENDELSE_ID")
	private String hendelseTidligereHendelseId;

	@Column(name = "HENDELSE")
	private String hendelse;
}
