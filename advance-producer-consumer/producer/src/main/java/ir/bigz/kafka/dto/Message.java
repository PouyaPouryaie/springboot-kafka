package ir.bigz.kafka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
public class Message<T> {

    UUID messageId;
    T message;

    public Message(T message) {
        messageId = UUID.randomUUID();
        this.message = message;
    }

    public Message() {
    }

    public Message(UUID messageId, T message) {
        this.messageId = messageId;
        this.message = message;
    }
}
