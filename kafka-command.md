
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

- The spring application is abled to create a new topic for you with default config (partition 1, replication 0),<br> when a message and topicName send to kafka with `kafkaTemplate`
- Create Topic Programmatically: define bean from NewTopic and set name, partition, and replication for the topic
- For consuming, you should define a group for consumer
    - by annotation: `@KafkaListener(topics = "kafka-spring-topic", groupId = "spring-group")`
    - by properties: `kafka.consumer.group-id=spring-group`
- If you create just one instance for consume data. the consumer will connect to all the partitions of the topic
- Assign partitions to the multi consumer is managed by kafka by default, so it choose which partition connect <br> to which consumer
- For test, we can define multi method instead of multi instance to connect to kafka and check it
- If the number of consumers in the group is more than the number of partitions, some of the consumers are ideal <br> and won't be used until one of the other consumers is corrupted
- If during the process, the consumer is dead, we will have Lag, and we need to republish Lag data to consumer <br> after it is lived
- Serialize & Deserialize
    - if you want to send custome object (DTO), you need to define either serializer at producer and deserializer at consumer side
    - the trusted package path at consumer should be the same path package at producer
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
        # Producer
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
        # Consumer
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
    - producer: if you want to send message to specific parition, you need to mention parition number as <br> a parameter at the `send` method of `kafkaTemplate`. <br> 
    example: send for parition 3 -> `template.send("kafka-topic", 3, null, eventMessageObject)`
    - consumer: you need to define `topicParitions` at `@kafkaListener` <br>
    example: read from parition 3 -> `@kafkaListener(topicPartitions = {@TopicPartition(topic = "kafka-topic", partitions = {"2"})})`
- Retry Strategy:
    - define a config for kafka Retry and set how many times, the server can send the message before <br> send that message directly to `dead letter topic`  - DLT is a topic that store all the failed message
    - it helps us to prevent lost message. (reliable message process)
    - you just need to add `@RetryableTopic` on top of your KafkaListener and also define a method that annotates with `@DltHandler`

- Schema Registery (Avro Schema)
    - to ensure that new data can be consumed by older consumers that were designed to work with the old schema
    - the requirements which we need are an Avro Maven plugin to define object from Avro Schema file, <br> and Avro Serializer and Deserializer for serialize and deserialize
    - the schema registery is going to provide a way to store, retrive and evolve schema in a consistent <br> manner between producer and consumer
    - To check subjects in service registry: http://localhost:8081/subjects
    - To check entity: `http://localhost:8081/subjects/<topic-name>-value/versions/latest`
    - when adding a new field to a schema, you should add a default value for that field in `avsc` file.



# Keyword
- Consumer rebalancing


# Resource
- [Kafka-UI](https://docs.kafka-ui.provectus.io/)
- [Apache Avro](https://avro.apache.org/docs/1.12.0/getting-started-java/)
