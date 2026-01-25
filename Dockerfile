# ---------- Build stage (Gradle + Java 21 already installed) ----------
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew --no-daemon clean bootJar -Dorg.gradle.java.installations.auto-download=false

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
