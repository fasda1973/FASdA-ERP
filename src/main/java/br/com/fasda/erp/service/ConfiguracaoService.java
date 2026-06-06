package br.com.fasda.erp.service;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.fasda.erp.model.Configuracao;
import br.com.fasda.erp.repository.ConfiguracaoRepository;

import java.io.Serializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
@ApplicationScoped
public class ConfiguracaoService implements Serializable {
	
	private static final long serialVersionUID = 1L;

    // Guarda as configurações em memória: Chave -> Valor
    private Map<String, String> mapaConfiguracoes = new HashMap<>();

    @Inject
    private ConfiguracaoRepository configuracaoRepository;

    @PostConstruct
    public void carregarTodas() {
    	// Proteção contra NullPointerException caso o banco falhe
        if (configuracaoRepository == null) {
            System.out.println("ERRO CRÍTICO: O CDI não injetou o ConfiguracaoDAO!");
            return;
        }
    	
    	List<Configuracao> lista = configuracaoRepository.listarTodas();
    	
    	if (lista != null) {
	        for (Configuracao config : lista) {
	        	System.out.println("Chave carregada: " + config.getChave() + " | Valor: " + config.getValor());
	            mapaConfiguracoes.put(config.getChave(), config.getValor());
	        }
    	}
    }	

    // Métodos utilitários para ler os valores já convertidos no tipo certo
    public String getCaminhoUpload() {
        return mapaConfiguracoes.getOrDefault("CAMINHO_UPLOAD_IMAGENS", "/uploads");
    }
    
    public double getMargemLucroMinima() {
        return Double.parseDouble(mapaConfiguracoes.getOrDefault("MARGEM_LUCRO_MINIMA", "0.0"));
    }

    public boolean isPermitirEstoqueNegativo() {
        return Boolean.parseBoolean(mapaConfiguracoes.getOrDefault("PERMITIR_ESTOQUE_NEGATIVO", "false"));
    }

    public boolean isPermitirCadastroUsuarios() {
        // Busca o valor no mapa. Se não achar, o padrão por segurança é 'false'
        return Boolean.parseBoolean(mapaConfiguracoes.getOrDefault("PERMITIR_CADASTRO_USUARIOS", "false"));
    }
    
    public String getSmtpHost() {
        return mapaConfiguracoes.getOrDefault("SMTP_HOST", "smtp.gmail.com");
    }
    
    public String getSmtpPort() {
        return mapaConfiguracoes.getOrDefault("SMTP_PORT", "587");
    }
    
    public String getSmtpUser() {
        return mapaConfiguracoes.getOrDefault("SMTP_USER", "sistema@suaempresa.com");
    }
    
    // Método para atualizar a memória quando o usuário salvar na tela
    public void atualizarConfiguracao(String chave, String novoValor) {
        mapaConfiguracoes.put(chave, novoValor);
    }
}