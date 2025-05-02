FROM maven:3.9.6-eclipse-temurin-17 AS build

COPY . /app
WORKDIR /app

RUN mvn clean package -DskipTests

FROM tomcat:10.1.34-jdk17

RUN rm -rf /usr/local/tomcat/webapps/*

COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war
