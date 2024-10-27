package ir.bigz.kafka;

import ir.bigz.kafka.dto.Customer;
import org.junit.jupiter.api.Test;
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
class ProducerApplicationTests {

	@Autowired
	private KafkaMessagePublisher publisher;


	@Test
	public void testSendEventToTopic() {
		Customer customer = new Customer(1, "sina", "sina@yahoo.com");
		publisher.sendEventToTopic(customer);
		Awaitility.await()
				.pollInterval(Duration.ofSeconds(3))
				.atMost(10, TimeUnit.SECONDS)
				.untilAsserted(() -> {

		});
	}
}
