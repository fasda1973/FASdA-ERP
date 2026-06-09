package br.com.fasda.erp.repository; // Ou .dao, dependendo da sua estrutura

import br.com.fasda.erp.model.Configuracao;
import br.com.fasda.erp.util.AuditoriaUtil;

import java.io.Serializable;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class ConfiguracaoRepository implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private EntityManager manager; // Injeção padrão do seu produtor do CDI
    
    @Inject
    private LogRepository logRepository; // 1. Injetar o repositório de logs

    /**
     * Busca uma configuração exata pela chave primária (String).
     * Retorna o objeto se existir, ou NULL se não encontrar nada.
     */
    public Configuracao buscarPorChave(String chave) {
        if (chave == null) {
            return null;
        }
        return manager.find(Configuracao.class, chave);
    }
    
    public List<Configuracao> pesquisarPorChave(String chave) {
        String jpql = "from Configuracao where chave like :chave";
        TypedQuery<Configuracao> query = manager.createQuery(jpql, Configuracao.class);
        query.setParameter("chave", chave + "%");
        return query.getResultList();
    }

    // Busca todas as configurações para carregar na memória no startup
    public List<Configuracao> listarTodas() {
        return manager.createQuery("from Configuracao", Configuracao.class).getResultList();
    }
    
    public Configuracao guardarComAuditoria(Configuracao configuracao, String origemTela, String usuarioLogado) {
    	return AuditoriaUtil.salvarComAuditoria(configuracao, manager, logRepository, origemTela, usuarioLogado);
    }

    /**
     * REMOVA OU DESATIVE O MÉTODO SALVAR ANTIGO
     * O 'AuditoriaUtil.salvarComAuditoria' já faz o papel inteligente do merge interno.
     * Deixar esse método concorrendo com a mesma assinatura pode gerar chamadas duplicadas no Bean.
     */
    @Deprecated
    public void salvar(Configuracao configuracao) {
        if (buscarPorChave(configuracao.getChave()) != null) {
            manager.merge(configuracao);
        } else {
            manager.persist(configuracao);
        }
    }
}