package br.com.fasda.erp.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;



@Entity
@Table(name = "dados_cliente")
public class DadosCliente implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @OneToOne
    @MapsId // Faz com que o ID desta tabela seja o mesmo ID da tabela Pessoa
    @JoinColumn(name = "pessoa_id")
    private Pessoa pessoa;

    @Column(name = "limite_credito", precision = 10, scale = 2)
    private BigDecimal limiteCredito;
    
    // --- NOVOS CAMPOS ---
    @Column(name = "saldo_credito_utilizado", nullable = false)
    private BigDecimal saldoCreditoUtilizado = BigDecimal.ZERO;

    @Column(name = "data_ultima_compra")
    private LocalDateTime dataUltimaCompra;

    @Column(name = "observacao_financeira", columnDefinition = "TEXT")
    private String observacaoFinanceira;

    @Column(name = "bloqueado_por_atraso", nullable = false)
    private boolean bloqueadoPorAtraso = false;
    
    @OneToOne(mappedBy = "pessoa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DadosCliente dadosCliente;

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

	public BigDecimal getLimiteCredito() {
		return limiteCredito;
	}

	public void setLimiteCredito(BigDecimal limiteCredito) {
		this.limiteCredito = limiteCredito;
	}

	public DadosCliente getDadosCliente() {
		return dadosCliente;
	}

	public void setDadosCliente(DadosCliente dadosCliente) {
		this.dadosCliente = dadosCliente;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public BigDecimal getSaldoCreditoUtilizado() {
		return saldoCreditoUtilizado;
	}

	public void setSaldoCreditoUtilizado(BigDecimal saldoCreditoUtilizado) {
		this.saldoCreditoUtilizado = saldoCreditoUtilizado;
	}

	public LocalDateTime getDataUltimaCompra() {
		return dataUltimaCompra;
	}

	public void setDataUltimaCompra(LocalDateTime dataUltimaCompra) {
		this.dataUltimaCompra = dataUltimaCompra;
	}

	public String getObservacaoFinanceira() {
		return observacaoFinanceira;
	}

	public void setObservacaoFinanceira(String observacaoFinanceira) {
		this.observacaoFinanceira = observacaoFinanceira;
	}

	public boolean isBloqueadoPorAtraso() {
		return bloqueadoPorAtraso;
	}

	public void setBloqueadoPorAtraso(boolean bloqueadoPorAtraso) {
		this.bloqueadoPorAtraso = bloqueadoPorAtraso;
	}
	
	
   
}