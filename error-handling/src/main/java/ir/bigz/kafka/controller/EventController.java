package ir.bigz.kafka.controller;

import ir.bigz.kafka.dto.User;
import ir.bigz.kafka.publisher.KafkaMessagePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST controller for publishing events to Kafka topics.
 */
@RestController
@RequestMapping("/producer")
public class EventController {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    KafkaMessagePublisher kafkaMessagePublisher;

    @Value("${app.topic.custom.name}")
    private String customErrorHandlerTopicName;

    public EventController(KafkaMessagePublisher kafkaMessagePublisher) {
        this.kafkaMessagePublisher = kafkaMessagePublisher;
    }

    /**
     * Publishes a single message to the default Kafka topic.
     *
     * @param user the user payload
     * @return a response indicating success
     */
    @PostMapping("/")
    public ResponseEntity<?> publishMessage(@RequestBody User user) {
        log.info("Received request to publish message: {}", user);

        kafkaMessagePublisher.send(user, Optional.empty());
        return ResponseEntity.ok(new ApiResponse("Message published successfully",
                HttpStatus.OK.value()));
    }

    /**
     * Publishes messages from a CSV file to the default Kafka topic.
     *
     * @return a response indicating the request was accepted
     */
    @GetMapping("/")
    public ResponseEntity<?> publishBulkMessage() {
        log.info("Received request to publish bulk messages to default topic");

        kafkaMessagePublisher.sendCSVFile(Optional.empty());
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ApiResponse("Bulk messages publishing initiated",
                        HttpStatus.ACCEPTED.value()));
    }

    /**
     * Publishes a single message to the custom-error topic.
     *
     * @param user the user payload
     * @return a response indicating success
     */
    @PostMapping("/custom-error-handler")
    public ResponseEntity<?> publishMessageForCustomErrorHandlerTopic(@RequestBody User user) {
        log.info("Received request to publish to custom-error-handler Topic message: {}", user);

        kafkaMessagePublisher.send(user, Optional.of(customErrorHandlerTopicName));
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ApiResponse("Message published for custom-error-handler topic",
                        HttpStatus.ACCEPTED.value()));
    }

    /**
     * Publishes messages from a CSV file to a specific Kafka topic (custom-error-handler topic).
     *
     * @return a response indicating the request was accepted
     */
    @GetMapping("/custom-error-handler")
    public ResponseEntity<?> publishBulkMessageForCustomErrorHandlerTopic() {
        log.info("Received request to publish bulk messages to custom-error-handler topic: {}", customErrorHandlerTopicName);

        kafkaMessagePublisher.sendCSVFile(Optional.of(customErrorHandlerTopicName));
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ApiResponse("Bulk messages publishing initiated for custom-error-handler topic",
                        HttpStatus.ACCEPTED.value()));
    }


    /**
     * A custom API response structure for consistency.
     */
    public static class ApiResponse {
        private final String message;
        private final int statusCode;

        public ApiResponse(String message, int statusCode) {
            this.message = message;
            this.statusCode = statusCode;
        }

        public String getMessage() {
            return message;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }
}
