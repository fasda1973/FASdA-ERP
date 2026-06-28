package br.com.fasda.erp.controller;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import br.com.fasda.erp.model.Usuario;
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
                
                // Criptografa a senha do novo usuário
                usuario.setSenha(SenhaUtil.criptografar(novaSenha));
                
                // Salva o novo registro
                manager.persist(usuario);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuário cadastrado com sucesso!"));
                
                // Opcional: Redirecionar para tela de login ou limpar o formulário
                this.usuario = new Usuario(); 
                
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
                    
                    usuario.setSenha(SenhaUtil.criptografar(novaSenha));
                }

                // Salva alterações do usuário existente
                manager.merge(usuario);
                
                if (loginBean != null && loginBean.getUsuarioLogado() != null) {
                    loginBean.getUsuarioLogado().setNome(usuario.getNome());
                }
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Perfil atualizado com sucesso!"));
            }

            // Limpa os campos de senha da tela pós-execução
            this.senhaAtual = null;
            this.novaSenha = null;
            this.confirmaNovaSenha = null;
            
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
}