package ir.bigz.kafka;

import ir.bigz.kafka.dto.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.IntStream;

@RestController
@RequestMapping("/producer")
public class EventController {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);
    private final KafkaMessagePublisher publisher;

    public EventController(KafkaMessagePublisher publisher) {
        this.publisher = publisher;
    }

    @GetMapping("/publish/{message}")
    public ResponseEntity<?> publishMessage(@PathVariable String message) {
        try {
            publisher.sendMessageToTopic(message);
            return ResponseEntity.ok("message publish successfully ...");
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }

    @PostMapping("/publish/customer")
    public ResponseEntity<?> publishEventMessage(@RequestBody Customer customer) {
        try {
            publisher.sendCustomerToTopic(customer);
            return ResponseEntity.ok("Event message publish successfully ...");
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }

    @PostMapping("/publish/message")
    public ResponseEntity<?> publishCustomerMessage(@RequestBody Customer customer) {
        try {
            publisher.sendMessageToTopic(customer);
            return ResponseEntity.ok("Event message publish successfully ...");
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }

    @GetMapping("/publish/message/batch/{size}")
    public ResponseEntity<?> publishBatchMessage(@PathVariable int size) {
        try {
            publisher.sendBatchMessageToTopic(size > 0 ? size : 20);
            return ResponseEntity.ok("Event message publish successfully ...");
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }
}
