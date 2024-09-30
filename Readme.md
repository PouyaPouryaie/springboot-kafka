# Spring boot & Kafka

This project showcases how to leverage Apache Kafka with Spring Boot and Java applications. It provides a comprehensive guide on:

- **Deploying Kafka with Docker and Docker Compose**: Streamline development and testing with a readily available Kafka instance.
- **Building Spring Kafka Producers and Consumers**: Facilitate seamless communication between application components.
- **Implementing Error Handling and Retry Strategies**: Ensure message delivery and data integrity by handling potential errors and retry mechanisms.
- **Utilizing Avro and Schema Registry**: Enhance data management with Avro serialization and centralized schema management through Schema Registry.

This project empowers developers to gain practical knowledge of Kafka and its integration with Spring Boot Java applications.
Getting Started

To run this project, ensure you have Docker and Docker Compose installed on your system.

### Prerequisites:

- Java 21
- Docker
- Docker Compose

### Instructions:
- find command in `kafka-command.md` file
- use `kafka-service-registry-compose.yml` to test schema registry
- also there are compose file related to run kafka and kafka-ui

### Exploring the Project

The project demonstrates essential Kafka functionalities through code examples:

**producer**: demonstrating how to send messages to Kafka topics.
**consumer**: demonstrating how to receive and process messages from Kafka topics.
**error-handling**: Implemented within the producer and consumer to manage potential errors and ensure message delivery. also show how to use Retry Strategy 
**serivce-registry**: Integrated for efficient data serialization and centralized schema management by using Avro and schema registry
**testcontainer**: demonstrating how to use testcontainer to test producer and consumer. you can find it under `src/test/java/ir/bigz/kafka` package in consumer and producer module

### Contributing

We welcome contributions to this project. Feel free to fork the repository, make changes, and submit pull requests.
