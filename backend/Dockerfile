#compatível com arm e amd
FROM eclipse-temurin:21-jdk-jammy
COPY . .
RUN ./mvnw install -DskipTests
ENTRYPOINT["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]
