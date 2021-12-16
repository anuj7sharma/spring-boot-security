FROM adoptopenjdk/openjdk11:alpine-jre

WORKDIR /opt/app

RUN echo "Hello World"

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8090

ENTRYPOINT ["java","-jar","app.jar"]

