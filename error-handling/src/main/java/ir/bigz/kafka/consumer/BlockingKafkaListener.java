package ir.bigz.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.bigz.kafka.dto.User;
import ir.bigz.kafka.exception.ConsumerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Service
public class BlockingKafkaListener {

    Logger log = LoggerFactory.getLogger(BlockingKafkaListener.class);

    @KafkaListener(containerFactory = "kafkaCustomBlockingRetryContainerFactory",
            topics = "${app.topic.custom.name}", groupId = "consume-blocking-group")
    public void consumeEvents(User user,
                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                              @Header(KafkaHeaders.OFFSET) long offset
    ) throws IOException {
        try {
            log.info("CustomListener Received: {} from {} offset {}", new ObjectMapper()
                    .writeValueAsString(user), topic, offset);
            //validate restricted IP before process the records
            List<String> restrictedIpList = Stream
                    .of("32.241.244.236", "15.55.49.164", "81.1.95.253", "126.130.43.183")
                    .toList();
            if (restrictedIpList.contains(user.getIpAddress())) {
                throw new ConsumerException(String.format("Invalid IP: [%s] received !", user.getIpAddress()));
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }
}
