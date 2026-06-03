package br.com.fasda.erp.controller;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.fasda.erp.model.Configuracao;
import br.com.fasda.erp.repository.ConfiguracaoRepository;
import br.com.fasda.erp.service.ConfiguracaoService;
import br.com.fasda.erp.util.Transacional;

import java.io.Serializable;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;

@Named
@ViewScoped
public class ConfiguracaoBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private ConfiguracaoService configuracaoService; // Sua classe de negócio
    
    @Inject
    private ConfiguracaoRepository configuracaoRepository;

    // Atributos que serão espelhados na tela
    private String caminhoUploadImagens;
    private boolean permitirCadastroUsuarios; // Exemplo de campo booleano
    private boolean permitirEstoqueNegativo;
    private double margemLucroMinima;

    @PostConstruct
    public void init() {
    	// Carrega o estado atual que está na memória da aplicação
        this.caminhoUploadImagens = configuracaoService.getCaminhoUpload();
        this.permitirEstoqueNegativo = configuracaoService.isPermitirEstoqueNegativo();
        this.margemLucroMinima = configuracaoService.getMargemLucroMinima();
        
        // Exemplo de valor padrão caso esteja vazio
        if (caminhoUploadImagens == null || caminhoUploadImagens.isEmpty()) {
            caminhoUploadImagens = System.getProperty("user.home") + "/uploads/imagens";
        }
    }
    
    @Transacional
    public void salvar() {
        try {
        	java.io.File pasta = new java.io.File(this.caminhoUploadImagens);
        	if (!pasta.exists()) {
        	    // Tenta criar a pasta automaticamente se ela não existir
        	    boolean criada = pasta.mkdirs();
        	    if (!criada) {
        	        FacesContext.getCurrentInstance().addMessage(null, 
        	            new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", "O diretório informado não existe e o sistema não conseguiu criá-lo. Verifique as permissões."));
        	        return;
        	    }
        	}
        	
        	// 1. Instancia as entidades com os dados da tela
            Configuracao cUpload = new Configuracao("CAMINHO_UPLOAD_IMAGENS", this.caminhoUploadImagens, "Diretório de uploads");
            Configuracao cEstoque = new Configuracao("PERMITIR_ESTOQUE_NEGATIVO", String.valueOf(this.permitirEstoqueNegativo), "Estoque negativo");
            Configuracao cMargem = new Configuracao("MARGEM_LUCRO_MINIMA", String.valueOf(this.margemLucroMinima), "Margem de lucro mínima");

            // 2. Salva no banco via DAO
            configuracaoRepository.salvar(cUpload);
            configuracaoRepository.salvar(cEstoque);
            configuracaoRepository.salvar(cMargem);

            // 3. Atualiza a memória local da aplicação
            configuracaoService.atualizarConfiguracao("CAMINHO_UPLOAD_IMAGENS", this.caminhoUploadImagens);
            configuracaoService.atualizarConfiguracao("PERMITIR_ESTOQUE_NEGATIVO", String.valueOf(this.permitirEstoqueNegativo));
            configuracaoService.atualizarConfiguracao("MARGEM_LUCRO_MINIMA", String.valueOf(this.margemLucroMinima));

            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Configurações salvas e aplicadas com sucesso!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao persistir dados no banco: " + e.getMessage()));
        }
    }
    
    /*
    public void finalizarVenda(Venda venda) {
        // Se o estoque for negativo e o sistema NÃO permitir (config padrão false)
        if (venda.getProduto().getEstoque() <= 0 && !config.isPermitirEstoqueNegativo()) {
            throw new NegocioException("Venda bloqueada: O produto não possui saldo em estoque.");
        }
        
        // Segue o fluxo normal da venda...
    }
    */

    // Getters e Setters
    public String getCaminhoUploadImagens() { return caminhoUploadImagens; }
    public void setCaminhoUploadImagens(String caminhoUploadImagens) { this.caminhoUploadImagens = caminhoUploadImagens; }
    public boolean isPermitirCadastroUsuarios() { return permitirCadastroUsuarios; }
    public void setPermitirCadastroUsuarios(boolean permitirCadastroUsuarios) { this.permitirCadastroUsuarios = permitirCadastroUsuarios; }
    public boolean isPermitirEstoqueNegativo() { return permitirEstoqueNegativo; }
    public void setPermitirEstoqueNegativo(boolean permitirEstoqueNegativo) { this.permitirEstoqueNegativo = permitirEstoqueNegativo; }
    public double getMargemLucroMinima() { return margemLucroMinima; }
    public void setMargemLucroMinima(double margemLucroMinima) { this.margemLucroMinima = margemLucroMinima; }
}
