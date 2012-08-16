package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.model_gen.ModelFactory;

@Name("modelGenController")
@Scope(ScopeType.CONVERSATION)
public class ModelGenController {

	@In
	EntityManager entityManager;

	@Logger
	Log log;

	private ModelFactory modelFactory = ModelFactory.createFactory();

	public String init() {
		return "/modelGen.xhtml";
	}

	public void generar() {
		// ModelFactory factory=
		modelFactory.setLog(log);
		modelFactory.setEntityManager(entityManager);
		modelFactory.generate();
		FacesMessages.instance().add("Se ha generado el modelo de dominio con exito");
	}

	public ModelFactory getModelFactory() {
		return modelFactory;
	}

	public void setModelFactory(ModelFactory modelFactory) {
		this.modelFactory = modelFactory;
	}

}
