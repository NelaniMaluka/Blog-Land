# Use Maven + OpenJDK for build
FROM maven:3.9.2-eclipse-temurin-17 AS build

WORKDIR /app

# Copy source code
COPY pom.xml .
COPY src ./src

# Build the jar (skip tests)
RUN mvn clean package -DskipTests

# Use slim JDK for running
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/blog-land-backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
