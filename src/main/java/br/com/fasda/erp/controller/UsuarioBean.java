package br.com.fasda.erp.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;

import br.com.fasda.erp.model.Pessoa;
import br.com.fasda.erp.model.Usuario;
import br.com.fasda.erp.repository.UsuarioRepository;
import br.com.fasda.erp.service.ConfiguracaoService;
import br.com.fasda.erp.service.UsuarioService;
import br.com.fasda.erp.util.NegocioException;

@Named	
@ViewScoped
public class UsuarioBean extends CrudBean<Usuario> {
	
    private static final long serialVersionUID = 1L;

    @Inject
    private UsuarioService usuarioService;
    
    @Inject
    private ConfiguracaoService configuracaoService; // Injeta o serviço global
    
    @Inject
    private UsuarioRepository usuariosRepository;
    
    public UsuarioBean() {
        // Passamos a classe Pessoa para o CrudBean
        super(Usuario.class);
    }
    
    // --- MÉTODOS OBRIGATÓRIOS (OVERRIDE) ---
    
    @Override
    public void pesquisar() {   	
    	if (termoPesquisa == null || termoPesquisa.trim().isEmpty()) {
    		this.listaItens = usuariosRepository.todos(); // Traz tudo se não houver filtro
    	} else {
            this.listaItens = usuariosRepository.pesquisar(this.termoPesquisa);
        }
	}
    
    @Override
    public void salvar() {
    	try {
    	// Usamos 'entidade' que vem do CrudBean (substitui 'usuario')
    	usuarioService.salvar(this.entidade); // Tenta salvar através do Service
    	
        atualizarRegistros();
        
        // Se chegou aqui, deu certo!
        messages.info("Usuario salvo com sucesso!");
        
        // limpar o formulário após salvar:
        this.entidade = new Usuario();
                
        PrimeFaces.current().ajax().update(Arrays.asList("frm:dataTable", "frm:messages"));
        
    	} catch (NegocioException e) {
    		// Usando o novo método que criamos para direcionar ao campo específico
            messages.error("frm:nomeUsuario", e.getMessage());
    	}
    }
    
    @Override
    public void excluir() {
    	usuarioService.excluir(this.entidade);
    	this.entidade = null;
    	atualizarRegistros(); // Atualiza a lista após remover
        messages.info("Usuário excluído com sucesso!");
    }
    
    @Override
    public void prepararNovo() {
        this.entidade = new Usuario();
    }
    
    @Override
    public void prepararEdicao() {
        
    }
    
    @Override
    protected Object getEntidadeId(Usuario usuario) {
        return usuario.getId();
    }
    
    public void todosUsuarios() {
        this.listaItens = usuariosRepository.todos();
    }
    
    private void atualizarRegistros() {
        if (jaHouvePesquisa()) {
            pesquisar();
        } else {
            todosUsuarios();
        }
    }
    
    private boolean jaHouvePesquisa() {
        return termoPesquisa != null && !termoPesquisa.isEmpty();
    }
    
    /* Salva o local da imagem(Ex: Foto do usuário) no banco */
    public void handleFileUpload(FileUploadEvent event) {
        try {
        	
        	 // Pasta da imagem salva no servidor
            // ATENÇÃO!!! Se alterar o valor dessa variavel, precisa ajustar no ImageServlet.java
            String pastaImagem = "/Imagens/Pessoa";
            
            // 1. Define o caminho da pasta (Pode ser no C:/uploads ou /home/user/uploads)
            String caminhoDestino = configuracaoService.getCaminhoUpload() + pastaImagem; 
            
            File pasta = new File(caminhoDestino);
            if (!pasta.exists()) {
            	pasta.mkdirs();
            }

            // 2. Cria o nome do arquivo (Dica: use o ID do usuário ou timestamp para evitar nomes iguais)
            String nomeArquivo = System.currentTimeMillis() + "_" + event.getFile().getFileName();
            File arquivoFinal = new File(pasta, nomeArquivo);

            // 3. Salva o arquivo no servidor
            Files.copy(event.getFile().getInputStream(), arquivoFinal.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // 4. Atualiza o objeto Usuario para salvar o CAMINHO no banco depois
            this.entidade.setFotoCaminho(nomeArquivo);
            
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage("Sucesso", "Foto " + nomeArquivo + " enviada!"));
                
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Verifica disponibilidade de nome de usuario
    public void verificarDisponibilidade() {
        String login = entidade.getNomeUsuario();
        
        if (login != null && !login.trim().isEmpty()) {
            // Busca no repositório se já existe alguém com esse login
            boolean jaExiste = usuariosRepository.existeLogin(login, entidade.getId());
            
            if (jaExiste) {
                FacesContext context = FacesContext.getCurrentInstance();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuário já cadastrado", null);
                
                // O segredo está aqui: o primeiro parâmetro é o "Client ID" do componente
                // Se o campo estiver dentro de um form 'frm', o ID costuma ser 'frm:nomeUsuario'
                context.addMessage("frm:nomeUsuario", msg);
            }
        }
    }
    
}