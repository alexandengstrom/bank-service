# Bank Service

This is a school project where I collaborated with a classmate to build an API for managing bank accounts and transactions using Java and Maven. The service interacts with a `bank-api` and a `person-api`, built in **PHP** and **Rust**. We chose PHP and Rust simply because we had never used these languages before and saw this as a good learning opportunity. The Rust service uses a PostgreSQL database, while the PHP service stores data in a JSON file. Kafka is used for logging and everything runs in Docker.

## Setup
To start the whole project, run the following command in the root directory:
```bash
docker-compose up
```

If it is the first time running the project, it must first be built with:
```bash
docker-compose build
```

## Kafka
To start consuming all topics, run the following command:
```bash
./kafka_consumer.py
```

To start consuming a specific topic, run:
```bash
.kafka_consumer.py -t [TOPIC]
```
