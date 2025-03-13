package ir.bigz.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.bigz.kafka.dto.User;
import ir.bigz.kafka.exception.ConsumerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Kafka listener for consuming messages with custom error retry logic.
 */
@Service
public class CustomErrorRetryKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(CustomErrorRetryKafkaListener.class);
    private final ObjectMapper objectMapper;
    private final Set<String> restrictedIpList;

    public CustomErrorRetryKafkaListener(ObjectMapper objectMapper,
                                         @Value("${app.restricted.ips}") Set<String> restrictedIpList) {
        this.objectMapper = objectMapper;
        this.restrictedIpList = restrictedIpList;
    }

    /**
     * Consumes messages from the Kafka topic and processes them with custom error retry logic.
     *
     * @param user   the user object received from Kafka.
     * @param topic  the Kafka topic from which the message was received.
     * @param offset the offset of the message in the topic.
     * @param ack    the Kafka Acknowledgment to commit offset.
     */
    @KafkaListener(containerFactory = "kafkaCustomErrorRetryContainerFactory",
            topics = "${app.topic.custom.name}", groupId = "custom-error-handler-group")
    public void consumeEvents(User user,
                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                              @Header(KafkaHeaders.OFFSET) long offset,
                              Acknowledgment ack
    ) {

        log.info("Received message: {}  from topic: {} offset: {}", serializeUser(user), topic, offset);

        //validate restricted IP before process the records
        validateIp(user.getIpAddress(), topic, offset);

        // Manually acknowledge after successful processing
        ack.acknowledge();

        // Add further processing logic here
        log.info("Successfully processed message for user: {}", user.getId());
    }

    /**
     * Validates whether the given IP address is restricted.
     *
     * @param ipAddress The IP address to validate.
     * @param topic     The Kafka topic from which the message was received.
     * @param offset    The offset of the message in the topic.
     * @throws ConsumerException if the IP address is restricted.
     */
    private void validateIp(String ipAddress, String topic, long offset) {
        if (restrictedIpList.contains(ipAddress)) {
            String errorMessage = String.format("Restricted IP [%s] received from topic [%s], offset [%d]",
                    ipAddress, topic, offset);
            log.warn(errorMessage);
            throw new ConsumerException(errorMessage, topic, offset);
        }
    }

    /**
     * Serializes the User object to a JSON string.
     *
     * @param user the user object
     * @return A JSON string representation of the user or "Serialization Error" if serialization fails.
     */
    private String serializeUser(User user) {
        try {
            return objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize User object: {}", e.getMessage(), e);
            return "Serialization Error";
        }
    }
}
