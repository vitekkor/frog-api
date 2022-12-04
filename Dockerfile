FROM gradle:7.6.0-jdk11-alpine
WORKDIR /vitekkor/frogapi
COPY . .
RUN gradle clean bootJar

FROM openjdk:11-jre-slim
WORKDIR /vitekkor/frogapi
COPY --from=0 /vitekkor/frogapi/images ./images
COPY --from=0 /vitekkor/frogapi/build/libs/frog-api-1.0-SNAPSHOT.jar .
EXPOSE 8080:8080
CMD java -jar frog-api-1.0-SNAPSHOT.jar --spring.config.location=classpath:/application.yml,optional:/etc/vitekkor/frogapi/application.yml
