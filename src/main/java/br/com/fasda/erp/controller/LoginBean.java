package br.com.fasda.erp.controller;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.fasda.erp.model.Usuario;
import br.com.fasda.erp.repository.UsuarioRepository;

@Named
@SessionScoped
public class LoginBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Inject
    private UsuarioRepository usuarios; // Injeta o novo repositório
    
    private String nomeUsuario;
    private String senha;
    private Usuario usuarioLogado; // Guarda o objeto completo do banco

    public String login() {
    	
    	// Busca no banco
        this.usuarioLogado = usuarios.porLogin(nomeUsuario, senha);
    	
        // Lógica simples para exemplo (você pode buscar no banco depois)
    	if (this.usuarioLogado != null) {
            return "/Dashboard?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuário ou senha inválidos", null));
            return null;
        }
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/Login?faces-redirect=true";
    }
    
    // Getters e Setters para nomeUsuario e senha

	public UsuarioRepository getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(UsuarioRepository usuarios) {
		this.usuarios = usuarios;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Usuario getUsuarioLogado() {
		return usuarioLogado;
	}

	public void setUsuarioLogado(Usuario usuarioLogado) {
		this.usuarioLogado = usuarioLogado;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}