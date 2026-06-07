package br.com.fasda.erp.util;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;
import br.com.fasda.erp.service.ConfiguracaoService;

public class ArquivosUploads {

    /**
     * Processa o upload do arquivo e retorna o nome único gerado para ser salvo no banco.
     * Retorna NULL caso ocorra algum erro.
     */
    public static String realizarUpload(FileUploadEvent event, ConfiguracaoService configuracaoService, String subPastaModulo) {
        try {
            // 1. BUSCA O CAMINHO INFORMADO EM Configuracoes.xhtml!
            String caminhoDestino = configuracaoService.getCaminhoUpload() + subPastaModulo;
            
            // 2. Garante que a pasta física existe no servidor
            File pasta = new File(caminhoDestino);
            if (!pasta.exists()) {
                pasta.mkdirs();
            }
            
            // 3. Torna o nome do arquivo único para evitar sobrescritas
            String nomeArquivoUnico = System.currentTimeMillis() + "_" + event.getFile().getFileName();
            File arquivoFinal = new File(pasta, nomeArquivoUnico);
            
            // 4. Copia de alta performance (NIO)
            try (InputStream input = event.getFile().getInputStream()) {
                Files.copy(input, arquivoFinal.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            
            // 5. Feedback visual genérico
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Arquivo enviado!"));
            
            System.out.println("Arquivo salvo com sucesso em: " + arquivoFinal.getAbsolutePath());
            
            // RETORNA o nome que deve ser gravado no banco
            return nomeArquivoUnico;
            
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao processar o upload do arquivo."));
            e.printStackTrace();
            return null;
        }
    }
}