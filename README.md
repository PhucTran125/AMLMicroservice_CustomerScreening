# Customer Screening Service - Kafka Setup

This guide will help you set up and run a Kafka broker using Docker for local development.

## Prerequisites

- Docker installed on your machine
- Docker Compose installed on your machine
- Port 9092 and 2181 available on your localhost

## Quick Setup

### Step 1: Create Docker Compose Configuration

Create a `docker-compose.yml` file in your project root:

```yaml
version: '3.8'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.4.0
    hostname: zookeeper
    container_name: zookeeper
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000

  kafka:
    image: confluentinc/cp-kafka:7.4.0
    hostname: kafka
    container_name: kafka
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
      - "9101:9101"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181'
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:29092,PLAINTEXT_HOST://0.0.0.0:9092
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
```

### Step 2: Start Kafka Services

Run the following command in your project root directory:

```bash
docker-compose up -d
```

This will start both Zookeeper and Kafka in detached mode.

### Step 3: Verify Services Are Running

Check if the containers are running:

```bash
docker-compose ps
```

You should see both `zookeeper` and `kafka` containers in the "Up" state.

### Step 4: Create Topics (Optional)

Create the topics your application needs:

```bash
# Create customer-screening-requests topic
docker exec -it kafka kafka-topics --create --topic customer-screening-requests --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

# Create screening-alerts topic
docker exec -it kafka kafka-topics --create --topic screening-alerts --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

# List all topics to verify
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092
```

## Testing Your Setup

### Test Producer

Send a test message to a topic:

```bash
docker exec -it kafka kafka-console-producer --topic customer-screening-requests --bootstrap-server localhost:9092
```

Type a message and press Enter. Press Ctrl+C to exit.

### Test Consumer

In another terminal, consume messages from the topic:

```bash
docker exec -it kafka kafka-console-consumer --topic customer-screening-requests --from-beginning --bootstrap-server localhost:9092
```

You should see the message you sent from the producer.

## Application Configuration

Update your `src/main/resources/application.properties` to connect to the local Kafka:

```properties
# Kafka Configuration
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=customer-screening-group
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer
```

## Running Your Spring Boot Application

1. Start Kafka services:

   ```bash
   docker-compose up -d
   ```

2. Run your Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```

## Useful Commands

### Stop Kafka Services

```bash
docker-compose down
```

### View Kafka Logs

```bash
docker-compose logs kafka
docker-compose logs zookeeper
```

### Access Kafka Container

```bash
docker exec -it kafka bash
```

### List All Topics

```bash
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092
```

### Delete a Topic

```bash
docker exec -it kafka kafka-topics --delete --topic <topic-name> --bootstrap-server localhost:9092
```

### Clear All Messages from Topics

#### Method 1: Delete and Recreate Topic (Recommended)

```bash
# Delete the topic
docker exec -it kafka kafka-topics --delete --topic customer-screening-requests --bootstrap-server localhost:9092

# Recreate the topic
docker exec -it kafka kafka-topics --create --topic customer-screening-requests --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

#### Method 2: Clear All Topics at Once

```bash
# List all topics first to see what will be deleted
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092

# Delete all user topics (system topics starting with __ will remain)
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092 | grep -v "^__" | xargs -I {} docker exec kafka kafka-topics --delete --topic {} --bootstrap-server localhost:9092
```

#### Method 3: Temporary Retention Policy Change

```bash
# Set retention to 1 second to clear old messages
docker exec -it kafka kafka-configs --bootstrap-server localhost:9092 --entity-type topics --entity-name customer-screening-requests --alter --add-config retention.ms=1000

# Wait a few seconds, then reset retention to normal (7 days)
docker exec -it kafka kafka-configs --bootstrap-server localhost:9092 --entity-type topics --entity-name customer-screening-requests --alter --add-config retention.ms=604800000
```

#### Method 4: Nuclear Option - Reset Entire Kafka

```bash
# Stop Kafka and remove all data volumes
docker-compose down -v
docker-compose up -d
```

### View Consumer Groups

```bash
docker exec -it kafka kafka-consumer-groups --bootstrap-server localhost:9092 --list
```

## Working with Consumer Groups

Consumer groups in Kafka are automatically created when a consumer starts consuming with a specific group ID. Here's how to work with them:

### Create a Consumer Group

Consumer groups are created automatically when you start a consumer with a group ID:

```bash
# Create a consumer group by starting a consumer with --group parameter
docker exec -it kafka kafka-console-consumer --topic customer-screening-requests --bootstrap-server localhost:9092 --group customer-screening-group --from-beginning
```

### List All Consumer Groups

```bash
docker exec -it kafka kafka-consumer-groups --bootstrap-server localhost:9092 --list
```

### Describe a Consumer Group

```bash
# Get detailed information about a specific consumer group
docker exec -it kafka kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group customer-screening-group
```

### Check Consumer Group Lag

```bash
# Monitor how far behind consumers are
docker exec -it kafka kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group customer-screening-group --verbose
```

### Reset Consumer Group Offsets

```bash
# Reset to earliest offsets (start from beginning)
docker exec -it kafka kafka-consumer-groups --bootstrap-server localhost:9092 --group customer-screening-group --reset-offsets --to-earliest --topic customer-screening-requests --execute

# Reset to latest offsets (skip to current)
docker exec -it kafka kafka-consumer-groups --bootstrap-server localhost:9092 --group customer-screening-group --reset-offsets --to-latest --topic customer-screening-requests --execute

# Reset to specific offset
docker exec -it kafka kafka-consumer-groups --bootstrap-server localhost:9092 --group customer-screening-group --reset-offsets --to-offset 100 --topic customer-screening-requests --execute
```

### Delete a Consumer Group

```bash
# Delete an inactive consumer group
docker exec -it kafka kafka-consumer-groups --bootstrap-server localhost:9092 --delete --group customer-screening-group
```

### Testing Multiple Consumers in Same Group

Start multiple consumers in the same group to see load balancing:

```bash
# Terminal 1 - Consumer 1
docker exec -it kafka kafka-console-consumer --topic customer-screening-requests --bootstrap-server localhost:9092 --group test-group

# Terminal 2 - Consumer 2
docker exec -it kafka kafka-console-consumer --topic customer-screening-requests --bootstrap-server localhost:9092 --group test-group

# Terminal 3 - Producer (send messages)
docker exec -it kafka kafka-console-producer --topic customer-screening-requests --bootstrap-server localhost:9092
```

Messages will be distributed between the two consumers in the same group.

### Consumer Group Best Practices

1. **Group ID Naming**: Use descriptive names like `customer-screening-service`, `alert-processors`
2. **Partition Strategy**: Number of consumers in a group should not exceed number of partitions
3. **Offset Management**: Kafka automatically manages offsets for consumer groups
4. **Rebalancing**: When consumers join/leave, Kafka automatically rebalances partition assignments

## Troubleshooting

### Port Already in Use

If ports 9092 or 2181 are already in use, you can:

1. Stop the conflicting service
2. Change the ports in docker-compose.yml
3. Update your application.properties accordingly

### Kafka Connection Issues

- Ensure Docker containers are running: `docker-compose ps`
- Check Kafka logs: `docker-compose logs kafka`
- Verify port accessibility: `telnet localhost 9092`

### Clean Start

To completely reset Kafka data:

```bash
docker-compose down -v
docker-compose up -d
```

## Next Steps

1. Start the Kafka services using the commands above
2. Create the necessary topics for your customer screening service
3. Run your Spring Boot application
4. Test the message flow using the console producer/consumer or your application endpoints

Your Kafka broker will be accessible at `localhost:9092` for your Spring Boot application to connect to.
