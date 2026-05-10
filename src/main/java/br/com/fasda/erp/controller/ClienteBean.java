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
public class ClienteBean extends CrudBean<Pessoa> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private PessoaService pessoaService;
    
    @Inject
    private PessoaRepository pessoaRepository;

    public ClienteBean() {
        super(Pessoa.class);
    }

    @Override
    public void pesquisar() {
        // Aqui está o segredo: a tela de Clientes só pesquisa quem é Cliente
        this.listaItens = pessoaRepository.listarClientes(); 
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
            entidade.setCliente(true); // Garante que ao salvar por esta tela, seja cliente
            pessoaService.salvar(entidade);
            messages.info("Cliente salvo com sucesso!");
            prepararNovo();
        } catch (NegocioException e) {
            messages.error(e.getMessage());
        }
    }
    
    // ... implementar prepararNovo (iniciando como FISICA ou JURIDICA) e prepararEdicao
    
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