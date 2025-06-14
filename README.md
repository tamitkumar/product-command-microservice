### 🔷 1. System Architecture Diagram
````
+-----------------------------+
|  Product Command Service   |
|  (Spring Boot App)         |
+-------------+---------------+
              |
              v
      +---------------+
      | MySQL DB      | <--- Stores product data
      +---------------+
              |
              v
      +------------------+
      | Kafka Producer   | <--- Publishes ProductEvent
      +------------------+
              |
              v
  Kafka Topic: product-event-topic
````
### Architecture Diagram
````
                     +----------------------+
                     |  Product Command API |
                     |  (Spring Boot App)   |
                     +----------+-----------+
                                |
                                v
                        +---------------+
                        | CommandService|
                        +---------------+
                                |
                                v
                    +-----------------------+
                    |  MySQL DB (product)   |
                    +-----------------------+
                                |
                                v
                        +----------------+
                        | KafkaProducer  |
                        +--------+-------+
                                 |
                                 v
                     Kafka Topic: "product-event-topic"
                                 |
                --------------------------------------
               |                  |                  |
        Consumer App 1     Consumer App 2     Consumer App 3
       (Query Service)     (Audit Logger)     (Notification)

````

### 🔷 2. Component Responsibilities
````
Component                            Responsibility
---------------------------------------------------------------------------------------------------
CommandController                    Exposes HTTP endpoints like /add, /update, /delete

CommandService                       Business logic; saves data to DB and sends event to Kafka

CommandRepository                    Interacts with MySQL using Spring Data JPA

KafkaProducerConfig                  Kafka producer configurations (broker, key/value serializers)

KafkaTemplate                        Sends messages to Kafka topic

ProductEvent                         Java object containing eventType and product details
````

### 🔷 3. Kafka Flow (Step-by-Step)
````
A. API is called (e.g., /add)

B. Controller passes to service

C. Service saves to MySQL DB

D. Service creates ProductEvent object

E. Sends event to Kafka via KafkaTemplate

F. Kafka sends it to product-event-topic

G. Other services (like query service) can consume this
````
### 🔷 4. Class Diagram
````
+---------------------+
|     Product         |  <---- Kafka message payload
+---------------------+
| productCode         |
| name                |
| description         |
| price               |
+---------------------+

+---------------------+
| ProductEntity       |  <---- Database Entity
+---------------------+
| id (Long)           |
| productCode         |
| name                |
| description         |
| price               |
| version             |
+---------------------+

+---------------------+         +------------------------+
| CommandController   |-------> |   CommandService       |
+---------------------+         +------------------------+
                                        |
                                        v
                              +---------------------+
                              | CommandRepository   |
                              +---------------------+

+--------------------------+
| KafkaTemplate<String, Object> |
+--------------------------+
         |
         v
"product-event-topic" (Kafka)

___________________________________________________________________________________________________
---------------------------------------------------------------------------------------------------
+-----------------------------+
|  Product Command Service   |
|  (Spring Boot App)         |
+-------------+---------------+
              |
              v
      +---------------+
      | MySQL DB      | <--- Stores product data
      +---------------+
              |
              v
      +------------------+
      | Kafka Producer   | <--- Publishes ProductEvent
      +------------------+
              |
              v
  Kafka Topic: product-event-topic

````