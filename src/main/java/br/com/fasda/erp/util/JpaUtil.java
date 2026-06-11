package br.com.fasda.erp.util;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JpaUtil {

	private static EntityManagerFactory factory;

	static {
		Map<String, String> propriedades = new HashMap<>();

		// O Railway fornece essas variáveis de ambiente por padrão no banco MySQL
		String dbHost = System.getenv("MYSQLHOST");
		String dbPort = System.getenv("MYSQLPORT");
		String dbName = System.getenv("MYSQLDATABASE");
		String dbUser = System.getenv("MYSQLUSER");
		String dbPassword = System.getenv("MYSQLPASSWORD");

		// Se a variável MYSQLHOST existir, significa que estamos rodando na nuvem (Railway)
		if (dbHost != null) {
			String urlNuvem = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName 
					+ "?allowPublicKeyRetrieval=true&useTimezone=true&serverTimezone=America/Sao_Paulo&useSSL=false";
			
			propriedades.put("javax.persistence.jdbc.url", urlNuvem);
			propriedades.put("javax.persistence.jdbc.user", dbUser);
			propriedades.put("javax.persistence.jdbc.password", dbPassword);
		} else {
			// Se for nulo, significa que você deu Play no Eclipse/sua máquina local. Usa o seu localhost!
			String urlLocal = "jdbc:mysql://localhost:3306/fasda_erp"
					+ "?allowPublicKeyRetrieval=true&useTimezone=true&serverTimezone=America/Sao_Paulo&useSSL=false";
			
			propriedades.put("javax.persistence.jdbc.url", urlLocal);
			propriedades.put("javax.persistence.jdbc.user", "root");
			propriedades.put("javax.persistence.jdbc.password", "brcd2605"); // Sua senha local antiga
		}

		// Cria a fábrica do Hibernate injetando as propriedades dinâmicas
		factory = Persistence.createEntityManagerFactory("AlgaWorksPU", propriedades);
	}

	public static EntityManager getEntityManager() {
		return factory.createEntityManager();
	}
}