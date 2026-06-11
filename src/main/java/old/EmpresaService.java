package old;

import java.io.Serializable;

import javax.inject.Inject;

import br.com.fasda.erp.util.Transacional;

public class EmpresaService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private EmpresaRepository empresaRepository;
	
	@Transacional
	public void salvar(Empresa empresa) {
		empresaRepository.guardar(empresa);
	}
	
	@Transacional
	public void excluir(Empresa empresa) {
		empresaRepository.remover(empresa);
	}

}
