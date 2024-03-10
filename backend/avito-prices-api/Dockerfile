FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17
WORKDIR /app
COPY target/*.jar .
ENTRYPOINT ["java", "-jar", "avito-prices-api-0.0.1.jar"]