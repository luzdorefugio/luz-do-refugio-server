# --------------------------------------------------------
# 1. Fase de Build
# --------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Compila o projeto (cria o server-0.0.1-SNAPSHOT.jar)
RUN mvn clean package -DskipTests

# TRUQUE: Renomeia QUALQUER .jar que foi criado para 'app.jar'
# Assim temos certeza absoluta que o nome fica correto para a fase seguinte
RUN mv target/*.jar target/app.jar

# --------------------------------------------------------
# 2. Fase de Execução
# --------------------------------------------------------
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copia o ficheiro que renomeámos em cima
COPY --from=build /app/target/app.jar app.jar

# Usa a porta flexível do Render/Railway (ou 8080 se não houver nenhuma)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]