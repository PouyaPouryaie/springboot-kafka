package ir.bigz.kafka;

import ir.bigz.kafka.dto.Customer;
import ir.bigz.kafka.dto.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@Service
public class KafkaMethodListener {

    Logger log = LoggerFactory.getLogger(KafkaMethodListener.class);

    private CountDownLatch latch = new CountDownLatch(1);

    @KafkaListener(topics = "message-customer-topic")
    public void consume(Message<Customer> customer) {
        log.info("received message= {}", customer);
        latch.countDown();
    }

    @KafkaListener(topics = "message-string-topic", groupId = "method-listener-group",
            topicPartitions = {@TopicPartition(topic = "message-string-topic",
                    partitionOffsets = { @PartitionOffset(partition = "0", initialOffset = "0")})})
    public void consumeSpecificPartitionFromBeginning(String message,
                                                      @Header(KafkaHeaders.RECEIVED_PARTITION) long partition,
                                                      @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("received message: {}, with partition-offset= {}", message, partition + "-" + offset);
        latch.countDown();
    }

    @KafkaListener(topics = "message-string-topic", groupId = "method-listener-group",
        topicPartitions = {@TopicPartition(topic = "message-string-topic", partitions = {"1"})})
    public void consumeSpecificPartition(String message,
                                         @Header(KafkaHeaders.RECEIVED_PARTITION) long partition,
                                         @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("received message: {}, with partition-offset= {}", message, partition + "-" + offset);
        latch.countDown();
    }

// To testing, you can define multiple method that listen to specific topic and show you mimic of working as different instance in group consuming
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
