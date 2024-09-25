package ir.bigz.kafka;

import ir.bigz.kafka.dto.Customer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Testcontainers
class ConsumerApplicationTests {

    Logger log = LoggerFactory.getLogger(ConsumerApplicationTests.class);

    @Container
    public static KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.4.0")
                    .asCompatibleSubstituteFor("apache/kafka"))
            .withKraft();

    @DynamicPropertySource
    static void overridePropertiesInternal(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    public void testConsumeEvent() {
        log.info("test consume Event execution started ....");
        Customer customer = new Customer(-1, "test user", "test@user.com");
        kafkaTemplate.send("kafka-spring-topic", customer);
        log.info("test consume Event execution ended ....");
        Awaitility.await().pollInterval(Duration.ofSeconds(3)).atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {

        });
    }
}
