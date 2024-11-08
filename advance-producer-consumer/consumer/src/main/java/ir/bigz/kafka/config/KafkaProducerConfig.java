package ir.bigz.kafka.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration(proxyBeanMethods = false)
@Profile("production")
public class KafkaProducerConfig {

    @Value("${kafka.bootstrap-server}")
    private String bootstrapServer;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        KafkaProperties kafkaProperties = KafkaProperties.Builder.getInstance(bootstrapServer)
                .defaultProducerConfig().build();
        return new DefaultKafkaProducerFactory<>(kafkaProperties.getProps());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public AdminClient adminClient() {
        KafkaProperties kafkaProperties = KafkaProperties.Builder.getInstance(bootstrapServer)
                .defaultProducerConfig().build();
        return AdminClient.create(kafkaProperties.getProps());
    }
}
