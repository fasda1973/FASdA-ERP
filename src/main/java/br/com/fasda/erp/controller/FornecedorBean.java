package br.com.fasda.erp.controller;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.fasda.erp.model.Pessoa;
import br.com.fasda.erp.model.PessoaJuridica;
import br.com.fasda.erp.repository.PessoaRepository;
import br.com.fasda.erp.service.PessoaService;
import br.com.fasda.erp.util.NegocioException;

@Named
@ViewScoped
public class FornecedorBean extends CrudBean<Pessoa> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private PessoaService service;
    
    @Inject
    private PessoaRepository repository;

    public FornecedorBean() {
        super(Pessoa.class);
    }

    @Override
    public void pesquisar() {
        // Filtra para exibir apenas fornecedores
        this.listaItens = repository.listarFornecedores();
    }

    @Override
    public void prepararNovo() {
        // Fornecedores geralmente são Pessoa Jurídica
        this.entidade = new PessoaJuridica();
        this.setTipoPessoa("JURIDICA");
        this.entidade.setFornecedor(true);
    }
    
    @Override
    public void prepararEdicao() {
        
    }

    @Override
    public void salvar() {
        try {
            entidade.setFornecedor(true);
            service.salvar(entidade);
            messages.info("Fornecedor salvo com sucesso!");
            prepararNovo();
        } catch (NegocioException e) {
            messages.error(e.getMessage());
        }
    }
    
    @Override
    public void excluir() {
    	
    }
    
    @Override
    protected Object getEntidadeId(Pessoa pessoa) {
        return pessoa.getId();
    }
    
    public void setTipoPessoa(String tipoPessoa) {
        
    }
}