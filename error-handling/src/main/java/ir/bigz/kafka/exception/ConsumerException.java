package ir.bigz.kafka.exception;

public class ConsumerException extends RuntimeException {

    private final String topic;
    private final Long offset;

    /**
     * Constructor with message only.
     *
     * @param message the error message
     */
    public ConsumerException(String message) {
        super(message);
        this.topic = null;
        this.offset = null;
    }

    /**
     * Constructor with message and cause.
     *
     * @param message the error message
     * @param cause   the root cause
     */
    public ConsumerException(String message, Throwable cause) {
        super(message, cause);
        this.topic = null;
        this.offset = null;
    }

    /**
     * Constructor with message, topic, and offset.
     *
     * @param message the error message
     * @param topic   the Kafka topic where the error occurred
     * @param offset  the offset of the message
     */
    public ConsumerException(String message, String topic, Long offset) {
        super(message);
        this.topic = topic;
        this.offset = offset;
    }

    /**
     * Constructor with message, cause, topic, and offset.
     *
     * @param message the error message
     * @param cause   the root cause
     * @param topic   the Kafka topic where the error occurred
     * @param offset  the offset of the message
     */
    public ConsumerException(String message, Throwable cause, String topic, Long offset) {
        super(message, cause);
        this.topic = topic;
        this.offset = offset;
    }

    /**
     * Gets the Kafka topic where the error occurred.
     *
     * @return the topic name, or null if not set
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Gets the Kafka message offset where the error occurred.
     *
     * @return the offset, or null if not set
     */
    public Long getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return "ConsumerException{" +
                "message='" + getMessage() + '\'' +
                ", topic='" + topic + '\'' +
                ", offset=" + offset +
                ", cause=" + getCause() +
                '}';
    }
}
