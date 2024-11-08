package ir.bigz.kafka.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.RoutingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Configuration(proxyBeanMethods = false)
@Profile("production")
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

    @Bean
    public NewTopic createBatchTopic() {
        return new NewTopic("kafka-batch", 2, (short) 1);
    }

    @Bean
    public KafkaConfigDto kafkaConfigDto() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.21.0.2:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new KafkaConfigDto(props);
    }

    @Bean
    public ProducerFactory<Object, Object> defaultProducerFactory(KafkaConfigDto kafkaConfigDto) {
        return new DefaultKafkaProducerFactory<>(kafkaConfigDto.getPropsMap());
    }

    @Bean
    public Map<Pattern, ProducerFactory<Object, Object>> producerFactories(ProducerFactory<Object, Object> defaultProducerFactory) {
        Map<Pattern, ProducerFactory<Object, Object>> factories = new HashMap<>();

        // Create a default ProducerFactory for general usage which is producer string
        factories.put(Pattern.compile(".*-string"), defaultProducerFactory);
        factories.put(Pattern.compile("kafka-batch"), defaultProducerFactory);

        // ProducerFactory with Json serializer
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.21.0.2:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        DefaultKafkaProducerFactory<Object, Object> jsonProducerFactory = new DefaultKafkaProducerFactory<>(props);
        factories.put(Pattern.compile("message-.*"), jsonProducerFactory);

        return factories;
    }

    @Bean
    public RoutingKafkaTemplate routingTemplate(Map<Pattern, ProducerFactory<Object, Object>> producerFactories) {
        return new RoutingKafkaTemplate(producerFactories);
    }


    @Bean
    public AdminClient adminClient(KafkaConfigDto kafkaConfigDto) {
        return AdminClient.create(kafkaConfigDto.getPropsMap());
    }
}
