package br.com.fasda.erp.model;

import java.io.Serializable;

//O <ID> permite que cada classe decida se o ID é Long, String, Integer, etc.
public interface BaseEntity<ID> extends Serializable {
	ID getId();
	String getNome();
}