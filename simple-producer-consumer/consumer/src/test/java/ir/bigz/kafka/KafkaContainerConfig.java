package ir.bigz.kafka;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration(proxyBeanMethods = false)
public class KafkaContainerConfig {

    @Bean
    @ServiceConnection
    public KafkaContainer kafkaContainer() {
        return new KafkaContainer(
                DockerImageName.parse("confluentinc/cp-kafka:7.6.1")
                        .asCompatibleSubstituteFor("apache/kafka"))
                .withKraft();
    }

}
