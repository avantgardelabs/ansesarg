package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IDependeable;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IVersionableRegla;

@SuppressWarnings("serial")
@Entity
@Name("accionActivaRegla")
public class AccionActivaRegla implements IDependeable {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	/*@OneToOne( mappedBy = "accionActivaRegla", cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
	private Accion accion;*/
	
	@ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
	private ReglaPseudocodigo reglaPorPseudocodigo = new ReglaPseudocodigo();
	
	@ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
	private TablaDecision tablaDecision = new TablaDecision();

	/*public Accion getAccion() {
		return accion;
	}

	public void setAccion(Accion accion) {
		this.accion = accion;
	}*/

	public ReglaPseudocodigo getReglaPorPseudocodigo() {
		return reglaPorPseudocodigo;
	}

	public void setReglaPorPseudocodigo(ReglaPseudocodigo reglaPorPseudocodigo) {
		this.reglaPorPseudocodigo = reglaPorPseudocodigo;
	}

	public TablaDecision getTablaDecision() {
		return tablaDecision;
	}

	public void setTablaDecision(TablaDecision tablaDecision) {
		this.tablaDecision = tablaDecision;
	}
	
	public IVersionableRegla getReglaQueActiva(){
		if(this.getTablaDecision()!= null){
			return this.getTablaDecision();
		}else{
			return this.getReglaPorPseudocodigo();
		}
	}

}
