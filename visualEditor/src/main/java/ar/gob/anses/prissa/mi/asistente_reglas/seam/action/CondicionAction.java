package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Condicion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Literal;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
import ar.gob.anses.prissa.mi.asistente_reglas.services.PersistenceService;

@SuppressWarnings("serial")
@Name("condicionAction")
@Scope(ScopeType.SESSION)
public class CondicionAction implements Serializable, ICondicionAction {

	@In	PersistenceService persistenceService;
	
	@In(create = true, required = false)
	@Out(required = false)
	List<Entidad> entidades;
	private List<Atributo> hechos;

	@In(create = true,required=false) @Out(required=false)
    Condicion condicion;

	@In(create = true, required = false)
	Entidad entidad;
	@Logger
	Log log;

	@Out(required=false)
	Literal literal;

	private List<Literal> literalesSeleccionados;

	public List<Atributo> getHechos() {
		return hechos;
	}

	public void setHechos(List<Atributo> hechos) {
		this.hechos = hechos;
	}

	public CondicionAction() {

	}

	
	@Begin
	public String init() {

		entidades = (List<Entidad>) persistenceService
				.createQuery(
						""
								+ "select"
								+ "	Entidad from Entidad as Entidad order by Entidad.nombre"
								+ "").getResultList();

		hechos = new ArrayList<Atributo>();

		/*
		 * hechos =(List<Atributo>)entityManager.createQuery("" + "select" +
		 * "	Atributo from Atributo as Atributo order by Atributo.nombre" +
		 * "").getResultList();
		 */
		return "/editor/nueva_condicion.xhtml";

	}

	@End
	public String cancelar() {
		return "/home.xhtml";
	}

	public void filtrarHechos() {
		log.info("Filtrando hechos de la entidad " + entidad.getNombre());
		hechos = entidad.getAtributos();
	}

	
	@End
	public String guardarCondicion() {
		
		persistenceService.save(condicion);
		FacesMessages.instance().add(
				new FacesMessage("Se ha creado una nueva condicion de regla"));
		literalesSeleccionados = new ArrayList();
		return "/exito.xhtml";
	}

	public void guardarLiteral(Literal l) {

		if (condicion.getLiterales() == null){
			log.info("La lista es nulla");
			condicion.setLiterales(new ArrayList<Literal>());
		}
			

		condicion.getLiterales().add(l);
	}

	public void eliminarLiteral(Literal l) {

		Literal lit;
		for (int i = 0; i < condicion.getLiterales().size(); i++) {
			lit = (Literal) condicion.getLiterales().get(i);
			if (lit == l) {
				condicion.getLiterales().remove(i);
				break;
			}
		}
	}

}
