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

    public KafkaProperties() {
    }

    public String getBootstrapServer() {
        return bootstrapServer;
    }

    public String getTrustedPackage() {
        return trustedPackage;
    }

    public String getDefaultConsumerGroupId() {
        return defaultConsumerGroupId;
    }

    public void setBootstrapServer(String bootstrapServer) {
        this.bootstrapServer = bootstrapServer;
    }

    public void setTrustedPackage(String trustedPackage) {
        this.trustedPackage = trustedPackage;
    }

    public void setDefaultConsumerGroupId(String defaultConsumerGroupId) {
        this.defaultConsumerGroupId = defaultConsumerGroupId;
    }
}
