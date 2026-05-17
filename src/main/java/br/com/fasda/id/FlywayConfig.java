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
    	    System.out.println("Iniciando Migração do Banco(fasda_erp) de Dados com Flyway...");

    	    // Criando o DataSource manualmente para garantir a conexão
    	    com.mysql.cj.jdbc.MysqlDataSource dataSource = new com.mysql.cj.jdbc.MysqlDataSource();
    	    dataSource.setURL("jdbc:mysql://localhost:3306/fasda_erp?useSSL=false&allowPublicKeyRetrieval=true");
    	    dataSource.setUser("root");
    	    dataSource.setPassword("brcd2605");

    	    Flyway flyway = Flyway.configure()
    	        .dataSource(dataSource) // Passamos o objeto pronto, não apenas a String
    	        .baselineOnMigrate(true) // Importante se você já tem tabelas
    	        .baselineVersion("2") // Aqui está o segredo! Começa a versionar a partir de V003__
    	        .load();
    	    
    	    flyway.migrate();
    	    System.out.println("Migração finalizada com sucesso!");

    	} catch (Exception e) {
    	    e.printStackTrace(); // Use printStackTrace para ver o erro completo no console
    	}
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nada a fazer aqui
    }
}