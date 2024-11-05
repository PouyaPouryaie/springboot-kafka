package ir.bigz.kafka.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.bigz.kafka.dto.Message;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class CustomJsonDeserializer implements Deserializer<Message> {

    private static final Logger log = LoggerFactory.getLogger(CustomJsonDeserializer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Message deserialize(String topic, byte[] data) {

        try {
            if (data == null) {
                log.info("Null received at deserializing");
                return null;
            }
            return objectMapper.readValue(new String(data, StandardCharsets.UTF_8), Message.class);
        } catch (Exception e) {
            log.error("Error when deserializing data: {}",e.getMessage());
            throw new SerializationException("Error when deserializing byte[] to Message");
        }
    }
}
