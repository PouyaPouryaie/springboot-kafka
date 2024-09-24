package ir.bigz.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
public class KafkaProducerConfig {

    @Bean
    public NewTopic createTopicWithTopicBuilder() {
        return TopicBuilder.name("kafka-spring-topic-builder")
                .partitions(1)
                .replicas(1)
                .compact()
                .build();
    }

    @Bean
    public NewTopic createTopicWithNewTopic() {
        return new NewTopic("kafka-spring-topic", 3, (short) 1);
    }

//    if proxyBeanMethods is true, you can use it instead of KafkaConfigDto bean
//    @Bean
//    public Map<String, Object> producerConfigs() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.21.0.2:9092");
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        return props;
//    }

    @Bean
    public KafkaConfigDto kafkaConfigDto() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.21.0.2:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new KafkaConfigDto(props);
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory(KafkaConfigDto kafkaConfigDto) {
        return new DefaultKafkaProducerFactory<>(kafkaConfigDto.propsMap);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public AdminClient adminClient(KafkaConfigDto kafkaConfigDto) {
        return AdminClient.create(kafkaConfigDto.propsMap);
    }
}
