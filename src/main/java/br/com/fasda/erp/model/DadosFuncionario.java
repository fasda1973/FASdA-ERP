package br.com.fasda.erp.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import br.com.fasda.erp.util.DateUtils;
import br.com.fasda.erp.util.ObjetoFormataUtil;

@Entity
@Table(name = "dados_funcionario")
public class DadosFuncionario implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    private Long id;

    @OneToOne
    @MapsId // Faz com que o ID desta tabela seja o mesmo ID da tabela Pessoa
    @JoinColumn(name = "pessoa_id")
    private Pessoa pessoa;
    
    @Column(name = "matricula", length = 20, nullable = false, unique = true)
    private String matricula;
    
    @Column(name = "data_admissao")
    private LocalDate dataAdmissao;
    
    // --- NOVOS CAMPOS ---
    @Column(length = 100)
    private String cargo;

    @Column(name = "salario_base")
    private BigDecimal salarioBase = BigDecimal.ZERO;

    @Column(name = "data_demissao")
    private LocalDate dataDemissao;

    @Column(length = 30)
    private String ctps;

    @Column(name = "pis_pasep", length = 20)
    private String pisPasep;
    
    @OneToOne(mappedBy = "pessoa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DadosFuncionario dadosFuncionario;

    // Getters e Setters
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public LocalDate getDataAdmissao() {
		return dataAdmissao;
	}
	
	public String getDataAdmissaoFormatada() {
	    return ObjetoFormataUtil.formatarData(this.dataAdmissao);
	}

	public void setDataAdmissao(LocalDate dataAdmissao) {
		this.dataAdmissao = dataAdmissao;
	}

	public DadosFuncionario getDadosFuncionario() {
		return dadosFuncionario;
	}

	public void setDadosFuncionario(DadosFuncionario dadosFuncionario) {
		this.dadosFuncionario = dadosFuncionario;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public BigDecimal getSalarioBase() {
		return salarioBase;
	}

	public void setSalarioBase(BigDecimal salarioBase) {
		this.salarioBase = salarioBase;
	}

	public LocalDate getDataDemissao() {
		return dataDemissao;
	}
	
	public String getDataDemissaoFormatada() {
	    return ObjetoFormataUtil.formatarData(this.dataDemissao);
	}

	public void setDataDemissao(LocalDate dataDemissao) {
		this.dataDemissao = dataDemissao;
	}

	public String getCtps() {
		return ctps;
	}

	public void setCtps(String ctps) {
		this.ctps = ctps;
	}

	public String getPisPasep() {
		return pisPasep;
	}

	public void setPisPasep(String pisPasep) {
		this.pisPasep = pisPasep;
	}
	
	
    
}