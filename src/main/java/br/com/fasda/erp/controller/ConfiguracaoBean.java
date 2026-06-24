package br.com.fasda.erp.controller;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import br.com.fasda.erp.model.Configuracao;
import br.com.fasda.erp.model.PermissaoNo;
import br.com.fasda.erp.repository.ConfiguracaoRepository;
import br.com.fasda.erp.service.ConfiguracaoService;
import br.com.fasda.erp.util.Transacional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    
    // Adicione estes atributos ao seu ConfiguracaoBean
    private TreeNode raizPermissoes;
    private TreeNode[] nosSelecionados;
    
    // 1. Substitua o atributo raizPermissoes por este:
    private List<LinhaPermissao> listaPermissoes;
    

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
               
        // Exemplo de valor padrão caso esteja vazio
        if (caminhoUploadImagens == null || caminhoUploadImagens.isEmpty()) {
            caminhoUploadImagens = System.getProperty("user.home") + "/uploads/imagens";
        }
        
        // Teste temporário: Force uma mensagem no console para ver se o Java leu algo
        System.out.println("Caminho carregado na tela: " + this.caminhoUploadImagens);
         
        // Monta a estrutura lógica da árvore
        montarArvorePermissoes();
        
        //inicializarMatriz();
    }
        
    @Transacional
    public void salvar() {
        try {        	
        	String loginDoUsuario = "SISTEMA"; // Valor padrão de segurança 
			if (loginBean != null && loginBean.getUsuarioLogado() != null) {                
			     // 2. Pega o login através do getLogin
				loginDoUsuario = loginBean.getUsuarioLogado().getLogin();
			}	
			
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
                        
            // 2. SALVAMENTO CORRETO: Envia cada um para o Service tratar com Auditoria
            configuracaoService.salvar(cUpload, "Configuração", loginDoUsuario);
            configuracaoService.salvar(cEstoque, "Configuração", loginDoUsuario);
            configuracaoService.salvar(cMargem, "Configuração", loginDoUsuario);            
            configuracaoService.salvar(cUsuario, "Configuração", loginDoUsuario);            
            configuracaoService.salvar(cSmtpHost, "Configuração", loginDoUsuario);
            configuracaoService.salvar(cSmtpPort, "Configuração", loginDoUsuario);
            configuracaoService.salvar(cSmtpUser, "Configuração", loginDoUsuario);
            
            // 3. Atualiza a memória local da aplicação
            configuracaoService.atualizarConfiguracao("CAMINHO_UPLOAD_IMAGENS", this.caminhoUploadImagens);
            configuracaoService.atualizarConfiguracao("PERMITIR_ESTOQUE_NEGATIVO", String.valueOf(this.permitirEstoqueNegativo));
            configuracaoService.atualizarConfiguracao("MARGEM_LUCRO_MINIMA", String.valueOf(this.margemLucroMinima));
            configuracaoService.atualizarConfiguracao("PERMITIR_CADASTRO_USUARIOS", String.valueOf(this.permitirCadastroUsuarios));           
            configuracaoService.atualizarConfiguracao("SMTP_HOST", String.valueOf(this.smtpHost));
            configuracaoService.atualizarConfiguracao("SMTP_PORT", String.valueOf(this.smtpPort));
            configuracaoService.atualizarConfiguracao("SMTP_USER", String.valueOf(this.smtpUser));
                       
            // Arrays com as definições da sua matriz (Deixe em CAIXA ALTA para bater com o padrão de chaves do banco)
            String[] perfis = {"ADMIN", "COMUM"};
            String[] telas = {"CLIENTES", "FORN", "FUNC", "PESSOAS", "USUARIOS", "CONFIG"};
            String[] acoes = {"VER", "NOVO", "EDITAR", "EXCLUIR"};
            
            java.util.Map<String, String> todasAsPermissoes = new java.util.HashMap<>();
            
            // Loops aninhados corrigidos usando a sintaxe nativa do Java
            for (String perfil : perfis) {
                for (String tela : telas) {
                    for (String acao : acoes) {
                        // Concatena as strings gerando: PERFIL_TELA_ACAO (Ex: COMUM_CLIENTES_VER)
                        String chaveRegra = perfil + "_" + tela + "_" + acao;
                        
                        // Define o valor padrão inicial como "false" para todo mundo
                        todasAsPermissoes.put(chaveRegra, "false");
                        
                        System.out.println("Loop: " + chaveRegra);
                    }
                }
            }
            
            // Sobrescreve para "true" quem de fato estiver selecionado na tela
            if (nosSelecionados != null) {
                for (TreeNode no : nosSelecionados) {
                    PermissaoNo noInfo = (PermissaoNo) no.getData();
                    if (noInfo.getChaveConfig() != null && !noInfo.getChaveConfig().isEmpty()) {
                        todasAsPermissoes.put(noInfo.getChaveConfig(), "true");
                        
                        System.out.println("Sobrescreve: " + noInfo.getChaveConfig());
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
    	            new FacesMessage("INFORMAÇÃO", "Configurações salvas com sucesso"));

        } catch (Exception e) {
        	FacesContext.getCurrentInstance().addMessage(null, 
    	            new FacesMessage("ATENÇÃO", "Não foi possivel salvar as configuraçoes"));
        	
        	e.printStackTrace();
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

        
        
        // Definições de estruturas lado a lado (Nome de exibição vs Código da Chave)
        String[] perfisNome = {"Admin", "Comum"};
        String[] perfisChave = {"ADMIN", "COMUM"};
        
        String[] telasNome = {"Clientes", "Fornecedores", "Funcionários", "Pessoas", "Usuários", "Configurações"};
        String[] telasChave = {"CLIENTES", "FORN", "FUNC", "PESSOAS", "USUARIOS", "CONFIG"};
        
        String[] acoesNome = {"Ver", "Novo", "Editar", "Excluir"};
        String[] acoesChave = {"VER", "NOVO", "EDITAR", "EXCLUIR"};

        java.util.List<TreeNode> nosFolha = new java.util.ArrayList<>();

        // 1. Loop de Perfis (Comum, Admin)
        for (int u = 0; u < perfisChave.length; u++) {
            TreeNode noPerfil = new DefaultTreeNode(new PermissaoNo(perfisNome[u], ""), noUsuarios);
            noPerfil.setExpanded(true);
            
            // 2. Loop de Telas (Clientes, Forn...)
            for (int t = 0; t < telasChave.length; t++) {
                TreeNode noTela = new DefaultTreeNode("tela", new PermissaoNo(telasNome[t], ""), noPerfil);
                
                // 3. Loop de Ações (Ver, Novo...)
                for (int a = 0; a < acoesChave.length; a++) {
                    // Monta a chave dinâmica idêntica ao banco: EX: ADMIN_FORN_EDITAR
                    String chaveBanco = perfisChave[u] + "_" + telasChave[t] + "_" + acoesChave[a];
                    
                    // Cria o nó folha (o checkbox final)
                    nosFolha.add(new DefaultTreeNode("acao", new PermissaoNo(acoesNome[a], chaveBanco), noTela));
                }
            }
        }
        
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
        
     // No final do método, depois de criar tudo com TRUE:
        //recolherArvoreAbstrata(raizPermissoes);
    }
    
    private void recolherArvoreAbstrata(TreeNode<?> no) {
        if (no == null) return;
        
        // Não muda a raiz principal, apenas os nós filhos
        if (no.getParent() != null) {
            no.setExpanded(false);
        }
        
        // CORREÇÃO AQUI: Especificamos TreeNode<?> para o compilador aceitar a conversão
        for (TreeNode<?> filho : no.getChildren()) {
            recolherArvoreAbstrata(filho);
        }
    }

    public Configuracao getConfiguracao() {
		return configuracao;
	}

	public void setConfiguracao(Configuracao configuracao) {
		this.configuracao = configuracao;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

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
    public ConfiguracaoService getConfiguracaoService() { return configuracaoService; }
	public void setConfiguracaoService(ConfiguracaoService configuracaoService) { this.configuracaoService = configuracaoService; }
	public ConfiguracaoRepository getConfiguracaoRepository() { return configuracaoRepository; }
	public void setConfiguracaoRepository(ConfiguracaoRepository configuracaoRepository) { this.configuracaoRepository = configuracaoRepository; }
	public static long getSerialversionuid() { return serialVersionUID; }
  
	// Nova estrategia para prefil de usuarios
	public static class LinhaPermissao implements java.io.Serializable {
	    private static final long serialVersionUID = 1L;
	    
	    private String perfil;
	    private String tela;
	    private String acao;
	    private String chaveConfig;
	    private boolean selecionado;

	    public LinhaPermissao() {}

	    public LinhaPermissao(String perfil, String tela, String acao, String chaveConfig, boolean selecionado) {
	        this.perfil = perfil;
	        this.tela = tela;
	        this.acao = acao;
	        this.chaveConfig = chaveConfig;
	        this.selecionado = selecionado;
	    }

	    // GETTERS E SETTERS (Essenciais para o JSF ler e gravar o checkbox)
	    public String getPerfil() { return perfil; }
	    public void setPerfil(String perfil) { this.perfil = perfil; }
	    public String getTela() { return tela; }
	    public void setTela(String tela) { this.tela = tela; }
	    public String getAcao() { return acao; }
	    public void setAcao(String acao) { this.acao = acao; }
	    public String getChaveConfig() { return chaveConfig; }
	    public void setChaveConfig(String chaveConfig) { this.chaveConfig = chaveConfig; }
	    public boolean isSelecionado() { return selecionado; }
	    public void setSelecionado(boolean selecionado) { this.selecionado = selecionado; }
	}
	
	public static class GrupoPermissao implements java.io.Serializable {
	    private static final long serialVersionUID = 1L;
	    
	    private String perfil;
	    private List<LinhaPermissao> linhas = new ArrayList<>();

	    public GrupoPermissao(String perfil) {
	        this.perfil = perfil;
	    }

	    public String getPerfil() { return perfil; }
	    public void setPerfil(String perfil) { this.perfil = perfil; }
	    public List<LinhaPermissao> getLinhas() { return linhas; }
	    public void setLinhas(List<LinhaPermissao> linhas) { this.linhas = linhas; }
	}
	
	// 2. Chame este método no seu @PostConstruct (ou onde iniciava a árvore antiga)
	public void inicializarMatriz() {
		try {
	        this.listaPermissoes = new java.util.ArrayList<>();
	        
	        java.util.Map<String, String> mapa = (configuracaoService != null) ? configuracaoService.getMapaConfiguracoes() : null;
	        if (mapa == null) mapa = new java.util.HashMap<>();

	        String[] perfis = {"ADMIN", "COMUM" };
	        String[] telas = {"CLIENTES", "FORN", "FUNC", "PESSOAS", "USUARIOS", "CONFIG"};
	        String[] acoes = {"VER", "NOVO", "EDITAR", "EXCLUIR"};

	        for (String p : perfis) {
	            for (String t : telas) {
	                for (String a : acoes) {
	                    String chave = p + "_" + t + "_" + a;
	                    boolean ativo = Boolean.parseBoolean(mapa.getOrDefault(chave, "false"));
	                    
	                    // Alimenta diretamente a lista plana
	                    this.listaPermissoes.add(new LinhaPermissao(p, t, a, chave, ativo));
	                }
	            }
	        }
	        System.out.println("LOG: Matriz inicializada com " + this.listaPermissoes.size() + " linhas.");
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	public List<LinhaPermissao> getListaPermissoes() {
	    return listaPermissoes;
	}

	public void setListaPermissoes(List<LinhaPermissao> listaPermissoes) {
	    this.listaPermissoes = listaPermissoes;
	}
	
	
}
