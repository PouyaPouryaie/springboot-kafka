
# Run Kafka by using Docker & docker-compose

## Using Docker Command
```bash
docker run -d --name kafka-sample --hostname kafka-sample \
    --network kafka-network \
    -e KAFKA_ENABLE_KRAFT=yes \
    -e KAFKA_CFG_PROCESS_ROLES=controller,broker \
    -e KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093,EXTERNAL://:9094 \
    -e KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT,EXTERNAL:PLAINTEXT \
    -e KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://127.0.0.1:9092,EXTERNAL://kafka-sample:9094 \
    -e KAFKA_CFG_NODE_ID=0 \
    -e KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=0@127.0.0.1:9093 \
    -e KAFKA_AUTO_CREATE_TOPICS_ENABLE=true \
    -e KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER \
    -e ALLOW_PLAINTEXT_LISTENER=yes \
    -p 9092:9092 \
    -p 9094:9094 \
    bitnami/kafka:3.8.1
```
## Using Docker Compose
1. To run Kafka you could use `Kafka-compose.yml` file.
3. create the `docker-volume` folder as a volume at the location where `Kafka-compose.yml` exists.
4. open a terminal at the location where the `Kafka-compose.yml` file has existed there
5. run below command at the terminal
```bash
docker compose -f Kafka-compose.yml up -d
```
## Access kafa instance on Docker
To access the kafka instance on Docker, you should use the following command:
`docker exec -it <kafka-instance-name> </bin/sh or bash>`
```bash
# Example
docker exec -it kafka-sample bash
```

## UI for Kafka
Kafka-UI, a user interface tool developed by Provectus Labs, is used in this project to facilitate user interaction with Kafka. <br>

### Step 1: Run the Kafka-ui
### First Approach - use compose file :
    1. To run Kafka you could use `Kafka-ui-compose.yml` file.
    2. create the `kafka-ui-volume` folder as a volume att the location where `Kafka-ui-compose.yml` is exist 
    3. open a terminal at the location where the `Kafka-ui-compose.yml` file is exist there
    4. run below command at the terminal

```bash
docker compose -f Kafka-ui-compose.yml up -d
```

### Second Approach - use command :

```bash
docker run -it -p 8080:8080 --network <kafka-docker-network> -e DYNAMIC_CONFIG_ENABLED=true --name kafka-ui provectuslabs/kafka-ui:v0.7.2
```

- __Attention: The network between kafka and kafka-ui should be same for rest of project__

### Second Step:
Connect kafka-ui to kafka instance.
1. Open a browser and navigate to `http://localhost:8080`
2. At the dashboad panel click configure new cluster
3. Enter the Kafka instance name (`kafka-sample`) and port number (`9094`) in the Bootstrap Servers field


# Kafka CLI Commands With Docker

## Create a topic
```bash
docker run -it --rm \
--network <kafka-docker-network> bitnami/kafka:3.8.1 \
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
docker run -it --rm --network kafka-network \
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

## Check Kafka Version
```bash
docker exec -it kafka-sample /opt/bitnami/kafka/bin/kafka-broker-api-versions.sh --version
```

## Check kafka Cluster
```bash
kafka-metadata-quorum.sh --bootstrap-controller <localhost>:<controller-IP> describe --status

# example: 
# The controller listener config is: CONTROLLER://:9093 at kafka-cluster-compose file, so the command should be write like below:
kafka-metadata-quorum.sh --bootstrap-controller http://127.0.0.1:9093 describe --status
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
- To specify the key separator character, the flags to enable printing for both the key and the value, as well as the key and value deserializer
```bash
kafka-console-consumer.sh --bootstrap-server localhost:9092 \
--topic <topic-name> \
--group <group-id> \
--property print.key=true \
--property print.value=true \
--property key.separator=":" \
--property print.timestamp=true \
--value-deserializer org.apache.kafka.common.serialization.StringDeserializer \
--key-deserializer org.apache.kafka.common.serialization.StringDeserializer
```
- If you want to reset consumer group offset to the latest
```bash
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group <cunsomer-group> --reset-offsets --to-latest --all-topics --execute
```

