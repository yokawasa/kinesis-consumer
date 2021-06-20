# Multi-stage build Dockerfile for amazon-kinesis-consumer
# Building Jar stage
FROM maven:3.6.3-adoptopenjdk-11 as builder
RUN apt-get update
WORKDIR /amazon-kinesis-consumer
COPY . .
RUN mvn install -DskipTests

# Setting up execution environment stage
FROM adoptopenjdk/openjdk11:jre-11.0.9_11-debianslim as executor
RUN apt-get update \
  && apt-get install -yq apache2-utils \
  && rm -rf /var/lib/apt/lists/*
WORKDIR /
COPY --from=builder /amazon-kinesis-consumer/target/*.jar /amazon-kinesis-consumer.jar
COPY /entrypoint.sh /entrypoint.sh
CMD ["./entrypoint.sh", "./amazon-kinesis-consumer.jar"]
