package ir.bigz.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.DelegatingByTopicDeserializer;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

public class KafkaProperties {

    private final KafkaConfigDto kafkaConfigDto;
    private static KafkaProperties kafkaProperties;

    public static KafkaProperties getInstance() {
        if(kafkaProperties == null) {
            synchronized (KafkaProperties.class) {
                if(kafkaProperties == null) {
                    kafkaProperties = new KafkaProperties();
                }
            }
        }
        return kafkaProperties;
    }

    private KafkaProperties() {
        this.kafkaConfigDto = new KafkaConfigDto(defaultConfig());
    }

    private Map<String, Object> defaultConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.21.0.2:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, DelegatingByTopicDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "ir.bigz.kafka.dto");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    public KafkaConfigDto getKafkaConfigDto() {
        return kafkaConfigDto;
    }
}
