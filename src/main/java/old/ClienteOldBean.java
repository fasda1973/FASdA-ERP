package old;

import java.util.Arrays;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import br.com.fasda.erp.controller.CrudBean;
import br.com.fasda.erp.model.Pessoa;
import br.com.fasda.erp.util.NegocioException;

@Named
@ViewScoped
public class ClienteOldBean extends CrudBean<ClienteOld> {
    
	private static final long serialVersionUID = 1L;

	@Inject
    private ClienteServiceOld clienteServiceOld;

    @Inject
    private ClienteRepositoryOld clientesRepository;
    
    public ClienteOldBean() {
        // Passamos a classe ClienteOld para o CrudBean
        super(ClienteOld.class);
    }
    
    // --- MÉTODOS OBRIGATÓRIOS (OVERRIDE) ---
    
    @Override
    public void pesquisar() {   	
    	if (termoPesquisa == null || termoPesquisa.trim().isEmpty()) {
    		this.listaItens = clientesRepository.todos(); // Traz tudo se não houver filtro
    	} else {
            this.listaItens = clientesRepository.pesquisar(this.termoPesquisa);
        }
	}
           
    @Override
    public void salvar() { 
    	try{
    		
	    	// Usamos 'entidade' que vem do CrudBean (substitui 'cliente')
	        clienteServiceOld.salvar(this.entidade);
	        atualizarRegistros();
	        messages.info("ClienteOld salvo com sucesso!");
	                
	        //PrimeFaces.current().ajax().update(Arrays.asList("frm:dataTable"));
	    
    	} catch (NegocioException e) {
    		// Erro de regra de negócio amigável
            messages.error(e.getMessage());
    	} catch (Exception e) {
    		// Erro inesperado
            messages.error("Ocorreu um erro inesperado ao salvar.");
    	}
    }
    
    @Override
    public void excluir() {
    	clienteServiceOld.excluir(this.entidade);
        this.entidade = null;
        atualizarRegistros(); // Atualiza a lista após remover
        messages.info("ClienteOld excluído com sucesso!");
    }
    
    @Override
    public void prepararNovo() {
        this.entidade = new ClienteOld();
    }
    
    @Override
    public void prepararEdicao() {
        
    }
    
    @Override
    protected Object getEntidadeId(ClienteOld clienteOld) {
        return clienteOld.getId();
    }
    
    public void todosClientes() {
        this.listaItens = clientesRepository.todos();
    }
    
    private void atualizarRegistros() {
        if (jaHouvePesquisa()) {
            pesquisar();
        } else {
            todosClientes();
        }
    }

    private boolean jaHouvePesquisa() {
        return termoPesquisa != null && !termoPesquisa.isEmpty();
    }
    
}