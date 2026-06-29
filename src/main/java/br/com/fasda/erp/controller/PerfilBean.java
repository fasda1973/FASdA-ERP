package br.com.fasda.erp.controller;

import java.io.IOException;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import br.com.fasda.erp.model.Usuario;
import br.com.fasda.erp.repository.UsuarioRepository;
import br.com.fasda.erp.service.UsuarioService;
import br.com.fasda.erp.util.NegocioException;
import br.com.fasda.erp.util.SenhaUtil;
import br.com.fasda.erp.util.Transacional;

@Named
@ViewScoped
public class PerfilBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private EntityManager manager;

    @Inject
    private LoginBean loginBean; // Injeta o loginBean para sabermos quem está logado
    
    @Inject
    private UsuarioService usuarioService;
    
    @Inject
	private UsuarioRepository usuarioRepository;

    private Usuario usuario;
    private String senhaAtual;
    private String novaSenha;
    private String confirmaNovaSenha;

    @PostConstruct
    public void init() {
        // Busca o usuário atualizado direto do banco para evitar dados obsoletos da sessão
        //if (loginBean.getUsuarioLogado() != null) {
            //this.usuario = manager.find(Usuario.class, loginBean.getUsuarioLogado().getId());
        //}
    	
    	// Se houver usuário logado, busca do banco para edição
        if (loginBean != null && loginBean.getUsuarioLogado() != null) {
            this.usuario = manager.find(Usuario.class, loginBean.getUsuarioLogado().getId());
        } else {
            // SE NÃO HOUVER LOGADO: Inicializa um objeto limpo para o fluxo de NOVO USUÁRIO
            this.usuario = new Usuario();
        }
    }

    @Transacional
    public void salvarPerfil() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            // Prepara loginAuditoria
            String loginDoUsuario = "SISTEMA"; 
            if (loginBean != null && loginBean.getUsuarioLogado() != null) {                
                loginDoUsuario = loginBean.getUsuarioLogado().getLogin();
            }
            
            boolean ehNovoUsuario = (usuario.getId() == null);

            if (ehNovoUsuario) {
                // --- REGRA PARA NOVO USUÁRIO ---
                if (novaSenha == null || novaSenha.trim().isEmpty()) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "A senha é obrigatória para um novo cadastro!"));
                    return;
                }
                if (!novaSenha.equals(confirmaNovaSenha)) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "A senha e a confirmação não coincidem!"));
                    return;
                }
                
                // CRUCIAL: Verifique se o login existe ANTES de mexer na senha ou enviar para o Service
                boolean jaExiste = usuarioRepository.existeLogin(usuario.getLogin(), usuario.getId());
                
                if (jaExiste) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Já existe um usuário com o login informado."));
                    
                    // SOLUÇÃO DO BUG: Reseta o login para null para manter a aba "Criar login" visível!
                    this.usuario.setLogin(null); 
                    return;
                }
                
                // Atribui a senha limpa. Deixe a criptografia APENAS dentro do UsuarioService para não duplicar!
                usuario.setSenha(novaSenha);
                
                // Tenta salvar através do Service
                usuarioService.salvar(this.usuario, "Perfil", loginDoUsuario); 
                
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuário cadastrado com sucesso!"));
                
                // Inicializa um novo objeto limpo pós-sucesso
                this.usuario = new Usuario(); 
                
             // Aguarda um instante ou redireciona direto. O mais comum e seguro para Ajax:
                redirecionarParaLogin();
                return; // Interrompe a execução já que vai mudar de página
                
            } else {
                // --- REGRA PARA ALTERAÇÃO DE PERFIL EXISTENTE ---
                if (novaSenha != null && !novaSenha.trim().isEmpty()) {
                    
                    if (!SenhaUtil.verificar(senhaAtual, usuario.getSenha())) {
                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Senha atual incorreta!"));
                        return;
                    }
                    
                    if (!novaSenha.equals(confirmaNovaSenha)) {
                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "A nova senha e a confirmação não coincidem!"));
                        return;
                    }
                    
                    usuario.setSenha(novaSenha);
                }

                // Salva através do Service
                usuarioService.salvar(this.usuario, "Perfil", loginDoUsuario); 
                
                if (loginBean != null && loginBean.getUsuarioLogado() != null) {
                    loginBean.getUsuarioLogado().setNome(usuario.getNome());
                }
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Perfil updated com sucesso!"));
            
            }

            // Limpa os campos de senha da tela pós-execução (Sucesso)
            this.senhaAtual = null;
            this.novaSenha = null;
            this.confirmaNovaSenha = null;
            
            // Se você quiser que na alteração de perfil ele também deslogue e volte para o Login:
            redirecionarParaLogin();
            return;
            
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Não foi possível atualizar o perfil."));
        }
    }

    // GETTERS E SETTERS
    public Usuario getUsuario() { return usuario; }
    public String getSenhaAtual() { return senhaAtual; }
    public void setSenhaAtual(String senhaAtual) { this.senhaAtual = senhaAtual; }
    public String getNovaSenha() { return novaSenha; }
    public void setNovaSenha(String novaSenha) { this.novaSenha = novaSenha; }
    public String getConfirmaNovaSenha() { return confirmaNovaSenha; }
    public void setConfirmaNovaSenha(String confirmaNovaSenha) { this.confirmaNovaSenha = confirmaNovaSenha; }


	private void redirecionarParaLogin() {
	    FacesContext context = FacesContext.getCurrentInstance();
	    try {
	        // Se for uma alteração de perfil e o usuário já estava logado, 
	        // é uma boa prática invalidar a sessão atual para ele logar de novo
	        if (loginBean != null && loginBean.getUsuarioLogado() != null) {
	            context.getExternalContext().invalidateSession();
	        }
	
	        // Faz o redirecionamento HTTP correto que o PrimeFaces/Ajax conseguem entender
	        String urlLogin = context.getExternalContext().getRequestContextPath() + "/Login.xhtml?faces-redirect=true";
	        context.getExternalContext().redirect(urlLogin);
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

}