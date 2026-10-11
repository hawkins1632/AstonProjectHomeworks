FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY common ./common
COPY discovery-server ./discovery-server
COPY user-service ./user-service
COPY notification-service ./notification-service
COPY gateway-service ./gateway-service
COPY config-server ./config-server

RUN mvn -B -pl config-server -am package -DskipTests

FROM eclipse-temurin:17-jre

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=build /app/config-server/target/config-server-1.0-SNAPSHOT.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]
