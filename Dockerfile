FROM docker.io/library/maven:3.9-eclipse-temurin-17 AS build

WORKDIR /src

COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q package -DskipTests

FROM docker.io/library/tomcat:10.1-jdk17-temurin

RUN rm -rf /usr/local/tomcat/webapps/*

COPY --from=build /src/target/vaultify.war /usr/local/tomcat/webapps/vaultify.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
