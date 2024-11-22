package ir.bigz.kafka;

import ir.bigz.kafka.dto.Customer;
import ir.bigz.kafka.dto.Message;
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

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
@Import(KafkaConsumerTestConfig.class)
class ConsumerApplicationTests {

    Logger log = LoggerFactory.getLogger(ConsumerApplicationTests.class);

    @Autowired
    KafkaMethodListener kafkaMethodListener;

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
        Message<Customer> message = new Message<>(customer);
        kafkaTemplate.send("message-customer-topic", message);
        log.info("test consume Event execution ended ....");

        await().pollInterval(Duration.ofSeconds(3))
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    long messageConsumed = kafkaMethodListener.getLatch().getCount();
                    Assertions.assertEquals(0, messageConsumed);
        });
    }

    @Test
    public void testConsumeMessage() throws Exception{
        log.info("test message Event execution started ....");
        kafkaTemplate.send("message-string-topic", 1, null, "Hello There");
        log.info("test message Event execution ended ....");
        boolean messageConsumed = kafkaMethodListener.getLatch().await(10, TimeUnit.SECONDS);
        Assertions.assertTrue(messageConsumed);
    }
}
