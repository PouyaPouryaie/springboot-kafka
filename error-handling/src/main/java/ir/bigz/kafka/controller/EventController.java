package ir.bigz.kafka.controller;

import ir.bigz.kafka.dto.User;
import ir.bigz.kafka.publisher.KafkaMessagePublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/producer")
public class EventController {

    KafkaMessagePublisher kafkaMessagePublisher;

    @Value("${app.topic.custom.name}")
    private String retryBlockTopicName;

    public EventController(KafkaMessagePublisher kafkaMessagePublisher) {
        this.kafkaMessagePublisher = kafkaMessagePublisher;
    }

    @PostMapping("/")
    public ResponseEntity<?> publishMessage(@RequestBody User user) {
        kafkaMessagePublisher.send(user, Optional.empty());
        return ResponseEntity.ok("Message published successfully");
    }

    @GetMapping("/retry-non-blocking")
    public ResponseEntity<?> publishBulkMessage() {
        kafkaMessagePublisher.sendCSVFile(Optional.empty());
        return ResponseEntity.ok("Message published successfully");
    }

    @GetMapping("/retry-blocking")
    public ResponseEntity<?> publishBulkMessageForRetryBlock() {
        kafkaMessagePublisher.sendCSVFile(Optional.of(retryBlockTopicName));
        return ResponseEntity.ok("Message published successfully");
    }
}
