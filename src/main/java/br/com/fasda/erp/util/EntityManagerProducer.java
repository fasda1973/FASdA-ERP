package br.com.fasda.erp.util;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.flywaydb.core.Flyway;

@ApplicationScoped
public class EntityManagerProducer {

    private EntityManagerFactory factory;

    // Construtor padrão do CDI (precisa ficar vazio ou simples)
    public EntityManagerProducer() {
    }

    /**
     * Este método monitora a inicialização da aplicação. 
     * O CDI é OBRIGADO a rodar este método assim que o Tomcat sobe!
     */
    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
        System.out.println("[FASdA-ERP] >>> SISTEMA INICIALIZADO - ACORDANDO EM_PRODUCER <<<");
        
        String railwayUrl = System.getenv("MYSQL_URL");
        String railwayUser = System.getenv("MYSQLUSER");
        String railwayPassword = System.getenv("MYSQLPASSWORD");

        com.mysql.cj.jdbc.MysqlDataSource dataSource = new com.mysql.cj.jdbc.MysqlDataSource();
        Map<String, String> propriedades = new HashMap<>();

        if (railwayUrl != null && railwayUrl.startsWith("mysql://")) {
            railwayUrl = "jdbc:" + railwayUrl;
        }

        // Configura conexões mantendo os parâmetros idênticos ao seu persistence.xml
        if (railwayUrl != null && !railwayUrl.isEmpty()) {
            System.out.println("[FASdA-ERP] Configurando conexão de Produção (Railway)...");
            
            // Garantindo os parâmetros de Timezone e SSL iguais aos que você usa localmente
            String sufixo = "?allowPublicKeyRetrieval=true&useTimezone=true&serverTimezone=America/Sao_Paulo&useSSL=false";
            
            dataSource.setURL(railwayUrl + sufixo);
            dataSource.setUser(railwayUser);
            dataSource.setPassword(railwayPassword);

            propriedades.put("javax.persistence.jdbc.url", railwayUrl + sufixo);
            propriedades.put("javax.persistence.jdbc.user", railwayUser);
            propriedades.put("javax.persistence.jdbc.password", railwayPassword);
        } else {
            System.out.println("[FASdA-ERP] Configurando conexão Local (Localhost)...");
            // Usando exatamente a string do seu persistence.xml para não dar divergência local
            dataSource.setURL("jdbc:mysql://localhost:3306/fasda_erp?allowPublicKeyRetrieval=true&useTimezone=true&serverTimezone=America/Sao_Paulo&useSSL=false");
            dataSource.setUser("root");
            dataSource.setPassword("brcd2605");
        }

        // Executa o Flyway imediatamente na subida
        try {
            System.out.println("[FASdA-ERP] Flyway disparando migrações obrigatórias...");
            Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .baselineOnMigrate(true) 
                .baselineVersion("1") 
                .load();
            
            flyway.migrate();
            System.out.println("[FASdA-ERP] Flyway concluiu a estruturação das tabelas!");
        } catch (Exception e) {
            System.err.println("[FASdA-ERP] ERRO CRÍTICO NO START DO FLYWAY:");
            e.printStackTrace();
        }

        // Inicializa o Hibernate de forma síncrona
        try {
            System.out.println("[FASdA-ERP] Inicializando EntityManagerFactory do Hibernate...");
            this.factory = Persistence.createEntityManagerFactory("AlgaWorksPU", propriedades);
            System.out.println("[FASdA-ERP] Hibernate pronto para uso!");
        } catch (Exception e) {
            System.err.println("[FASdA-ERP] ERRO CRÍTICO AO INICIALIZAR O HIBERNATE:");
            e.printStackTrace();
        }
    }

    @Produces
    @RequestScoped
    public EntityManager createEntityManager() {
        if (this.factory == null) {
            throw new IllegalStateException("Fábrica de conexões não foi inicializada corretamente na subida.");
        }
        return factory.createEntityManager();
    }
}