# Stage 1: Build the Spring Boot application
FROM maven:3.9.6-eclipse-temurin-17 AS build
# Set the working directory inside the container
WORKDIR /app
# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline
# Copy the rest of the source code
COPY . .
# Package the application into a fat JAR
RUN mvn clean install -DskipTests

# Stage 2: Create the final image
# Use a smaller base image (recommended for production)
FROM eclipse-temurin:17-jre-alpine
# Set the working directory
WORKDIR /app
# Expose the default Spring Boot port
EXPOSE 8080
# Copy the built JAR from the build stage (replace the JAR name)
COPY --from=build /app/target/aqisystem-0.0.1-SNAPSHOT.jar aqisystem.jar
# Start the Spring Boot application
ENTRYPOINT ["java", "-jar", "aqisystem.jar"]
