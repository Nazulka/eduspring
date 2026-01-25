# ---------- Build stage ----------
FROM gradle:8.5-jdk21 AS build
WORKDIR /app

# Force Gradle to use the installed JDK (bypass toolchains)
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH=$JAVA_HOME/bin:$PATH

COPY . .
RUN chmod +x gradlew
RUN ./gradlew --no-daemon clean bootJar

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
