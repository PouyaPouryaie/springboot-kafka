package ir.bigz.kafka.config;

import ir.bigz.kafka.dto.Customer;
import ir.bigz.kafka.dto.Message;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaConsumerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.DelegatingByTopicDeserializer;
import org.springframework.kafka.support.serializer.DelegatingByTypeSerializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Configuration(proxyBeanMethods = false)
@Profile("production")
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {

        /*
        Defining a Map to config deserializer for message based on different topic and then set it in Kafka consumer factory
         */
        Map<Pattern, Deserializer<?>> deserializers = new HashMap<>();
        deserializers.put(Pattern.compile(".*-string"), new StringDeserializer());
        deserializers.put(Pattern.compile("message-topic"), new JsonDeserializer<>(Customer.class));
        deserializers.put(Pattern.compile("message-*."), new JsonDeserializer<>(Message.class));

        return new DefaultKafkaConsumerFactory<>(KafkaProperties.getInstance().getKafkaConfigDto().propsMap,
                null, new DelegatingByTopicDeserializer(deserializers, new JsonDeserializer<>()));
    }


    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Object>> kafkaListenerContainerFactory
            (ConsumerFactory<String, Object> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

}
