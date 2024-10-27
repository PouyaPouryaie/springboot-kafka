package ir.bigz.kafka;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

    @Bean
    @ServiceConnection
    public KafkaContainer kafkaContainer() {
//        KafkaContainer kafkaContainer = new KafkaContainer(
//                DockerImageName.parse("confluentinc/cp-kafka:7.4.0")
//                .asCompatibleSubstituteFor("apache/kafka"));
        KafkaContainer kafkaContainer = new KafkaContainer(
                DockerImageName.parse("confluentinc/cp-kafka:7.4.0")
                        .asCompatibleSubstituteFor("apache/kafka"))
                .withKraft();
        return kafkaContainer;
    }

}
