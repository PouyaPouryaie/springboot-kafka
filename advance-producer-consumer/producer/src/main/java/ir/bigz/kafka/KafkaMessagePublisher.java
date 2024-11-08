package ir.bigz.kafka;

import ir.bigz.kafka.dto.Customer;
import ir.bigz.kafka.dto.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.RoutingKafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaMessagePublisher {

    Logger log = LoggerFactory.getLogger(KafkaMessagePublisher.class);

    // to communicate with kafka we need to use kafkaTemplate class
    private final RoutingKafkaTemplate template;

    public KafkaMessagePublisher(RoutingKafkaTemplate template) {
        this.template = template;
    }

    public void sendMessageToTopic(String message) {
        CompletableFuture<SendResult<Object, Object>> future = template.send("spring-topic-string", message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent String Message={} with offset={}", message, result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send message={} due to {}", message, ex.getMessage());
            }
        });
    }

    public void sendCustomerToTopic(Customer customer) {
        CompletableFuture<SendResult<Object, Object>> future = template.send("message-topic", customer);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent Customer={} with offset={}", customer.toString(), result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send Customer={} due to {}", customer.toString(), ex.getMessage());
            }
        });
    }

    public void sendMessageToTopic(Customer customer) {
        var customerMessage = new Message<>(customer);
        CompletableFuture<SendResult<Object, Object>> future = template.send("message-topic", customerMessage);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent Message={} with offset={}", customerMessage, result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send Message={} due to {}", customerMessage, ex.getMessage());
            }
        });
    }

    public void sendBatchMessageToTopic(int size) {
        for (int i = 0 ; i < size; i++) {
            var payload = "message: " + i;
            CompletableFuture<SendResult<Object, Object>> future = template.send("kafka-batch", payload);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Sent Batch Message={} with offset={}", payload, result.getRecordMetadata().offset());
                } else {
                    log.error("Unable to send Batch Message={} due to {}", payload, ex.getMessage());
                }
            });
        }
    }
}
