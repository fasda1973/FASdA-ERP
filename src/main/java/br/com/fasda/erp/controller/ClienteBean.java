package br.com.fasda.erp.controller;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.fasda.erp.model.DadosCliente;
import br.com.fasda.erp.model.Pessoa;
import br.com.fasda.erp.model.PessoaFisica;
import br.com.fasda.erp.model.PessoaJuridica;
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
    
    private String tipoPessoa;

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
        // Clientes geralmente são Pessoa Física
        this.entidade = new PessoaFisica();
        this.setTipoPessoa("FISICA");
        this.entidade.setCliente(true); // Já marca o papel automaticamente
    }
    
    @Override
    public void prepararEdicao() {
    	// Sincroniza a variável de controle com o tipo real do objeto
        if (this.entidade instanceof PessoaJuridica) {
            this.setTipoPessoa("JURIDICA");
        } else {
            this.setTipoPessoa("FISICA");
        }
        
        // Garante que os dados auxiliares não estejam nulos
        if (this.entidade.getDadosCliente() == null) {
            DadosCliente dados = new DadosCliente();
            dados.setPessoa(this.entidade);
            this.entidade.setDadosCliente(dados);
        }       
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
    	try {
            pessoaService.excluir(entidade);
            pesquisar(); // Atualiza a tabela após excluir
            messages.info("Registro excluído com sucesso!");
        } catch (NegocioException e) {
            messages.error(e.getMessage());
        }    	
    }
    
    @Override
    protected Object getEntidadeId(Pessoa pessoa) {
        return pessoa.getId();
    }
    
    public String getTipoPessoa() {
        return tipoPessoa;
    }
    
    public void setTipoPessoa(String tipoPessoa) {
    	this.tipoPessoa = tipoPessoa;        
    }
}