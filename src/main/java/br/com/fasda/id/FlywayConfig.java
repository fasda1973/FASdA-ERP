package br.com.fasda.id;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.flywaydb.core.Flyway;

@WebListener
public class FlywayConfig implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            System.out.println("Iniciando Migração do Banco de Dados com Flyway...");

            com.mysql.cj.jdbc.MysqlDataSource dataSource = new com.mysql.cj.jdbc.MysqlDataSource();

            // 1. Pegamos as variáveis ocultas do Railway
            String railwayUrl = System.getenv("MYSQL_URL");
            String railwayUser = System.getenv("MYSQLUSER");
            String railwayPassword = System.getenv("MYSQLPASSWORD");

            if (railwayUrl != null && railwayUrl.startsWith("mysql://")) {
                railwayUrl = "jdbc:" + railwayUrl;
            }

            // 2. Se estiver na nuvem, usa o banco do Railway. Se não, usa o seu localhost.
            if (railwayUrl != null && !railwayUrl.isEmpty()) {
                System.out.println("Flyway detectou ambiente de produção (Railway). Conectando...");
                dataSource.setURL(railwayUrl + "?useSSL=false&allowPublicKeyRetrieval=true");
                dataSource.setUser(railwayUser);
                dataSource.setPassword(railwayPassword);
            } else {
                System.out.println("Flyway detectou ambiente local. Conectando no localhost/fasda_erp...");
                dataSource.setURL("jdbc:mysql://localhost:3306/fasda_erp?useSSL=false&allowPublicKeyRetrieval=true");
                dataSource.setUser("root");
                dataSource.setPassword("brcd2605");
            }

            // 3. Configuração do Flyway adaptada para o histórico do seu projeto
            Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .baselineOnMigrate(true) 
                .baselineVersion("1") 
                .load();
            
            flyway.migrate();
            System.out.println("Migração finalizada com sucesso!");

        } catch (Exception e) {
            System.err.println("ERRO CRÍTICO NA MIGRAÇÃO DO FLYWAY:");
            e.printStackTrace(); 
        }
    }

    // O compilador exige que este método apareça aqui, mesmo vazio!
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nada a fazer aqui quando o sistema desliga
    }
}