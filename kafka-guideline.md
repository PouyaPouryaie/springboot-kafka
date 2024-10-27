
# Run Kafka by using Docker & docker-compose
1. write a compose file
2. open a terminal at the location where the docker-compose file is created there before
3. run below command at the terminal
```bash
docker compose -f <docker-compose-file> up -d
```
## Access kafa instance on Docker
```bash
docker exec -it kafka # </bin/sh or bash>
```

## UI for Kafka
Provectus Kafka UI
```bash
docker run -it -p 8080:8080 -e DYNAMIC_CONFIG_ENABLED=true provectuslabs/kafka-ui
```
- For a successful connection to Kafka Instance, you need to specify the correct hostname (kafka-instance-name) and port (external port)


# Kafka CLI Commands With Docker

## Create a topic
```bash
docker run -it --rm \
--network <kafka-docker-network> bitnami/kafka:latest \
kafka-topics.sh --bootstrap-server <kafka-instance>:<external-port> --create \
--topic <topic-name> --partitions 3 --replication-factor 1
```
## Create a topic with zookeeper
```bash
- docker exec -it kafka bash
- kafka-topics.sh --create --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1 --topic <topic-name>
```

## List of Topics
```bash
docker run -it --rm --network <kafka-docker-network> \
bitnami/kafka:latest kafka-topics.sh --bootstrap-server <kafka-instance>:<external-port> --list

# example:
docker run -it --rm --network kafka-docker_default \
bitnami/kafka:latest kafka-topics.sh --bootstrap-server kafka-sample:9094 --list
```

## Check Topic Info
you need to use `--describe` command and ask the describer to get information for topic by using `--topic`
```bash
docker run -it --rm --network <kafka-docker-network> \
bitnami/kafka/latest kafka-topics.sh --bootstrap-server <kafka-instance>:<external-port> \
--describe --topic <topic-name>

# example:
docker run -it --rm --network kafka-docker_default \
bitnami/kafka:latest kafka-topics.sh --bootstrap-server kafka-sample:9094 --describe --topic pouya-topic

docker exec -it kafka-sample /opt/bitnami/kafka/bin/kafka-topics.sh --bootstrap-server 127.0.0.1:9092 --describe --topic pouya-topic
```

## Produce & Consume Data with Kafka-console
```bash
# Producer
- kafka-console-producer.sh --broker-list <list-of-broker-ip & port> --topic <topic-name> # use the broker-list option, if you want to produce to the different broker
- kafka-console-producer.sh --bootstrap-server <ip-address>:<port> --topic <topic-name>

# Consumer
kafka-console-consumer.sh --bootstrap-server <ip-address>:<port> --topic <topic-name>

# Example
kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic pouya-topic 
kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic pouya-topic --from-beginning # if you want to listen from beginning
```
- if you want to the producer reads file
```bash
kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic <topic-name> <path-to-file> 

# Example:
kafka-console-producer.sh --broker-list localhost:9092 --topic pouya-topic </Users/data/users.csv
```

# Note
- Access to kafka in the container without create another instance
```bash
docker exec -it <kafka-instance-name> <path-of-kafka-bin>/<kafka-exec-script> \
--bootstrap-server <ip-address>:<port> <commands> <options> <args>

# Example
docker exec -it kafka-sample /opt/bitnami/kafka/bin/kafka-topics.sh \
--bootstrap-server 127.0.0.1:9092 --describe --topic pouya-topic
```

- Version of the Kafka is 3.8.0
- For UI
    - `docker pull provectuslabs/kafka-ui`
    - `docker run -it -p 8080:8080 --network <kafka-docker-network> -e DYNAMIC_CONFIG_ENABLED=true --name kafka-ui provectuslabs/kafka-ui`



# Springboot & Kafka

- The Spring application is capable of dynamically provisioning new Kafka topics with default configurations (partition 1, replication 0),<br> this action happens when a message and topicName send to kafka with `kafkaTemplate`
- Create Topic Programmatically: defining a bean from NewTopic and set name, partition, and replication for the topic
- For consuming, you should define a group for consumer
    - by annotation: `@KafkaListener(topics = "kafka-spring-topic", groupId = "spring-group")`
      - This annotation could be used on top of class or method
      - Class-level annotation is suitable when you want to group related message handling logic together. <br> Messages from these topics will be distributed to the methods within the class based on their parameters
    - by properties: `kafka.consumer.group-id=spring-group`
