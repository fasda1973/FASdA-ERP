package br.com.fasda.erp.service;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import br.com.fasda.erp.model.DadosCliente;
import br.com.fasda.erp.model.DadosFornecedor;
import br.com.fasda.erp.model.DadosFuncionario;
import br.com.fasda.erp.model.Pessoa;
import br.com.fasda.erp.model.PessoaFisica;
import br.com.fasda.erp.model.PessoaJuridica;
import br.com.fasda.erp.repository.PessoaRepository;
import br.com.fasda.erp.util.NegocioException;
import br.com.fasda.erp.util.Transacional;

public class PessoaService implements Serializable {
	private static final long serialVersionUID = 1L;

    @Inject
    private PessoaRepository pessoaRepository;
      
    // 1. Injeta o seu EntityManager no Service se já não estiver injetado:
    @Inject 
    private EntityManager manager;
    
    // Não use o @Transacional customizado neste método de busca!
    public Pessoa buscarSnapshotDoBanco(Long id) {       
    	Pessoa snapshot = manager.createQuery("select p from Pessoa p where p.id = :id", Pessoa.class)
                .setParameter("id", id)
                .setHint("javax.persistence.cache.retrieveMode", "BYPASS")
                .setHint("javax.persistence.cache.storeMode", "REFRESH")
                .getSingleResult();
		// O PULO DO GATO: Desvincula este objeto do EntityManager atual.
		// Isso garante que o Hibernate não misture este snapshot com o objeto que veio da tela!
		manager.detach(snapshot); 
		
		return snapshot;
    }

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
            
            // O Service faz a verificação final
    	    boolean jaExiste = pessoaRepository.existeMatricula(pessoa.getDadosFuncionario().getMatricula(), pessoa.getId());
    	    
    	    if (jaExiste) {
    	        throw new NegocioException("Já existe pessoa com a matricula informada.");
    	    }
        }
        
        // Se NÃO for funcionário, remove o objeto
        if (!pessoa.isFuncionario()) {
            pessoa.setDadosFuncionario(null);
        }
        
        if (pessoa.isFornecedor()) {
            if (pessoa.getDadosFornecedor() == null) {
                pessoa.setDadosFornecedor(new DadosFornecedor());
            }
            // Vincula o "pai" ao "filho" para o @MapsId funcionar
            pessoa.getDadosFornecedor().setPessoa(pessoa);
        }
        
        // Se NÃO for fornecedor, remove o objeto
        if (!pessoa.isFornecedor()) {
            pessoa.setDadosFornecedor(null);
        }

        try {
        	
        	pessoaRepository.guardarComAuditoria(pessoa, origemTela, usuarioLogado);
        	
        	/*
            Pessoa pOrigem = null;
            
            // Se já tiver ID, precisamos capturar o "Antes" direto do banco para o Log
            if (pessoa.getId() != null) {
                try {
                    // BUSCA O ESTADO REAL E ATUAL DO BANCO (Antes de aplicar as alterações da tela)
                    // O JPA trará a instância correta (PessoaFisica ou PessoaJuridica) automaticamente
                    //pOrigem = buscarSnapshotDoBanco(pessoa.getId());
                    
                } catch (Exception e) {
                    System.out.println("Erro ao buscar snapshot para auditoria: " + e.getMessage());
                }
            }
            
            if (pessoa.getId() != null) {
                System.out.println("##################################################");
                System.out.println("Entrou na Edição");
                System.out.println("##################################################");
                
                // 1. Forçamos o EntityManager a esquecer temporariamente a entidade que veio da tela.
                // Isso impede que o JPA interligue os dois objetos e misture os dados digitados!
                manager.detach(pessoa);
                
                String tipoOperacao = "ALTERAÇÃO";
                //String acaoTexto = "Edição realizada";
                
                // 2. Agora buscamos o snapshot real do banco sem interferência
                pOrigem = buscarSnapshotDoBanco(pessoa.getId());
                
                // 2. GERA O DETALHE DAS ALTERAÇÕES (Agora comparando o objeto do banco com o da tela)
                String camposAlterados = ObjetoDiffUtil.compararAlteracoes(pOrigem, pessoa);
                
                // 3. SALVA A ENTIDADE ATUALIZADA
                pessoa = pessoaRepository.guardar(pessoa);
                
                // 4. GRAVA O LOG SE HOUVE MUDANÇAS
                // Exemplo detalhaLog sendo, %s para String e %d para Numeros :
                // ( "%s via tela [%s] - ID: %d | Nome: %s | Campos: %s", acaoTexto, origemTela.toUpperCase(), pessoa.getId(), pessoa.getNome(), camposAlterados);
                if (camposAlterados != null && !camposAlterados.trim().isEmpty()) {
                    String detalheLog = String.format("Campos: %s", camposAlterados);
                    
                    //LogAuditoria log = new LogAuditoria(tipoOperacao, origemTela.toUpperCase(), pessoa.getId(), detalheLog, usuarioLogado);
                    //logRepository.salvar(log);
                }
                
            } else {	            	 
                // Se o ID for nulo, a operação é CADASTRO (Mantenha igual ao seu)
                String tipoOperacao = "CADASTRO";
                //String acaoTexto = "Inclusão realizada";
                              
                pessoa = pessoaRepository.guardar(pessoa);
                
                // 2. GERA O DETALHE DOS CAMPOS PREENCHIDOS (Traz os campos que foram preenchidos no cadastro novo)
                String camposPreenchidos = ObjetoDiffUtil.buscaCamposPreenchidos(pessoa);
	                
                String detalheLog = String.format("Campos: %s", camposPreenchidos);
	                
                //LogAuditoria log = new LogAuditoria(tipoOperacao, origemTela.toUpperCase(), pessoa.getId(), detalheLog, usuarioLogado);
	                
                //logRepository.salvar(log);
	                                	
            }
            */
        } catch (Exception e) {
            //e.printStackTrace();
            throw new NegocioException("Erro ao salvar no banco de dados. Operação cancelada. Detalhe: " + e.getMessage());
        }
    }
    
    @Transacional
	public void excluir(Pessoa pessoa, String origemTela, String usuarioLogado) throws NegocioException {
    	//String tipoOperacao = "EXCLUSÃO";
        //String acaoTexto = "Exclução do registro";
    	
    	try {
    		pessoaRepository.removerComAuditoria(pessoa, origemTela, usuarioLogado);
    		
    		/*
    		pessoaRepository.remover(pessoa);
    		
    		// Monta a mensagem incluindo a tela de origem
	        String detalheLog = String.format("Nome: %s", pessoa.getNome());
	        
	        // 4. Instancia e grava o log
	        //LogAuditoria log = new LogAuditoria(tipoOperacao, origemTela.toUpperCase(), pessoa.getId(), detalheLog, usuarioLogado);
	        //logRepository.salvar(log);
	        */
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