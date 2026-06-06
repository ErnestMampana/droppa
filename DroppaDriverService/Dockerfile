FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

COPY src ./src
RUN mvn -B -ntp clean package -Dmaven.test.skip=true

FROM eclipse-temurin:17-jre-jammy

RUN groupadd --system spring && useradd --system --gid spring spring
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar

USER spring:spring
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
