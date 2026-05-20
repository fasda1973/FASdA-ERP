package br.com.fasda.erp.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ObjetoDiffUtil {

    public static String compararAlteracoes(Object antigo, Object novo) {
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
            
            // 1. Ignora os relacionamentos de composição para não sujar o log com referências de memória
            if (field.getName().equals("dadosCliente") || 
                field.getName().equals("dadosFornecedor") || 
                field.getName().equals("dadosFuncionario")) {
                continue;
            }

            try {
                field.setAccessible(true); // Permite ler atributos privados
                Object valorAntigo = field.get(antigo);
                Object valorNovo = field.get(novo);

                // Se os valores forem diferentes, houve alteração!
                if (!Objects.equals(valorAntigo, valorNovo)) {
                    String nomeCampo = field.getName();
                    // Formata: NomeDoCampo: antigo -> novo
                    modificacoes.add(nomeCampo + ": " + valorAntigo + " -> " + valorNovo);
                }
            } catch (IllegalAccessException e) {
                // Trata ou ignora campos inacessíveis
            }
        }

        // Junta todas as modificações separando por vírgula
        return String.join(", ", modificacoes);
    }
}