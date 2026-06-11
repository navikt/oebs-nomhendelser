package no.nav.oebs.nom.db.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * Entitetsklasse som representerer en logglinje i KALL_LOGG-tabellen.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Table(name = "XXRTV_NOM_LOGG", schema = "XXRTV")
public class Logg {

	public static final String RETNING_INN = "INN";
	public static final String RETNING_UT = "UT";
	public static final String TYPE_KAFKA = "KAFKA";
	public static final String TYPE_REST = "REST";

	public static final int MAX_KAFKA_KEY_LEN = 100;

    @Id
	@SequenceGenerator(name = "xxrtv_nom_logg_seq", sequenceName = "xxrtv_nom_logg_seq", schema = "APPS", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "xxrtv_nom_logg_seq")
	@Column(name = "KALL_LOGG_ID")
	private Long id;

	@Column(name = "KORRELASJON_ID")
	private String korrelasjonId;

	@Column(name = "TIDSPUNKT")
	private LocalDateTime tidspunkt;

	@Column(name = "TYPE")
	private String type;

	@Column(name = "KALL_RETNING")
	private String kallRetning;

	@Column(name = "METHOD")
	private String method;

	@Column(name = "OPERATION")
	private String operation;

	@Column(name = "STATUS")
	private Integer status;

	@Column(name = "KALLTID")
	private Long kalltid;

	@Column(name = "KAFKA_PARTITION")
	private Integer kafkaPartition;

	@Column(name = "KAFKA_OFFSET")
	private Long kafkaOffset;

	@Column(name = "KAFKA_TIMESTAMP")
	private LocalDateTime kafkaTimestamp;

	@Column(name = "KAFKA_TIMESTAMPTYPE")
	private String kafkaTimestampType;

	@Column(name = "KAFKA_KEY")
	private String kafkaKey;

	@Column(name = "REQUEST")
	private String request;

	@Column(name = "RESPONSE")
	private String response;

	@Column(name = "LOGGINFO")
	private String logginfo;
}
