FROM --platform=linux/amd64 openjdk:17-alpine
COPY target/*.jar app.jar
EXPOSE 8091
ENTRYPOINT ["java", "-jar", "app.jar"]