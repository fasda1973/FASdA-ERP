package br.com.fasda.erp.controller;

import java.io.Serializable;

import java.util.stream.LongStream;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import br.com.fasda.erp.repository.PessoaRepository;
import br.com.fasda.erp.repository.UsuarioRepository;

@Named
@ViewScoped
public class DashboardBean implements Serializable {
    private static final long serialVersionUID = 1L;
   
    @Inject
    private PessoaRepository pessoaRepository;
    @Inject
    private UsuarioRepository usuarioRepository; 

    private Long totalClientes; //roles em pessoaRepository
    private Long totalFuncionarios; //roles em pessoaRepository
    private Long totalFornecedores; //roles em pessoaRepository
    private Long totalPessoas;
    private Long totalUsuarios;
    private BarChartModel barModel;

    public void inicializar() {
    	this.totalClientes = pessoaRepository.contarTodosClientes();
    	this.totalFuncionarios = pessoaRepository.contarTodosFuncionarios();
        this.totalFornecedores = pessoaRepository.contarTodosFornecedores();
        this.totalPessoas = pessoaRepository.contarTodas();
        this.totalUsuarios = usuarioRepository.contarTodos();
        createBarModel();
    }
    
    private void createBarModel() {
    	barModel = new BarChartModel();
    	
    	// Série 1: ClienteRepository (Ficará Verde)
        ChartSeries serieClientes = new ChartSeries();
        serieClientes.setLabel("ClienteRepository");
        serieClientes.set("Cadastros", totalClientes);
    	
    	// Série 1: FuncionarioRepository (Ficará Verde)
        ChartSeries serieFuncionarios = new ChartSeries();
        serieFuncionarios.setLabel("FuncionarioRepository");
        serieFuncionarios.set("Cadastros", totalFuncionarios);

        // Série 1: FornecedorRepository (Ficará Verde)
        ChartSeries serieFornecedores = new ChartSeries();
        serieFornecedores.setLabel("FornecedorRepository");
        serieFornecedores.set("Cadastros", totalFornecedores);
        
        // Série 2: PessoaRepository (Ficará Amarelo)
        ChartSeries seriePessoas = new ChartSeries();
        seriePessoas.setLabel("PessoaRepository");
        seriePessoas.set("Cadastros", totalPessoas);
        
        // Série 3: Usuários (Ficará Azul)
        ChartSeries serieUsuarios = new ChartSeries();
        serieUsuarios.setLabel("Usuários");
        serieUsuarios.set("Cadastros", totalUsuarios); // O nome do eixo X deve ser igual

        barModel.addSeries(serieClientes);
        barModel.addSeries(serieFuncionarios);
        barModel.addSeries(serieFornecedores);
        barModel.addSeries(seriePessoas);
        barModel.addSeries(serieUsuarios);

        // Configurações visuais
        barModel.setTitle("Comparativo de Cadastros");
        barModel.setAnimate(true);
        //barModel.setLegendPosition("ne");
        
        // Agora sim! A primeira série usa a primeira cor, a segunda usa a segunda.
        barModel.setSeriesColors("4242FF, FF2F2F, 4CAF50, FF9800, 2196F3"); 

        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setMin(0);
        
        // Vai listando quantos totais quiser aqui dentro:
        long maxValor = LongStream.of(totalClientes, totalFuncionarios, totalFornecedores, totalPessoas, totalUsuarios)
                                  .max()
                                  .orElse(0); // caso a lista esteja vazia, assume 0
        
        // Ajuste o Max se quiser dar um "respiro" no topo do gráfico
        yAxis.setMax(maxValor + 5);        
    }

    // Getters somente

    public Long getTotalClientes() {
		return totalClientes;
	}
    
    public Long getTotalFuncionarios() {
		return totalFuncionarios;
	}
	
    public Long getTotalFornecedores() {
		return totalFornecedores;
	}
	
	public Long getTotalPessoas() {
		return totalPessoas;
	}
	
	 public Long getTotalUsuarios() {
		return totalUsuarios;
	}

	public BarChartModel getBarModel() {
		return barModel;
	}
   
    
}