FROM openjdk:jdk-alpine
RUN mkdir -p /opt/app/
RUN apk add maven
COPY . /opt/app/
WORKDIR /opt/app/
RUN mvn clean install

EXPOSE 8080
ENTRYPOINT ["java", "-Djava.net.preferIPv4Stack=true", "-jar", "/opt/app/target/latamtv-thorntail.jar"]
