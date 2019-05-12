FROM openjdk:8-jdk-slim

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_OPTS=""

COPY ./build/libs/geolocation.jar /app.jar

CMD java ${JAVA_OPTS} -jar /app.jar 
