FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY settings.xml /root/.m2/settings.xml

ENV MAVEN_OPTS="-Xmx768m -XX:MaxMetaspaceSize=256m"

RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/rest-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8001
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75.0","-jar","app.jar"]
