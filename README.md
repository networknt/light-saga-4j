# light-saga-4j

A saga implementation to manage distributed transaction across multiple microservices

## Introduction

One big challenge with using the microservice architecture is that developers must deal with the additional complexity of creating a distributed system.
Developers must use an inter-process communication mechanism. Implementing use cases that span multiple services requires the use of unfamiliar techniques.

Saga is a type of Compensating Transaction pattern, which provides a simple way to help users solve the data consistency problems encountered in micro-service applications.




## Prerequisites

You will need:

1. [JDK 1.8+][jdk]
2. [Maven 3.x][maven]
3. [Kafka 0.11+][kafka]
4. [Docker][docker]
5. [MySQL][mysql]
6. [Docker compose(optional)][docker_compose]
