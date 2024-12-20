package ir.bigz.kafka;

import ir.bigz.kafka.config.KafkaProducerTestConfig;
import ir.bigz.kafka.dto.Customer;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Testcontainers
@Import(KafkaProducerTestConfig.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class KafkaMessagePublisherTest {

	Logger log = LoggerFactory.getLogger(KafkaMessagePublisherTest.class);

	static KafkaContainer kafkaContainer;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	private KafkaMessagePublisher publisher;

	@PostConstruct
	void beforeAll() {
		kafkaContainer = applicationContext.getBean(KafkaContainer.class);
	}

	@AfterAll
	static void destroy() {
		kafkaContainer.stop();
	}

	@Test
	public void sendMessageToTopic_customer_input_pass_test() {
		Customer customer = new Customer(1, "sina", "sina@yahoo.com");
		publisher.sendMessageToTopic(customer);
		Awaitility.await()
				.pollInterval(Duration.ofSeconds(3))
				.atMost(10, TimeUnit.SECONDS)
				.untilAsserted(() -> {
					log.info("The test is passed");
				});
	}
}
