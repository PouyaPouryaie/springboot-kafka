package ir.bigz.kafka;

import ir.bigz.kafka.dto.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageListener {

    Logger log = LoggerFactory.getLogger(KafkaMessageListener.class);

//    @KafkaListener(topics = "kafka-spring-topic", groupId = "spring-group-1")
    @KafkaListener(topics = "kafka-spring-topic")
    public void consume(Customer customer) {
        log.info("Consumer consume the customer: {}", customer);
    }

// for testing
//    @KafkaListener(topics = "kafka-spring-topic")
//    public void consumeTwo(String message) {
//        log.info("Consumer 2 consume the message: {}", message);
//    }
//
//    @KafkaListener(topics = "kafka-spring-topic")
//    public void consumeThree(String message) {
//        log.info("Consumer 3 consume the message: {}", message);
//    }
}
