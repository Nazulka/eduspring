# ---------- Build stage ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy everything
COPY . .

# Make gradlew executable
RUN chmod +x gradlew

# Build using Gradle Wrapper
RUN ./gradlew clean bootJar

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
