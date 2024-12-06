# Use an official Maven image to build the Spring Boot app
FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the app
COPY src ./src
RUN mvn clean package -DskipTests

# Use an OpenJDK image to run the application
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the built JAR from the Maven image
COPY --from=build /app/target/devops-accommodation-0.0.1-SNAPSHOT.jar /app/devops-accommodation.jar

# Expose the port Spring Boot will run on
EXPOSE 8082

# Command to run the JAR
ENTRYPOINT ["java", "-jar", "/app/devops-accommodation.jar"]
