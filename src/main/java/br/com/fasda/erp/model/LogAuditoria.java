package br.com.fasda.erp.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "log_auditoria")
public class LogAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private String operacao; // Ex: "CADASTRO", "ALTERAÇÃO", "EXCLUSÃO"

    @Column(nullable = false, length = 500)
    private String detalhe; // Ex: "Pessoa cadastrada com ID: 12 e Nome: João"

    @Column(name = "usuario", nullable = false, length = 100)
    private String usuario; 

    // Construtor padrão do Hibernate
    public LogAuditoria() {}

    // Construtor utilitário para facilitar a criação do log
    public LogAuditoria(String operacao, String detalhe, String usuario) {
        this.dataHora = LocalDateTime.now(); // Pega a hora exata do servidor
        this.operacao = operacao;
        this.detalhe = detalhe;
        this.usuario = usuario;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getOperacao() { return operacao; }
    public void setOperacao(String operacao) { this.operacao = operacao; }

    public String getDetalhe() { return detalhe; }
    public void setDetalhe(String detalhe) { this.detalhe = detalhe; }

	public String getUsuario() { return usuario; }

	public void setUsuario(String usuario) { this.usuario = usuario; }
}