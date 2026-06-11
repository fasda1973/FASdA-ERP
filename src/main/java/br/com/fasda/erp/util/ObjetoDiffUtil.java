package br.com.fasda.erp.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ObjetoDiffUtil {

    // Guarda os objetos que já estão sendo processados na thread atual para evitar StackOverflow
    private static final ThreadLocal<Map<Object, Boolean>> objetosVisitados = ThreadLocal.withInitial(IdentityHashMap::new);

    public static String compararAlteracoes(Object antigo, Object novo) {
        if (antigo == null || novo == null) return "";
        if (!antigo.getClass().equals(novo.getClass())) return "";

        List<String> modificacoes = new ArrayList<>();
        
        Object objAntigoReal = antigo;
        if (antigo instanceof org.hibernate.proxy.HibernateProxy) {
            objAntigoReal = ((org.hibernate.proxy.HibernateProxy) antigo).getHibernateLazyInitializer().getImplementation();
        }
        Object objNovoReal = novo;
        if (novo instanceof org.hibernate.proxy.HibernateProxy) {
            objNovoReal = ((org.hibernate.proxy.HibernateProxy) novo).getHibernateLazyInitializer().getImplementation();
        }

        Class<?> classeAlvo = objAntigoReal.getClass();
        if (classeAlvo.getName().contains("_$$_Weld")) {
            classeAlvo = classeAlvo.getSuperclass();
        }

        List<Field> allFields = new ArrayList<>();
        for (Field f : classeAlvo.getDeclaredFields()) {
            allFields.add(f);
        }
        
        Class<?> superClasse = classeAlvo.getSuperclass();
        if (superClasse != null && superClasse.getName().startsWith("br.com.fasda.erp.model")) {
            for (Field f : superClasse.getDeclaredFields()) {
                allFields.add(f);
            }
        }

        for (Field field : allFields) {
            String nomeCampo = field.getName();
            Class<?> tipoCampo = field.getType();

            if (nomeCampo.equals("serialVersionUID") || tipoCampo.equals(List.class) || nomeCampo.equals("id") || nomeCampo.equalsIgnoreCase("datacadastro")) {
                continue;
            }

            if (tipoCampo.getName().startsWith("br.com.fasda.erp.model") && 
                !nomeCampo.equals("dadosCliente") && 
                !nomeCampo.equals("dadosFornecedor") && 
                !nomeCampo.equals("dadosFuncionario")) {
                continue; 
            }

            try {
                field.setAccessible(true);
                Object valorAntigo = field.get(objAntigoReal);
                Object valorNovo = field.get(objNovoReal);

                if (valorAntigo == null && valorNovo == null) {
                    continue;
                }

                if (nomeCampo.equals("dadosCliente") || 
                    nomeCampo.equals("dadosFornecedor") || 
                    nomeCampo.equals("dadosFuncionario")) {
                    
                    if (valorAntigo == null || valorNovo == null) {
                        modificacoes.add(nomeCampo + ": " + (valorAntigo == null ? "vazio" : "preenchido") + " -> " + (valorNovo == null ? "vazio" : "preenchido"));
                    } else {
                        List<String> alteracoesInternas = compararCamposInternos(valorAntigo, valorNovo);
                        modificacoes.addAll(alteracoesInternas);
                    }
                    continue; 
                }

                if (!Objects.equals(valorAntigo, valorNovo)) {
                    modificacoes.add(nomeCampo + ": " + (valorAntigo == null ? "vazio" : valorAntigo) + " -> " + (valorNovo == null ? "vazio" : valorNovo));
                }
            } catch (IllegalAccessException e) {
                // Ignora
            }
        }
        
        return String.join(", ", modificacoes);
    }

    private static List<String> compararCamposInternos(Object antigo, Object novo) {
        List<String> modificacoesInternas = new ArrayList<>();
        if (antigo == null || novo == null) return modificacoesInternas;
        
        Object objAntigoReal = antigo;
        if (antigo instanceof org.hibernate.proxy.HibernateProxy) {
            objAntigoReal = ((org.hibernate.proxy.HibernateProxy) antigo).getHibernateLazyInitializer().getImplementation();
        }
        Object objNovoReal = novo;
        if (novo instanceof org.hibernate.proxy.HibernateProxy) {
            objNovoReal = ((org.hibernate.proxy.HibernateProxy) novo).getHibernateLazyInitializer().getImplementation();
        }

        Class<?> clazz = objAntigoReal.getClass();
        if (clazz.getName().contains("_$$_Weld")) {
            clazz = clazz.getSuperclass();
        }
        
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            String nomeCampo = f.getName();
            Class<?> tipoCampo = f.getType();

            if (nomeCampo.equals("serialVersionUID") || 
                nomeCampo.equalsIgnoreCase("pessoa") ||
                nomeCampo.equalsIgnoreCase("dataCadastro") ||
                tipoCampo.equals(List.class)) {
                continue;
            }
            
            if (tipoCampo.getName().startsWith("br.com.fasda.erp.model")) {
                continue;
            }
            
            try {
                f.setAccessible(true);
                Object valAntigo = f.get(objAntigoReal);
                Object valNovo = f.get(objNovoReal);
                
                if (valAntigo == null && valNovo == null) {
                    continue;
                }
                
                if (!Objects.equals(valAntigo, valNovo)) {
                    modificacoesInternas.add(f.getName() + ": " + (valAntigo == null ? "vazio" : valAntigo) + " -> " + (valNovo == null ? "vazio" : valNovo));
                }
            } catch (IllegalAccessException e) {
                // Ignora
            }
        }
        
        return modificacoesInternas;
    }
    
    public static String buscaCamposPreenchidos(Object novo) {
        // Inicializa o controle se for o início da execução da Thread
        Map<Object, Boolean> visitados = objetosVisitados.get();
        boolean isRaizdaChamada = visitados.isEmpty();

        try {
            if (novo == null || (novo instanceof String && ((String) novo).trim().isEmpty())) {
                return "";
            }
            
            Object objetoReal = novo;
            if (novo instanceof org.hibernate.proxy.HibernateProxy) {
                objetoReal = ((org.hibernate.proxy.HibernateProxy) novo).getHibernateLazyInitializer().getImplementation();
            }
            
            // TRAVA COMPLETA ANTILOOP: Se este objeto exato já está na pilha de processamento, aborte!
            if (visitados.containsKey(objetoReal)) {
                return "";
            }
            // Marca o objeto atual como "em processamento"
            visitados.put(objetoReal, Boolean.TRUE);
            
            Class<?> classeAlvo = objetoReal.getClass();
            if (classeAlvo.getName().contains("_$$_Weld")) {
                classeAlvo = classeAlvo.getSuperclass();
            }

            List<String> camposNovos = new ArrayList<>();
            List<Field> allFields = new ArrayList<>();
            
            for (Field f : classeAlvo.getDeclaredFields()) {
                allFields.add(f);
            }
            
            Class<?> superClasse = classeAlvo.getSuperclass();
            if (superClasse != null && superClasse.getName().startsWith("br.com.fasda.erp.model")) {
                for (Field f : superClasse.getDeclaredFields()) {
                    allFields.add(f);
                }
            }
            
            for (Field field : allFields) {
                String nomeCampo = field.getName();
                Class<?> tipoCampo = field.getType();

                if (nomeCampo.equals("serialVersionUID") || tipoCampo.equals(List.class) || nomeCampo.equals("id") || nomeCampo.equalsIgnoreCase("datacadastro")) {
                    continue;
                }

                boolean ehComposicaoValida = nomeCampo.equals("dadosCliente") || 
                                             nomeCampo.equals("dadosFornecedor") || 
                                             nomeCampo.equals("dadosFuncionario");

                if (!ehComposicaoValida) {
                    boolean ehTipoBasico = tipoCampo.isPrimitive() || 
                                           Number.class.isAssignableFrom(tipoCampo) || 
                                           tipoCampo.equals(String.class) || 
                                           tipoCampo.equals(Boolean.class) || 
                                           tipoCampo.equals(Character.class) || 
                                           java.util.Date.class.isAssignableFrom(tipoCampo) || 
                                           java.time.temporal.Temporal.class.isAssignableFrom(tipoCampo) ||
                                           tipoCampo.isEnum();
                    
                    if (!ehTipoBasico) {
                        continue; 
                    }
                }

                try {
                    field.setAccessible(true);
                    Object valorNovo = field.get(objetoReal);

                    if (valorNovo == null) {
                        continue;
                    }
                    
                    if (valorNovo instanceof Boolean && !((Boolean) valorNovo)) {
                        continue;
                    }

                    if (valorNovo instanceof String && ((String) valorNovo).trim().isEmpty()) {
                        continue;
                    }
                    
                    if (valorNovo instanceof java.math.BigDecimal && ((java.math.BigDecimal) valorNovo).compareTo(java.math.BigDecimal.ZERO) == 0) {
                        continue;
                    }

                    if (valorNovo instanceof Number && ((Number) valorNovo).doubleValue() == 0.0) {
                        continue;
                    }

                    if (ehComposicaoValida) {
                        String camposInternosBrutos = buscaCamposPreenchidos(valorNovo);
                        
                        if (!camposInternosBrutos.trim().isEmpty()) {
                            String[] campos = camposInternosBrutos.split(", ");
                            for (String campo : campos) {
                                if (campo.startsWith("pessoa:") || campo.contains(".pessoa:")) {
                                    continue;
                                }
                                //camposNovos.add(nomeCampo + "." + campo);
                                camposNovos.add(campo);
                            }
                        }
                        continue; 
                    }
                    
                    camposNovos.add(nomeCampo + ": " + valorNovo);

                } catch (IllegalAccessException e) {
                    // Ignora
                }
            }        
            
            return String.join(", ", camposNovos);

        } finally {
            // Limpa o mapa ao concluir a execução principal para não vazar memória na Thread
            if (isRaizdaChamada) {
                objetosVisitados.remove();
            }
        }
    }
}