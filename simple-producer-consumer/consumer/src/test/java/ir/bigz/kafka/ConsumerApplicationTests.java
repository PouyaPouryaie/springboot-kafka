package ir.bigz.kafka;

import ir.bigz.kafka.dto.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Testcontainers
@Import(KafkaConsumerTestConfig.class)
class ConsumerApplicationTests {

    Logger log = LoggerFactory.getLogger(ConsumerApplicationTests.class);

    @Autowired
    KafkaMethodListener kafkaMethodListener;

//    @Container
//    public static KafkaContainer kafkaContainer = new KafkaContainer(
//            DockerImageName.parse("confluentinc/cp-kafka:7.4.0")
//                    .asCompatibleSubstituteFor("apache/kafka"))
//            .withKraft();
//
//    @DynamicPropertySource
//    static void overridePropertiesInternal(DynamicPropertyRegistry registry) {
//        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
//    }

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    public void setup() {
        kafkaMethodListener.resetLatch();
    }

    @Test
    public void testConsumeEvent() throws Exception{
        log.info("test consume Event execution started ....");
        Customer customer = new Customer(-1, "test user", "test@user.com");
        kafkaTemplate.send("kafka-spring-topic", customer);
        log.info("test consume Event execution ended ....");

        Awaitility.await().pollInterval(Duration.ofSeconds(3))
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    long messageConsumed = kafkaMethodListener.getLatch().getCount();
                    Assertions.assertEquals(0, messageConsumed);
        });
    }

    @Test
    public void testConsumeMessage() throws Exception{
        log.info("test message Event execution started ....");
        kafkaTemplate.send("pouya-topic",1, null, "Hello There");
        log.info("test message Event execution ended ....");
        boolean messageConsumed = kafkaMethodListener.getLatch().await(10, TimeUnit.SECONDS);
        Assertions.assertTrue(messageConsumed);
    }
}
