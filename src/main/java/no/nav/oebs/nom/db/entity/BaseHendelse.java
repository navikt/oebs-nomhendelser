package no.nav.oebs.nom.db.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Basishendelse med felles tabellkolonner.
 */
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public abstract class BaseHendelse {

	// Lovlige statusverdier
	public static final String STATUS_NY = "NY";
	public static final String STATUS_BEHANDLET = "BEHANDLET";
	public static final String STATUS_RETRY = "RETRY";
	public static final String STATUS_DUPLIKAT = "DUPLIKAT";
	public static final String STATUS_ERSTATTET = "ERSTATTET";
	public static final String STATUS_FEILET = "FEILET";
	public static final String STATUS_IKKE = "IKKE";

	@Column(name = "KORRELASJON_ID")
	private String korrelasjonId;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "RETRY_TELLER")
	private Integer retryTeller;

	@Column(name = "RETRY_TIDSPUNKT")
	private LocalDateTime retryTidspunkt;

	@Column(name = "FEILINFORMASJON")
	private String feilinformasjon;

	public abstract Long getId();

	public void decrementRetryTeller() {
		retryTeller--;
	}

	public void appendFeilinformasjon(String feilinformasjon) {
		this.feilinformasjon += "\n\n" + feilinformasjon;
	}
}
