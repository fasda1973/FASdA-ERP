package br.com.fasda.erp.enums;

public enum PerfilUsuario {
    ADMINISTRADOR("Administrador"),
    COMUM("Usuário Comum");

    private String descricao;

    PerfilUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}