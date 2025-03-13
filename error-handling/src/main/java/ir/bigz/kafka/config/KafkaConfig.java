package ir.bigz.kafka.config;

import ir.bigz.kafka.config.KafkaConfigMap.KafkaType;
import ir.bigz.kafka.exception.ConsumerException;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;


@Configuration(proxyBeanMethods = false)
@EnableKafka
public class KafkaConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConfig.class);

    private final KafkaConfigMap kafkaConfigMap;
    private final KafkaProperties kafkaProperties;

    public KafkaConfig(KafkaConfigMap kafkaConfigMap, KafkaProperties kafkaProperties) {
        this.kafkaConfigMap = kafkaConfigMap;
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public NewTopic mainTopic() {
        return TopicBuilder.name(kafkaProperties.getTopicName())
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic deadLetterTopic() {
        return TopicBuilder.name(kafkaProperties.getTopicName() + ".DLT")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(kafkaConfigMap.getKafkaConfig(KafkaType.PRODUCER));
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }


    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(kafkaConfigMap.getKafkaConfig(KafkaType.CONSUMER));
    }

    // Default Kafka Listener Factory
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Object>> defaultKafkaListenerContainerFactory
            (ConsumerFactory<String, Object> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    // Kafka Listener with Custom Error Handling and Retry
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Object>> kafkaCustomErrorRetryContainerFactory
            (ConsumerFactory<String, Object> consumerFactory, DefaultErrorHandler defaultErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConcurrency(1); // Ensures sequential message processing per partition
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE); // (Use AckMode.MANUAL_IMMEDIATE when setSeekAfterError(false), and you should manually call ack mode for successful processing)
//        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        factory.setCommonErrorHandler(defaultErrorHandler);
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    // Custom Error Handler with Retry and Dead Letter Publishing
    @Bean
    public DefaultErrorHandler customErrorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        int attempts = 3;
        BackOff backOff = new FixedBackOff(2000, attempts);

        // Dead Letter Publishing Recoverer: Sends messages to DLT after max retries
        final DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (consumerRecord, ex) -> {
                    log.error(" Received: {} , after {} attempts, exception: {}",
                            consumerRecord.value(), attempts, ex.getMessage());
                    return new TopicPartition(consumerRecord.topic() + ".DLT", consumerRecord.partition());
                });

        DefaultErrorHandler customErrorHandler = new DefaultErrorHandler(recoverer, backOff);

        // Configure retryable and non-retryable exceptions
        customErrorHandler.addRetryableExceptions(IOException.class, ConsumerException.class);
        customErrorHandler.addNotRetryableExceptions(NullPointerException.class);

        // Set error handling behavior
        customErrorHandler.setSeekAfterError(false); // Prevents re-seeking failed records
        customErrorHandler.setCommitRecovered(true); // Ensures committed offsets for failed records

        return customErrorHandler;
    }
}
