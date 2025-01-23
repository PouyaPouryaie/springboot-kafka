package ir.bigz.kafka.exception;

/**
 * Custom exception for handling publishing errors.
 */
public class PublisherException extends RuntimeException {

    /**
     * Constructs a new PublisherException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public PublisherException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new PublisherException with the specified detail message.
     *
     * @param message the detail message
     */
    public PublisherException(String message) {
        super(message);
    }

    /**
     * Constructs a new PublisherException with no detail message or cause.
     */
    public PublisherException() {
        super();
    }

    /**
     * Returns a string representation of the exception.
     *
     * @return a string containing the exception class name and message
     */
    @Override
    public String toString() {
        return String.format("%s: %s", getClass().getName(), getMessage());
    }
}
