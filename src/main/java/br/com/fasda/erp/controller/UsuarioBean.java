package br.com.fasda.erp.controller;

import java.util.Arrays;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;

import br.com.fasda.erp.model.Usuario;
import br.com.fasda.erp.repository.UsuarioRepository;
import br.com.fasda.erp.service.ConfiguracaoService;
import br.com.fasda.erp.service.UsuarioService;
import br.com.fasda.erp.util.ArquivosUploads;
import br.com.fasda.erp.util.NegocioException;

@Named	
@ViewScoped
public class UsuarioBean extends CrudBean<Usuario> {
	
    private static final long serialVersionUID = 1L;

    @Inject
    private UsuarioService usuarioService;
    
    @Inject
    private ConfiguracaoService configuracaoService; // Injeta o serviço global
    
    @Inject
    private LoginBean loginBean; // Injeta o seu bean de login/sessão
    
    @Inject
    private UsuarioRepository usuariosRepository;
    
    public UsuarioBean() {
        // Passamos a classe Pessoa para o CrudBean
        super(Usuario.class);
    }
    
    // --- MÉTODOS OBRIGATÓRIOS (OVERRIDE) ---
    
    @Override
    public void pesquisar() {   	
    	if (termoPesquisa == null || termoPesquisa.trim().isEmpty()) {
    		this.listaItens = usuariosRepository.todos(); // Traz tudo se não houver filtro
    	} else {
            this.listaItens = usuariosRepository.pesquisar(this.termoPesquisa);
        }
	}
    
    @Override
    public void salvar() {
    	try {
    		// Prepara loginAuditoria
        	String loginDoUsuario = "SISTEMA"; // Valor padrão de segurança      	
        	// 1. Verifica se o loginBean e o usuário logado não estão nulos
            if (loginBean != null && loginBean.getUsuarioLogado() != null) {                
                // 2. Pega o login através do getLogin
                loginDoUsuario = loginBean.getUsuarioLogado().getLogin();
            }	
    		
	    	// Usamos 'entidade' que vem do CrudBean (substitui 'usuario')
	    	usuarioService.salvar(this.entidade, "Usuario", loginDoUsuario); // Tenta salvar através do Service
	    	
	        atualizarRegistros();
	        
	        // Se chegou aqui, deu certo!
	        messages.info("Usuario salvo com sucesso!");
	        
	        // limpar o formulário após salvar:
	        this.entidade = new Usuario();
	                
	        PrimeFaces.current().ajax().update(Arrays.asList("frm:dataTable", "frm:messages"));
        
    	} catch (NegocioException e) {
    		// Usando o novo método que criamos para direcionar ao campo específico
            messages.error("frm:login", e.getMessage());
    	}
    }
    
    @Override
    public void excluir() {    	
    	try {
    		String loginDoUsuario = "SISTEMA"; // Valor padrão de segurança
    		
    		loginDoUsuario = loginBean.getUsuarioLogado().getLogin();
    		
	    	usuarioService.excluir(this.entidade, "Usuario", loginDoUsuario);
	    	this.entidade = null;
	    	atualizarRegistros(); // Atualiza a lista após remover
	        messages.info("Usuário excluído com sucesso!");
    	} catch (NegocioException e) {
            messages.error(e.getMessage());
        }
    }
    
    @Override
    public void prepararNovo() {
        this.entidade = new Usuario();
    }
    
    @Override
    public void prepararEdicao() {
        
    }
    
    @Override
    protected Object getEntidadeId(Usuario usuario) {
        return usuario.getId();
    }
    
    public void todosUsuarios() {
        this.listaItens = usuariosRepository.todos();
    }
    
    private void atualizarRegistros() {
        if (jaHouvePesquisa()) {
            pesquisar();
        } else {
            todosUsuarios();
        }
    }
    
    private boolean jaHouvePesquisa() {
        return termoPesquisa != null && !termoPesquisa.isEmpty();
    }
    
    // Esse é o método que o p:fileUpload vai chamar no XHTML
    public void handleFileUpload(FileUploadEvent event) {
        
        // Chamamos a classe utilitária passando o evento, o serviço de configuração e a pasta destino
        String nomeArquivoSalvo = ArquivosUploads.realizarUpload(event, configuracaoService, "/Imagens/Pessoa");
        
        if (nomeArquivoSalvo != null) {
            // O Bean recebe o nome e seta no objeto correto!
            this.entidade.setFotoCaminho(nomeArquivoSalvo);
        }
    }
    
    // Verifica disponibilidade de nome de usuario
    public void verificarDisponibilidade() {
        String login = entidade.getLogin();
        
        if (login != null && !login.trim().isEmpty()) {
            // Busca no repositório se já existe alguém com esse login
            boolean jaExiste = usuariosRepository.existeLogin(login, entidade.getId());
            
            if (jaExiste) {
                FacesContext context = FacesContext.getCurrentInstance();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuário já cadastrado", null);
                
                // O segredo está aqui: o primeiro parâmetro é o "Client ID" do componente
                // Se o campo estiver dentro de um form 'frm', o ID costuma ser 'frm:login'
                context.addMessage("frm:login", msg);
            }
        }
    }
    
}