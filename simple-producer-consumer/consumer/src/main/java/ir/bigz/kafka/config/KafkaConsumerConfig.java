package ir.bigz.kafka.config;

import ir.bigz.kafka.dto.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@Configuration(proxyBeanMethods = false)
@Profile("production")
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, Message<Object>> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(KafkaProperties.getInstance().getKafkaConfigDto().propsMap);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Message<Object>>> kafkaListenerContainerFactory
            (ConsumerFactory<String, Message<Object>> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Message<Object>> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

}
