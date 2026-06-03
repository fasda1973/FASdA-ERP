package br.com.fasda.erp.repository; // Ou .dao, dependendo da sua estrutura

import br.com.fasda.erp.model.Configuracao;
import java.io.Serializable;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;

public class ConfiguracaoRepository implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private EntityManager manager; // Injeção padrão do seu produtor do CDI

    // Busca uma configuração específica pela chave primária
    public Configuracao buscarPorChave(String chave) {
        return manager.find(Configuracao.class, chave);
    }

    // Busca todas as configurações para carregar na memória no startup
    public List<Configuracao> listarTodas() {
        return manager.createQuery("from Configuracao", Configuracao.class).getResultList();
    }

    // O "salvar" com EntityManager precisa ser inteligente: 
    // Se o registro já existe, atualiza (merge). Se for novo, insere (persist).
    public void salvar(Configuracao configuracao) {
        if (buscarPorChave(configuracao.getChave()) != null) {
            manager.merge(configuracao);
        } else {
            manager.persist(configuracao);
        }
    }
}