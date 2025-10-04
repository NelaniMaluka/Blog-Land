# ===== Build Stage: Maven + OpenJDK =====
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# First copy pom.xml for caching dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Now copy the source
COPY src ./src

# Package the application (skip tests since they already ran)
RUN mvn package -DskipTests


# ===== Runtime Stage: Lightweight JRE =====
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Add non-root user
RUN addgroup --system app && adduser --system --ingroup app app
USER app

# Copy the jar from build stage
COPY --from=build /app/target/blog-land-backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Basic healthcheck (ping JVM process since no Actuator yet)
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
  CMD ps -o comm= 1 | grep java || exit 1

ENTRYPOINT ["java","-jar","/app/app.jar"]
