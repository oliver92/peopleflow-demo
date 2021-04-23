# the first stage of our build will use a maven 3.6.1 parent image
FROM maven:3.8.1-jdk-11-slim AS MAVEN_BUILD
# copy the pom and src code to the container
COPY ./ ./
# package our application code
RUN mvn clean package -Dmaven.test.skip=true

#copy and run the built jar
FROM amazoncorretto:11-alpine-jdk
MAINTAINER baeldung.com
COPY --from=MAVEN_BUILD /target/employee-1.0.0.jar /employee-1.0.0.jar
ENTRYPOINT ["java","-jar","/employee-1.0.0.jar"]
