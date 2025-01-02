package ir.bigz.kafka.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class KafkaExceptionHandler {

    @ExceptionHandler(PublisherException.class)
    public ResponseEntity<?> publisherHandler(PublisherException e) {
        return ResponseEntity.internalServerError().body(e.getMessage());
    }
}
