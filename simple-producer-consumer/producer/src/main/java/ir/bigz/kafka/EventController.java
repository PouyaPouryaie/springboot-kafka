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

    @GetMapping("/publish/{partition}/{message}")
    public ResponseEntity<?> publishMessage(@PathVariable int partition, @PathVariable String message) {
        try {
            publisher.sendMessageToSpecificPartition(message, partition);
            return ResponseEntity.ok("message publish successfully ...");
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }

    @GetMapping("/publish/bulk/{message}")
    public ResponseEntity<?> publishBulkMessage(@PathVariable String message) {
        try {
            IntStream.range(0, 1000)
                    .forEach(i -> publisher.sendMessageToTopic(message + " " + i));
            return ResponseEntity.ok("bulk message publish successfully ...");
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
}
