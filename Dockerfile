FROM openjdk:8-jdk-alpine
COPY target/courier-0.0.1-SNAPSHOT.jar spring.jar
ENTRYPOINT ["java","-jar","/spring.jar"]