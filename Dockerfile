# Build stage: dependencies are copied separately to maximise Docker layer caching.
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# Runtime stage: contains only the JRE and the application JAR.
FROM eclipse-temurin:17-jre-jammy
RUN groupadd --system spring && useradd --system --gid spring --create-home spring
WORKDIR /app
COPY --from=build /build/target/calculator-devops-1.0.0.jar app.jar
USER spring:spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
