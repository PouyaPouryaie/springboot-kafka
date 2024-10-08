package ir.bigz.kafka;

import ir.bigz.kafka.dto.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

@Service
public class KafkaMessageListener {

    Logger log = LoggerFactory.getLogger(KafkaMessageListener.class);

    private CountDownLatch latch = new CountDownLatch(1);

//    @KafkaListener(topics = "kafka-spring-topic", groupId = "spring-group-1")
    @KafkaListener(topics = "kafka-spring-topic")
    public void consume(Customer customer) {
        log.info("Consumer consume the customer: {}", customer);
        latch.countDown();
    }

    @KafkaListener(topics = "pouya-topic", groupId = "pouya-group",
            topicPartitions = {@TopicPartition(topic = "pouya-topic", partitions = {"1"})})
    public void consumeSpecificPartition(String message) {
        log.info("Consumer consume the message: {}, partition: 1", message);
        latch.countDown();
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

    public CountDownLatch getLatch() {
        return latch;
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }
}
