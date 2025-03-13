package ir.bigz.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.bigz.kafka.dto.User;
import ir.bigz.kafka.exception.ConsumerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.converter.ConversionException;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.invocation.MethodArgumentResolutionException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Kafka consumer for non-blocking retry processing.
 */
@Service
public class NonBlockingRetryKafkaListener {

    private final static Logger log = LoggerFactory.getLogger(NonBlockingRetryKafkaListener.class);
    private final ObjectMapper objectMapper;
    private final Set<String> restrictedIpList;

    public NonBlockingRetryKafkaListener(ObjectMapper objectMapper,
                                         @Value("${app.restricted.ips}") Set<String> restrictedIpList) {
        this.objectMapper = objectMapper;
        this.restrictedIpList = restrictedIpList;
    }


    /**
     * Consumes events from Kafka with retry logic applied.
     *
     * @param user   the user payload
     * @param topic  the topic from which the message was received
     * @param offset the offset of the message in the topic
     */
    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 3000, multiplier = 1.5, maxDelay = 15000),
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            exclude = {DeserializationException.class,
                    MessageConversionException.class,
                    ConversionException.class,
                    MethodArgumentResolutionException.class,
                    NoSuchMethodException.class,
                    ClassCastException.class}
//            include = {SocketTimeoutException.class, IOException.class} // you just can use one of exclude or include simultaneously
    )
    @KafkaListener(topics = "${kafka.properties.topic-name}",
            containerFactory = "defaultKafkaListenerContainerFactory")
    public void consumeEvents(User user,
                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                              @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("Received message: {}  from topic: {} offset: {}", serializeUser(user), topic, offset);

        //validate restricted IP before process the records
        if (isRestrictedIp(user.getIpAddress())) {
            String errorMessage = String.format("Invalid IP [%s] received at topic [%s], offset [%d]",
                    user.getIpAddress(), topic, offset);
            log.warn(errorMessage);
            throw new ConsumerException(errorMessage, topic, offset);
        }

        // Add further processing logic hereNonBlockingKafkaMessageConsumer
        log.info("Message successfully processed for user ID: {}", user.getId());
    }

    /**
     * Handles messages sent to the Dead Letter Topic (DLT).
     *
     * @param user   the user payload
     * @param topic  the topic from which the message was received
     * @param offset the offset of the message in the topic
     */
    @DltHandler
    public void sendToDlt(@Payload User user,
                          @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                          @Header(KafkaHeaders.OFFSET) long offset) {
        log.error("DLT Received message: {} , from topic: {} , offset: {}", serializeUser(user), topic, offset);
    }

    /**
     * Serializes the User object to a JSON string.
     *
     * @param user the user object
     * @return a JSON string representation of the user
     */
    private String serializeUser(User user) {
        try {
            return objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize User object: {}", e.getMessage(), e);
            return "Serialization Error";
        }
    }

    /**
     * Checks if the given IP address is restricted.
     *
     * @param ipAddress the IP address to validate
     * @return true if the IP is restricted, false otherwise
     */
    private boolean isRestrictedIp(String ipAddress) {
        return restrictedIpList.contains(ipAddress);
    }
}
