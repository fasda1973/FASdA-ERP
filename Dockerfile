# ETAPA 1: O Cozinheiro (Compilar o projeto com cache de dependências)
FROM maven:3.6.3-jdk-8 AS build
WORKDIR /app

# Copia apenas o pom.xml primeiro para baixar as dependências separadamente
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Agora copia o código fonte e compila sem precisar baixar tudo de novo
COPY src ./src
RUN mvn clean package -DskipTests -B

# ETAPA 2: O Garçom (Rodar o projeto no Tomcat)
FROM tomcat:8.5-jdk8-openjdk
COPY --from=build /app/target/fasda-erp-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]