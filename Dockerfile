# 1. Fase de Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Forçamos o nome do ficheiro final para 'app.jar' no package
RUN mvn clean package -DskipTests -DfinalName=app

# 2. Fase de Execução
FROM eclipse-temurin:21-jdk
WORKDIR /app
# Agora sabemos o nome exato, sem asteriscos mágicos
COPY --from=build /app/target/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]