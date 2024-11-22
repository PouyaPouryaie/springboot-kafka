package ir.bigz.kafka.config;

import java.util.Map;

/*
I created this Dto, because I disabled proxyBeanMethods for dependency Injection.
so I have to create a bean and fill the properties map for kafka and then injected as a parameter to the ProducerFactory bean class
 */

public class KafkaConfigDto {

    private Map<String, Object> propsMap;

    public KafkaConfigDto(Map<String, Object> propsMap) {
        this.propsMap = propsMap;
    }

    public Map<String, Object> getPropsMap() {
        return propsMap;
    }
}
