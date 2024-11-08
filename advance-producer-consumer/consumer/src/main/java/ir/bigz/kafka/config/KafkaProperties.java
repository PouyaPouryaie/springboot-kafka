package ir.bigz.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

public class KafkaProperties {

    private final Map<String, Object> props;

    private KafkaProperties(Builder builder) {
        this.props = new HashMap<>(Map.copyOf(builder.props));
    }

    public Map<String, Object> getProps() {
        return props;
    }

    public static class Builder {
        private final Map<String, Object> props = new HashMap<>();

        private Builder(String bootstrapServers) {
            this.props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        }

        public static Builder getInstance(String bootstrapServers) {
            return new Builder(bootstrapServers);
        }

        public Builder defaultConfig(String trustedPackage, String groupId) {
            this.props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            this.props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            this.props.put(JsonDeserializer.TRUSTED_PACKAGES, trustedPackage);
            this.props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            this.props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            this.props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            return this;
        }

        public Builder defaultBatchConfig(String trustedPackage, String groupId, int maxPollRecords) {
            this.props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            this.props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            this.props.put(JsonDeserializer.TRUSTED_PACKAGES, trustedPackage);
            this.props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            this.props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
            this.props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            this.props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            return this;
        }

        public Builder defaultProducerConfig() {
            this.props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            this.props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            return this;
        }

        public <T> Builder withKeySerializer(Class<Serializer<? extends T>> keySerializer) {
            this.props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
            return this;
        }

        public <K> Builder withValueSerializer(Class<Serializer<? extends K>> valueSerializer) {
            this.props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
            return this;
        }

        public <T,K> Builder producerConfig(Class<Serializer<? extends T>> keySerializer,
                                            Class<Serializer<? extends K>> valueSerializer) {
            this.props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
            this.props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
            return this;
        }

        public <T> Builder withKeyDeserializer(Class<? extends Deserializer<T>> keyDeserializer) {
            this.props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
            return this;
        }

        public <K> Builder withValueDeserializer(Class<? extends Deserializer<K>> valueDeserializer) {
            this.props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
            return this;
        }

        public Builder withMaxPoolRecord(int maxPollRecords) {
            this.props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
            return this;
        }

        public  Builder withTrustedPackage(String trustedPackage) {
            this.props.put(JsonDeserializer.TRUSTED_PACKAGES, trustedPackage);
            return this;
        }

        public  Builder withGroupId(String groupId) {
            this.props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            return this;
        }

        public Builder defaultConsumerConfig(String trustedPackage, String groupId) {
            this.props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            this.props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            this.props.put(JsonDeserializer.TRUSTED_PACKAGES, trustedPackage);
            this.props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            return this;
        }

        public <T,K> Builder consumerConfig(Class<? extends Deserializer<T>> keyDeserializer,
                                            Class<? extends Deserializer<K>> valueDeserializer) {
            this.props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
            this.props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
            return this;
        }

        public <K> Builder consumerConfig(Class<? extends Deserializer<K>> valueDeserializer,
                                            String trustedPackage,
                                            String groupId) {
            this.props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            this.props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
            this.props.put(JsonDeserializer.TRUSTED_PACKAGES, trustedPackage);
            this.props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            return this;
        }

        public <K> Builder consumerBatchConfig(Class<? extends Deserializer<K>> valueDeserializer,
                                            String trustedPackage,
                                            String groupId,
                                            int maxPollRecords) {
            this.props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            this.props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
            this.props.put(JsonDeserializer.TRUSTED_PACKAGES, trustedPackage);
            this.props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            this.props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
            return this;
        }

        public <T,K> Builder consumerConfig(Class<? extends Deserializer<T>> keyDeserializer,
                                            Class<? extends Deserializer<K>> valueDeserializer,
                                            String trustedPackage,
                                            String groupId) {
            this.props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
            this.props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
            this.props.put(JsonDeserializer.TRUSTED_PACKAGES, trustedPackage);
            this.props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            return this;
        }


        public <T,K, M, N> Builder Config(Class<Serializer<? extends T>> keySerializer,
                                    Class<Serializer<? extends K>> valueSerializer,
                                    Class<? extends Deserializer<M>> keyDeserializer,
                                    Class<? extends Deserializer<N>> valueDeserializer,
                                    String trustedPackage,
                                    String groupId) {
            this.props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
            this.props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
            this.props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
            this.props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
            this.props.put(JsonDeserializer.TRUSTED_PACKAGES, trustedPackage);
            this.props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            return this;
        }

        public <T,K, M, N> Builder Config(Class<Serializer<? extends T>> keySerializer,
                                          Class<Serializer<? extends K>> valueSerializer,
                                          Class<? extends Deserializer<M>> keyDeserializer,
                                          Class<? extends Deserializer<N>> valueDeserializer,
                                          String trustedPackage,
                                          String groupId,
                                          int maxPollRecords) {
            this.props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
            this.props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
            this.props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
            this.props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
            this.props.put(JsonDeserializer.TRUSTED_PACKAGES, trustedPackage);
            this.props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            this.props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
            return this;
        }

        public KafkaProperties build() {
            return new KafkaProperties(this);
        }
    }
}
