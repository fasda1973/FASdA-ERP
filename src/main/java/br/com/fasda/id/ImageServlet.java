package br.com.fasda.id;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.enterprise.inject.spi.CDI; // Importe o CDI do pacote javax.enterprise
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.fasda.erp.service.ConfiguracaoService; // Importe o seu serviço global

@WebServlet("/img-pessoas/*")
public class ImageServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. PROTEÇÃO: Verifica se veio algum nome de arquivo na URL
        if (request.getPathInfo() == null || request.getPathInfo().equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nome do arquivo não informado.");
            return;
        }

        // Pega o nome do arquivo da URL (ex: /fotos/Pantera.jpg -> vira "Pantera.jpg")
        String filename = request.getPathInfo().substring(1);
        
        // Outra proteção: Se o filename vier vazio após o substring, evita tentar ler o diretório pai
        if (filename.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nome do arquivo inválido.");
            return;
        }

        // 2. O PULO DO GATO: Busca o ConfiguracaoService gerenciado pelo CDI de forma manual
        ConfiguracaoService configuracaoService = CDI.current().select(ConfiguracaoService.class).get();
        
        // 3. RECUPERA O CAMINHO DO BANCO DE DADOS DINAMICAMENTE!
        // No seu log anterior, o caminho impresso foi "C:\Dev\Java\FASdA\Uploads\Imagens"
        String diretorioRaiz = configuracaoService.getCaminhoUpload();
        
        // Se no cadastro de Pessoas você salvou dentro de uma subpasta chamada "Pessoa", 
        // nós concatenamos ela aqui para o Servlet achar o arquivo no lugar certo:
        File file = new File(diretorioRaiz + File.separator + "Imagens/Pessoa", filename);

        // 4. VALIDAÇÃO: Se o arquivo físico não existir no HD do servidor, retorna erro 404 limpo
        if (!file.exists() || file.isDirectory()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Arquivo não encontrado.");
            return;
        }

        // 5. CONFIGURAÇÃO DO FLUXO DE RETORNO: Define o tipo do arquivo (jpg, png, etc)
        String contentType = getServletContext().getMimeType(file.getName());
        if (contentType == null) {
            contentType = "application/octet-stream"; // Tipo padrão genérico caso o tipo não seja identificado
        }
        
        response.setContentType(contentType);
        response.setHeader("Content-Length", String.valueOf(file.length()));

        // 6. ENVIAR PARA A TELA: Copia o arquivo para o corpo da resposta HTTP
        Files.copy(file.toPath(), response.getOutputStream());
    }
}