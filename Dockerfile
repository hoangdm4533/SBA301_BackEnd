# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/demo-login-0.0.1-SNAPSHOT.jar app.jar

# Expose port for Render
EXPOSE 8080
EXPOSE $PORT

# Set environment variable for Spring Boot to use PORT from Render
ENV SERVER_PORT=$PORT

# Use shell form to allow environment variable substitution
CMD java -jar app.jar --server.port=${PORT:-8080} 