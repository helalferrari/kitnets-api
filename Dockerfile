# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Compila o projeto (skip tests para agilizar o build do container, assumindo que testes rodaram no CI ou local)
RUN mvn clean package -DskipTests

# Stage 2: Runtime
# Usamos Eclipse Temurin JRE (mais leve que o JDK completo e muito estável/padrão de mercado)
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Variáveis de ambiente padrão (podem ser sobrescritas pelo docker-compose)
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
