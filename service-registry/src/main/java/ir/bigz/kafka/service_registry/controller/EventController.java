package ir.bigz.kafka.service_registry.controller;

import ir.bigz.kafka.service_registry.dto.Employee;
import ir.bigz.kafka.service_registry.producer.KafkaProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events/v1")
public class EventController {

    private final KafkaProducer kafkaProducer;

    public EventController(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/publish")
    public ResponseEntity<?> publishEvent(@RequestBody Employee employee) {
        try {
            kafkaProducer.send(employee);
            return ResponseEntity.ok().body("Event successfully is published ...");
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
