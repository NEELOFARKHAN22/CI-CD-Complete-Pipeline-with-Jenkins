FROM openjdk:8-jdk-alpine
WORKDIR /app
COPY target/rest-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]
