package ir.bigz.kafka.service_registry.consumer;

import ir.bigz.kafka.service_registry.dto.Employee;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "${topic.name}")
    public void listen(ConsumerRecord<String, Employee> record) {
        String key = record.key();
        Employee employee = record.value();
        log.info("Avro message received for key : {}, value : {}", key, employee.toString());
    }
}
