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
    
    // --- NOVOS CAMPOS ---
    @Column(name = "chave_pix", length = 100)
    private String chavePix;

    @Column(name = "banco_dados_bancarios", length = 100)
    private String bancoDadosBancarios;

    @Column(name = "email_xml_nfe", length = 100)
    private String emailXmlNfe;

    @Column(name = "avaliacao_qualidade", length = 20)
    private String avaliacaoQualidade;
    
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

	public String getChavePix() {
		return chavePix;
	}

	public void setChavePix(String chavePix) {
		this.chavePix = chavePix;
	}

	public String getBancoDadosBancarios() {
		return bancoDadosBancarios;
	}

	public void setBancoDadosBancarios(String bancoDadosBancarios) {
		this.bancoDadosBancarios = bancoDadosBancarios;
	}

	public String getEmailXmlNfe() {
		return emailXmlNfe;
	}

	public void setEmailXmlNfe(String emailXmlNfe) {
		this.emailXmlNfe = emailXmlNfe;
	}

	public String getAvaliacaoQualidade() {
		return avaliacaoQualidade;
	}

	public void setAvaliacaoQualidade(String avaliacaoQualidade) {
		this.avaliacaoQualidade = avaliacaoQualidade;
	}
	
	// Método para a tela ler as estrelas (Conversão de Letra para Número)
	public Integer getAvaliacaoEstrelas() {
	    if ("A".equals(this.avaliacaoQualidade)) return 5; // Excelente
	    if ("B".equals(this.avaliacaoQualidade)) return 3; // Regular
	    if ("C".equals(this.avaliacaoQualidade)) return 1; // Ruim
	    return 0; // Não avaliado
	}

	// Método para a tela salvar as estrelas (Conversão de Número para Letra)
	public void setAvaliacaoEstrelas(Integer estrelas) {
	    if (estrelas == null || estrelas == 0) this.avaliacaoQualidade = null;
	    else if (estrelas >= 4) this.avaliacaoQualidade = "A";
	    else if (estrelas == 3) this.avaliacaoQualidade = "B";
	    else this.avaliacaoQualidade = "C";
	}
    
}