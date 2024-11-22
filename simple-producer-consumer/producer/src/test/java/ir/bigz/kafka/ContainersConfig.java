package ir.bigz.kafka;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

    @Bean
    @ServiceConnection
    public KafkaContainer kafkaContainer() {
        KafkaContainer kafkaContainer = new KafkaContainer(
                DockerImageName.parse("confluentinc/cp-kafka:7.6.1")
                        .asCompatibleSubstituteFor("apache/kafka"))
                .withKraft()
//                .withExposedPorts(9092)
//                .withEnv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
//                .withEnv("KAFKA_LISTENERS", "PLAINTEXT://:9092")
        ;
        return kafkaContainer;
    }

}
