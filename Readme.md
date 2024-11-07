# Spring boot & Kafka

This project showcases how to leverage Apache Kafka with Spring Boot and Java applications. It provides a comprehensive guide on:

- **Deploying Kafka with Docker and Docker Compose**: Streamline development and testing with a readily available Kafka instance.
- **Building Spring Kafka Producers and Consumers**: Facilitate seamless communication between application components.
- **Implementing Error Handling and Retry Strategies**: Ensure message delivery and data integrity by handling potential errors and retry mechanisms.
- **Utilizing Avro and Schema Registry**: Enhance data management with Avro serialization and centralized schema management through Schema Registry.
- **Advance Producer and Consumer**: Customization Producer and Consumer to more flexible for real project

This project empowers developers to gain practical knowledge of Kafka and its integration with Spring Boot Java applications.
Getting Started

To run this project, ensure you have Docker and Docker Compose installed on your system.

### Prerequisites:

- Java 21
- Docker
- Docker Compose

### Instructions:
- There is some guideLines, hints and command that you need to know to how to use kafka and which options provide by Spring team
  The name of document is `kafka-guideline.md`
- Also, There are few Docker composer that prepare to run Kafka and Kafka-UI at your local machine
  - use `kafka-compose.yml` to run kafka on your local machine 
  - use `kafka-service-registry-compose.yml` to test schema registry
  - use `kafka-ui-compose.yml` to run Kafka-ui 
    - For more information -> [UI for Apache Kafka](https://github.com/provectus/kafka-ui)
- For Testing, you can find a simple setup that show how to use `Testcontainer` for test Kafka. you can find it at `simple-producer-consumer` Project

### Exploring the Project

The project demonstrates essential Kafka functionalities through code examples:

**1. Simple Producer-Consumer**: Demonstrating how to send and receive messages from/to Kafka topics with basic configuration <br>
**2. Error Handling**: Implemented within the producer and consumer to manage potential errors and ensure message delivery. also show how to use Blocking and Non Blocking Retry Strategy for Dead Letter Topic (DLT) <br>
**3. Service Registry (Avro)**: Integrated for efficient data serialization and centralized schema management by using Avro and schema registry <br>
**4. Testcontainer**: Demonstrating how to use Testcontainer to test producer and consumer. you can find it under `src/test/java/ir/bigz/kafka` package in Simple Producer-Consumer module <br>
**5. RoutingKafkaTemplate**: Demonstrating how to config Spring Kafka that allows you to dynamically route messages to different topics based on message content. `advance-producer-consumer` project<br>
**6. DelegatingByTopicDeserializer**: Demonstrating how to config Spring Kafka that allows you to dynamically deserialize message based on different topics. `advance-producer-consumer` project<br>
**7. CustomizeDeserializer**: Demonstrating how to define custom deserializer and use it. you can find it under `producer` at `advance-producer-consumer` project.<br>
**8. CustomizeContainerFactory**: Demonstrating how to define a customize container factory and attach to specific `@KafkaListener`. you can find it under `producer` at `advance-producer-consumer` project.<br>

### Contributing

We welcome contributions to this project. Feel free to fork the repository, make changes, and submit pull requests.
