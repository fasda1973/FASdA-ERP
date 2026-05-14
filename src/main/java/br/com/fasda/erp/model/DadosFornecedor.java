package br.com.fasda.erp.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "dados_fornecedor")
public class DadosFornecedor implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    private Long id;
    
    @OneToOne
    @MapsId // Faz com que o ID desta tabela seja o mesmo ID da tabela Pessoa
    @JoinColumn(name = "pessoa_id")
    private Pessoa pessoa;
    
    @Column(name = "prazo_pagamento")
    private Long prazoPagamento;
    
    @OneToOne(mappedBy = "pessoa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DadosFornecedor dadosFornecedor;

    // Getters e Setters
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public Long getPrazoPagamento() {
		return prazoPagamento;
	}

	public void setPrazoPagamento(Long prazoPagamento) {
		this.prazoPagamento = prazoPagamento;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public DadosFornecedor getDadosFornecedor() {
		return dadosFornecedor;
	}

	public void setDadosFornecedor(DadosFornecedor dadosFornecedor) {
		this.dadosFornecedor = dadosFornecedor;
	}
    
}