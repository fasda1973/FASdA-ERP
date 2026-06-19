package br.com.fasda.erp.model;

import java.io.Serializable;

public class PermissaoNo implements Serializable {
    private String nomeExibicao; // Ex: "Ver", "Clientes"
    private String chaveConfig;   // Ex: "COMUM_CLIENTES_VER" (Vazio se for nó pai)

    public PermissaoNo(String nomeExibicao, String chaveConfig) {
        this.nomeExibicao = nomeExibicao;
        this.chaveConfig = chaveConfig;
    }

    public String getNomeExibicao() { return nomeExibicao; }
    public String getChaveConfig() { return chaveConfig; }

    @Override
    public String toString() {
        return nomeExibicao; // O p:tree usa o toString para renderizar o texto padrão
    }
}