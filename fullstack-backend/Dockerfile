FROM maven:3.8.1-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/fullstack-backend-0.0.1-SNAPSHOT.jar /app/app.jar

COPY src/main/resources/keys/gcs-key.json /app/keys/gcs-key.json

RUN ls -la /app
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
