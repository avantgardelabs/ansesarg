package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.util.Arrays;
import java.util.List;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Condicion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.FilaTablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.custom.BaseQuery;

@Name("filaTablaDecisionList")
public class FilaTablaDecisionList extends BaseQuery<Condicion>{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3591649337839415765L;
	
	private static final String[] RESTRICTIONS = {
		"lower(filaTablaDecision.nombre) like lower(concat(#{reglaList.condicion.nombre},'%'))",
		"lower(filaTablaDecision.descripcion) like lower(concat(#{tablaDecisionList.tablaDecision.descripcion},'%'))",
		"lower(filaTablaDecision.instrumentos) like lower(concat(#{tablaDecisionList.tablaDecision.instrumentos},'%'))",
		"lower(filaTablaDecision.dominio.nombre) like lower(concat(#{tablaDecisionList.tablaDecision.dominio.nombre},'%'))",
	};

	@Override
	public String getEjbql() {
		return "select filaTablaDecision from FilaTablaDecision filaTablaDecision";
	}


	private FilaTablaDecision filaTablaDecision = new FilaTablaDecision();

	public FilaTablaDecision getFilaTablaDecision() {
		return filaTablaDecision;
	}

	@Override
	public List<String> getRestrictions() {
		return Arrays.asList(RESTRICTIONS);
	}
}
