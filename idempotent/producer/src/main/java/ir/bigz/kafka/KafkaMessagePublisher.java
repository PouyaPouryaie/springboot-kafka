package ir.bigz.kafka;

import ir.bigz.kafka.config.KafkaProperties;
import ir.bigz.kafka.dto.Customer;
import ir.bigz.kafka.dto.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaMessagePublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaMessagePublisher.class);
    private final KafkaProperties kafkaProperties;

    // to communicate with kafka we need to use kafkaTemplate class
    private final KafkaTemplate<String, Object> template;

    public KafkaMessagePublisher(KafkaProperties kafkaProperties, KafkaTemplate<String, Object> template) {
        this.kafkaProperties = kafkaProperties;
        this.template = template;
    }

//    using transaction annotation
//    @Transactional
//    public void sendMessageToTopic(Customer customer) {
//        var customerMessage = new Message<>(customer);
//        CompletableFuture<SendResult<String, Object>> future = template.send(kafkaProperties.getTopicName(), customerMessage);
//        future.whenComplete((result, ex) -> {
//            if (ex == null) {
//                log.info("Sent Message= {} to topic= {} with offset= {}", customerMessage, kafkaProperties.getTopicName(), result.getRecordMetadata().offset());
//            } else {
//                log.error("Unable to send Message= {} due to {}", customerMessage, ex.getMessage());
//            }
//        });
//    }

    // wrap operations in a transaction
    public void sendMessageToTopic(Customer customer) {
        var customerMessage = new Message<>(customer);
        template.executeInTransaction(operations -> {
            CompletableFuture<SendResult<String, Object>> future = operations.send(kafkaProperties.getTopicName(),
                    "" + customer.id(), customerMessage);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Sent Message= {} to topic= {} with offset= {}", customerMessage, kafkaProperties.getTopicName(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Unable to send Message= {} due to {}", customerMessage, ex.getMessage());
                }
            });
            return true;
        });
    }
}
