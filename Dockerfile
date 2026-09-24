FROM gradle:8.14.3-jdk21-alpine AS builder
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src
RUN gradle bootJar -x test -Pgradle.properties="jar.enabled=false"

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache docker-cli
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
