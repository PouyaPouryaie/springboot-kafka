package ir.bigz.kafka.publisher;

import ir.bigz.kafka.dto.User;
import ir.bigz.kafka.exception.PublisherException;
import ir.bigz.kafka.utils.CsvReaderUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class KafkaMessagePublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.properties.topic-name}")
    private String defaultTopicName;

    public KafkaMessagePublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void send(User user, Optional<String> topicName) {
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                    topicName.orElseGet(() -> defaultTopicName), user);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("Sent message=[" + user.toString() +
                            "] with offset=[" + result.getRecordMetadata().offset() + "]");
                } else {
                    System.out.println("Unable to send message=[" +
                            user.toString() + "] due to : " + ex.getMessage());
                }
            });
        } catch (Exception ex) {
            throw new PublisherException(ex.getMessage());
        }
    }

    public void sendCSVFile(Optional<String> topicName) {
        try {
            List<User> users = CsvReaderUtils.readDataFromCsv();
            Objects.requireNonNull(users).forEach(user -> this.send(user, topicName));
        } catch (Exception ex) {
            throw new PublisherException(ex.getMessage());
        }
    }
}
