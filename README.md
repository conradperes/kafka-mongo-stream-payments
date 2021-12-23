# Getting started with Apache Kafka and Quarkus 

This project demonstrates how to build a Quarkus application using Apache Kafka in less than 10 minutes.
It uses Reactive Messaging to simplify the interaction with Kafka.

## Start the broker

You would need a Kafka broker.
Start one using:

```shell script
docker-compose up -d
```

**NOTE:** Stop the broker using `docker-compose down; docker-compose rm`

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw quarkus:dev
```

## Use the application


```shell script
 curl -d '{"id":"value1", "stepStatus":"value2", \
 "sagaStatus":"STARTED", "version":"1","type":"Validate_payment",\
  "paylosion":"1", "payload":{"balance":"1", "idCard":"1", "pin":"1234"} }' -H \
  "Content-Type: application/json \
" -X POST http://localhost:8099/finalProducer
```

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `coordinator-coreography-atom-1.0.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar target/coordinator-coreography-atom-1.0.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/coordinator-coreography-atom-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.
