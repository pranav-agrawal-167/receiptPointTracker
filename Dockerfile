FROM ubuntu:latest
RUN apt-get update && \
    apt-get install -y software-properties-common && \
    add-apt-repository ppa:openjdk-r/ppa && \
    apt-get update && \
    apt-get install -y openjdk-8-jdk
WORKDIR /app
COPY pom.xml .
RUN apt-get install -y maven && \
    mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean install
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "target/receipt-processor-0.0.1-SNAPSHOT.jar"]
