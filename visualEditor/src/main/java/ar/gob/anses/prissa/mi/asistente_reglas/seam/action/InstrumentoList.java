package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.util.Arrays;
import java.util.List;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Instrumento;

@Name("instrumentoList")
public class InstrumentoList extends EntityQuery<Instrumento>{
	
	private static final long serialVersionUID = -4887229858868490353L;
	
	private static final String[] RESTRICTIONS = {
		"lower(instrumento.descripcion) like lower(concat(#{instrumentoList.instrumento.descripcion},'%'))", };
	
	@Override
	public String getEjbql() {
		return "select instrumento from Instrumento instrumento";
	}


	private Instrumento instrumento = new Instrumento();

	public Instrumento getInstrumento() {
		return instrumento;
	}

	@Override
	public List<String> getRestrictions() {
		return Arrays.asList(RESTRICTIONS);
	}
		
}
