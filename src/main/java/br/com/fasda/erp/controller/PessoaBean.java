package br.com.fasda.erp.controller;

import java.io.Serializable;
import java.util.Arrays;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import br.com.fasda.erp.model.DadosCliente;
import br.com.fasda.erp.model.DadosFornecedor;
import br.com.fasda.erp.model.DadosFuncionario;
import br.com.fasda.erp.model.Pessoa;
import br.com.fasda.erp.model.PessoaFisica;
import br.com.fasda.erp.model.PessoaJuridica;
import br.com.fasda.erp.repository.PessoaRepository;
import br.com.fasda.erp.service.PessoaService;
import br.com.fasda.erp.util.NegocioException;

@Named
@ViewScoped
public class PessoaBean extends CrudBean<Pessoa> implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
    @Inject
    private PessoaService service; // O Service a gente injeta aqui
    
    @Inject
    private PessoaRepository repository;
    
    @Inject
    private LoginBean loginBean; // Injeta o seu bean de login/sessão
    
    private String tipoPessoa = "FISICA";
    
    public PessoaBean() {
        // Passamos a classe Pessoa para o CrudBean
        super(Pessoa.class);
    }
    
    // --- MÉTODOS OBRIGATÓRIOS (OVERRIDE) ---
    
    @Override
    public void pesquisar() {   	
    	if (termoPesquisa == null || termoPesquisa.trim().isEmpty()) {
    		this.listaItens = repository.todas(); // Traz tudo se não houver filtro
    	} else {
            this.listaItens = repository.pesquisar(this.termoPesquisa);
        }
	}
    
    @Override
    public void salvar() {
        try {
        	// Define que essa pessoa terá o papel de cliente
            //getEntidade().setCliente(true);
        	
        	String loginDoUsuario = "SISTEMA"; // Valor padrão de segurança
        	
        	// 1. Verifica se o loginBean e o usuário logado não estão nulos
            if (loginBean != null && loginBean.getUsuarioLogado() != null) {
                
                // 2. Pega o login (se na sua classe Usuario o método for getLogin())
                loginDoUsuario = loginBean.getUsuarioLogado().getNomeUsuario();
            }
            
            // Chama o seu service especializado
            service.salvar(getEntidade(), "Cadastro de Pessoas", loginDoUsuario);
            atualizarRegistros();
            
            messages.info("Pessoa salva com sucesso!");
            prepararNovo(); // Limpa o formulário
            
            PrimeFaces.current().ajax().update(Arrays.asList("frm:dataTable"));
            
        } catch (NegocioException e) {
            messages.error(e.getMessage());
        }
    }
    
    @Override
    public void excluir() {
    	try {
	    	service.excluir(this.entidade);
	        this.entidade = null;
	        atualizarRegistros(); // Atualiza a lista após remover
	        messages.info("Pessoa excluída com sucesso!");
    	} catch (NegocioException e) {
            messages.error(e.getMessage());
        }
    }
    
    @Override
    public void prepararNovo() {

        // 1. Instancia o tipo correto baseado na seleção atual
        if ("JURIDICA".equals(this.tipoPessoa)) {
            this.entidade = new PessoaJuridica();
        } else {
            this.entidade = new PessoaFisica();
            this.tipoPessoa = "FISICA"; // Garante consistência
        }

        // 2. Instancia o objeto de detalhes (essencial para evitar Target Unreachable)
        DadosCliente dados = new DadosCliente();
        
        // 3. Faz a ligação bidirecional (Pai aponta pro Filho e Filho pro Pai)
        dados.setPessoa(this.entidade);
        this.entidade.setDadosCliente(dados);
        
        DadosFuncionario dadosFuncionario = new DadosFuncionario();
        
        dadosFuncionario.setPessoa(this.entidade);
        this.entidade.setDadosFuncionario(dadosFuncionario);
        
        DadosFornecedor dadosFornecedor = new DadosFornecedor();
        
        dadosFornecedor.setPessoa(this.entidade);
        this.entidade.setDadosFornecedor(dadosFornecedor);
        
        System.out.println("Instanciando entidade para Tipo: " + this.tipoPessoa);
    	
    }

    // Chamado pelo <p:ajax> quando o rádio button muda
    // Chamado quando o usuário troca o botão de rádio na tela
    public void alternarTipoPessoa() {
        prepararNovo();
    }
    
    @Override
    public void prepararEdicao() {
    	
    	// 1. Verifica qual é a classe real da entidade que veio da tabela
        if (this.entidade instanceof PessoaJuridica) {
            this.tipoPessoa = "JURIDICA";
        } else if (this.entidade instanceof PessoaFisica) {
            this.tipoPessoa = "FISICA";
        }

        // 2. Garante que os DadosCliente não sejam nulos para evitar erro nos campos de limite/crédito
        if (this.entidade.getDadosCliente() == null) {
            DadosCliente dados = new DadosCliente();
            dados.setPessoa(this.entidade);
            this.entidade.setDadosCliente(dados);
        }
        
        if (this.entidade.getDadosFuncionario() == null) {
        	DadosFuncionario dadosFuncionario = new DadosFuncionario();
        	dadosFuncionario.setPessoa(this.entidade);
            this.entidade.setDadosFuncionario(dadosFuncionario);
        }
        
        if (this.entidade.getDadosFornecedor() == null) {
        	DadosFornecedor dadosFornecedor = new DadosFornecedor();
        	dadosFornecedor.setPessoa(this.entidade);
            this.entidade.setDadosFornecedor(dadosFornecedor);
        }
        
        System.out.println("Editando Entidade Tipo: " + this.tipoPessoa);
        
    }

    @Override
    protected Object getEntidadeId(Pessoa pessoa) {
        return pessoa.getId();
    }
    
    public void todasPessoas() {
        this.listaItens = repository.todas();
    }
    
    private void atualizarRegistros() {
        if (jaHouvePesquisa()) {
            pesquisar();
        } else {
            todasPessoas();
        }
    }
    
    private boolean jaHouvePesquisa() {
        return termoPesquisa != null && !termoPesquisa.isEmpty();
    }

    @Override
    public Pessoa getEntidade() {
        return this.entidade;
    }
    
    // Adicione estes dois métodos no final do seu PessoaBean.java
    public String getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(String tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }
    
    
}