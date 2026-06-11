package old;

import java.util.Arrays;
import java.util.List;

import javax.faces.convert.Converter;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import br.com.fasda.erp.controller.CrudBean;
import br.com.fasda.erp.model.Pessoa;

@Named
@ViewScoped
public class EmpresaBean extends CrudBean<Empresa> {

    private static final long serialVersionUID = 1L;

    @Inject
    private EmpresaRepository empresaRepository;

    @Inject
    private RamoAtividades ramoAtividades;

    @Inject
    private EmpresaService empresaService;

    private Converter ramoAtividadeConverter;
    
    public EmpresaBean() {
        // Passamos a classe Pessoa para o CrudBean
        super(Empresa.class);
    }

    // --- MÉTODOS OBRIGATÓRIOS (OVERRIDE) ---

    @Override
    public void pesquisar() {
        if (termoPesquisa == null || termoPesquisa.trim().isEmpty()) {
            this.listaItens = empresaRepository.todas(); // Traz tudo se não houver filtro
        } else {
            this.listaItens = empresaRepository.pesquisar(this.termoPesquisa);
        }
    }

    @Override
    public void salvar() {
        // Usamos 'entidade' que vem do CrudBean (substitui 'empresa')
        empresaService.salvar(this.entidade);
        atualizarRegistros();
        messages.info("Empresa salva com sucesso!!!");
        
        PrimeFaces.current().ajax().update(Arrays.asList("frm:dataTable", "frm:messages"));
    }

    @Override
    public void excluir() {
        empresaService.excluir(this.entidade);
        this.entidade = null;
        atualizarRegistros();
        messages.info("Empresa excluída com sucesso!!!");
    }

    @Override
    public void prepararNovo() {
        this.entidade = new Empresa();
    }

    @Override
    public void prepararEdicao() {
        ramoAtividadeConverter = new RamoAtividadeConverter(Arrays.asList(this.entidade.getRamoAtividade()));
    }

    @Override
    protected Object getEntidadeId(Empresa empresa) {
        return empresa.getId();
    }

    // --- MÉTODOS ESPECÍFICOS DE EMPRESA ---

    public void todasEmpresas() {
        this.listaItens = empresaRepository.todas();
    }

    public List<RamoAtividade> CompletarRamoAtividade(String termo) {
        List<RamoAtividade> listaRamoAtividades = ramoAtividades.pesquisar(termo);
        ramoAtividadeConverter = new RamoAtividadeConverter(listaRamoAtividades);
        return listaRamoAtividades;
    }

    private void atualizarRegistros() {
        if (jaHouvePesquisa()) {
            pesquisar();
        } else {
            todasEmpresas();
        }
    }

    private boolean jaHouvePesquisa() {
        return termoPesquisa != null && !termoPesquisa.isEmpty();
    }

    public TipoEmpresa[] getTiposEmpresa() {
        return TipoEmpresa.values();
    }

    public Converter getRamoAtividadeConverter() {
        return ramoAtividadeConverter;
    }
}
