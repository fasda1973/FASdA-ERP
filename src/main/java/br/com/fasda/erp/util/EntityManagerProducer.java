package br.com.fasda.erp.util;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.flywaydb.core.Flyway;

@WebListener // ISSO OBRIGA O TOMCAT NA NUVEM A RODAR ESSA CLASSE NA SUBIDA!
@ApplicationScoped
public class EntityManagerProducer implements ServletContextListener {

    // Transformamos em estático para garantir que o CDI consiga enxergar a fábrica
    private static EntityManagerFactory factory;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("[FASdA-ERP] >>> TOMCAT ACORDOU O EM_PRODUCER VIA WEBLISTENER <<<");
        
        String railwayUrl = System.getenv("MYSQL_URL");
        String railwayUser = System.getenv("MYSQLUSER");
        String railwayPassword = System.getenv("MYSQLPASSWORD");

        com.mysql.cj.jdbc.MysqlDataSource dataSource = new com.mysql.cj.jdbc.MysqlDataSource();
        Map<String, String> propriedades = new HashMap<>();

        if (railwayUrl != null && railwayUrl.startsWith("mysql://")) {
            railwayUrl = "jdbc:" + railwayUrl;
        }

        if (railwayUrl != null && !railwayUrl.isEmpty()) {
            System.out.println("[FASdA-ERP] Configurando conexão de Produção (Railway)...");
            String sufixo = "?allowPublicKeyRetrieval=true&useTimezone=true&serverTimezone=America/Sao_Paulo&useSSL=false";
            
            dataSource.setURL(railwayUrl + sufixo);
            dataSource.setUser(railwayUser);
            dataSource.setPassword(railwayPassword);

            propriedades.put("javax.persistence.jdbc.url", railwayUrl + sufixo);
            propriedades.put("javax.persistence.jdbc.user", railwayUser);
            propriedades.put("javax.persistence.jdbc.password", railwayPassword);
        } else {
            System.out.println("[FASdA-ERP] Configurando conexão Local (Localhost)...");
            dataSource.setURL("jdbc:mysql://localhost:3306/fasda_erp?allowPublicKeyRetrieval=true&useTimezone=true&serverTimezone=America/Sao_Paulo&useSSL=false");
            dataSource.setUser("root");
            dataSource.setPassword("brcd2605");
        }

        // DISPARA O FLYWAY
        try {
            System.out.println("[FASdA-ERP] Flyway disparando migrações obrigatórias via WebListener...");
            Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .load();
            
            flyway.migrate();
            System.out.println("[FASdA-ERP] Flyway concluiu a estruturação das tabelas!");
        } catch (Exception e) {
            System.err.println("[FASdA-ERP] ERRO CRÍTICO NO START DO FLYWAY:");
            e.printStackTrace();
        }

        // INICIALIZA O HIBERNATE
        try {
            System.out.println("[FASdA-ERP] Inicializando EntityManagerFactory do Hibernate...");
            factory = Persistence.createEntityManagerFactory("AlgaWorksPU", propriedades);
            System.out.println("[FASdA-ERP] Hibernate pronto para uso!");
        } catch (Exception e) {
            System.err.println("[FASdA-ERP] ERRO CRÍTICO AO INICIALIZAR O HIBERNATE:");
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }

    @Produces
    @RequestScoped
    public EntityManager createEntityManager() {
        if (factory == null) {
            throw new IllegalStateException("Fábrica de conexões não foi inicializada na subida do Tomcat.");
        }
        return factory.createEntityManager();
    }
}