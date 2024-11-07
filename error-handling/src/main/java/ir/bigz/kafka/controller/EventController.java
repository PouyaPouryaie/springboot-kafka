package ir.bigz.kafka.controller;

import ir.bigz.kafka.dto.User;
import ir.bigz.kafka.publisher.KafkaMessagePublisher;
import ir.bigz.kafka.utils.CsvReaderUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/producer")
public class EventController {

    KafkaMessagePublisher kafkaMessagePublisher;

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
        kafkaMessagePublisher.sendCSVFile(Optional.of("kafka-blocking-retry"));
        return ResponseEntity.ok("Message published successfully");
    }
}
