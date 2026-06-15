package br.com.fasda.erp.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import br.com.fasda.erp.model.Usuario;
import br.com.fasda.erp.util.AuditoriaUtil;
import br.com.fasda.erp.util.SenhaUtil;
import br.com.fasda.erp.util.Transacional;

public class UsuarioRepository implements Serializable {
	
    private static final long serialVersionUID = 1L;

    @Inject
    private EntityManager manager;
    
    @Inject
    private LogRepository logRepository; // 1. Injetar o repositório de logs
    
    public UsuarioRepository() {

	}
    
    public UsuarioRepository(EntityManager manager) {
		this.manager = manager;
	}
    
    public Usuario porId(Long id) {
        return manager.find(Usuario.class, id);
    }
    
    public List<Usuario> pesquisar(String nome) {
		String jpql = "from Usuario where login like :login";
		
		TypedQuery<Usuario> query = manager.createQuery(jpql,Usuario.class);
		
		query.setParameter("login", nome + "%");

		return query.getResultList();
	}
    
    public List<Usuario> todos() {
        return manager.createQuery("from Usuario", Usuario.class).getResultList();
    }
    
    public Usuario guardarComAuditoria(Usuario usuario, String origemTela, String usuarioLogado) {
        // É aqui dentro que a mágica da persistência com auditoria se consolida!
        return AuditoriaUtil.salvarComAuditoria(usuario, manager, logRepository, origemTela, usuarioLogado);
    }
    
    @Transacional
    public Usuario guardar(Usuario usuario) {
        // Como é uma inserção, precisamos de uma transação se não estiver usando @Transactional
        return manager.merge(usuario);
    }
    
    public void removerComAuditoria(Usuario usuario, String origemTela, String usuarioLogado) {
    	AuditoriaUtil.removerComAuditoria(usuario, Usuario.class, manager, logRepository, origemTela, usuarioLogado);
    }
    
    public void remover(Usuario usuario) {
    	usuario = porId(usuario.getId());
		manager.remove(usuario);
    }
    
    /* PRA TELA DE LOGIN (CUIDADO AO MODIFICAR) */
    public Usuario porLogin(String login, String senha) {
        try {           
        	// 1. Busque o usuário no banco usando apenas o login/username
        	Usuario usuario = manager.createQuery("from Usuario where login = :login", Usuario.class)
                .setParameter("login", login)
                .getSingleResult();
        	
        	// 2. Valide a senha usando a nossa utilitária
        	if (usuario != null && SenhaUtil.verificar(senha, usuario.getSenha())) {
        	    // Login com sucesso!
        		return usuario;
        	} else {
        	    // Usuário ou senha inválidos
        		return null;
        	}
        	
        } catch (NoResultException e) {
            return null; // Usuário ou senha incorretos
        }
    }

	public String buscarSenhaAtual(Long id) {
		return manager.createQuery("select u.senha from Usuario u where u.id = :id", String.class)
				.setParameter("id", id)
	            .getSingleResult();
	}
	
	public boolean existeLogin(String login, Long idAtual) {
	    String jpql = "select count(u) from Usuario u where u.login = :login";
	    if (idAtual != null) {
	        jpql += " and u.id != :id";
	    }
	    
	    TypedQuery<Long> query = manager.createQuery(jpql, Long.class);
	    query.setParameter("login", login);
	    if (idAtual != null) {
	        query.setParameter("id", idAtual);
	    }
	    
	    return query.getSingleResult() > 0;
	}
	
	// DashBoard
	public Long contarTodos() {
	    return manager.createQuery("select count(u) from Usuario u", Long.class)
	                  .getSingleResult();
	}
    
}