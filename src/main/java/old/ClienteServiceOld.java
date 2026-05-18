package old;

import java.io.Serializable;

import javax.inject.Inject;

import br.com.fasda.erp.util.NegocioException;
import br.com.fasda.erp.util.Transacional;

public class ClienteServiceOld implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private ClienteRepositoryOld clienterepository;
	
	@Transacional
	public void salvar(ClienteOld clienteOld) throws NegocioException {
		
		
		clienterepository.guardar(clienteOld);
	}
	
	@Transacional
	public void excluir(ClienteOld clienteOld) {
		clienterepository.remover(clienteOld);
	}

}