package br.com.fasda.erp.model;

import java.io.Serializable;

public interface BaseEntity extends Serializable {
    Long getId();
    String getNome(); // Se um produto tiver 'getDescricao', adapte para retornar o campo de texto principal
}