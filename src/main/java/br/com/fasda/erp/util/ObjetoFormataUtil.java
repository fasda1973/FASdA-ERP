package br.com.fasda.erp.util;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ObjetoFormataUtil {
	
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	
	public static String formatarDocumento(String documento) {
        if (documento == null) {
            return "";
        }
        
        // Remove qualquer espaço ou caractere residual
        String doc = documento.replaceAll("\\D", ""); 

        // Se for CPF (11 dígitos)
        if (doc.length() == 11) {
            return doc.substring(0, 3) + "." + doc.substring(3, 6) + "." + 
                   doc.substring(6, 9) + "-" + doc.substring(9);
        } 
        // Se for CNPJ (14 dígitos)
        else if (doc.length() == 14) {
            return doc.substring(0, 2) + "." + doc.substring(2, 5) + "." + 
                   doc.substring(5, 8) + "/" + doc.substring(8, 12) + "-" + doc.substring(12);
        }
        
        // Se não for nenhum dos dois, retorna o texto puro que veio do banco
        return documento; 
    }
	
	// Formata LocalDate (apenas data)
    public static String formatarData(LocalDate data) {
        if (data == null) {
            return "";
        }
        return data.format(DATE_FORMATTER);
    }

    // Formata LocalDateTime (data e hora, caso use no futuro para logs ou vendas)
    public static String formatarDataHora(LocalDateTime dataHora) {
        if (dataHora == null) {
            return "";
        }
        return dataHora.format(DATE_TIME_FORMATTER);
    }
	
}