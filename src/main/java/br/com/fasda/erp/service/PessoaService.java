package br.com.fasda.erp.service;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.inject.Inject;

import br.com.fasda.erp.model.DadosCliente;
import br.com.fasda.erp.model.DadosFuncionario;
import br.com.fasda.erp.model.LogAuditoria;
import br.com.fasda.erp.model.Pessoa;
import br.com.fasda.erp.model.PessoaFisica;
import br.com.fasda.erp.model.PessoaJuridica;
import br.com.fasda.erp.repository.LogRepository;
import br.com.fasda.erp.repository.PessoaRepository;
import br.com.fasda.erp.util.NegocioException;
import br.com.fasda.erp.util.Transacional;

public class PessoaService implements Serializable {

    @Inject
    private PessoaRepository pessoaRepository;
    
    @Inject
    private LogRepository logRepository; // 1. Injetar o repositório de logs

    @Transacional
    public void salvar(Pessoa pessoa, String origemTela, String usuarioLogado) throws NegocioException {
        // 1. Validações comuns (Ex: Nome obrigatório)
        if (pessoa.getNome() == null || pessoa.getNome().isEmpty()) {
            throw new NegocioException("O nome é obrigatório.");
        }
        
        if (pessoa instanceof PessoaFisica) {
        	// Aqui o getCpf() já vai devolver só os números por causa do setter acima
            String cpfParaBusca = ((PessoaFisica) pessoa).getCpf();
            Long idCompara = ((PessoaFisica) pessoa).getId();
            if (pessoaRepository.verificarCpfExistente(cpfParaBusca, idCompara)) {
                // Adiciona mensagem de erro
            	throw new NegocioException("Este CPF já está cadastrado no sistema!");
            }
        } else if (pessoa instanceof PessoaJuridica) {
        	String cnpjParaBusca = ((PessoaJuridica) pessoa).getCnpj();
        	Long idCompara = ((PessoaJuridica) pessoa).getId();
            if (pessoaRepository.verificarCnpjExistente(cnpjParaBusca, idCompara)) {
                // Adiciona mensagem de erro
            	throw new NegocioException("Este CNPJ já está cadastrado no sistema!");
            }
        }

        // 2. Validações específicas por tipo (O pulo do gato!)
        if (pessoa instanceof PessoaFisica) {
            validarCPF((PessoaFisica) pessoa);
        } else if (pessoa instanceof PessoaJuridica) {
            validarCNPJ((PessoaJuridica) pessoa);
        }
        
        if (pessoa.isCliente()) {
            if (pessoa.getDadosCliente() == null) {
                pessoa.setDadosCliente(new DadosCliente());
            }
            // Vincula o "pai" ao "filho" para o @MapsId funcionar
            pessoa.getDadosCliente().setPessoa(pessoa);
            
            // Aqui você poderia validar o limite de crédito, por exemplo
            if (pessoa.getDadosCliente().getLimiteCredito() == null) {
                pessoa.getDadosCliente().setLimiteCredito(BigDecimal.ZERO);
            }
        }
        
        // Se NÃO for cliente, remove o objeto para o Hibernate não tentar salvar
        if (!pessoa.isCliente()) {
            pessoa.setDadosCliente(null);
        }
        
        if (pessoa.isFuncionario()) {
            if (pessoa.getDadosFuncionario() == null) {
                pessoa.setDadosFuncionario(new DadosFuncionario());
            }
            // Vincula o "pai" ao "filho" para o @MapsId funcionar
            pessoa.getDadosFuncionario().setPessoa(pessoa);
        }
        
        // Se NÃO for funcionário, remove o objeto
        if (!pessoa.isFuncionario()) {
            pessoa.setDadosFuncionario(null);
        }
        
        // Se NÃO for fornecedor, remove o objeto
        if (!pessoa.isFornecedor()) {
            pessoa.setDadosFornecedor(null);
        }
        
        // >>> O PULO DO GATO: Descobre a operação ANTES de salvar <<<
        // Se o ID for nulo, a operação é CADASTRO. Se já tiver ID, é ALTERAÇÃO.
        String tipoOperacao = (pessoa.getId() == null) ? "CADASTRO" : "ALTERAÇÃO";
        String acaoTexto = (pessoa.getId() == null) ? "Inclusão realizada" : "Edição realizada";

        try {
	        // 3. Persistência única
	        // O Hibernate fará o INSERT na tabela 'pessoa' 
	        // e na 'pessoa_fisica' ou 'pessoa_juridica' num piscar de olhos.
	        pessoa = pessoaRepository.guardar(pessoa);
	        
	        // Monta a mensagem incluindo a tela de origem
	        String detalheLog = String.format("%s via tela [%s] - ID: %d | Nome: %s", 
	                                          acaoTexto, origemTela.toUpperCase(), pessoa.getId(), pessoa.getNome());
	        
	        // 4. Instancia e grava o log
	        LogAuditoria log = new LogAuditoria(tipoOperacao, detalheLog, usuarioLogado);
	        logRepository.salvar(log);
	        
        } catch (Exception e) {
        	// Se der erro de banco (ConstraintViolationException, coluna nula, etc), cai aqui
            // Repassa o erro para o JSF exibir na tela e NÃO grava o log
            throw new NegocioException("Erro ao salvar no banco de dados. Operação cancelada. Detalhe: " + e.getMessage());
        }
    }
    
    @Transacional
	public void excluir(Pessoa pessoa, String origemTela, String usuarioLogado) throws NegocioException {
    	String tipoOperacao = "EXCLUSÃO";
        String acaoTexto = "Exclução do registro";
    	
    	try {
    		pessoaRepository.remover(pessoa);
    		
    		// Monta a mensagem incluindo a tela de origem
	        String detalheLog = String.format("%s via tela [%s] - ID: %d | Nome: %s", 
	                                          acaoTexto, origemTela.toUpperCase(), pessoa.getId(), pessoa.getNome());
	        
	        // 4. Instancia e grava o log
	        LogAuditoria log = new LogAuditoria(tipoOperacao, detalheLog, usuarioLogado);
	        logRepository.salvar(log);
    	} catch (Exception e) {
    		throw new NegocioException("Erro ao salvar no banco de dados. Operação cancelada. Detalhe: " + e.getMessage());
    	}
	}
    
    private void validarCPF(PessoaFisica pf) throws NegocioException {
        if (pf.getCpf() == null || pf.getCpf().length() < 11) {
            throw new NegocioException("CPF inválido.");
        }
        // Aqui você pode colocar a lógica real de validação de CPF
    }

    private void validarCNPJ(PessoaJuridica pj) throws NegocioException {
        if (pj.getCnpj() == null || pj.getCnpj().length() < 14) {
            throw new NegocioException("CNPJ inválido.");
        }
        // Aqui você pode colocar a lógica real de validação de CNPJ
    }
}