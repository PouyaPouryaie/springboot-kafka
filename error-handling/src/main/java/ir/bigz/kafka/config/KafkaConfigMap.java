package ir.bigz.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaConfigMap {

    private final KafkaProperties kafkaProperties;
    private final Map<KafkaType, Map<String, Object>> kafkaConfigs;

    public KafkaConfigMap(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
        kafkaConfigs = new EnumMap<>(KafkaType.class);
        kafkaConfigs.put(KafkaType.PRODUCER, Collections.synchronizedMap(getDefaultKafkaProducerConfig()));
        kafkaConfigs.put(KafkaType.CONSUMER, Collections.synchronizedMap(getDefaultKafkaConsumerConfig()));
    }

    public enum KafkaType {
        PRODUCER, CONSUMER
    }


    public Map<String, Object> getKafkaConfig(KafkaType kafkaType) {
        Objects.requireNonNull(kafkaType, "KafkaType cannot be null");

        return switch (kafkaType) {
            case PRODUCER, CONSUMER -> Collections.unmodifiableMap(kafkaConfigs.get(kafkaType));
            default -> throw new IllegalArgumentException("Invalid KafkaType: " + kafkaType);
        };
    }

    public void updateProducer(String key, Object value) {
        updateConfig(KafkaType.PRODUCER, key, value);
    }

    public void updateConsumer(String key, Object value) {
        updateConfig(KafkaType.CONSUMER, key, value);
    }

    public void updateProducer(Map<String, Object> props) {
        props.forEach((k, v) -> updateConfig(KafkaType.PRODUCER, k, v));
    }

    public void updateConsumer(Map<String, Object> props) {
        props.forEach((k, v) -> updateConfig(KafkaType.CONSUMER, k, v));
    }

    private void updateConfig(KafkaType kafkaType, String key, Object value) {

        Objects.requireNonNull(kafkaType, "KafkaType cannot be null");
        Objects.requireNonNull(key, "Configuration key cannot be null");
        Objects.requireNonNull(value, "Configuration value cannot be null");

        Map<String, Object> config = kafkaConfigs.get(kafkaType);
        if (config != null) {
            synchronized (config) {
                config.put(key, value);
            }
        } else {
            throw new IllegalArgumentException("Invalid KafkaType: " + kafkaType);
        }
    }

    private Map<String, Object> getDefaultKafkaProducerConfig() {
        Map<String, Object> propsMap = new HashMap<>();
        propsMap.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServer());
        propsMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        propsMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        propsMap.put(ProducerConfig.RETRIES_CONFIG, kafkaProperties.getRetries());
        propsMap.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, kafkaProperties.getDeliveryTimeout());
        propsMap.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, kafkaProperties.getRetryBackoff());
        propsMap.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, kafkaProperties.getEnableIdempotence());
        propsMap.put(ProducerConfig.ACKS_CONFIG, kafkaProperties.getAcks());
        propsMap.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,
                kafkaProperties.getMaxInFlightRequestsPerConnection()); // set it to One if Ordering of messages are important
        return propsMap;
    }

    private Map<String, Object> getDefaultKafkaConsumerConfig() {
        Map<String, Object> propsMap = new HashMap<>();
        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServer());
        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        propsMap.put(JsonDeserializer.TRUSTED_PACKAGES, kafkaProperties.getTrustedPackage());
        propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumerGroupId());
        propsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProperties.getAutoOffsetReset());
        propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaProperties.getEnableAutoCommit());
        return propsMap;
    }

}