- If you create just one instance for consuming data. the consumer will connect to all the partitions of that topic
- Assigning partitions to the multi consumer is managed by kafka by default, so it choose which partition connect <br> to which consumer
- If the number of consumers in the group is more than the number of partitions, some of the consumers are going to move to ideal state <br> and won't be used until one of the other consumers is corrupted
- If during the process, the consumer is dead, we will have Lag, and we need to republish Lag data to consumer <br> after it is lived
- Serialize & Deserialize
    - If you want to send Java object (DTO), you need to define both serializer at producer and deserializer at consumer side
      - Your Java Objects should be followed Pojo structure (Getter, Setter, No Args Constructor, All Args Constructor)
    - For handle deserialization errors, you can use `ErrorHandlingDeserializer`, which is provided by spring. <br> it is a wrapper around your custom deserializer 
    - The trusted package path at consumer should be the same path package at producer
    1) Use Properties file
        - Example
        ```yml
        # Producer
        spring: 
            kafka:
                producer:
                    key-serializer: org.apache.kafka.common.serialization.StringSerializer
                    value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
        # Consumer
        spring: 
            kafka:
                consumer:
                    key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
                    value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
                    properties:
                        spring:
                            json:
                                trusted:
                                    packages: ir.bigz.kafka.dto
                
        ``` 
    2) Use Java Config Class
        - Example
        ```java
        // Producer
            @Bean
            public Map<String, Object> producerConfigs() {
                Map<String, Object> props = new HashMap<>();
                props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.21.0.2:9092");
                props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
                props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
                return props;
            }

            @Bean
            public ProducerFactory<String, Object> producerFactory() {
                return new DefaultKafkaProducerFactory<>(producerConfigs());
            }

            @Bean
            public KafkaTemplate<String, Object> kafkaTemplate() {
                return new KafkaTemplate<>(producerFactory());
            }
        // Consumer
            @Bean
            public Map<String, Object> consumerConfigs() {
                Map<String, Object> props = new HashMap<>();
                props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.21.0.2:9092");
                props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
                props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
                props.put(JsonDeserializer.TRUSTED_PACKAGES, "ir.bigz.kafka.dto");
                props.put(ConsumerConfig.GROUP_ID_CONFIG, "spring-group");
                return props;
            }

            @Bean
            public ConsumerFactory<String, Object> consumerFactory() {
                return new DefaultKafkaConsumerFactory<>(consumerConfigs());
            }

            @Bean
            public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Object>> kafkaListenerContainerFactory() {
                ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
                factory.setConsumerFactory(consumerFactory());
                return factory;
            }
        ```
- Message Routing: 
    - Producer: if you want to send message to the specific partition, you need to mention that partition number as <br> a parameter at the `send` method of `kafkaTemplate`. <br> 
    example: send for partition 3 -> `template.send("kafka-topic", 3, null, eventMessageObject)`
    - consumer: you need to define `topicPartitions` at `@kafkaListener` <br>
    example: read from partition 3 -> `@kafkaListener(topicPartitions = {@TopicPartition(topic = "kafka-topic", partitions = {"2"})})`
- Retry Strategy:
    - Establish a retry mechanism for Kafka messages. Define the maximum number of retries before routing undeliverable <br> messages to the `Dead Letter Topic` (DLT is a topic that store all the failed message)
    - It prevents lost message. (reliable message process)
    - You just need to add `@RetryableTopic` on top of your KafkaListener and also define a method that annotates with `@DltHandler`
- Schema Registery (Avro Schema)
    - to ensure that new data can be consumed by older consumers that were designed to work with the old schema
    - the requirements which we need are an Avro Maven plugin to define object from Avro Schema file, <br> and Avro Serializer and Deserializer for serialize and deserialize
    - the schema registery is going to provide a way to store, retrive and evolve schema in a consistent <br> manner between producer and consumer
    - To check subjects in service registry: http://localhost:8081/subjects
    - To check entity: `http://localhost:8081/subjects/<topic-name>-value/versions/latest`
    - when adding a new field to a schema, you should add a default value for that field in `avsc` file.
- RoutingKafkaTemplate
    - using RoutingKafkaTemplate to serialize data based on different topic name
    - you need to define a bean to return `Map<Pattern, ProducerFactory<Object, Object>>` which is contains different type of <br> DefaultKafkaProducerFactory based on Pattern that you are defined like `Pattern.compile("message-.*")`
    - then initialize a new RoutingKafkaTemplate with value of `Map<Pattern, ProducerFactory<Object, Object>>`
    ```Java
        @Bean
    public KafkaConfigDto kafkaConfigDto() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.21.0.3:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new KafkaConfigDto(props);
    }

    @Bean
    public ProducerFactory<Object, Object> defaultProducerFactory(KafkaConfigDto kafkaConfigDto) {
        return new DefaultKafkaProducerFactory<>(kafkaConfigDto.getPropsMap());
    }

    @Bean
    public Map<Pattern, ProducerFactory<Object, Object>> producerFactories(ProducerFactory<Object, Object> defaultProducerFactory) {
        Map<Pattern, ProducerFactory<Object, Object>> factories = new HashMap<>();

        // Create a default ProducerFactory for general usage which is producer string
        factories.put(Pattern.compile(".*-string"), defaultProducerFactory);

        // ProducerFactory with Json serializer
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.21.0.3:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        DefaultKafkaProducerFactory<Object, Object> jsonProducerFactory = new DefaultKafkaProducerFactory<>(props);
        factories.put(Pattern.compile("message-.*"), jsonProducerFactory);

        return factories;
    }

    @Bean
    public RoutingKafkaTemplate routingTemplate(Map<Pattern, ProducerFactory<Object, Object>> producerFactories) {
        return new RoutingKafkaTemplate(producerFactories);
    }
    ```



## Hints
- For test consumer part, you can define multi method instead of multi instance to connect to kafka 

# Keyword
- Consumer rebalancing


# Resource
- [Kafka-UI](https://docs.kafka-ui.provectus.io/)
- [Apache Avro](https://avro.apache.org/docs/1.12.0/getting-started-java/)
