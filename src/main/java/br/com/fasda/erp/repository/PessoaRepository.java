package br.com.fasda.erp.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import br.com.fasda.erp.model.Cliente;
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

    public Pessoa guardar(Pessoa pessoa) {
        return manager.merge(pessoa);
    }

    public void remover(Pessoa pessoa) {
        pessoa = porId(pessoa.getId());
        manager.remove(pessoa);
    }
    
    public boolean verificarCpfExistente(String cpf) {
        try {
            // Usamos o COUNT para ser performático
            // O JPA retorna Long para resultados de count
            Long count = manager.createQuery(
                    "select count(p) from PessoaFisica p where p.cpf = :cpf", Long.class)
                    .setParameter("cpf", cpf)
                    .getSingleResult();
            
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean verificarCnpjExistente(String cnpj) {
        try {
            Long count = manager.createQuery(
                    "select count(p) from PessoaJuridica p where p.cnpj = :cnpj", Long.class)
                    .setParameter("cnpj", cnpj)
                    .getSingleResult();
            
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
