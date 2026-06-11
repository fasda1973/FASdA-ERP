package old;

public enum TipoEmpresa {
	
	MEI("Microempreendedor Individual"),
	EIRELI("Empresa individual de Reponsabilidade Limitada"),
	LTDA("Sociedade Limitada"),
	SA("Sociedade An�nima");
	
	private String descricao;
	
	TipoEmpresa(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	
}
