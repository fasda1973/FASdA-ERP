package br.com.fasda.erp.util;

import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig; // Adicione este import
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import br.com.fasda.erp.controller.LoginBean;
import br.com.fasda.erp.service.ConfiguracaoService;

@WebFilter("*.xhtml")
public class AutorizacaoFilter implements Filter {

    @Inject
    private LoginBean loginBean;
    
    @Inject
    private ConfiguracaoService configuracaoService;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        
        String requestURI = request.getRequestURI();
        
     // 1. Verifica se é a página de login (aceita maiúsculo ou minúsculo de forma direta)
        boolean paginaLogin = requestURI.endsWith("/Login.xhtml") || requestURI.endsWith("/login.xhtml");

        // 2. Verifica se é a página de cadastro de usuários (troque pelo nome real do seu arquivo se for diferente)
        boolean paginaCadastro = requestURI.endsWith("/usuarios.xhtml") || requestURI.endsWith("/Usuarios.xhtml");

        // 3. Verifica se o cadastro está liberado pelo administrador
        boolean cadastroLiberado = configuracaoService != null && configuracaoService.isPermitirCadastroUsuarios();

        // 4. Verifica se é um recurso (CSS, JS, Imagens)
        boolean recursoJSF = requestURI.contains("/javax.faces.resource/");

        // --- A DECISÃO ---
        if (paginaLogin || recursoJSF || (paginaCadastro && cadastroLiberado) || (loginBean != null && loginBean.getLogin() != null)) {
            // Passagem livre!
            chain.doFilter(req, res);
        } else {
            // Se caiu aqui, é porque o filtro barrou. 
            // IMPORTANTE: Mandamos estritamente para o mesmo padrão do item 1 (/Login.xhtml)
            response.sendRedirect(request.getContextPath() + "/Login.xhtml");
        }
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Pode deixar vazio, mas o método PRECISA existir
    }

    @Override
    public void destroy() {
        // Pode deixar vazio, mas o método PRECISA existir
    }
}