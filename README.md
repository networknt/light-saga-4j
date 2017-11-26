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

## Project module:

--saga-core: Base components and interface to saga framework; Define saga basic command, event and message;

--saga-framework: Saga framework implementation. Define saga participant, orchestration, and message repository;

--saga-dsl: Base saga definition which includes saga steps definition, saga execution state, etc...

--order-service: End-to-end integration test module for saga framework.

--saga-cdc-mysql-connector: Base components for mysql to kafka binlog data transfer.

--saga-cdc-service-mysql: cdc (capture data change) service for mysql to kafka

