# ===== Étape 1 : Build du JAR Spring Boot =====
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ===== Étape 2 : Image finale légère =====
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/eventsProject-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
