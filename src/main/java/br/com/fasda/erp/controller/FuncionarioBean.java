package br.com.fasda.erp.controller;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.fasda.erp.model.DadosFuncionario;
import br.com.fasda.erp.model.Pessoa;
import br.com.fasda.erp.model.PessoaFisica;
import br.com.fasda.erp.model.PessoaJuridica;
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
    
    private String tipoPessoa;
    
    @Inject
    private LoginBean loginBean; // Injeta o seu bean de login/sessão

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
        // Aqui está o segredo:
        this.entidade.setDadosFuncionario(new DadosFuncionario());
        this.setTipoPessoa("FISICA");
        this.entidade.setFuncionario(true); // Já marca o papel automaticamente
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
        if (this.entidade.getDadosFuncionario() == null) {
            DadosFuncionario dados = new DadosFuncionario();
            dados.setPessoa(this.entidade);
            this.entidade.setDadosFuncionario(dados);
        }           
    }

    @Override
    public void salvar() {
        try {
            entidade.setFuncionario(true); // Garantia extra
            
        	String loginDoUsuario = "SISTEMA"; // Valor padrão de segurança
        	
        	// 1. Verifica se o loginBean e o usuário logado não estão nulos
            if (loginBean != null && loginBean.getUsuarioLogado() != null) {
                
                // 2. Pega o login (se na sua classe Usuario o método for getLogin())
                loginDoUsuario = loginBean.getUsuarioLogado().getNomeUsuario();
            }
            
            pessoaService.salvar(entidade, "Cadastro de Funcionários", loginDoUsuario);
            pesquisar(); // Atualiza a tabela
            messages.info("Funcionário salvo com sucesso!");
            prepararNovo();
        } catch (NegocioException e) {
            messages.error(e.getMessage());
        }
    }
    
    @Override
    public void excluir() {
    	try {
    		String loginDoUsuario = "SISTEMA"; // Valor padrão de segurança
    		
    		loginDoUsuario = loginBean.getUsuarioLogado().getNomeUsuario();
    		
            pessoaService.excluir(entidade, "Cadastro de Funcionários", loginDoUsuario);
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