package ir.bigz.kafka.publisher;

import ir.bigz.kafka.config.KafkaProperties;
import ir.bigz.kafka.dto.User;
import ir.bigz.kafka.exception.PublisherException;
import ir.bigz.kafka.utils.CsvReaderUtils;
import org.apache.kafka.common.errors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


/**
 * Kafka message publisher for sending messages to Kafka topics.
 */

@Service
public class KafkaMessagePublisher {

    private final static Logger log = LoggerFactory.getLogger(KafkaMessagePublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public KafkaMessagePublisher(KafkaTemplate<String, Object> kafkaTemplate, KafkaProperties kafkaProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProperties = kafkaProperties;
    }


    /**
     * Sends a message to the specified Kafka topic.
     *
     * @param <T>      the type of the message payload
     * @param message  the message payload
     * @param topicName the optional topic name; uses default if not provided
     */
    public <T> void send(T message, Optional<String> topicName) {

        Objects.requireNonNull(message, "message cannot be null");
        String targetTopic = topicName.orElse(kafkaProperties.getTopicName());
        log.info("Publishing message to topic: {}", targetTopic);

        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                    targetTopic, message);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    onSuccess(result, message);
                } else {
                    handleException(ex, message, targetTopic);
                }
            });
        } catch (Exception ex) {
            log.error("Exception occurred while sending message to Kafka", ex);
            throw new PublisherException("Failed to publish message to Kafka", ex);
        }
    }

    /**
     * Reads a CSV file and sends each entry as a Kafka message.
     *
     * @param topicName the optional topic name; uses default if not provided
     */
    public void sendCSVFile(Optional<String> topicName) {

        log.info("Attempting to publish messages from CSV to topic: {}",
                topicName.orElse(kafkaProperties.getTopicName()));

        try {
            List<User> users = CsvReaderUtils.readDataFromCsv();
            Objects.requireNonNull(users, "CSV file contains no data")
                    .forEach(user -> this.send(user, topicName));

        } catch (Exception ex) {
            log.error("Exception occurred while processing CSV file", ex);
            throw new PublisherException("Failed to publish messages from CSV file to Kafka", ex);
        }
    }

    /**
     * Handles successful message publishing.
     *
     * @param result the result of the publish operation
     * @param t      the message payload
     */
    private <T> void onSuccess(final SendResult<String, Object> result, final T t) {
        log.info("Successfully send message=[{}] to topic-partition={}-{} with offset={}",
                t,
                result.getRecordMetadata().topic(),
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset());
    }

    /**
     * Handles exception message publishing.
     *
     * @param ex the exception that occurred
     * @param payload the message payload
     */
    private <T> void handleException(Throwable ex, final T payload, final String targetTopic) {

        if (ex instanceof UnsupportedVersionException
                || ex instanceof RecordTooLargeException
                || ex instanceof CorruptRecordException) {
            log.error("Non-Retriable exception occurred. Sending message to dead-letter queue: [{}] Error: {}", payload, ex.getMessage());
            sendToDeadLetterTopic(payload, targetTopic);
        } else if (ex instanceof SerializationException) {
            log.error("Serialization error! message=[{}]", payload);
        } else if (ex instanceof RetriableException) {
            log.warn("Retriable exception occurred. Kafka will retry automatically: {}", ex.getMessage());
        }
    }

    /**
     * Sending Non-Retriable exception to DLT Queue.
     *
     * @param topic the DLT name
     * @param payload  the message payload
     */
    private <T> void sendToDeadLetterTopic(T payload, final String topic) {
        String deadLetterTopic = topic + ".DLT";
        log.info("Sending failed message to Dead Letter Topic: {}", deadLetterTopic);
        kafkaTemplate.send(deadLetterTopic, payload);
    }
}
