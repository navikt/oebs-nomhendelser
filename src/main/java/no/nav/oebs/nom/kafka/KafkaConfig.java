package no.nav.oebs.nom.kafka;

import java.time.Duration;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.util.backoff.FixedBackOff;

/**
 * Konfigurasjonsklasse for Kafka.
 */
@EnableKafka
@Configuration
public class KafkaConfig {

	// Max antall forsøk på retry når feil kastes tilbake til Spring Kafka fra applikasjonens Kafka-listener.
	@Value("${app.kafka.retry-max-attempts}")
	private int retryMaxAttempts;

	// Antall millisekunder mellom hver retry.
	@Value("${app.kafka.retry-backoff-period-ms}")
	private long retryBackoffPeriod;

	// Antall sekunder mellom hver gang Spring gjør retry ved AuthorizationException fra Kafka.
	@Value("${app.kafka.authorization-exception-retry-interval-secs}")
	private long authorizationExceptionRetryIntervalSecs;


	/**
	 * Kafka listener container factory for nomshendelser. Se {@link #kafkaNomshendelseListenerContainerFactory} for en
	 * detaljert beskrivelse som også gjelder denne container factoryen.
	 */

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> kafkaNomshendelseListenerContainerFactory(
			KafkaProperties properties) {

		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(kafkaNomshendelseConsumerFactory(properties));
		factory.getContainerProperties()
				.setAuthExceptionRetryInterval(Duration.ofSeconds(authorizationExceptionRetryIntervalSecs));
		factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(retryBackoffPeriod, retryMaxAttempts)));

		return factory;
	}

	private ConsumerFactory<String, Object> kafkaNomshendelseConsumerFactory(KafkaProperties properties) {
		Map<String, Object> consumerProperties = properties.buildConsumerProperties(null);
		consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
		consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
		consumerProperties.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
		consumerProperties.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, StringDeserializer.class);

		return new DefaultKafkaConsumerFactory<>(consumerProperties);
	}
}
