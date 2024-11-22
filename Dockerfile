FROM maven:3.9.4-eclipse-temurin-21 AS build

WORKDIR /app

COPY ChatGuard/pom.xml ./
COPY ChatGuard/mvnw ./
COPY ChatGuard/.mvn ./.mvn

RUN chmod +x ./mvnw

RUN ./mvnw dependency:go-offline

COPY ChatGuard/. ./

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY --from=build /app/target/ChatGuard-1.0.1.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]