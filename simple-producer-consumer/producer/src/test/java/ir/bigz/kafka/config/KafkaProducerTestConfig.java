package ir.bigz.kafka.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile("test")
public class KafkaProducerTestConfig {

    @Bean
    @ServiceConnection
    public KafkaContainer kafkaContainer() {
        return new KafkaContainer(
                DockerImageName.parse("confluentinc/cp-kafka:7.6.1")
                        .asCompatibleSubstituteFor("apache/kafka"))
                .withKraft()
//                .withExposedPorts(9092)
//                .withEnv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
//                .withEnv("KAFKA_LISTENERS", "PLAINTEXT://:9092")
                ;
    }

    @Bean
    public KafkaConfigDto kafkaConfigDto(KafkaContainer kafkaContainer) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new KafkaConfigDto(props);
    }

    @Bean
    public AdminClient adminClient(KafkaConfigDto kafkaConfigDto) {
        return AdminClient.create(kafkaConfigDto.getPropsMap());
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory(KafkaContainer kafkaContainer) {
        return new DefaultKafkaConsumerFactory<>(kafkaConfigDto(kafkaContainer).getPropsMap());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Object>> kafkaListenerContainerFactory(KafkaContainer kafkaContainer) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(kafkaContainer));
        return factory;
    }
}
