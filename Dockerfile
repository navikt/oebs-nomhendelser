# FROM ghcr.io/navikt/baseimages/temurin:21
FROM gcr.io/distroless/java21
WORKDIR /app

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","app.jar"]

COPY src/main/scripts/init.sh /init-scripts/init.sh







