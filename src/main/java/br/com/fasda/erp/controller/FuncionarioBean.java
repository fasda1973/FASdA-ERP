package br.com.fasda.erp.controller;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.fasda.erp.model.Pessoa;
import br.com.fasda.erp.model.PessoaFisica;
import br.com.fasda.erp.repository.PessoaRepository;
import br.com.fasda.erp.service.PessoaService;
import br.com.fasda.erp.util.NegocioException;

@Named
@ViewScoped
public class FuncionarioBean extends CrudBean<Pessoa> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private PessoaService pessoaService;
    
    @Inject
    private PessoaRepository pessoaRepository;

    public FuncionarioBean() {
        super(Pessoa.class);
    }

    @Override
    public void pesquisar() {
        // Filtra a lista para exibir apenas funcionários
        this.listaItens = pessoaRepository.listarFuncionarios();
    }

    @Override
    public void prepararNovo() {
        // Funcionários geralmente são Pessoa Física
        this.entidade = new PessoaFisica();
        this.setTipoPessoa("FISICA");
        this.entidade.setFuncionario(true); // Já marca o papel automaticamente
    }
    
    @Override
    public void prepararEdicao() {
        
    }

    @Override
    public void salvar() {
        try {
            entidade.setFuncionario(true); // Garantia extra
            pessoaService.salvar(entidade);
            messages.info("Funcionário salvo com sucesso!");
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