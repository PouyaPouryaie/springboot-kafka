package ir.bigz.kafka.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ConfigurationProperties(prefix = "kafka.config")
@Profile("production")
public class KafkaProperties {

    private String bootstrapServer;
    private String trustedPackage;
    private String defaultConsumerGroupId;
    private String topicName;

    public KafkaProperties() {
    }

    public String getBootstrapServer() {
        return bootstrapServer;
    }

    public void setBootstrapServer(String bootstrapServer) {
        this.bootstrapServer = bootstrapServer;
    }

    public String getTrustedPackage() {
        return trustedPackage;
    }

    public void setTrustedPackage(String trustedPackage) {
        this.trustedPackage = trustedPackage;
    }

    public String getDefaultConsumerGroupId() {
        return defaultConsumerGroupId;
    }

    public void setDefaultConsumerGroupId(String defaultConsumerGroupId) {
        this.defaultConsumerGroupId = defaultConsumerGroupId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
