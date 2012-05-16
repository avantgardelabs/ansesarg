package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.util.Arrays;
import java.util.List;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.custom.BaseQuery;
@Name("atributoList")
public class AtributoList extends BaseQuery<Atributo>{
	
	private static final long serialVersionUID = -4887229858868490353L;
	
	private static final String[] RESTRICTIONS = {
		"lower(atributo.nombre) like lower(concat(#{atributoList.atributo.nombre},'%'))", 
		"lower(atributo.entidad.id) like lower(#{atributoList.atributo.entidad.id})", 		
	};

	@Override
	public String getEjbql() {
		return "select atributo from Atributo atributo";
	}

	private Atributo atributo = new Atributo();

	public Atributo getAtributo() {
		return atributo;
	}

	public void setAtributo(Atributo a) {
		atributo = a;
	}
	
	@Override
	public List<String> getRestrictions() {
		return Arrays.asList(RESTRICTIONS);
	}
}
