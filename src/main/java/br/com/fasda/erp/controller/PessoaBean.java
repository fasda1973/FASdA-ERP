package br.com.fasda.erp.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.Serializable;
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
import org.primefaces.model.file.UploadedFile; // Dependendo da versão do PF

import br.com.fasda.erp.model.DadosCliente;
import br.com.fasda.erp.model.DadosFornecedor;
import br.com.fasda.erp.model.DadosFuncionario;
import br.com.fasda.erp.model.Pessoa;
import br.com.fasda.erp.model.PessoaFisica;
import br.com.fasda.erp.model.PessoaJuridica;
import br.com.fasda.erp.repository.PessoaRepository;
import br.com.fasda.erp.service.PessoaService;
import br.com.fasda.erp.service.ConfiguracaoService;
import br.com.fasda.erp.util.NegocioException;

@Named
@ViewScoped
public class PessoaBean extends CrudBean<Pessoa> implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
    @Inject
    private PessoaService pessoaService; // O Service a gente injeta aqui
    
    @Inject
    private PessoaRepository pessoaRepository;
    
    @Inject
    private LoginBean loginBean; // Injeta o seu bean de login/sessão
    
    @Inject
    private ConfiguracaoService configuracaoService; // Injeta o serviço global
    
    private String tipoPessoa = "FISICA";
    
    public PessoaBean() {
        // Passamos a classe Pessoa para o CrudBean
        super(Pessoa.class);
    }
    
    // --- MÉTODOS OBRIGATÓRIOS (OVERRIDE) ---
    
    @Override
    public void pesquisar() {   	
    	if (termoPesquisa == null || termoPesquisa.trim().isEmpty()) {
    		this.listaItens = pessoaRepository.todas(); // Traz tudo se não houver filtro
    	} else {
            this.listaItens = pessoaRepository.pesquisar(this.termoPesquisa);
        }
	}
    
    @Override
    public void salvar() {
        try {
        	// Prepara loginAuditoria
        	String loginDoUsuario = "SISTEMA"; // Valor padrão de segurança      	
        	// 1. Verifica se o loginBean e o usuário logado não estão nulos
            if (loginBean != null && loginBean.getUsuarioLogado() != null) {                
                // 2. Pega o login através do getNomeUsuario
                loginDoUsuario = loginBean.getUsuarioLogado().getNomeUsuario();
            }
            
            // Chama o seu service especializado
            pessoaService.salvar(getEntidade(), "Pessoas", loginDoUsuario);
            //atualizarRegistros();
            
            // 2. RECARREGA usando o método otimizado ANTES da tela renderizar
            pessoaRepository.porId(this.entidade.getId());
            messages.info("Pessoa salva com sucesso!");
            prepararNovo(); // Limpa o formulário
            
            PrimeFaces.current().ajax().update(Arrays.asList("frm:dataTable"));
            
        } catch (NegocioException e) {
            messages.error(e.getMessage());
        }
    }
    
    @Override
    public void excluir() {
    	try {
    		String loginDoUsuario = "SISTEMA"; // Valor padrão de segurança
    		
    		loginDoUsuario = loginBean.getUsuarioLogado().getNomeUsuario();
    		
    		pessoaService.excluir(this.entidade, "Pessoas", loginDoUsuario);
	        this.entidade = null;
	        atualizarRegistros(); // Atualiza a lista após remover
	        messages.info("Pessoa excluída com sucesso!");
    	} catch (NegocioException e) {
            messages.error(e.getMessage());
        }
    }
    
    @Override
    public void prepararNovo() {

        // 1. Instancia o tipo correto baseado na seleção atual
        if ("JURIDICA".equals(this.tipoPessoa)) {
            this.entidade = new PessoaJuridica();
        } else {
            this.entidade = new PessoaFisica();
            this.tipoPessoa = "FISICA"; // Garante consistência
        }

        // 2. Instancia o objeto de detalhes (essencial para evitar Target Unreachable)
        DadosCliente dados = new DadosCliente();
        
        // 3. Faz a ligação bidirecional (Pai aponta pro Filho e Filho pro Pai)
        dados.setPessoa(this.entidade);
        this.entidade.setDadosCliente(dados);
        
        DadosFuncionario dadosFuncionario = new DadosFuncionario();
        
        dadosFuncionario.setPessoa(this.entidade);
        this.entidade.setDadosFuncionario(dadosFuncionario);
        
        DadosFornecedor dadosFornecedor = new DadosFornecedor();
        
        dadosFornecedor.setPessoa(this.entidade);
        this.entidade.setDadosFornecedor(dadosFornecedor);
        
        System.out.println("Instanciando entidade para Tipo: " + this.tipoPessoa);
    	
    }

    // Chamado pelo <p:ajax> quando o rádio button muda
    // Chamado quando o usuário troca o botão de rádio na tela
    public void alternarTipoPessoa() {
        prepararNovo();
    }
    
    @Override
    public void prepararEdicao() {
    	
    	// 1. Verifica qual é a classe real da entidade que veio da tabela
        if (this.entidade instanceof PessoaJuridica) {
            this.tipoPessoa = "JURIDICA";
        } else if (this.entidade instanceof PessoaFisica) {
            this.tipoPessoa = "FISICA";
        }

        // 2. Garante que os DadosCliente não sejam nulos para evitar erro nos campos de limite/crédito
        if (this.entidade.getDadosCliente() == null) {
            DadosCliente dados = new DadosCliente();
            dados.setPessoa(this.entidade);
            this.entidade.setDadosCliente(dados);
        }
        
        if (this.entidade.getDadosFuncionario() == null) {
        	DadosFuncionario dadosFuncionario = new DadosFuncionario();
        	dadosFuncionario.setPessoa(this.entidade);
            this.entidade.setDadosFuncionario(dadosFuncionario);
        }
        
        if (this.entidade.getDadosFornecedor() == null) {
        	DadosFornecedor dadosFornecedor = new DadosFornecedor();
        	dadosFornecedor.setPessoa(this.entidade);
            this.entidade.setDadosFornecedor(dadosFornecedor);
        }
        
        System.out.println("Editando Entidade Tipo: " + this.tipoPessoa);
        
    }

    @Override
    protected Object getEntidadeId(Pessoa pessoa) {
        return pessoa.getId();
    }
    
    public void todasPessoas() {
        this.listaItens = pessoaRepository.todas();
    }
    
    private void atualizarRegistros() {
        if (jaHouvePesquisa()) {
            pesquisar();
        } else {
            todasPessoas();
        }
    }
    
    private boolean jaHouvePesquisa() {
        return termoPesquisa != null && !termoPesquisa.isEmpty();
    }
    
    public void handleFileUpload(org.primefaces.event.FileUploadEvent event) {
        try {
            UploadedFile arquivoUpload = event.getFile();
            
            // 1. BUSCA O CAMINHO DINÂMICO QUE VOCÊ CONFIGUROU NA TELA!
            String diretorioDestino = configuracaoService.getCaminhoUpload();
            
            // 2. Garante que a pasta física existe no servidor. Se não existir, o Java cria!
            File pasta = new File(diretorioDestino);
            if (!pasta.exists()) {
                pasta.mkdirs();
            }
            
            // 3. Cria o arquivo final no diretório configurado
            File arquivoFinal = new File(pasta, arquivoUpload.getFileName());
            
            // 4. Fluxo padrão de escrita do Java (Stream)
            try (InputStream input = arquivoUpload.getInputStream();
                 FileOutputStream output = new FileOutputStream(arquivoFinal)) {
                
                byte[] buffer = new byte[1024];
                int bytesLidos;
                while ((bytesLidos = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesLidos);
                }
            }
            
            // Salva o caminho ou o nome do arquivo no seu objeto Produto antes de mandar pro banco...
            System.out.println("Arquivo salvo com sucesso em: " + arquivoFinal.getAbsolutePath());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Pessoa getEntidade() {
        return this.entidade;
    }
    
    // Adicione estes dois métodos no final do seu PessoaBean.java
    public String getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(String tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }
    
    
}