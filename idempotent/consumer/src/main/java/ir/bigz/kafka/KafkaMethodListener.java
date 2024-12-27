package ir.bigz.kafka;

import ir.bigz.kafka.dto.Customer;
import ir.bigz.kafka.dto.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

@Service
public class KafkaMethodListener {

    private static final Logger log = LoggerFactory.getLogger(KafkaMethodListener.class);

    private CountDownLatch latch = new CountDownLatch(1);

    private final ConcurrentMap<String, String> processKeys;

    public KafkaMethodListener(ConcurrentMap<String, String> processKeys) {
        this.processKeys = processKeys;
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 3000, multiplier = 1.5, maxDelay = 15000),
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            exclude = {NullPointerException.class}
    )
    @KafkaListener(topics = "idempotence-topic", topicPartitions = {
            @TopicPartition(topic = "idempotence-topic",
                    partitionOffsets = { @PartitionOffset(partition = "2", initialOffset = "0")})
    })
    public void consume(ConsumerRecord<String, Message<Customer>> record, Acknowledgment ack) {

        String key = record.key();

        if(!processKeys.containsKey(key)) {
            try {
                Message<Customer> message = record.value();
                log.info("received message= {}", message);
                processKeys.put(key, "processed");
                ack.acknowledge(); // Commit offset
                latch.countDown();
            } catch (Exception e) {
                // Do not acknowledge to retry later
                log.info("Error during Process the message, key= {}", key);
            }

        } else {
            log.info("Duplicate message detected and ignored, key= {}", key);
            ack.acknowledge(); // Acknowledge duplicates
        }

    }

    @DltHandler
    public void sendToDlt(@Payload Message<Customer> message,
                          @Headers MessageHeaders headers,
                          @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                          @Header(KafkaHeaders.OFFSET) long offset,
                          @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {

        String receivedKey = String.valueOf(headers.get("kafka_receivedMessageKey"));

        log.info("DLT Received : {} , from {} , partition {}, offset {}, key {}",
                message.getMessage(), topic, partition, offset, receivedKey);
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }
}
