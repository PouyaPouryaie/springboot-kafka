package ir.bigz.kafka.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.transaction.KafkaTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
@Profile("production")
public class KafkaProducerConfig {

    private final KafkaProperties kafkaProperties;

    public KafkaProducerConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public NewTopic createTopicWithTopicBuilder() {
        return TopicBuilder.name(kafkaProperties.getTopicName())
                .partitions(3)
                .replicas(1)
                .compact()
                .build();
    }

    /*
     *  When you set the ProducerConfig.TRANSACTIONAL_ID_CONFIG to a unique value
     *  in a Kafka producer, it enables transactional messaging for the producer,
     *  allowing it to write messages atomically across multiple partitions or topic.
     *  Either all messages in a transaction are successfully written and visible for consumer (marker commit)
     *  or none of them are visible (marker aborted)
     */
    @Bean
    public KafkaConfigDto kafkaConfigDto() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServer());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "transaction-id-idempotence");
        return new KafkaConfigDto(props);
    }

    @Bean
    public ProducerFactory<String, Object> defaultProducerFactory(KafkaConfigDto kafkaConfigDto) {
        return new DefaultKafkaProducerFactory<>(kafkaConfigDto.getPropsMap());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public KafkaTransactionManager<String, Object> kafkaTransactionManager(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTransactionManager<>(producerFactory);
    }

    @Bean
    public AdminClient adminClient(KafkaConfigDto kafkaConfigDto) {
        return AdminClient.create(kafkaConfigDto.getPropsMap());
    }
}
