FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /workspace
COPY . .
RUN ./gradlew :gateway:bootJar --no-daemon -x test

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=builder /workspace/gateway/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
