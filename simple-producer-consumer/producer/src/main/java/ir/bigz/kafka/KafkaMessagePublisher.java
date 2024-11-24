package ir.bigz.kafka;

import ir.bigz.kafka.dto.Customer;
import ir.bigz.kafka.dto.Message;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.common.TopicPartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaMessagePublisher {

    Logger log = LoggerFactory.getLogger(KafkaMessagePublisher.class);

    // to communicate with kafka we need to use kafkaTemplate class
    private final KafkaTemplate<String, Object> template;
    private final AdminClient adminClient;

    public KafkaMessagePublisher(KafkaTemplate<String, Object> template, AdminClient adminClient) {
        this.template = template;
        this.adminClient = adminClient;
    }

    public void sendMessageToTopic(String message) {
        CompletableFuture<SendResult<String, Object>> future = template.send("message-string-topic", message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent Message= {} to topic= {} with offset= {}", message, "message-string-topic",result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send message= {} due to {}", message, ex.getMessage());
            }
        });
    }

    public void sendMessageToSpecificPartition(String message, int partition) {
        if(partitionExists("message-string-topic", partition)) {
            CompletableFuture<SendResult<String, Object>> future = template.send("message-string-topic", partition, null, message);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Sent Message= {} to topic= {} with offset= {} to partition= {}", message, "message-string-topic",result.getRecordMetadata().offset(), partition);
                } else {
                    log.error("Unable to send message= {} due to {}", message, ex.getMessage());
                }
            });
        } else {
            log.error("Unable to send message= {} due to partition= {} doesn't exist", message, partition);
            throw new RuntimeException("Partition doesn't exist");
        }

    }

    public void sendMessageToTopic(Customer customer) {
        var customerMessage = new Message<>(customer);
        CompletableFuture<SendResult<String, Object>> future = template.send("message-customer-topic", customerMessage);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent Message= {} to topic= {} with offset= {}", customerMessage, "message-customer-topic", result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send Message= {} due to {}", customerMessage, ex.getMessage());
            }
        });
    }

    private boolean partitionExists(String topic, int partition) {

        ListTopicsOptions listTopicsOptions = new ListTopicsOptions();
        listTopicsOptions.listInternal(true);
        try {
            List<TopicPartitionInfo> partitions = adminClient.describeTopics(List.of(topic)).allTopicNames().get().get(topic).partitions();
            for (TopicPartitionInfo partitionInfo : partitions) {
                if (partitionInfo.partition() == partition) {
                    return true;
                }
            }
            return false;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
