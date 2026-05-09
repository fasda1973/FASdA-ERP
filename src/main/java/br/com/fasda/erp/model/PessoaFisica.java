package br.com.fasda.erp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.validator.constraints.br.CPF;

@Entity
@Table(name = "pessoa_fisica")
@PrimaryKeyJoinColumn(name = "pessoa_id")
public class PessoaFisica extends Pessoa {

    @CPF // Bean Validation
    @Column(name = "cpf", length = 14, nullable = false, unique = true)
    private String cpf;
    
    @Column(name = "rg")
    private String rg;
    
    // Getters e Setters...
	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		if (cpf != null) {
	        // Substitui tudo que NÃO for número por nada
	        this.cpf = cpf.replaceAll("[^0-9]", "");
	    } else {
	        this.cpf = null;
	    }
	}

	public String getRg() {
		return rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}
       
}