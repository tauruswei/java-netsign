FROM java:8
EXPOSE 34997
COPY  target/netsign-0.0.1-SNAPSHOT.jar /netsign-0.0.1-SNAPSHOT.jar
COPY  src/main/resources/application.yml /application.yml
ENTRYPOINT ["java","-jar","netsign-0.0.1-SNAPSHOT.jar","--spring.config.location=/application.yml"]
