package ir.bigz.kafka;

import ir.bigz.kafka.dto.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

@Service
public class KafkaMethodListener {

    Logger log = LoggerFactory.getLogger(KafkaMethodListener.class);

    private CountDownLatch latch = new CountDownLatch(1);

//    @KafkaListener(topics = "spring-topic-string", groupId = "pouya-group",
//            topicPartitions = {@TopicPartition(topic = "spring-topic-string",
//                    partitionOffsets = { @PartitionOffset(partition = "0", initialOffset = "0")})})
    @KafkaListener(topics = "spring-topic-string", groupId = "pouya-group")
    public void consumeSpecificPartitionFromBeginning(String message, @Headers MessageHeaders headers) {
        headers.keySet().forEach(s -> log.info("{}: {}", s, headers.get(s)));
        log.info("Consumer From Beginning consume the string message: {}, partition: 0", message);
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }
}
