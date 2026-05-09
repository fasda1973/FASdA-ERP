package br.com.fasda.erp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.validator.constraints.br.CNPJ;

@Entity
@Table(name = "pessoa_juridica")
@PrimaryKeyJoinColumn(name = "pessoa_id")
public class PessoaJuridica extends Pessoa {

    @CNPJ // Bean Validation
    @Column(name = "cnpj", length = 18, nullable = false, unique = true)
    private String cnpj;
    
    @Column(name = "inscricao_estadual")
    private String inscricaoEstadual;

    // Getters e Setters...
	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		if (cnpj != null) {
	        this.cnpj = cnpj.replaceAll("[^0-9]", "");
	    } else {
	        this.cnpj = null;
	    }
	}

	public String getInscricaoEstadual() {
		return inscricaoEstadual;
	}

	public void setInscricaoEstadual(String inscricaoEstadual) {
		this.inscricaoEstadual = inscricaoEstadual;
	}
    
}