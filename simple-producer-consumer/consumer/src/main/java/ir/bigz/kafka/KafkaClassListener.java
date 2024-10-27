package ir.bigz.kafka;

import ir.bigz.kafka.dto.Customer;
import ir.bigz.kafka.dto.Message;
import ir.bigz.kafka.dto.Purchase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@KafkaListener(topics = "customer-message", groupId = "pouya-group")
public class KafkaClassListener {

    private final Logger log = LoggerFactory.getLogger(KafkaClassListener.class);
    private final KafkaMessagePublisher kafkaMessagePublisher;

    public KafkaClassListener(KafkaMessagePublisher kafkaMessagePublisher) {
        this.kafkaMessagePublisher = kafkaMessagePublisher;
    }

    @KafkaHandler
    public void consumeMessage(@Payload Message<Customer> message) {
        log.info("Consumer consume the message payload: {}", message.toString());
        Random random = new Random();
        int randomNum = random.nextInt(1000 - 1 + 1) + 1;
        long randomPrice = random.nextInt(10_000 - 1_00 + 1);
        var purchase = new Purchase(randomNum, true, String.valueOf(randomPrice));
        kafkaMessagePublisher.sendMessageToTopic(message.getMessageId(), purchase);
    }
}
