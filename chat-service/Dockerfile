FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /workspace
COPY . .
RUN ./gradlew :chat-service:bootJar --no-daemon -x test

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=builder /workspace/chat-service/build/libs/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
