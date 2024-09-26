package ir.bigz.kafka.controller;

import ir.bigz.kafka.dto.User;
import ir.bigz.kafka.publisher.KafkaMessagePublisher;
import ir.bigz.kafka.utils.CsvReaderUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/producer")
public class EventController {

    KafkaMessagePublisher kafkaMessagePublisher;

    public EventController(KafkaMessagePublisher kafkaMessagePublisher) {
        this.kafkaMessagePublisher = kafkaMessagePublisher;
    }

    @PostMapping("/")
    public ResponseEntity<?> publishMessage(@RequestBody User user) {
        kafkaMessagePublisher.send(user);
        return ResponseEntity.ok("Message published successfully");
    }

    @GetMapping("/")
    public ResponseEntity<?> publishBulkMessage() {
        kafkaMessagePublisher.sendCSVFile();
        return ResponseEntity.ok("Message published successfully");
    }
}
