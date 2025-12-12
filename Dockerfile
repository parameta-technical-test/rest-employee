FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app
COPY settings.xml /root/.m2/settings.xml

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=build /app/target/rest-0.0.1-SNAPSHOT.jar rest-ms.jar

EXPOSE 8001

ENTRYPOINT ["java", "-jar", "rest-ms.jar"]