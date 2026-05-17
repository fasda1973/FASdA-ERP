package br.com.fasda.erp.model;

import java.time.LocalDate;

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
    
    // --- NOVOS CAMPOS ---
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(length = 15)
    private String sexo; // Dica: Pode virar um Enum no futuro

    @Column(name = "estado_civil", length = 20)
    private String estadoCivil;
    
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

	public LocalDate getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(LocalDate dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getEstadoCivil() {
		return estadoCivil;
	}

	public void setEstadoCivil(String estadoCivil) {
		this.estadoCivil = estadoCivil;
	}
	
	
       
}