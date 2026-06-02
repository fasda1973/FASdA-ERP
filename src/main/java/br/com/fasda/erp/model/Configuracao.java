package br.com.fasda.erp.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "configuracao")
public class Configuracao implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "chave", length = 50)
    private String chave;

    @Column(name = "valor", nullable = false, length = 255)
    private String valor;

    @Column(name = "descricao", length = 255)
    private String descricao;

    // Construtor padrão obrigatório pelo Hibernate
    public Configuracao() {
    }

    // Construtor auxiliar para facilitar a criação de objetos
    public Configuracao(String chave, String valor, String descricao) {
        this.chave = chave;
        this.valor = valor;
        this.descricao = descricao;
    }

    // Getters e Setters
    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    // Equals e HashCode baseados na Chave Primária (chave)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Configuracao that = (Configuracao) o;
        return Objects.equals(chave, that.chave);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chave);
    }

    @Override
    public String toString() {
        return "Configuracao{" +
                "chave='" + chave + '\'' +
                ", valor='" + valor + '\'' +
                '}';
    }
}