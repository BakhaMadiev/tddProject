FROM openjdk:8

ARG JAR_FILE

COPY /target/$JAR_FILE /app/app.jar

ENTRYPOINT [ "java", "-cp", "app/app.jar", "com.example.inventory.MainApp" ]