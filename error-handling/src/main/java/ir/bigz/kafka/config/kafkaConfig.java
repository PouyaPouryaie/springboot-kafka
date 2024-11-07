package ir.bigz.kafka.config;

import ir.bigz.kafka.exception.ConsumerException;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

import java.io.IOException;


@Configuration(proxyBeanMethods = false)
public class kafkaConfig {

    Logger log = LoggerFactory.getLogger(kafkaConfig.class);

    @Value("${app.topic.name}")
    private String topicName;

    KafkaConfigMap kafkaConfigMap = KafkaConfigMap.getKafkaConfigMap();

    @Bean
    public NewTopic createTopicWithTopicBuilder() {
        return TopicBuilder.name(topicName)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(kafkaConfigMap.getPropsMap());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }


    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(kafkaConfigMap.getPropsMap());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Object>> defaultKafkaListenerContainerFactory
            (ConsumerFactory<String, Object> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Object>> kafkaCustomBlockingRetryContainerFactory
            (ConsumerFactory<String, Object> consumerFactory, DefaultErrorHandler defaultErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        factory.setCommonErrorHandler(defaultErrorHandler);
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public DefaultErrorHandler errorHandler() {
        BackOff backOff = new FixedBackOff(1000, 3);
        DefaultErrorHandler customErrorHandler = new DefaultErrorHandler( (consumerRecord, exception) -> {
            // put your logic to execute when all the retry attempts are exhausted
            log.error(" Received: {} , after {} attempts, exception: {}",consumerRecord.value(), 3, exception.getMessage());
        }, backOff);
        customErrorHandler.addRetryableExceptions(IOException.class, ConsumerException.class);
        customErrorHandler.addNotRetryableExceptions(NullPointerException.class);
        return customErrorHandler;
    }
}
