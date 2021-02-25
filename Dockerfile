FROM openjdk:jdk-alpine
RUN mkdir -p /opt/app/
COPY ./manifest.json /opt/app
COPY ./data.dat /opt/app
COPY ./target/latamtv-thorntail.jar /opt/app/
WORKDIR /opt/app/

EXPOSE 8080
ENTRYPOINT ["java", "-Djava.net.preferIPv4Stack=true", "-jar", "/opt/app/latamtv-thorntail.jar"]
