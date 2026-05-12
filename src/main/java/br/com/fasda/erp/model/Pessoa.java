package br.com.fasda.erp.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "pessoa")
public abstract class Pessoa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;
    
    @Column(name = "email")
    private String email;

    // Flags que definem o que essa pessoa representa no sistema
    @Column(name = "is_cliente")
    private boolean cliente;

    @Column(name = "is_fornecedor")
    private boolean fornecedor;

    @Column(name = "is_funcionario")
    private boolean funcionario;

    @OneToOne(mappedBy = "pessoa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DadosCliente dadosCliente;
	
    // Getters e Setters...
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public boolean isCliente() {
		return cliente;
	}

	public void setCliente(boolean cliente) {
		this.cliente = cliente;
	}

	public boolean isFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(boolean fornecedor) {
		this.fornecedor = fornecedor;
	}

	public boolean isFuncionario() {
		return funcionario;
	}

	public void setFuncionario(boolean funcionario) {
		this.funcionario = funcionario;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public DadosCliente getDadosCliente() {
		return dadosCliente;
	}

	public void setDadosCliente(DadosCliente dadosCliente) {
		this.dadosCliente = dadosCliente;
	}
	
	public String getDocumento() {
	    if (this instanceof PessoaJuridica) {
	        return ((PessoaJuridica) this).getCnpj();
	    } else if (this instanceof PessoaFisica) {
	        return ((PessoaFisica) this).getCpf();
	    }
	    return "";
	}
   
}