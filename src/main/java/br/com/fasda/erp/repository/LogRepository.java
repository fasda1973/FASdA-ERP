package br.com.fasda.erp.repository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import br.com.fasda.erp.model.LogAuditoria;
import br.com.fasda.erp.util.Transacional; // Seu interceptador customizado
import java.io.Serializable;

public class LogRepository implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private EntityManager manager;

    @Transacional // Garante que o interceptador abra uma transação aqui
    public void salvar(LogAuditoria log) {
        this.manager.persist(log);
        this.manager.flush(); 
    }
}