FROM openjdk:17-alpine

WORKDIR /usr/src/app

COPY ./build/libs/curi-0.0.1-SNAPSHOT.jar /build/libs/curi-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/build/libs/curi-0.0.1-SNAPSHOT.jar"]