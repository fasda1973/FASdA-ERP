package br.com.fasda.erp.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ObjetoDiffUtil {

    public static String compararAlteracoes(Object antigo, Object novo) {
    	 //System.out.println("##################################################");
         //System.out.println("1- Entrou na Função compararAlteracoes");
         //System.out.println("##################################################");
    	
        if (antigo == null || novo == null) return "";
        if (!antigo.getClass().equals(novo.getClass())) return "";

        List<String> modificacoes = new ArrayList<>();
        
        // Criamos uma lista dinâmica para agrupar TODOS os campos (da classe atual e das mães/pais)
        List<Field> allFields = new ArrayList<>();
        Class<?> currentClass = antigo.getClass();
        
        // Loop caminha subindo na herança até chegar em Object (raiz de tudo)
        while (currentClass != null && !currentClass.equals(Object.class)) {
            Field[] declaredFields = currentClass.getDeclaredFields();
            for (Field f : declaredFields) {
                allFields.add(f);
            }
            currentClass = currentClass.getSuperclass(); // Sobe um nível na herança (ex: PessoaFisica -> Pessoa)
        }

        // Agora o loop roda sobre a lista completa de atributos encontrados
        for (Field field : allFields) {
        	
            // Ignora coleções (Lists) e o serialVersionUID para não dar erro
            if (field.getName().equals("serialVersionUID") || field.getType().equals(List.class)) {
                continue;
            }

            // TRATAMENTO EXCLUSIVO PARA COMPOSIÇÕES DE DADOS ESPECÍFICOS
            if (field.getName().equals("dadosCliente") || 
                field.getName().equals("dadosFornecedor") || 
                field.getName().equals("dadosFuncionario")) {
                
                try {
                    field.setAccessible(true);
                    Object valorAntigo = field.get(antigo);
                    Object valorNovo = field.get(novo);
                    
                    if (valorAntigo == null && valorNovo == null) {
                        continue;
                    }
                    
                    //if (field.getName().equals("dadosCliente")) {        	
                    	//continue;
                    //}
                    
                    // Se a composição foi preenchida agora ou removida por completo
                    if (valorAntigo == null || valorNovo == null) {
                    	                  	
                    	modificacoes.add(field.getName() + ": " + (valorAntigo == null ? "vazio" : "preenchido") + " -> " + (valorNovo == null ? "vazio" : "preenchido"));
                        
                    } else {
                        // Se ambos existem na memória, em vez de comparar as referências, comparamos seus campos reais de negócio
                        List<String> alteracoesInternas = compararCamposInternos(valorAntigo, valorNovo);
                        modificacoes.addAll(alteracoesInternas);
                    }
                } catch (IllegalAccessException e) {
                    // Ignora falhas de reflexão nestas propriedades
                }
                continue; // CRITICAL: Impede o fluxo principal de logar a referência de memória feia (@hashcode)
            }

            try {
            	
                field.setAccessible(true); // Permite ler atributos privados
                Object valorAntigo = field.get(antigo);
                Object valorNovo = field.get(novo);

                // Se os valores forem diferentes, houve alteração!
                if (!Objects.equals(valorAntigo, valorNovo)) {
                    String nomeCampo = field.getName();
                                      
	                // Formata: NomeDoCampo: antigo -> novo (colocando 'vazio' se for nulo)
	                modificacoes.add(nomeCampo + ": " + (valorAntigo == null ? "vazio" : valorAntigo) + " -> " + (valorNovo == null ? "vazio" : valorNovo));
                    
                }
            } catch (IllegalAccessException e) {
                // Trata ou ignora campos inacessíveis
            }
        }
        
        // Junta todas as modificações separando por vírgula
        return String.join(", ", modificacoes);
    }

    /**
     * Método auxiliar privado para inspecionar e comparar exclusivamente atributos primitivos
     * de dados de negócio, evitando referências circulares e filtrando proxies do Hibernate.
     */
    private static List<String> compararCamposInternos(Object antigo, Object novo) {
        List<String> modificacoesInternas = new ArrayList<>();
        if (antigo == null || novo == null) return modificacoesInternas;
        
        Class<?> clazz = antigo.getClass();
        
        // Desembrulha proxies do Hibernate (Lazy Initializers) caso existam em tempo de execução
        if (clazz.getName().contains("$$EnhancerBy") || clazz.getName().contains("HibernateProxy")) {
            clazz = clazz.getSuperclass();
        }
        
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            // Ignora metadados, coleções e o mapeamento reverso que aponta de volta para o objeto Pessoa
            if (f.getName().equals("serialVersionUID") || 
                f.getName().equalsIgnoreCase("pessoa") || 
                f.getType().equals(List.class)) {
                continue;
            }
            
            try {
                f.setAccessible(true);
                Object valAntigo = f.get(antigo);
                Object valNovo = f.get(novo);
                
                if (valAntigo == null && valNovo == null) {
                    continue;
                }
                
                if (!Objects.equals(valAntigo, valNovo)) {
                	
                	String nomeCampo = f.getName();
                    
                    System.out.println("##################################################");
                    System.out.println("Nome do Campo em compararCamposInternos:: " + nomeCampo);
                    System.out.println("##################################################");
                	
                    if (f.getName().equals("dadosCliente") || 
                        f.getName().equals("dadosFornecedor") || 
                        f.getName().equals("dadosFuncionario")) {
                    	modificacoesInternas.add("em " + nomeCampo);
                    } else {                    
                    	modificacoesInternas.add(f.getName() + ": " + (valAntigo == null ? "vazio" : valAntigo) + " -> " + (valNovo == null ? "vazio" : valNovo));
                    }
                }
            } catch (IllegalAccessException e) {
                // Ignora falhas de leitura
            }
        }
        
        return modificacoesInternas;
    }
 
}