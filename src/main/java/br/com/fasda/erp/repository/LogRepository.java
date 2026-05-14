package br.com.fasda.erp.repository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import br.com.fasda.erp.model.LogAuditoria;
import java.io.Serializable;

public class LogRepository implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private EntityManager manager;

    public void salvar(LogAuditoria log) {
        this.manager.persist(log);
    }
}