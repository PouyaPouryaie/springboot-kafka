package ir.bigz.kafka.service_registry.producer;

import ir.bigz.kafka.service_registry.dto.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducer {

    Logger log = LoggerFactory.getLogger(KafkaProducer.class);

    @Value("${topic.name}")
    private String topicName;
    private final KafkaTemplate<String, Employee> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, Employee> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(Employee employee) {
        CompletableFuture<SendResult<String, Employee>> future = kafkaTemplate.send(topicName, employee.getId().toString(), employee);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Employee sent successfully. Employee:{} and offset:{}", employee, result.getRecordMetadata().offset());
            } else {
                log.error("Employee sent failed, Employee: {}, ex: {}", employee, ex.getMessage());
                throw new RuntimeException("Employee sent failed, Employee: " + employee, ex);
            }
        });
    }

}
