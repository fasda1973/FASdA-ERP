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
        if (loginBean.getUsuarioLogado() != null) {
            this.usuario = manager.find(Usuario.class, loginBean.getUsuarioLogado().getId());
        }
    }

    @Transacional
    public void salvarPerfil() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            // Se o usuário tentou preencher alguma informação de nova senha
            if (novaSenha != null && !novaSenha.trim().isEmpty()) {
                
                // 1. Valida se a senha atual digitada está correta
                if (!SenhaUtil.verificar(senhaAtual, usuario.getSenha())) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Senha atual incorreta!"));
                    return;
                }
                
                // 2. Valida se a nova senha coincide com a confirmação
                if (!novaSenha.equals(confirmaNovaSenha)) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "A nova senha e a confirmação não coincidem!"));
                    return;
                }
                
                // 3. Criptografa a nova senha antes de salvar
                usuario.setSenha(SenhaUtil.criptografar(novaSenha));
            }

            // Salva as alterações do usuário (Nome, etc) no banco
            manager.merge(usuario);
            
            // Atualiza o usuário na sessão do LoginBean para refletir o novo nome na topbar imediatamente
            loginBean.getUsuarioLogado().setNome(usuario.getNome());

            // Limpa os campos de senha da tela
            this.senhaAtual = null;
            this.novaSenha = null;
            this.confirmaNovaSenha = null;
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Perfil atualizado com sucesso!"));
            
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