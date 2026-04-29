# Build stage
FROM maven:3.8.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/auth-1.0.0.jar app.jar

# Expose the port from application.properties
EXPOSE 8082

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
