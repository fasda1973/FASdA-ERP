package br.com.fasda.erp.controller;

import java.io.Serializable;
import java.util.Arrays;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import br.com.fasda.erp.model.DadosCliente;
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
    
    private String tipoPessoa;
    
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
            getEntidade().setCliente(true);            
            
            // Chama o seu service especializado
            service.salvar(getEntidade());
            
            messages.info("Pessoa salva com sucesso!");
            prepararNovo(); // Limpa o formulário
            
            PrimeFaces.current().ajax().update(Arrays.asList("formCadastro:dataTable", "formCadastro:messages", "formCadastro:msgs"));
            
        } catch (NegocioException e) {
            messages.error(e.getMessage());
        }
    }
    
    @Override
    public void excluir() {
    	service.excluir(this.entidade);
        this.entidade = null;
        atualizarRegistros(); // Atualiza a lista após remover
        messages.info("Pessoa excluída com sucesso!");
    }
    
    @Override
    public void prepararNovo() {
    	
    	System.out.println("Instanciando entidade para Tipo: " + this.tipoPessoa);

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
    	
    }

    // Chamado pelo <p:ajax> quando o rádio button muda
    // Chamado quando o usuário troca o botão de rádio na tela
    public void alternarTipoPessoa() {
        prepararNovo();
    }
    
    @Override
    public void prepararEdicao() {
        
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