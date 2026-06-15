package br.com.fasda.erp.util;

import org.mindrot.jbcrypt.BCrypt;

public class SenhaUtil {

    // Transforma a senha em texto puro em um hash seguro
    public static String criptografar(String senhaPuroTexto) {
        // O gensalt() gera um salt aleatório e adiciona complexidade à senha
        return BCrypt.hashpw(senhaPuroTexto, BCrypt.gensalt());
    }

    // Verifica se a senha digitada pelo usuário bate com o hash salvo no banco
    public static boolean verificar(String senhaPuroTexto, String hashDoBanco) {
        try {
            return BCrypt.checkpw(senhaPuroTexto, hashDoBanco);
        } catch (Exception e) {
            return false;
        }
    }
}