## Kafka configs
- To update a Kafka topic's configuration (e.g., retention settings, number of partitions), use the `kafka-configs.sh` tool.
```bash
kafka-configs.sh --alter --entity-type topics --entity-name <topic-name> 
--add-config 'cleanup.policy=compact' --bootstrap-server <broker_host>:<broker_port>
```
## Kafka logs
- For reading logs and debugging a specific Kafka topic, use the `kafka-dump-log.sh` tool to inspect the contents of its log segments
```bash
kafka-dump-log.sh --files /bitnami/kafka/data/<topic-name>-<partition-number>/<file-name>.log

# Example
kafka-dump-log.sh --files /bitnami/kafka/data/part-three-2/00000000000000000000.log
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

- Kafka
    - version: 3.8.1
- Kafka-UI
    - provectuslabs/Kafka-ui version v0.7.2
- The network between kafka and kafka-ui should be same




# Springboot & Kafka

- The Spring application is capable of dynamically provisioning new Kafka topics with default configurations (partition 1, replication 0),<br> this action happens when a message and topicName send to kafka with `kafkaTemplate`
- Create Topic Programmatically: defining a bean from NewTopic and set name, partition, and replication for the topic
- **KafkaAdmin**: It is a class in the Spring Kafka library that provides a convenient way to perform administrative operations on <br> a Kafka cluster. It simplifies tasks like **creating, deleting, and configuring** topics
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
    - Establish a retry strategy for Producer when encountering an error during sending the message to Kafka broker <br>
        - you just need to config below properties in Producer
            - **retries:** setting determines how many times the producer will attempt to send a message before marking it as failed
            - **delivery.timeout.ms:** Records will be failed if they can’t be delivered in `delivery.timeout.ms`
            - **retry.backoff.ms:** the producer will wait `100ms` between retries, but you can control this using the `retry.backoff.ms` parameter
            - **max.in.flight.requests.per.connection:** This setting basically controls how many requests can be made in parallel to any partition
                - if we enable idempotence `enable.idempotence=true`, then it is required for `max.in.flight.requests.per.connection` to be less than or equal to 5
            - **enable.idempotence**: Producer idempotence ensures that duplicates are not introduced due to unexpected retries.
            - **ack**: Kafka producers must also specify a level of acknowledgment `acks` to specify if the message must be written to a minimum number of replicas before being considered a successful write.
    - Establish a retry mechanism for Kafka messages at **Consumer side**. Define the maximum number of retries before routing undeliverable <br> messages to the `Dead Letter Topic` (DLT is a topic that store all the failed message)
    - It prevents lost message. (reliable message process)
    - You just need to add `@RetryableTopic` on top of your KafkaListener and also define a method that annotates with `@DltHandler`
        - `@RetryableTopic` is non-blocking approach and can improve performance
        - but there are some disadvantage like change order of a message or risk of message duplication
        - `retryTopicSuffix`: You can give the suffix yourself when naming the retry topic
        - `TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE` -> If this value is given, 1 retry topics will be created as {topic_name}-retry-0
        - `dltTopicSuffix` -> you can give suffix to your dlt topic. if you give the value as “-dead-t”, it will be created as {topic_name}-dead-t.
        - There are Three strategy for DLT
            - `FAIL_ON_ERROR`: strategy when the DLT consumer won’t try to reprocess an event in case of failure
            - `ALWAYS_RETRY_ON_ERROR`: strategy ensures that the DLT consumer tries to reprocess the event in case of failure
            - `No_DLT`: strategy, which turns off the DLT mechanism altogether
        ```Java
        // Config dlt strategy at Retryable
        @RetryableTopic(
                attempts = "1",
                dltStrategy = DltStrategy.FAIL_ON_ERROR,
                topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
                retryTopicSuffix = "-custom-try",
                dltTopicSuffix = "-dead-t")
        @KafkaListener(topics = "topic-name", groupId = "kafka-error-group")
        public void consumeEvents(Payload data){
            // put logic here
        } 
        ```
    - **Custom Error Handler**  
      - Another Approach is to define a custom error handler, `DefaultErrorHandler` and inject it into The `ConcurrentKafkaListenerContainerFactory` as a global error handler.
      - `seekAfterError` property: allows you to control whether the consumer seeks back to the offset of the failed record or retains unprocessed records for resubmission after an error, providing flexibility in handling message redelivery scenarios
        - By default, when an error happens during message processing, the `DefaultErrorHandler` seeks the consumer back to the offset of the failed record, causing it and any subsequent unprocessed records to be redelivered.
          - This behavior ensures that messages are reprocessed in the correct order.
        - If you use `setSeekAfterError(false)`, then you must use `AckMode.MANUAL_IMMEDIATE`.
          - Manually call `ack.acknowledge();` only after successful processing to ensure the offset is committed.
      - `setCommitRecovered(true)`: allows the successful offset commit of records that were previously retried and recovered. This ensures that the consumer does not reprocess messa.ges that have already been handled by the error handler
        - the offset is committed for recovered (skipped or sent to DLT) records, preventing reprocessing
        - Recommended when using Dead Letter Topics (DLT).
      - Use Cases for `DefaultErrorHandler`:
        - Situations where you want to retry message processing a few times and then ignore the message if it continues to fail.
        - Scenarios where the order of messages is not critical.
        - When you want to prevent indefinitely retrying a message that is fundamentally unprocessable.
- Schema Registry (Avro Schema)
    - to ensure that new data can be consumed by older consumers that were designed to work with the old schema
    - the requirements which we need are an Avro Maven plugin to define object from Avro Schema file, <br> and Avro Serializer and Deserializer for serialize and deserialize
    - the schema registry is going to provide a way to store, retrieve and evolve schema in a consistent <br> manner between producer and consumer
    - To check subjects in service registry: http://localhost:8081/subjects
    - To check entity: `http://localhost:8081/subjects/<topic-name>-value/versions/latest`
    - when adding a new field to a schema, for `Backward compatibility` you should add a default value for that field in `avsc` file.
    - each time after updating `avsc` file, the project needs to compile again to reflects changes on the entity
    - Configs:
    - - `SCHEMA_REGISTRY_URL_CONFIG`: The URL for connecting to Schema Registry
      - `SPECIFIC_AVRO_READER_CONFIG`: It implies that the Avro consumer (e.g. Kafka consumer) is configured to deserialize messages into specific Avro classes generated from an Avro schema
      - `AUTO_REGISTER_SCHEMAS`: To determines whether the schema registry should automatically register new schemas when it encounters them (__should be false at Production mode__)
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
- Ack Mode at Consumer
    - This ensures the message is consumed, and won’t be delivered to the current listener again.
    - To implement a **Retry logic** for message processing in Kafka, we need to select an **AckMode**.
        - We shouldn’t set the ack mode to `AckMode.BATCH` or `AckMode.TIME` for Retry logic,  because the consumer won’t redeliver all messages in the batch or time window to itself if an error occurs while processing a message
    - Types:
        1. `Auto-commit`, 2. `After-processing`, 3. `Manual`
    - Several ack modes available that we can configure:

        1. `AckMode.RECORD`: In this after-processing mode, the consumer sends an acknowledgment for each message it processes, whether it was successful or failed.
        2. `AckMode.BATCH`: In this manual mode, the consumer sends an acknowledgment for a batch of messages, rather than for each message.
        3. `AckMode.COUNT`: In this manual mode, the consumer sends an acknowledgment after it has processed a specific number of messages.
        4. `AckMode.MANUA`L: In this manual mode, the consumer doesn’t send an acknowledgment for the messages it processes.
        5. `AckMode.TIME`: In this manual mode, the consumer sends an acknowledgment after a certain amount of time has passed.
        6. `AckMode.MANUAL_IMMEDIATE`: Like MANUAL, but commits immediately instead of waiting for the listener to finish.
    ```Java
        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, Object> greetingKafkaListenerContainerFactory() {
            ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
            // Other configurations
            factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
            factory.afterPropertiesSet();
            return factory;
        }
    ```
