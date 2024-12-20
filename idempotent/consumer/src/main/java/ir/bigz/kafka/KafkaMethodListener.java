package ir.bigz.kafka;

import ir.bigz.kafka.dto.Customer;
import ir.bigz.kafka.dto.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

@Service
public class KafkaMethodListener {

    private static final Logger log = LoggerFactory.getLogger(KafkaMethodListener.class);

    private CountDownLatch latch = new CountDownLatch(1);

    private final ConcurrentMap<String, String> processKeys;

    private static int random_fail = 0;

    public KafkaMethodListener(ConcurrentMap<String, String> processKeys) {
        this.processKeys = processKeys;
    }

    @KafkaListener(topics = "idempotence-topic", topicPartitions = {
            @TopicPartition(topic = "idempotence-topic",
                    partitionOffsets = { @PartitionOffset(partition = "2", initialOffset = "0")})
    })
    public void consume(ConsumerRecord<String, Message<Customer>> record, Acknowledgment ack) {

        String key = record.key();

        if(!processKeys.containsKey(key)) {
            try {

                if(random_fail % 3 == 0) {
                    throw new Exception(String.format("random failed for key: %s, random-failed: %s", key, random_fail));
                }

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

        random_fail += 1;

    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }
}
