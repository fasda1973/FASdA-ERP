package old;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class RamoAtividadeConverter implements Converter {
	
	private List<RamoAtividade> listaRamoAtividade;

	public RamoAtividadeConverter(List<RamoAtividade> listaRamoAtividade) {
		this.listaRamoAtividade = listaRamoAtividade;
	}

	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null) {
			return null;
		}
		
		Long id = Long.valueOf(value);
		
		for (RamoAtividade ramoAtividade: listaRamoAtividade) {
			if (id.equals(ramoAtividade.getId())) {
				return ramoAtividade;
			}
		}
		
		return null;
	}

	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return null;
		}
		
		RamoAtividade ramoAtividade = (RamoAtividade) value;
		
		return ramoAtividade.getId().toString();
	}

}
