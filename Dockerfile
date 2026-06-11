# Estágio 1: Build (Compilação) usando Maven e Java 8
FROM maven:3.6.3-jdk-8 AS build
WORKDIR /app
# Copia as configurações e o código-fonte
COPY pom.xml .
COPY src ./src
# Executa o comando do Maven para gerar o arquivo .war ignorando testes
RUN mvn clean package -DskipTests

# Estágio 2: Ambiente de Execução com Tomcat oficial e Java 8
FROM tomcat:8.5-jdk8-openjdk
# Pega o arquivo .war gerado no estágio anterior e joga na pasta webapps do Tomcat como ROOT.war
# (Mudar para ROOT.war faz com que o seu ERP abra direto na página inicial, sem precisar digitar /fasda-erp na URL)
COPY --from=build /app/target/fasda-erp-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]