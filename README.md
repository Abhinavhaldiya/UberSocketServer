# UberSocketServer

UberSocketServer is a robust, real-time messaging server that leverages **WebSockets** for seamless client-server communication and **Apache Kafka** for scalable message brokering and distributed event streaming.

## Features
* **Real-time Communication:** Low-latency bidirectional messaging using WebSockets.
* **Scalable Event Streaming:** Integration with Apache Kafka to handle high-throughput message queuing and broadcasting.
* **Easily Configurable:** Environment-based configuration for easy deployment across different environments.

## Prerequisites
* Java (adjust according to your primary tech stack)
* An active Apache Kafka cluster (local or remote)
* Docker & Docker Compose (optional, for running Kafka locally)

---

## Configuration

The server's behavior is controlled via environment variables. Create a `.env` file in the root directory of the project and define the following settings.

### 🔌 WebSocket Configuration

These settings control how the WebSocket server accepts and manages client connections:

| Environment Variable | Description | Default Value |
| :--- | :--- | :--- |
| `WS_PORT` | The port on which the WebSocket server will listen. | `8080` |
| `WS_PATH` | The specific endpoint path for client connections. | `/ws` |
| `WS_MAX_PAYLOAD` | The maximum allowed message payload size in bytes. | `1048576` (1MB) |
| `WS_PING_INTERVAL` | Time in milliseconds between server ping messages to keep connections alive. | `30000` |

### 🚀 Kafka Configuration

These settings dictate how the server connects to and interacts with your Kafka brokers:

| Environment Variable | Description | Default Value |
| :--- | :--- | :--- |
| `KAFKA_BROKERS` | Comma-separated list of Kafka broker URIs. | `localhost:9092` |
| `KAFKA_CLIENT_ID` | A unique identifier for this Kafka client instance. | `uber-socket-server` |
| `KAFKA_GROUP_ID` | The consumer group ID for load balancing and scaling. | `uber-socket-group` |
| `KAFKA_PRODUCE_TOPIC`| The topic where incoming WebSocket messages are published. | `inbound-events` |
| `KAFKA_CONSUME_TOPIC`| The topic the server listens to for broadcasting to clients. | `outbound-events` |
