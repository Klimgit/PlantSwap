FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /workspace
COPY . .
RUN ./gradlew :deals-service:bootJar --no-daemon -x test

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=builder /workspace/deals-service/build/libs/*.jar app.jar
EXPOSE 8083 9090
ENTRYPOINT ["java", "-jar", "app.jar"]
