package br.com.fasda.erp.service;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.inject.Inject;

import br.com.fasda.erp.model.DadosCliente;
import br.com.fasda.erp.model.Pessoa;
import br.com.fasda.erp.model.PessoaFisica;
import br.com.fasda.erp.model.PessoaJuridica;
import br.com.fasda.erp.repository.PessoaRepository;
import br.com.fasda.erp.util.NegocioException;
import br.com.fasda.erp.util.Transacional;

public class PessoaService implements Serializable {

    @Inject
    private PessoaRepository pessoaRepository;
    

    @Transacional
    public void salvar(Pessoa pessoa) throws NegocioException {
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

        // 3. Persistência única
        // O Hibernate fará o INSERT na tabela 'pessoa' 
        // e na 'pessoa_fisica' ou 'pessoa_juridica' num piscar de olhos.
        pessoaRepository.guardar(pessoa);
    }
    
    @Transacional
	public void excluir(Pessoa pessoa) {
		pessoaRepository.remover(pessoa);
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