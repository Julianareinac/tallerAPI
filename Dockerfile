FROM openjdk:17
WORKDIR /app
COPY /target/demo-0.0.1-SNAPSHOT.jar /app/tallerAPI.jar
ENTRYPOINT ["java", "-jar", "/app/tallerAPI.jar"]
