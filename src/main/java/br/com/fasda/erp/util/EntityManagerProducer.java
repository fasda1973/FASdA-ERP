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

@ApplicationScoped
public class EntityManagerProducer {

	private EntityManagerFactory factory;
	
	public EntityManagerProducer() {
		// Criamos um mapa para injetar as configurações dinamicamente
        Map<String, String> propriedades = new HashMap<>();
        
        // Tentamos ler as variáveis de ambiente da nuvem
        String url = System.getenv("MYSQL_URL");
        String usuario = System.getenv("MYSQLUSER");
        String senha = System.getenv("MYSQLPASSWORD");

        // Se elas existirem (estamos na nuvem), nós usamos. 
        // Se não existirem (você rodando local), o Hibernate usa o persistence.xml padrão do localhost.
        if (url != null && !url.isEmpty()) {
            propriedades.put("javax.persistence.jdbc.url", url);
            propriedades.put("javax.persistence.jdbc.user", usuario);
            propriedades.put("javax.persistence.jdbc.password", senha);
        }
		
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
