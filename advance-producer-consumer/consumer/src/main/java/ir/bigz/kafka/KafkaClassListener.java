package ir.bigz.kafka;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import ir.bigz.kafka.dto.Customer;
import ir.bigz.kafka.dto.Message;
import ir.bigz.kafka.dto.Purchase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

@Component
@KafkaListener(topics = "message-topic", groupId = "pouya-group")
public class KafkaClassListener {

    private final Logger log = LoggerFactory.getLogger(KafkaClassListener.class);
    private final KafkaMessagePublisher kafkaMessagePublisher;
    private final Random random = new Random();

    public KafkaClassListener(KafkaMessagePublisher kafkaMessagePublisher) {
        this.kafkaMessagePublisher = kafkaMessagePublisher;
    }

//    private static final JavaType stringType = TypeFactory.defaultInstance().constructType(String.class);
//    private static final JavaType messageType = TypeFactory.defaultInstance().constructType(Message.class);
//
//    public static JavaType typer(byte[] data, Headers headers) {
//        return new String(data).contains("messageId") ? messageType : stringType;
//    }

    @KafkaHandler
    public void consumeMessage(@Payload Message<Customer> message, @Headers MessageHeaders headers) {
        log.info("Consumer consume the message payload: {}", message.toString());
        headers.keySet().forEach(key -> log.info("{}:{}", key, headers.get(key)));
        int randomNum = random.nextInt(1000 - 1 + 1) + 1;
        long randomPrice = random.nextInt(10_000 - 1_00 + 1);
        var purchase = new Purchase(randomNum, true, String.valueOf(randomPrice));
        kafkaMessagePublisher.sendMessageToTopic(message.getMessageId(), purchase);
    }

    @KafkaHandler
    public void consumeMessage(@Payload Customer message) {
        log.info("Consumer consume the customer payload: {}", message.toString());
        int randomNum = random.nextInt(1000 - 1 + 1) + 1;
        long randomPrice = random.nextInt(10_000 - 1_00 + 1);
        var purchase = new Purchase(randomNum, true, String.valueOf(randomPrice));
        kafkaMessagePublisher.sendMessageToTopic(UUID.randomUUID(), purchase);
    }

    @KafkaHandler
    public void consumeStringMessage(@Payload String message) {
        log.info("Consumer consume the string payload: {}", message);
    }
}
