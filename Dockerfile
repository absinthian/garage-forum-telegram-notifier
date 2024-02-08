## Stage 1 : build with maven
FROM maven:3.9.6-amazoncorretto-17-debian AS build
COPY pom.xml .
COPY src /src
RUN mvn package

## Stage 2 : create the docker final image
FROM amazoncorretto:21-alpine3.18-jdk
COPY --from=build /target/garage-forum-telegram-notifier-runner.jar /app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app.jar"]