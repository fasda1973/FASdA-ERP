package br.com.fasda.erp.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import br.com.fasda.erp.model.Cliente;

public class ClienteRepository implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public ClienteRepository() {

	}

	public ClienteRepository(EntityManager manager) {
		this.manager = manager;
	}

	public Cliente porId(Long id) {
		return manager.find(Cliente.class, id);
	}

	public List<Cliente> pesquisar(String nome) {
		String jpql = "from Cliente where nomeCliente like :nomeCliente";
		
		TypedQuery<Cliente> query = manager.createQuery(jpql,Cliente.class);
		
		query.setParameter("nomeCliente", nome + "%");

		return query.getResultList();
	}
	
	public List<Cliente> todos() {		
		return manager.createQuery("from Cliente",Cliente.class).getResultList();
	}

	public Cliente guardar(Cliente cliente) {
		return manager.merge(cliente);
	}

	public void remover(Cliente cliente) {
		cliente = porId(cliente.getId());
		manager.remove(cliente);
	}
}
