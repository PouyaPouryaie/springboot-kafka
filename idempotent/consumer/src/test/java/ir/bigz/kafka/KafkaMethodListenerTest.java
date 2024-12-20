package ir.bigz.kafka;

import ir.bigz.kafka.config.KafkaConsumerTestConfig;
import ir.bigz.kafka.dto.Customer;
import ir.bigz.kafka.dto.Message;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.AfterAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
@Import(KafkaConsumerTestConfig.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class KafkaMethodListenerTest {

    static final Logger log = LoggerFactory.getLogger(KafkaMethodListenerTest.class);

    static KafkaContainer kafkaContainer;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    KafkaMethodListener kafkaMethodListener;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${test.message.topic}")
    private String messageTopic;

    @Value("${test.string.topic}")
    private String stringTopic;

    @PostConstruct
    void beforeAll() {
        kafkaContainer = applicationContext.getBean(KafkaContainer.class);
    }

    @BeforeEach
    void setup() {
        kafkaMethodListener.resetLatch();
    }

    @AfterAll
    static void destroy() {
        kafkaContainer.stop();
    }

    @Test
    void consume_method_pass_test() {
        log.info("test execution started ....");
        Customer customer = new Customer(-1, "test user", "test@user.com");
        Message<Customer> message = new Message<>(customer);
        kafkaTemplate.send(messageTopic, message);
        log.info("message sent to kafka successfully ....");

        await().pollInterval(Duration.ofSeconds(3))
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    long messageConsumed = kafkaMethodListener.getLatch().getCount();
                    Assertions.assertEquals(0, messageConsumed);
        });

        log.info("test execution ended");
    }

    @Test
    void consumeSpecificPartition_method_pass_test() throws Exception {
        log.info("test execution started ....");
        kafkaTemplate.send(stringTopic, 1, null, "Hello There");
        log.info("message sent to kafka successfully ....");
        boolean messageConsumed = kafkaMethodListener.getLatch().await(10, TimeUnit.SECONDS);
        Assertions.assertEquals(0, kafkaMethodListener.getLatch().getCount());
        log.info("test execution ended");
    }

    @Test
    void consumeSpecificPartitionFromBeginning_method_pass_test() throws Exception {
        log.info("test execution started ....");
        kafkaTemplate.send(stringTopic, 0, null, "first message");
        kafkaTemplate.send(stringTopic, 0, null, "second message");
        log.info("message sent to kafka successfully ....");
        if(kafkaMethodListener.getLatch().await(5, TimeUnit.SECONDS)) {
            log.info("first message received successfully ....");
            Assertions.assertEquals(0, kafkaMethodListener.getLatch().getCount());
        }
        kafkaMethodListener.resetLatch();
        if(kafkaMethodListener.getLatch().await(5, TimeUnit.SECONDS)) {
            log.info("second message received successfully ....");
            Assertions.assertEquals(0, kafkaMethodListener.getLatch().getCount());
        }
        log.info("test execution ended");
    }
}
