package br.com.fasda.erp.util;

import javax.persistence.EntityManager;
import br.com.fasda.erp.model.BaseEntity;
import br.com.fasda.erp.model.LogAuditoria;
import br.com.fasda.erp.repository.LogRepository;

public class AuditoriaUtil {

    /**
     * Processa e grava o log de auditoria para inserções e alterações de qualquer entidade.
     * Retorna a entidade persistida e atualizada pelo banco.
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseEntity<?>> T salvarComAuditoria(
            T entidade, 
            EntityManager manager, 
            LogRepository logRepository, 
            String origemTela, 
            String usuarioLogado) {
        
        T entidadeSalva;
        
        if (entidade.getId() != null) { // --- FLUXO DE EDIÇÃO (ALTERAÇÃO) ---
            
            // 1. Desvincula o objeto atual da sessão para não misturar com a busca do banco
            manager.detach(entidade);
            
            String tipoOperacao = "ALTERAÇÃO";
            System.out.println("###########################################################");
            System.out.println("Entrou na alteração");
            System.out.println("###########################################################");
            
            // 2. Busca o Snapshot real diretamente do banco utilizando a Classe original do objeto
            T objetoSnapshot = (T) manager.find(entidade.getClass(), entidade.getId());
            
            // 3. Compara as alterações feitas usando sua classe de Diff
            String camposAlterados = ObjetoDiffUtil.compararAlteracoes(objetoSnapshot, entidade);
            
            // 4. Salva as alterações de fato no banco de dados
            entidadeSalva = manager.merge(entidade);
            
            System.out.println("###########################################################");
            System.out.println("Salvou no banco");
            System.out.println("###########################################################");
            
            // 5. Se houveram mudanças reais, grava o log de auditoria
            if (camposAlterados != null && !camposAlterados.trim().isEmpty())  {
                String detalheLog = String.format("Campos: %s", camposAlterados);
                
                // Criamos uma variável convertendo o ID para String de forma segura
                String idString = entidadeSalva.getId() != null ? entidadeSalva.getId().toString() : null;
                
                System.out.println("###########################################################");
                System.out.println("Chegou no log");
                System.out.println("###########################################################");
                
                try {
                
	                LogAuditoria log = new LogAuditoria(tipoOperacao, origemTela.toUpperCase(), 
	                		idString, detalheLog, usuarioLogado);
	                logRepository.salvar(log);
	                
	                System.out.println("###########################################################");
	                System.out.println("Passou salvar log com: " + log);
	                
	                System.out.println("Detalhe não gravou no log: " + detalheLog);
	                System.out.println("###########################################################");
                } catch (Exception e) {
                    e.printStackTrace();
                     e.getMessage();
                }
            }
            
        } else { // --- FLUXO DE NOVO CADASTRO ---
            
            String tipoOperacao = "CADASTRO";
            
            // 1. Salva o registro novo primeiro para gerar o ID na sequence/identity
            entidadeSalva = manager.merge(entidade);
            
            // 2. Captura os campos preenchidos iniciais
            String camposPreenchidos = ObjetoDiffUtil.buscaCamposPreenchidos(entidadeSalva);
            String detalheLog = String.format("Campos: %s", camposPreenchidos);
            
            // Criamos uma variável convertendo o ID para String de forma segura
            String idString = entidadeSalva.getId() != null ? entidadeSalva.getId().toString() : null;
            
            // 3. Grava o log do novo cadastro
            LogAuditoria log = new LogAuditoria(tipoOperacao, origemTela.toUpperCase(), 
            		idString, detalheLog, usuarioLogado);
            logRepository.salvar(log);
        }
        
        return entidadeSalva;
    }
    
    /**
     * Processa, grava o log de auditoria e remove a entidade do banco de dados.
     */
    public static <T extends BaseEntity<?>> void removerComAuditoria(
            T entidade,
            Class<T> classe, // Passa a classe explicitamente aqui
            EntityManager manager, 
            LogRepository logRepository, 
            String origemTela, 
            String usuarioLogado) {
        
        String tipoOperacao = "EXCLUSÃO";
        
        // Agora o compilador sabe exatamente o tipo, sem precisar de cast!
        T entidadeGerenciada = manager.contains(entidade) ? entidade : manager.find(classe, entidade.getId());
        
        if (entidadeGerenciada != null) {
            
            // 2. Captura todos os dados do objeto antes de apagá-lo, para o histórico do log
            String camposAntesDaExclusao = ObjetoDiffUtil.buscaCamposPreenchidos(entidadeGerenciada);
            
            String detalheLog = String.format("Dados do registro apagado: %s", camposAntesDaExclusao);
            
            // 3. Executa a remoção física no banco de dados
            manager.remove(entidadeGerenciada);
            
            // Criamos uma variável convertendo o ID para String de forma segura
            String idString = entidadeGerenciada.getId() != null ? entidadeGerenciada.getId().toString() : null;
            
            // 4. Se a remoção ocorreu sem erros, grava o log de auditoria
            LogAuditoria log = new LogAuditoria(tipoOperacao, origemTela.toUpperCase(), 
            		idString, detalheLog, usuarioLogado);
            logRepository.salvar(log);
        }
    }
}