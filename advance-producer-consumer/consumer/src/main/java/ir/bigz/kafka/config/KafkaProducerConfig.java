package ir.bigz.kafka.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration(proxyBeanMethods = false)
@Profile("production")
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        KafkaProperties kafkaProperties = KafkaProperties.getInstance();
        return new DefaultKafkaProducerFactory<>(kafkaProperties.getKafkaConfigDto().propsMap);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public AdminClient adminClient() {
        return AdminClient.create(KafkaProperties.getInstance().getKafkaConfigDto().propsMap);
    }
}
