package br.com.fasda.erp.model;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name = "usuario")
public class Usuario implements BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank // Garante que não seja nulo nem vazio ("")
    @Column(name = "nome_Usuario", nullable = false, unique = true, length = 20)
    private String nomeUsuario;

    @Column(nullable = false)
    private String senha;
    
    @Column(name = "foto_caminho")
    private String fotoCaminho; // Este nome deve ser igual ao que você usou no set
    
    @Column(name = "nome")
    private String nome;

	// Getters e Setters
	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	public Long getId() {
	    return id;
	}

	public void setId(Long id) {
	    this.id = id;
	}
	
	public String getFotoCaminho() {
		return fotoCaminho;
	}

	public void setFotoCaminho(String fotoCaminho) {
		this.fotoCaminho = fotoCaminho;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
    
}