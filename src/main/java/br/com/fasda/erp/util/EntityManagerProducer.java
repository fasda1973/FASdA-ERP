package br.com.fasda.erp.util;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.flywaydb.core.Flyway;

@ApplicationScoped
public class EntityManagerProducer {

	private EntityManagerFactory factory;
	
	public EntityManagerProducer() {
        // 1. Pegamos as variáveis ocultas do Railway
        String railwayUrl = System.getenv("MYSQL_URL");
        String railwayUser = System.getenv("MYSQLUSER");
        String railwayPassword = System.getenv("MYSQLPASSWORD");

        // Configuração do DataSource para o Flyway e Hibernate
        com.mysql.cj.jdbc.MysqlDataSource dataSource = new com.mysql.cj.jdbc.MysqlDataSource();
        Map<String, String> propriedades = new HashMap<>();

        if (railwayUrl != null && railwayUrl.startsWith("mysql://")) {
            railwayUrl = "jdbc:" + railwayUrl;
        }

        // 2. Configura os caminhos de conexão (Nuvem vs Local)
        if (railwayUrl != null && !railwayUrl.isEmpty()) {
            System.out.println("[FASdA-ERP] Configurando conexão de Produção (Railway)...");
            dataSource.setURL(railwayUrl + "?useSSL=false&allowPublicKeyRetrieval=true");
            dataSource.setUser(railwayUser);
            dataSource.setPassword(railwayPassword);

            propriedades.put("javax.persistence.jdbc.url", railwayUrl);
            propriedades.put("javax.persistence.jdbc.user", railwayUser);
            propriedades.put("javax.persistence.jdbc.password", railwayPassword);
        } else {
            System.out.println("[FASdA-ERP] Configurando conexão Local (Localhost)...");
            dataSource.setURL("jdbc:mysql://localhost:3306/fasda_erp?useSSL=false&allowPublicKeyRetrieval=true");
            dataSource.setUser("root");
            dataSource.setPassword("brcd2605");
        }

        // 3. DISPARA O FLYWAY PRIMEIRO (Garantia de tabelas criadas)
        try {
            System.out.println("[FASdA-ERP] Flyway iniciando migração antes do Hibernate...");
            Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .baselineOnMigrate(true) 
                .baselineVersion("1") 
                .load();
            
            flyway.migrate();
            System.out.println("[FASdA-ERP] Flyway finalizou as migrações com sucesso!");
        } catch (Exception e) {
            System.err.println("[FASdA-ERP] ERRO CRÍTICO NO FLYWAY DENTRO DO PRODUCER:");
            e.printStackTrace();
        }

        // 4. AGORA SIM, O HIBERNATE ENTRA EM AÇÃO
        System.out.println("[FASdA-ERP] Inicializando EntityManagerFactory do Hibernate...");
        this.factory = Persistence.createEntityManagerFactory("AlgaWorksPU", propriedades);
	
	}
	
	@Produces
	@RequestScoped
	public EntityManager createEntityManager() {
		return this.factory.createEntityManager();
	}
	
	public void closeEntityManager(@Disposes EntityManager manager) {
		manager.close();
	}
	
}
