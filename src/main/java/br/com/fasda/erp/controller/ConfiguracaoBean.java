package br.com.fasda.erp.controller;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import br.com.fasda.erp.model.Configuracao;
import br.com.fasda.erp.model.PermissaoNo;
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
    /*
    private boolean comumPodeVerClientes;
    private boolean comumPodeVerFornecedores;
    private boolean comumPodeVerFuncionarios;
    */
    // Permissões de usuario
    private TreeNode raizPermissoes;
    private TreeNode[] nosSelecionados;
    

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
        /*
        this.comumPodeVerClientes = configuracaoService.isComumPodeVerClientes();
        this.comumPodeVerFornecedores = configuracaoService.isComumPodeVerFornecedores();
        this.comumPodeVerFuncionarios = configuracaoService.isComumPodeVerFuncionarios();
        */
        
        // Exemplo de valor padrão caso esteja vazio
        if (caminhoUploadImagens == null || caminhoUploadImagens.isEmpty()) {
            caminhoUploadImagens = System.getProperty("user.home") + "/uploads/imagens";
        }
        
        // Teste temporário: Force uma mensagem no console para ver se o Java leu algo
        System.out.println("Caminho carregado na tela: " + this.caminhoUploadImagens);
        
        // Monta a estrutura lógica da árvore
        montarArvorePermissoes();
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
            /*
            Configuracao cComumClientes = new Configuracao("COMUM_PODE_VER_CLIENTES", String.valueOf(this.comumPodeVerClientes), "Permissão menu Clientes");
            Configuracao cComumForn = new Configuracao("COMUM_PODE_VER_FORNECEDORES", String.valueOf(this.comumPodeVerFornecedores), "Permissão menu Fornecedores");
            Configuracao cComumFunc = new Configuracao("COMUM_PODE_VER_FUNCIONARIOS", String.valueOf(this.comumPodeVerFuncionarios), "Permissão menu Funcionários");
            */
            
            // 2. SALVAMENTO CORRETO: Envia cada um para o Service tratar com Auditoria
            configuracaoService.salvar(cUpload, "Configuração", loginDoUsuario);
            configuracaoService.salvar(cEstoque, "Configuração", loginDoUsuario);
            configuracaoService.salvar(cMargem, "Configuração", loginDoUsuario);            
            configuracaoService.salvar(cUsuario, "Configuração", loginDoUsuario);            
            configuracaoService.salvar(cSmtpHost, "Configuração", loginDoUsuario);
            configuracaoService.salvar(cSmtpPort, "Configuração", loginDoUsuario);
            configuracaoService.salvar(cSmtpUser, "Configuração", loginDoUsuario);
            /*
            configuracaoService.salvar(cComumClientes, "Configuração", loginDoUsuario);
            configuracaoService.salvar(cComumForn, "Configuração", loginDoUsuario);
            configuracaoService.salvar(cComumFunc, "Configuração", loginDoUsuario);
            */
            // 3. Atualiza a memória local da aplicação
            configuracaoService.atualizarConfiguracao("CAMINHO_UPLOAD_IMAGENS", this.caminhoUploadImagens);
            configuracaoService.atualizarConfiguracao("PERMITIR_ESTOQUE_NEGATIVO", String.valueOf(this.permitirEstoqueNegativo));
            configuracaoService.atualizarConfiguracao("MARGEM_LUCRO_MINIMA", String.valueOf(this.margemLucroMinima));
            configuracaoService.atualizarConfiguracao("PERMITIR_CADASTRO_USUARIOS", String.valueOf(this.permitirCadastroUsuarios));           
            configuracaoService.atualizarConfiguracao("SMTP_HOST", String.valueOf(this.smtpHost));
            configuracaoService.atualizarConfiguracao("SMTP_PORT", String.valueOf(this.smtpPort));
            configuracaoService.atualizarConfiguracao("SMTP_USER", String.valueOf(this.smtpUser));
            /*
            configuracaoService.atualizarConfiguracao("COMUM_PODE_VER_CLIENTES", String.valueOf(this.comumPodeVerClientes));
            configuracaoService.atualizarConfiguracao("COMUM_PODE_VER_FORNECEDORES", String.valueOf(this.comumPodeVerFornecedores));
            configuracaoService.atualizarConfiguracao("COMUM_PODE_VER_FUNCIONARIOS", String.valueOf(this.comumPodeVerFuncionarios));
            */
            // Cria uma lista de todas as chaves folha possíveis para saber quem foi desmarcado
            java.util.Map<String, String> todasAsPermissoes = new java.util.HashMap<>();
            todasAsPermissoes.put("COMUM_CLIENTES_VER", "false");
            todasAsPermissoes.put("COMUM_CLIENTES_EDITAR", "false");
            todasAsPermissoes.put("COMUM_CLIENTES_EXCLUIR", "false");
            todasAsPermissoes.put("COMUM_FORN_VER", "false");
            todasAsPermissoes.put("COMUM_FORN_EDITAR", "false");
            todasAsPermissoes.put("COMUM_FORN_EXCLUIR", "false");
            todasAsPermissoes.put("COMUM_FUNC_VER", "false");
            todasAsPermissoes.put("COMUM_FUNC_EDITAR", "false");
            todasAsPermissoes.put("COMUM_FUNC_EXCLUIR", "false");
            
            // Sobrescreve para "true" quem de fato estiver selecionado na tela
            if (nosSelecionados != null) {
                for (TreeNode no : nosSelecionados) {
                    PermissaoNo noInfo = (PermissaoNo) no.getData();
                    if (noInfo.getChaveConfig() != null && !noInfo.getChaveConfig().isEmpty()) {
                        todasAsPermissoes.put(noInfo.getChaveConfig(), "true");
                    }
                }
            }
            
            // Salva e audita cada um dinamicamente no seu banco Chave/Valor existente!
            for (java.util.Map.Entry<String, String> permissao : todasAsPermissoes.entrySet()) {
                Configuracao cPerm = new Configuracao(permissao.getKey(), permissao.getValue(), "Controle de Acesso Matriz");
                configuracaoService.salvar(cPerm, "Configuração", loginDoUsuario);
                configuracaoService.atualizarConfiguracao(permissao.getKey(), permissao.getValue());
            }
            
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
    
    private void montarArvorePermissoes() {
        // Nível 1: Raiz Invisível do PrimeFaces (obrigatório) ou Nó "Usuários"
        raizPermissoes = new DefaultTreeNode(new PermissaoNo("Root", ""), null);
        TreeNode noUsuarios = new DefaultTreeNode(new PermissaoNo("Usuários", ""), raizPermissoes);
        noUsuarios.setExpanded(true);

        // Nível 2: Perfil Comum
        TreeNode noComum = new DefaultTreeNode(new PermissaoNo("Comum", ""), noUsuarios);
        TreeNode noAdmin = new DefaultTreeNode(new PermissaoNo("Admin", ""), noUsuarios);
        noComum.setExpanded(true);
        noAdmin.setExpanded(true);

        // Nível 3: Telas (.xhtml)
        TreeNode noClientes = new DefaultTreeNode("tela", new PermissaoNo("Clientes", ""), noComum);
        TreeNode noFornecedores = new DefaultTreeNode("tela", new PermissaoNo("Fornecedores", ""), noComum);
        TreeNode noFuncionarios = new DefaultTreeNode("tela", new PermissaoNo("Funcionários", ""), noComum);
        
        TreeNode noClientesAdm = new DefaultTreeNode("tela", new PermissaoNo("Clientes", ""), noAdmin);
        TreeNode noFornecedoresAdm = new DefaultTreeNode("tela", new PermissaoNo("Fornecedores", ""), noAdmin);
        TreeNode noFuncionariosAdm = new DefaultTreeNode("tela", new PermissaoNo("Funcionários", ""), noAdmin);

        // Nível 4: Ações com suas respectivas Chaves do Banco de Dados
        java.util.List<TreeNode> nosFolha = new java.util.ArrayList<>();
        
        nosFolha.add(new DefaultTreeNode("acao", new PermissaoNo("Ver", "COMUM_CLIENTES_VER"), noClientes));
        nosFolha.add(new DefaultTreeNode("acao", new PermissaoNo("Editar", "COMUM_CLIENTES_EDITAR"), noClientes));
        nosFolha.add(new DefaultTreeNode("acao", new PermissaoNo("Excluir", "COMUM_CLIENTES_EXCLUIR"), noClientes));

        nosFolha.add(new DefaultTreeNode("acao", new PermissaoNo("Ver", "COMUM_FORN_VER"), noFornecedores));
        nosFolha.add(new DefaultTreeNode("acao", new PermissaoNo("Editar", "COMUM_FORN_EDITAR"), noFornecedores));
        nosFolha.add(new DefaultTreeNode("acao", new PermissaoNo("Excluir", "COMUM_FORN_EXCLUIR"), noFornecedores));

        nosFolha.add(new DefaultTreeNode("acao", new PermissaoNo("Ver", "COMUM_FUNC_VER"), noFuncionarios));
        nosFolha.add(new DefaultTreeNode("acao", new PermissaoNo("Editar", "COMUM_FUNC_EDITAR"), noFuncionarios));
        nosFolha.add(new DefaultTreeNode("acao", new PermissaoNo("Excluir", "COMUM_FUNC_EXCLUIR"), noFuncionarios));
        
        // Admin
        nosFolha.add(new DefaultTreeNode("acao", new PermissaoNo("Ver", "ADMIN_CLIENTES_VER"), noClientesAdm));
        nosFolha.add(new DefaultTreeNode("acao", new PermissaoNo("Editar", "ADMIN_CLIENTES_EDITAR"), noClientesAdm));
        nosFolha.add(new DefaultTreeNode("acao", new PermissaoNo("Excluir", "ADMIN_CLIENTES_EXCLUIR"), noClientesAdm));

        nosFolha.add(new DefaultTreeNode("acao", new PermissaoNo("Ver", "ADMIN_FORN_VER"), noFornecedoresAdm));
        nosFolha.add(new DefaultTreeNode("acao", new PermissaoNo("Editar", "ADMIN_FORN_EDITAR"), noFornecedoresAdm));
        nosFolha.add(new DefaultTreeNode("acao", new PermissaoNo("Excluir", "ADMIN_FORN_EXCLUIR"), noFornecedoresAdm));

        nosFolha.add(new DefaultTreeNode("acao", new PermissaoNo("Ver", "ADMIN_FUNC_VER"), noFuncionariosAdm));
        nosFolha.add(new DefaultTreeNode("acao", new PermissaoNo("Editar", "ADMIN_FUNC_EDITAR"), noFuncionariosAdm));
        nosFolha.add(new DefaultTreeNode("acao", new PermissaoNo("Excluir", "ADMIN_FUNC_EXCLUIR"), noFuncionariosAdm));

        // Pré-marcar os checkboxes baseado no que já está salvo no banco
        java.util.List<TreeNode> marcados = new java.util.ArrayList<>();
        for (TreeNode noFolha : nosFolha) {
            PermissaoNo noInfo = (PermissaoNo) noFolha.getData();
            // Busca o estado atual no banco (Se não achar, assume 'true')
            boolean ativo = Boolean.parseBoolean(configuracaoService.getMapaConfiguracoes().getOrDefault(noInfo.getChaveConfig(), "true"));
            
            if (ativo) {
                noFolha.setSelected(true);
                marcados.add(noFolha);
                // Faz o PrimeFaces marcar visualmente os pais se os filhos estiverem selecionados
                atualizarSelecaoAscendente(noFolha);
            }
        }
        nosSelecionados = marcados.toArray(new TreeNode[0]);
    }

    private void atualizarSelecaoAscendente(TreeNode no) {
        TreeNode pai = no.getParent();
        if (pai != null && !pai.getData().toString().equals("Root")) {
            pai.setSelected(true);
            atualizarSelecaoAscendente(pai);
        }
    }

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
    /*
    public boolean isComumPodeVerClientes() { return comumPodeVerClientes; }
    public void setComumPodeVerClientes(boolean comumPodeVerClientes) { this.comumPodeVerClientes = comumPodeVerClientes; }
    public boolean isComumPodeVerFornecedores() { return comumPodeVerFornecedores; }
    public void setComumPodeVerFornecedores(boolean comumPodeVerFornecedores) { this.comumPodeVerFornecedores = comumPodeVerFornecedores; }
    public boolean isComumPodeVerFuncionarios() { return comumPodeVerFuncionarios; }
    public void setComumPodeVerFuncionarios(boolean comumPodeVerFuncionarios) { this.comumPodeVerFuncionarios = comumPodeVerFuncionarios; }
    */
    public ConfiguracaoService getConfiguracaoService() { return configuracaoService; }
	public void setConfiguracaoService(ConfiguracaoService configuracaoService) { this.configuracaoService = configuracaoService; }
	public ConfiguracaoRepository getConfiguracaoRepository() { return configuracaoRepository; }
	public void setConfiguracaoRepository(ConfiguracaoRepository configuracaoRepository) { this.configuracaoRepository = configuracaoRepository; }
	public static long getSerialversionuid() { return serialVersionUID; }

	public TreeNode getRaizPermissoes() {
		return raizPermissoes;
	}

	public void setRaizPermissoes(TreeNode raizPermissoes) {
		this.raizPermissoes = raizPermissoes;
	}

	public TreeNode[] getNosSelecionados() {
		return nosSelecionados;
	}

	public void setNosSelecionados(TreeNode[] nosSelecionados) {
		this.nosSelecionados = nosSelecionados;
	}
   
	
}
