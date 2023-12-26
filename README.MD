# Vicarius Quota assignment

The Quota Management System is a Spring Boot application designed to enhance
the efficiency of our Web API usage by preventing abuse caused by excessive
requests from multiple users. The system implements a robust access-limiting
mechanism that ensures optimal performance and resource utilization.

## Problem Statement

As a Backend Developer, the challenge was to create an access-limiting mechanism
that allows users to make a specified number of API requests per day, while also
accommodating users from two different data sources: Elasticsearch and MySQL.
During the day (9:00 AM to 5:00 PM UTC), user data is sourced from MySQL,
and during the night (5:01 PM to 8:59 AM UTC), user data is sourced from Elasticsearch.

## Features
- Accepts up to a specified number of API requests per user.
- Blocks users who exceed their API request quota and returns 429 HTTP error (Too many request)
- Utilizes two different data sources: Elasticsearch and MySQL, based on the time of day.
- Implements a robust, scalable, and extensible solution.
- Provides API functions for CRUD operations on user data.
- Implements consumeQuota function to count and manage users' quotas.
- Implements getUsersQuota function to return all users and their quota statuses.

## Dependencies

- Java 17
- Spring Boot 3.2.0
- Docker
- Docker Compose
- MySQL 5.7
- Elasticsearch 7.16.1
- Redis
- Lombok

## Running Locally
1. Clone project 
```sh
git clone https://github.com/arturo-velazquez-vargas/vicarius-quota-assignment.git
cd vicarius-quota-assignment
```

2. Start the required services (MySQL, Redis, Elasticsearch) using Docker Compose:
```sh
docker-compose up -d
```

3. Install project dependencies and run unit test:
```sh
mvn clean install
```

4. Run the project:
```bash
 mvn spring-boot:run -Dspring-boot.run.profiles=local
```
5. Test it with Postman:

In the root of the project there is a POSTMAN collection to be imported in Postman to test
the endpoints regarding CRUD, quotaConsume and getUserQuota

## Considerations in Quota Management

## Solution in Quota management
In the implementation of the Quota Management System, the choice of Redis as the primary data store for managing user quotas was made for several compelling reasons:

- Distributed Key-Value Database: Redis is a powerful distributed key-value database known for its exceptional speed in data retrieval. By using Redis, we ensure that quota limits for each user can be accessed swiftly, regardless of whether the user data is stored in Elasticsearch or MySQL.
- High Performance: Redis is designed for high-performance data operations. It excels in scenarios where low-latency access to data is crucial, making it an ideal choice for efficiently managing user quotas and ensuring optimal API performance.
- Scalability: Redis offers seamless scalability, making it well-suited for applications that may experience increased traffic over time. As the system grows, Redis can handle the increasing load without compromising on speed or responsiveness.
- Extensibility: Redis provides support for data structures beyond simple key-value pairs. This extensibility allows us to implement advanced quota management features, such as fine-grained control over quotas and easy integration with other data sources.
- Real-time Quota Updates: Redis's in-memory nature allows for real-time updates to user quotas.  This ensures that users receive accurate and up-to-date quota information, enhancing the overall user experience.
- Redis, with its Time-To-Live (TTL) feature, automates the lifecycle management of user quotas, ensuring that quota limits are automatically reset at defined intervals, simplifying administration and enhancing user experience.

### Max Request Configuration

The maximum number of requests per user is set to 5 but can be changed
by modifying the QuotaService.MAX_REQUESTS constant.

### Quota Expiry

After the first quota consumption, users have a Redis TTL of 5 minutes (configurable for testing).
To adjust the block duration, modify QuotaService.HOUR_T0_EXPIRE, QuotaService.MINUTE_TO_EXPIRE, and QuotaService.SECOND_TO_EXPIRE constants according to business needs.

## Design patterns implemented 

### Strategy

The Strategy design pattern is employed in this project to provide a flexible and 
interchangeable mechanism for fetching user data from different sources. 
Two concrete implementations of the UserFetchingStrategy interface, 
namely MySQLUserFetchingStrategy and ElasticsearchUserFetchingStrategy, 
allow data retrieval from MySQL and Elasticsearch, respectively. This pattern 
enables us to switch between data sources seamlessly based on specific criteria, 
enhancing extensibility and maintainability.

### Composite
The Composite design pattern is utilized in conjunction with the Strategy pattern to 
determine the appropriate data source for user data retrieval based on the time of day. 
The CompositeUserFetchingStrategy class orchestrates the selection between the 
MySQLUserFetchingStrategy and ElasticsearchUserFetchingStrategy as data sources, 
depending on whether it is daytime or nighttime. This pattern enhances scalability 
and allows the system to seamlessly adapt to changing requirements without modifying 
existing code.


## Additional technologies used

### Docker and Docker compose

Docker and Docker Compose simplify containerization, orchestration,
and testing of services like Elasticsearch, Redis, and MySQL.
They provide isolation, portability, scalability, and reproducibility benefits.

### Lombok

Lombok reduces code verbosity and enhances productivity in Java development.
It offers concise code, a builder pattern for object creation, reduced error-prone code,
and increased developer productivity.

## Alternative third party solutions for Quota Management

### Guava Rate Limiter:
Guava, a popular Java library, provides a Rate Limiter component that allows
you to control the rate at which events occur, which can be used to implement request quotas.
It offers a simple and efficient way to limit access to resources based on a predefined rate.

### Spring Security:
Spring Security offers robust mechanisms for access control and rate limiting.
It can be configured to restrict the number of requests a user can make within a specific time frame, making it a viable alternative for quota management.

### Amazon API Gateway:
For cloud-based solutions, Amazon API Gateway offers built-in rate limiting
and throttling features. It allows  to define request quotas,
burst rates, and other access control policies for  APIs without the
need for custom code.