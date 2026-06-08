package br.com.fasda.erp.service;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import br.com.fasda.erp.model.LogAuditoria;
import br.com.fasda.erp.model.Usuario;
import br.com.fasda.erp.repository.LogRepository;
import br.com.fasda.erp.repository.UsuarioRepository;
import br.com.fasda.erp.util.NegocioException;
import br.com.fasda.erp.util.ObjetoDiffUtil;
import br.com.fasda.erp.util.Transacional;

public class UsuarioService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private UsuarioRepository usuarioRepository;
	
	@Inject
    private LogRepository logRepository; // 1. Injetar o repositório de logs
	
	@Inject 
    private EntityManager manager;
	
	// Não usar o @Transacional customizado neste método de busca!
    public Usuario buscarSnapshotDoBanco(Long id) {       
    	Usuario snapshot = manager.createQuery("select p from Usuario p where p.id = :id", Usuario.class)
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
	public void salvar(Usuario usuario, String origemTela, String usuarioLogado) throws NegocioException {
		// Se for edição e a senha estiver vazia, recuperamos a senha antiga do banco
	    if (usuario.getId() != null && (usuario.getSenha() == null || usuario.getSenha().isEmpty())) {
	        String senhaAtual = usuarioRepository.buscarSenhaAtual(usuario.getId());
	        usuario.setSenha(senhaAtual);
	    }
	    
	    // O Service faz a verificação final
	    boolean jaExiste = usuarioRepository.existeLogin(usuario.getNomeUsuario(), usuario.getId());
	    
	    if (jaExiste) {
	        throw new NegocioException("Já existe um usuário com o login informado.");
	    }
	    
	    try {
	    	Usuario uOrigem = null;
	    		    	
	    	if (usuario.getId() != null) { // Edição
	    		manager.detach(usuario);
	    		
	    		String tipoOperacao = "ALTERAÇÃO";
	    		
	    		// 2. Agora buscamos o snapshot real do banco sem interferência
                uOrigem = buscarSnapshotDoBanco(usuario.getId());
                
                // 2. GERA O DETALHE DAS ALTERAÇÕES (Agora comparando o objeto do banco com o da tela)
                String camposAlterados = ObjetoDiffUtil.compararAlteracoes(uOrigem, usuario);
	    		
                usuario = usuarioRepository.guardar(usuario);
                
                // 4. GRAVA O LOG SE HOUVE MUDANÇAS
                // Exemplo detalhaLog sendo, %s para String e %d para Numeros :
                // ( "%s via tela [%s] - ID: %d | Nome: %s | Campos: %s", acaoTexto, origemTela.toUpperCase(), pessoa.getId(), pessoa.getNome(), camposAlterados);
                if (camposAlterados != null && !camposAlterados.trim().isEmpty()) {
                    String detalheLog = String.format("Campos: %s", camposAlterados);
                    
                    LogAuditoria log = new LogAuditoria(tipoOperacao, origemTela.toUpperCase(), usuario.getId(), detalheLog, usuarioLogado);
                    logRepository.salvar(log);
                }
                
	    	} else { // Novo
	    		String tipoOperacao = "CADASTRO";
	    		
	    		usuario = usuarioRepository.guardar(usuario);
	    		
	    		// 2. GERA O DETALHE DOS CAMPOS PREENCHIDOS (Traz os campos que foram preenchidos no cadastro novo)
                String camposPreenchidos = ObjetoDiffUtil.buscaCamposPreenchidos(usuario);
	                
                String detalheLog = String.format("Campos: %s", camposPreenchidos);
	                
                LogAuditoria log = new LogAuditoria(tipoOperacao, origemTela.toUpperCase(), usuario.getId(), detalheLog, usuarioLogado);
	                
                logRepository.salvar(log);
	    		
	    	}
	    	
	    	
	    
	    } catch (Exception e) {
            //e.printStackTrace();
            throw new NegocioException("Erro ao salvar no banco de dados. Operação cancelada. Detalhe: " + e.getMessage());
        }
	}
	
	@Transacional
	public void excluir(Usuario usuario, String origemTela, String usuarioLogado) throws NegocioException {
		String tipoOperacao = "EXCLUSÃO";
		
		try {
			
			usuarioRepository.remover(usuario);
			
			// Monta a mensagem incluindo a tela de origem
	        String detalheLog = String.format("Nome: %s", usuario.getNome());
	        
	        // 4. Instancia e grava o log
	        LogAuditoria log = new LogAuditoria(tipoOperacao, origemTela.toUpperCase(), usuario.getId(), detalheLog, usuarioLogado);
	        logRepository.salvar(log);
			
		} catch (Exception e) {
			throw new NegocioException("Erro ao salvar no banco de dados. Operação cancelada. Detalhe: " + e.getMessage());
			
		}
			
	}

}