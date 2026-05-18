package old;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class ClienteRepositoryOld implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public ClienteRepositoryOld() {

	}

	public ClienteRepositoryOld(EntityManager manager) {
		this.manager = manager;
	}

	public ClienteOld porId(Long id) {
		return manager.find(ClienteOld.class, id);
	}

	public List<ClienteOld> pesquisar(String nome) {
		String jpql = "from ClienteOld where nomeCliente like :nomeCliente";
		
		TypedQuery<ClienteOld> query = manager.createQuery(jpql,ClienteOld.class);
		
		query.setParameter("nomeCliente", nome + "%");

		return query.getResultList();
	}
	
	public List<ClienteOld> todos() {		
		return manager.createQuery("from ClienteOld",ClienteOld.class).getResultList();
	}

	public ClienteOld guardar(ClienteOld clienteOld) {
		return manager.merge(clienteOld);
	}

	public void remover(ClienteOld clienteOld) {
		clienteOld = porId(clienteOld.getId());
		manager.remove(clienteOld);
	}
}
