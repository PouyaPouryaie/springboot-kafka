package ir.bigz.kafka.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
@ConfigurationProperties(KafkaProperties.PREFIX)
public class KafkaProperties {

    public static final String PREFIX = "kafka.properties";

    private String bootstrapServer;
    private String retries;
    private String deliveryTimeout;
    private String retryBackoff;
    private String enableIdempotence;
    private String acks;
    private String maxInFlightRequestsPerConnection;
    private String trustedPackage;
    private String consumerGroupId;
    private String topicName;
    private String autoOffsetReset;

    public KafkaProperties() {
    }

    public String getBootstrapServer() {
        return Objects.requireNonNullElse(bootstrapServer, "localhost:9092");
    }

    public void setBootstrapServer(String bootstrapServer) {
        this.bootstrapServer = bootstrapServer;
    }

    public String getRetries() {
        return retries;
    }

    public void setRetries(String retries) {
        this.retries = retries;
    }

    public String getDeliveryTimeout() {
        return deliveryTimeout;
    }

    public void setDeliveryTimeout(String deliveryTimeout) {
        this.deliveryTimeout = deliveryTimeout;
    }

    public String getRetryBackoff() {
        return retryBackoff;
    }

    public void setRetryBackoff(String retryBackoff) {
        this.retryBackoff = retryBackoff;
    }

    public String getEnableIdempotence() {
        return enableIdempotence;
    }

    public void setEnableIdempotence(String enableIdempotence) {
        this.enableIdempotence = enableIdempotence;
    }

    public String getAcks() {
        return acks;
    }

    public void setAcks(String acks) {
        this.acks = acks;
    }

    public String getMaxInFlightRequestsPerConnection() {
        return maxInFlightRequestsPerConnection;
    }

    public void setMaxInFlightRequestsPerConnection(String maxInFlightRequestsPerConnection) {
        this.maxInFlightRequestsPerConnection = maxInFlightRequestsPerConnection;
    }

    public String getTrustedPackage() {
        return trustedPackage;
    }

    public void setTrustedPackage(String trustedPackage) {
        this.trustedPackage = trustedPackage;
    }

    public String getConsumerGroupId() {
        return Objects.requireNonNullElse(consumerGroupId, "consumer-group-id");
    }

    public void setConsumerGroupId(String consumerGroupId) {
        this.consumerGroupId = consumerGroupId;
    }

    public String getTopicName() {
        return Objects.requireNonNullElse(topicName, "kafka-topic");
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getAutoOffsetReset() {
        return autoOffsetReset;
    }

    public void setAutoOffsetReset(String autoOffsetReset) {
        this.autoOffsetReset = autoOffsetReset;
    }
}
