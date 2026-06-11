package old;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class RamoAtividades implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public RamoAtividades() {

	}

	public RamoAtividades(EntityManager manager) {
		this.manager = manager;
	}

	public List<RamoAtividade> pesquisar(String descricao) {
		// CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();

		// CriteriaQuery<RamoAtividade> criteriaQuery =
		// criteriaBuilder.createQuery(RamoAtividade.class);

		// Root<RamoAtividade> root = criteriaQuery.from(RamoAtividade.class);

		// criteriaQuery.select(root);

		// criteriaQuery.where(criteriaBuilder.like(root.get("descricao"), descricao +
		// "%"));

		// TypedQuery<RamoAtividade> query = manager.createQuery(criteriaQuery);

		// return query.getResultList();
		String jpql = "from RamoAtividade where descricao like :descricao";

		TypedQuery<RamoAtividade> query = manager.createQuery(jpql, RamoAtividade.class);

		query.setParameter("descricao", descricao + "%");

		return query.getResultList();
	}

}
