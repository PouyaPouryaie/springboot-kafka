package ir.bigz.kafka;

import ir.bigz.kafka.dto.Customer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Testcontainers
@Import(ContainersConfig.class)
class ProducerApplicationTests {

	Logger log = LoggerFactory.getLogger(ProducerApplicationTests.class);

	@Autowired
	private KafkaMessagePublisher publisher;

	@Test
	public void testSendEventToTopic() {
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
