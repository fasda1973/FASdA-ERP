package br.com.fasda.erp.controller;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.fasda.erp.model.Cliente;
import br.com.fasda.erp.model.DadosCliente;
import br.com.fasda.erp.model.Pessoa;
import br.com.fasda.erp.model.PessoaFisica;
import br.com.fasda.erp.model.PessoaJuridica;
import br.com.fasda.erp.repository.PessoaRepository;
import br.com.fasda.erp.service.PessoaService;
import br.com.fasda.erp.util.NegocioException;

@Named
@ViewScoped
public class PessoaBean extends CrudBean<Pessoa> {
	
    private static final long serialVersionUID = 1L;
    
    @Inject
    private PessoaService service; // O Service a gente injeta aqui
    
    @Inject
    private PessoaRepository repository;
    
    private String tipoPessoa = "FISICA"; // Padrão inicial
    
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
    	
    	// Se o tipoPessoa for nulo (primeira vez que abre o diálogo)
        if (tipoPessoa == null) {
            tipoPessoa = "FISICA"; // Default para não vir vazio
        }
    	
        // Sobrescrita necessária para decidir qual "tipo" de pessoa instanciar
        if ("FISICA".equals(tipoPessoa)) {
        	if (!(entidade instanceof PessoaFisica)) {
                this.entidade = new PessoaFisica();
            }
        } else if ("JURIDICA".equals(tipoPessoa)) {
        	if (!(entidade instanceof PessoaJuridica)) {
                this.entidade = new PessoaJuridica();
            }
        }
        
        // Inicializa os detalhes para evitar o erro de 'null' na aba de limites
        if (this.entidade.getDadosCliente() == null) {
            this.entidade.setDadosCliente(new DadosCliente());
            this.entidade.getDadosCliente().setPessoa(this.entidade);
        }
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

    // Getter e Setter para o tipoPessoa (para o Radio Button)
    public Pessoa getEntidade() { return entidade; }
    public String getTipoPessoa() { return tipoPessoa; }
    public void setTipoPessoa(String tipoPessoa) { this.tipoPessoa = tipoPessoa; }
}