package ir.bigz.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@Service
public class KafkaBatchListener {

    private static final Logger log = LoggerFactory.getLogger(KafkaBatchListener.class);
    private final CountDownLatch latch = new CountDownLatch(20);

    public CountDownLatch getLatch() {
        return latch;
    }

    @KafkaListener(id="batch-listener", topics = "${kafka.topic.batch}", containerFactory = "kafkaBatchListenerContainerFactory")
    public void receiveEvent(List<String> data,
                             @Header(KafkaHeaders.OFFSET) List<Long> offsets,
                             @Header(KafkaHeaders.RECEIVED_PARTITION) List<Long> partitions) {
        log.info("start of batch receive");
        for(int i=0; i<data.size(); i++) {
            log.info("received message='{}' with partition-offset='{}'",
                    data.get(i),
                    partitions.get(i) + "-" + offsets.get(i));
            latch.countDown();
        }

        log.info("end of batch receive");
    }
}
