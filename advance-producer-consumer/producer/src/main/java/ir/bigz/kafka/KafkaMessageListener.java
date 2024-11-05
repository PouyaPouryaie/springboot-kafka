package ir.bigz.kafka;

import ir.bigz.kafka.dto.Message;
import ir.bigz.kafka.dto.Purchase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageListener {

    Logger log = LoggerFactory.getLogger(KafkaMessageListener.class);

    @KafkaListener(topics = "purchase-topic",
            containerFactory = "kafkaCustomListenerContainerFactory")
    public void consumeMessage(@Payload Message<Purchase> message) {
        log.info("Purchase consume the message: {}", message);
    }


}
