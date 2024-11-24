package ir.bigz.kafka;

import ir.bigz.kafka.dto.Customer;
import ir.bigz.kafka.dto.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "message-customer-topic", groupId = "class-listener-group")
public class KafkaClassListener {

    private final Logger log = LoggerFactory.getLogger(KafkaClassListener.class);

    @KafkaHandler
    public void consumeMessage(@Payload Message<Customer> message, @Headers MessageHeaders headers) {
        log.info("received message: {}", message.toString());
        headers.keySet().forEach(key -> log.info("{}:{}", key, headers.get(key)));
    }
}
