FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/uptodate-0.2.1.jar /app/uptodate-0.2.1.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "uptodate-0.2.1.jar"]