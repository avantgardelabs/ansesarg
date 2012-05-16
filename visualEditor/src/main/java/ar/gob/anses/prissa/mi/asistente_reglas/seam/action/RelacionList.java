package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Relacion;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.custom.BaseQuery;
@Name("relacionList")
@Scope(ScopeType.CONVERSATION)
public class RelacionList extends BaseQuery<Relacion>{
	
	private static final long serialVersionUID = 1472526698183295082L;
	
	private static final String[] RESTRICTIONS = {"lower(relacion.entidad.nombre) like lower(concat('%',concat(#{relacionList.relacion.entidad.nombre},'%')))",
		"lower(relacion.atributo.nombre) like lower(concat('%',concat(#{relacionList.relacion.atributo.nombre},'%')))"};
		
	@Logger
	private Log log;
	private int scrollerPage;
	private boolean firstTime;
	List<Relacion>relaciones;
	
	@In	EntityManager entityManager;
	@In FacesMessages facesMessages;	
	
	private Relacion relacion = new Relacion();		
	private Entidad entidad = new Entidad();
	private Atributo atributo = new Atributo();		
		
	public RelacionList() {
		this.relacion.setEntidad(entidad);		
		this.relacion.setAtributo(atributo);	
		this.firstTime = true;
	}
	
	public Entidad getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	public Atributo getAtributo() {
		return atributo;
	}

	public void setAtributo(Atributo atributo) {
		this.atributo = atributo;
	}
	
	public List<Relacion> getRelaciones() {
		relaciones = new RelacionList().getMyResultList();		
		return relaciones;
	}

	public void setRelaciones(List<Relacion> relaciones) {
		this.relaciones = relaciones;
	}

	@Override
	public String getEjbql() {
		return "select relacion from Relacion relacion";
	}

	@Override
	public Integer getMaxResults() {
		return 25;
	}
	
	public Relacion getRelacion() {
		return relacion;
	}
	
	@Override
	public List<String> getRestrictions() {		
		return Arrays.asList(RESTRICTIONS);
	}
	
	@Override
	public java.util.List<Relacion> getResultList() {		
		relaciones = null;		
		if (this.getOrder()!= null){
			if (!this.getOrder().isEmpty())	{	
				this.firstTime = false;
			}
		}	
		if(!this.firstTime){
			this.clearDataModel();
			relaciones = super.getResultList();				
		}	
		return relaciones;
	}	
	
	public String searchWrap(){		
		this.firstTime = false;			
		return "/relacionList.xhtml";
	}
	
	public void limpiar(){
		this.entidad.setNombre("");
		this.atributo.setNombre("");		
	}
	
	public boolean isFirstTime() {
		return firstTime;
	}


	public void setFirstTime(boolean firstTime) {
		this.firstTime = firstTime;
	}
	
	public java.util.List<Relacion> getMyResultList(){
    	
			this.setFirstTime(false);       			
    	
    	return this.getResultList();
	}		
	
	public int getScrollerPage() {
		return scrollerPage;
	}
 
	public void setScrollerPage(int scrollerPage) {
		this.scrollerPage = scrollerPage;
	}	
	
	public String removeRelation(Relacion relacion ) throws Exception
	{	
		try {		
		entityManager.remove(relacion);
		entityManager.flush();	
		
		facesMessages.add("Relacion eliminada carrectamente");
		}
		catch (Exception ex) {
			throw ex;
		}
		return "/relacionList.xhtml";
	}
	
}
