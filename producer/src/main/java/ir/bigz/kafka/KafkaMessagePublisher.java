package ir.bigz.kafka;

import ir.bigz.kafka.dto.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaMessagePublisher {

    Logger log = LoggerFactory.getLogger(KafkaMessagePublisher.class);

    // to communicate with kafka we need to use kafkaTemplate class
    private final KafkaTemplate<String, Object> template;

    public KafkaMessagePublisher(KafkaTemplate<String, Object> template) {
        this.template = template;
    }

    public void sendMessageToTopic(String message) {
        CompletableFuture<SendResult<String, Object>> future = template.send("kafka-spring-topic", message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent Message={} with offset={}", message, result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send message={} due to {}", message, ex.getMessage());
            }
        });
    }

    public void sendEventToTopic(Customer customer) {
        CompletableFuture<SendResult<String, Object>> future = template.send("kafka-spring-topic", customer);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent Customer={} with offset={}", customer.toString(), result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send Customer={} due to {}", customer.toString(), ex.getMessage());
            }
        });
    }
}
