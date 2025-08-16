# ===== Build Stage: Maven + OpenJDK =====
FROM maven:3.9.2-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Run tests first (will fail build if tests fail)
RUN mvn clean verify

# Package the application (skip tests since they already ran)
RUN mvn package -DskipTests

# ===== Runtime Stage: Slim JDK =====
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the jar from build stage
COPY --from=build /app/target/blog-land-backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