- Batch Listener
    - a batch listener is a mechanism to consume and process multiple messages from Kafka in a single batch. This can significantly improve performance and reduce overhead, especially when dealing with high-throughput scenarios
    - To use Batch listener you should enable it at container Factory Object and also set the Batch Size at consumer Factory Object
    - Batch Size: Defines the maximum number of messages to be consumed in a single batch
    ```java
        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, Object> greetingKafkaListenerContainerFactory() {
            props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10); // batch size
            DefaultKafkaConsumerFactory<Object, Object> kafkaConsumerFactory =
                new DefaultKafkaConsumerFactory<>(props);
            ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
            // Other configurations
            factory.setBatchListener(true);
            factory.setConsumerFactory(kafkaConsumerFactory);
            return factory;
        }
    ```

- Idempotent
    - Producer side:
        - `enable.idempotence=true`
        - set `transactional.id` to ensures that messages are sent atomically and deduplicated by the broker
        - defining `KafkaTransactionManager` to manages transaction boundaries for the producer.
        - use `@Transactional` or use `executeInTransaction` of KafkaTemplate to produce message into a single transaction
    - Consumer side:
        - set `isolation.level=read_committed`
        - set `enable.auto.commit=false` to prevent auto commit (This can lead to a situation where offsets are committed before the message is fully processed, resulting in potential duplicate processing during retries)
        - set `AckMode` to `AckMode.MANUAL_IMMEDIATE` to define type of commit offsets for kafka
        - use Manual Acknowledgment (e.g. `acknowledgment.acknowledge()`) in service to commit offset after successful processing




## Hints
- For test consumer part, you can define multi method instead of multi instance to connect to kafka
- The method annotated with the @DltHandler annotation must be placed in the same class as the @KafkaListener annotated method

# Keyword
- Consumer re-balancing


# Resource
- [Kafka-UI](https://docs.kafka-ui.provectus.io/)
- [Apache Avro](https://avro.apache.org/docs/1.12.0/getting-started-java/)
