# ETAPA 1: O Cozinheiro (Compilar o projeto com cache de dependências)
FROM maven:3.6.3-jdk-8 AS build
WORKDIR /app

# Copia o pom.xml E a pasta com o arquivo do tema pago
COPY pom.xml .
COPY local-repo ./local-repo

# Agora o Maven vai encontrar o Barcelona localmente sem falhar!
RUN mvn dependency:go-offline -B

# Copia o restante do código fonte e compila
COPY src ./src
RUN mvn clean package -DskipTests -B

# ETAPA 2: O Garçom (Rodar o projeto no Tomcat)
FROM tomcat:8.5-jdk8-openjdk
COPY --from=build /app/target/fasda-erp-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]