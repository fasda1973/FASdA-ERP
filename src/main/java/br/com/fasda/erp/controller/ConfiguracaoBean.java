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
    
    @Inject
    private Configuracao configuracao;
    
    @Inject
    private LoginBean loginBean; // Injeta o seu bean de login/sessão

    // Atributos que serão espelhados na tela
    private String caminhoUploadImagens;
    private boolean permitirCadastroUsuarios; // Exemplo de campo booleano
    private boolean permitirEstoqueNegativo;
    private double margemLucroMinima;    
    private String smtpHost;
    private String smtpPort;
    private String smtpUser;
    
    private boolean comumPodeVerClientes;
    private boolean comumPodeVerFornecedores;
    private boolean comumPodeVerFuncionarios;
    

    @PostConstruct
    public void init() {
    	// Carrega o estado atual que está na memória da aplicação
        this.caminhoUploadImagens = configuracaoService.getCaminhoUpload();
        this.permitirEstoqueNegativo = configuracaoService.isPermitirEstoqueNegativo();
        this.margemLucroMinima = configuracaoService.getMargemLucroMinima();        
        this.permitirCadastroUsuarios = configuracaoService.isPermitirCadastroUsuarios();        
        this.smtpHost = configuracaoService.getSmtpHost();
        this.smtpPort = configuracaoService.getSmtpPort();
        this.smtpUser = configuracaoService.getSmtpUser();
        
        this.comumPodeVerClientes = configuracaoService.isComumPodeVerClientes();
        this.comumPodeVerFornecedores = configuracaoService.isComumPodeVerFornecedores();
        this.comumPodeVerFuncionarios = configuracaoService.isComumPodeVerFuncionarios();
        
        // Exemplo de valor padrão caso esteja vazio
        if (caminhoUploadImagens == null || caminhoUploadImagens.isEmpty()) {
            caminhoUploadImagens = System.getProperty("user.home") + "/uploads/imagens";
        }
        
     // Teste temporário: Force uma mensagem no console para ver se o Java leu algo
        System.out.println("Caminho carregado na tela: " + this.caminhoUploadImagens);
    }
        
    @Transacional
    public void salvar() {
        try {        	
        	String loginDoUsuario = "SISTEMA"; // Valor padrão de segurança 
			if (loginBean != null && loginBean.getUsuarioLogado() != null) {                
			     // 2. Pega o login através do getLogin
				loginDoUsuario = loginBean.getUsuarioLogado().getLogin();
			}	
        	
			//configuracaoService.salvar(this.configuracao, "Configuração", loginDoUsuario);
			
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
            Configuracao cUsuario = new Configuracao("PERMITIR_CADASTRO_USUARIOS", String.valueOf(this.permitirCadastroUsuarios), "Cadastro Usuário");            
            Configuracao cSmtpHost = new Configuracao("SMTP_HOST", String.valueOf(this.smtpHost), "Servidor de saída de e-mails");
            Configuracao cSmtpPort = new Configuracao("SMTP_PORT", String.valueOf(this.smtpPort), "Porta do servidor SMTP");
            Configuracao cSmtpUser = new Configuracao("SMTP_USER", String.valueOf(this.smtpUser), "Usuário do e-mail disparador");
            
            Configuracao cComumClientes = new Configuracao("COMUM_PODE_VER_CLIENTES", String.valueOf(this.comumPodeVerClientes), "Permissão menu Clientes");
            Configuracao cComumForn = new Configuracao("COMUM_PODE_VER_FORNECEDORES", String.valueOf(this.comumPodeVerFornecedores), "Permissão menu Fornecedores");
            Configuracao cComumFunc = new Configuracao("COMUM_PODE_VER_FUNCIONARIOS", String.valueOf(this.comumPodeVerFuncionarios), "Permissão menu Funcionários");
            
            
            // 2. SALVAMENTO CORRETO: Envia cada um para o Service tratar com Auditoria
            configuracaoService.salvar(cUpload, "Configuração", loginDoUsuario);
            configuracaoService.salvar(cEstoque, "Configuração", loginDoUsuario);
            configuracaoService.salvar(cMargem, "Configuração", loginDoUsuario);            
            configuracaoService.salvar(cUsuario, "Configuração", loginDoUsuario);            
            configuracaoService.salvar(cSmtpHost, "Configuração", loginDoUsuario);
            configuracaoService.salvar(cSmtpPort, "Configuração", loginDoUsuario);
            configuracaoService.salvar(cSmtpUser, "Configuração", loginDoUsuario);
            
            configuracaoService.salvar(cComumClientes, "Configuração", loginDoUsuario);
            configuracaoService.salvar(cComumForn, "Configuração", loginDoUsuario);
            configuracaoService.salvar(cComumFunc, "Configuração", loginDoUsuario);

            // 3. Atualiza a memória local da aplicação
            configuracaoService.atualizarConfiguracao("CAMINHO_UPLOAD_IMAGENS", this.caminhoUploadImagens);
            configuracaoService.atualizarConfiguracao("PERMITIR_ESTOQUE_NEGATIVO", String.valueOf(this.permitirEstoqueNegativo));
            configuracaoService.atualizarConfiguracao("MARGEM_LUCRO_MINIMA", String.valueOf(this.margemLucroMinima));
            configuracaoService.atualizarConfiguracao("PERMITIR_CADASTRO_USUARIOS", String.valueOf(this.permitirCadastroUsuarios));           
            configuracaoService.atualizarConfiguracao("SMTP_HOST", String.valueOf(this.smtpHost));
            configuracaoService.atualizarConfiguracao("SMTP_PORT", String.valueOf(this.smtpPort));
            configuracaoService.atualizarConfiguracao("SMTP_USER", String.valueOf(this.smtpUser));
            
            configuracaoService.atualizarConfiguracao("COMUM_PODE_VER_CLIENTES", String.valueOf(this.comumPodeVerClientes));
            configuracaoService.atualizarConfiguracao("COMUM_PODE_VER_FORNECEDORES", String.valueOf(this.comumPodeVerFornecedores));
            configuracaoService.atualizarConfiguracao("COMUM_PODE_VER_FUNCIONARIOS", String.valueOf(this.comumPodeVerFuncionarios));
            
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
    public String getSmtpHost() { return smtpHost; }
    public void setSmtpHost(String smtpHost) { this.smtpHost = smtpHost; }    
    public String getSmtpPort() { return smtpPort; }
    public void setSmtpPort(String smtpPort) { this.smtpPort = smtpPort; }    
    public String getSmtpUser() { return smtpUser; }
    public void setSmtpUser(String smtpUser) { this.smtpUser = smtpUser; }	
    public boolean isComumPodeVerClientes() { return comumPodeVerClientes; }
    public void setComumPodeVerClientes(boolean comumPodeVerClientes) { this.comumPodeVerClientes = comumPodeVerClientes; }
    public boolean isComumPodeVerFornecedores() { return comumPodeVerFornecedores; }
    public void setComumPodeVerFornecedores(boolean comumPodeVerFornecedores) { this.comumPodeVerFornecedores = comumPodeVerFornecedores; }
    public boolean isComumPodeVerFuncionarios() { return comumPodeVerFuncionarios; }
    public void setComumPodeVerFuncionarios(boolean comumPodeVerFuncionarios) { this.comumPodeVerFuncionarios = comumPodeVerFuncionarios; }
    
    public ConfiguracaoService getConfiguracaoService() { return configuracaoService; }
	public void setConfiguracaoService(ConfiguracaoService configuracaoService) { this.configuracaoService = configuracaoService; }
	public ConfiguracaoRepository getConfiguracaoRepository() { return configuracaoRepository; }
	public void setConfiguracaoRepository(ConfiguracaoRepository configuracaoRepository) { this.configuracaoRepository = configuracaoRepository; }
	public static long getSerialversionuid() { return serialVersionUID; }
   
    
}
