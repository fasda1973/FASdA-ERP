package br.com.fasda.erp.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import br.com.fasda.erp.model.ClienteOld;
import br.com.fasda.erp.model.Pessoa;

public class PessoaRepository implements Serializable {
	
    private static final long serialVersionUID = 1L;

    @Inject
    private EntityManager manager;
    
    public PessoaRepository() {
    	
    }
    
    public PessoaRepository(EntityManager manager) {
		this.manager = manager;
	}

    public Pessoa porId(Long id) {
        return manager.find(Pessoa.class, id);
    }
    
    public List<Pessoa> pesquisar(String nome) {
		String jpql = "from Pessoa where nome like :nome";
		
		TypedQuery<Pessoa> query = manager.createQuery(jpql,Pessoa.class);
		
		query.setParameter("nome", nome + "%");

		return query.getResultList();
	}
    
    // Busca todas as pessoas, independente de serem PF ou PJ
    public List<Pessoa> todas() {
        return manager.createQuery("from Pessoa", Pessoa.class).getResultList();
    }
    
    public List<Pessoa> listarClientes() {
        return manager.createQuery("from Pessoa p where p.isCliente = true", Pessoa.class).getResultList();
    }

    public List<Pessoa> listarFuncionarios() {
        return manager.createQuery("from Pessoa p where p.isFuncionario = true", Pessoa.class).getResultList();
    }

    public Pessoa guardar(Pessoa pessoa) {
        return manager.merge(pessoa);
    }

    public void remover(Pessoa pessoa) {
        pessoa = porId(pessoa.getId());
        manager.remove(pessoa);
    }
    
    public boolean verificarCpfExistente(String cpf, Long idAtual) {
        try {
            // Ajustamos a query para ignorar o ID da pessoa que está sendo editada
            StringBuilder jpql = new StringBuilder("select count(p) from PessoaFisica p where p.cpf = :cpf");
            
            if (idAtual != null) {
                jpql.append(" and p.id != :idAtual");
            }

            TypedQuery<Long> query = manager.createQuery(jpql.toString(), Long.class)
                    .setParameter("cpf", cpf);
            
            if (idAtual != null) {
                query.setParameter("idAtual", idAtual);
            }

            Long count = query.getSingleResult();
            
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean verificarCnpjExistente(String cnpj, Long idAtual) {
        try {
            // Ajustamos a query para ignorar o ID da pessoa que está sendo editada
            StringBuilder jpql = new StringBuilder("select count(p) from PessoaJuridica p where p.cnpj = :cnpj");
            
            if (idAtual != null) {
                jpql.append(" and p.id != :idAtual");
            }

            TypedQuery<Long> query = manager.createQuery(jpql.toString(), Long.class)
                    .setParameter("cnpj", cnpj);
            
            if (idAtual != null) {
                query.setParameter("idAtual", idAtual);
            }

            Long count = query.getSingleResult();
            
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
