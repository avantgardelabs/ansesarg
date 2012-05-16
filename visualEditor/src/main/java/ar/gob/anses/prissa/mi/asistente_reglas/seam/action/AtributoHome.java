package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import org.jboss.seam.framework.EntityHome;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import org.jboss.seam.annotations.Name;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.custom.BaseHome;

@Name("atributoHome")
public class AtributoHome extends BaseHome<Atributo>{

	private static final long serialVersionUID = 999426568179067865L;

	public void setAtributoId(Long id) {
		setId(id);
	}

	public Long getDominioId(){
		return (Long) getId();
	}

	@Override
	protected Atributo createInstance() {
		Atributo atributo = new Atributo();
		return atributo;
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}

	public Atributo getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	

	
}
