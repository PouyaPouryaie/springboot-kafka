package ir.bigz.kafka;

import ir.bigz.kafka.dto.Message;
import ir.bigz.kafka.dto.Purchase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class KafkaMessagePublisher {

    Logger log = LoggerFactory.getLogger(KafkaMessagePublisher.class);

    // to communicate with kafka we need to use kafkaTemplate class
    private final KafkaTemplate<String, Object> template;

    public KafkaMessagePublisher(KafkaTemplate<String, Object> template) {
        this.template = template;
    }

    public void sendMessageToTopic(UUID messageUUID, Purchase purchase) {
        var purchaseMessage = new Message<>(messageUUID, purchase);
        CompletableFuture<SendResult<String, Object>> future = template.send("purchase-message", purchaseMessage);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent Purchase={} with offset={}", purchase.toString(), result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send Purchase={} due to {}", purchase.toString(), ex.getMessage());
            }
        });
    }
}
