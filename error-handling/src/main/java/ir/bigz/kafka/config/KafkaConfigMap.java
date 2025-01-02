package ir.bigz.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class KafkaConfigMap {

    private Map<String, Object> propsMap;
    volatile static KafkaConfigMap kafkaConfigMap = null;

    public static KafkaConfigMap getKafkaConfigMap() {
        if (Objects.isNull(kafkaConfigMap)) {
            synchronized (KafkaConfigMap.class) {
                if (kafkaConfigMap == null) {
                    kafkaConfigMap = getKafkaConsumerConfigMap();
                }
            }
        }
        return kafkaConfigMap;
    }

    private KafkaConfigMap(Map<String, Object> propsMap) {
        this.propsMap = propsMap;
    }

    private static KafkaConfigMap getKafkaConsumerConfigMap() {
        Map<String, Object> propsMap = new HashMap<>();
        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.21.0.2:9092");
        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        propsMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        propsMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        propsMap.put(JsonDeserializer.TRUSTED_PACKAGES, "ir.bigz.kafka.dto");
        propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, "kafka-error-group");
        propsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        propsMap.put(ProducerConfig.RETRIES_CONFIG, "3");
        propsMap.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, "30000");
        propsMap.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, "1000");
        propsMap.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        propsMap.put(ProducerConfig.ACKS_CONFIG, "all");
        propsMap.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5"); // set it to One if Ordering of messages are important
        return new KafkaConfigMap(propsMap);
    }

    public Map<String, Object> getPropsMap() {
        return propsMap;
    }
}
