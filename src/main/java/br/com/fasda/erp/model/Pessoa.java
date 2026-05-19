package br.com.fasda.erp.model;

import java.io.Serializable;
import java.time.LocalDateTime;

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
    
    // --- NOVOS CAMPOS ---
    @Column(length = 20)
    private String telefone;

    @Column(length = 20)
    private String celular;

    @Column(length = 150)
    private String logradouro;

    @Column(length = 20)
    private String numero;

    @Column(length = 100)
    private String complemento;

    @Column(length = 100)
    private String bairro;

    @Column(length = 10)
    private String cep;

    @Column(length = 100)
    private String cidade;

    @Column(length = 2)
    private String estado;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro = LocalDateTime.now();

    @Column(nullable = false)
    private boolean ativo = true;

    @OneToOne(mappedBy = "pessoa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DadosCliente dadosCliente;
    
    @OneToOne(mappedBy = "pessoa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DadosFuncionario dadosFuncionario = new DadosFuncionario();
    
    @OneToOne(mappedBy = "pessoa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DadosFornecedor dadosFornecedor;
	
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
	
	public DadosFuncionario getDadosFuncionario() {
		return dadosFuncionario;
	}

	public void setDadosFuncionario(DadosFuncionario dadosFuncionario) {
		this.dadosFuncionario = dadosFuncionario;
	}
	
	public String getDocumento() {
	    if (this instanceof PessoaJuridica) {
	        return ((PessoaJuridica) this).getCnpj();
	    } else if (this instanceof PessoaFisica) {
	        return ((PessoaFisica) this).getCpf();
	    }
	    return "";
	}

	public DadosFornecedor getDadosFornecedor() {
		return dadosFornecedor;
	}

	public void setDadosFornecedor(DadosFornecedor dadosFornecedor) {
		this.dadosFornecedor = dadosFornecedor;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public LocalDateTime getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(LocalDateTime dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	
	
}