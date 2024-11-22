package ir.bigz.kafka;

import ir.bigz.kafka.dto.Customer;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Testcontainers
@Import(ContainersConfig.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ProducerApplicationTests {

	Logger log = LoggerFactory.getLogger(ProducerApplicationTests.class);

	@Autowired
	private KafkaMessagePublisher publisher;

	@Test
	public void send_event_to_topic_pass_test() {
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
