package ir.bigz.kafka;


import ir.bigz.kafka.dto.Customer;
import ir.bigz.kafka.dto.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentMap;

@Component
@KafkaListener(topics = "idempotence-topic", groupId = "class-listener-group", topicPartitions = {
        @TopicPartition(topic = "idempotence-topic", partitions = {"0, 1"})
})
public class KafkaClassListener {

    private static final Logger log = LoggerFactory.getLogger(KafkaClassListener.class);
    private final ConcurrentMap<String, String> processKeys;
    private static int random_fail = 0;

    public KafkaClassListener(@Qualifier("processKeys") ConcurrentMap<String, String> processKeys) {
        this.processKeys = processKeys;
    }

    @KafkaHandler
    public void consumeMessage(@Payload Message<Customer> message, @Headers MessageHeaders headers, Acknowledgment ack) {

        String key = String.valueOf(headers.get("kafka_receivedMessageKey"));

        if(!processKeys.containsKey(key)) {
            try {

                if(random_fail % 3 == 0) {
                    throw new Exception(String.format("random failed for key: %s, random-failed: %s", key, random_fail));
                }

                log.info("received message: {}", message.toString());
                headers.keySet().forEach(header -> log.info("{}:{}", header, headers.get(header)));
                processKeys.put(key, "processed");
                ack.acknowledge();
            } catch (Exception ex) {
                log.info("Error during Process the message, key= {}", key);
            }

        }else {
            log.info("Duplicate message detected and ignored, key= {}", key);
            ack.acknowledge();
        }

        random_fail += 1;
    }
}
