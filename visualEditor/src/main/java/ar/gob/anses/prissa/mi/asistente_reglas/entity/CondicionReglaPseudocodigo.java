package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IEntity;

@Entity
@Name("condicionReglaPseudocodigo")
@Scope(ScopeType.EVENT)
public class CondicionReglaPseudocodigo extends NodoCondicionReglaPseudocodigo implements IEntity {
		
	private static final long serialVersionUID = -7586417291898859960L;
		
		
		@Id @GeneratedValue(strategy=GenerationType.AUTO)
		private Long id;
		
		private Entidad entidad = new Entidad();
		
		
		private Clausula clausula;
		
		private String tipo = this.getClass().getSimpleName();
		
		@ManyToOne(fetch = FetchType.LAZY)
		private ReglaPseudocodigo reglaPseudocodigo;
		
		@OneToMany(cascade={CascadeType.PERSIST})
		private List<Clausula> clausulas = new ArrayList<Clausula>();
		
		@In(create = true,required=false) @Out(required=false)
		private Clausula clausulaActual = new Clausula();
		
		
		public void setId(Long id) {
			this.id = id;
		}

		public Long getId() {
			return id;
		}
		
		public void setEntidad(Entidad entidad) {
			this.entidad = entidad;
		}

		public Entidad getEntidad() {
			return entidad;
		}

		public void setClausula(Clausula clausula) {
			this.clausula = clausula;
		}

		public Clausula getClausula() {
			return clausula;
		}

		public void setReglaPseudocodigo(ReglaPseudocodigo reglaPseudocodigo) {
			this.reglaPseudocodigo = reglaPseudocodigo;
		}

		public ReglaPseudocodigo getReglaPseudocodigo() {
			return reglaPseudocodigo;
		}


		public void setClausulas(List<Clausula> clausulas) {
			this.clausulas = clausulas;
		}

		public List<Clausula> getClausulas() {
			return clausulas;
		}

		public void setClausulaActual(Clausula clausulaActual) {
			this.clausulaActual = clausulaActual;
		}

		public Clausula getClausulaActual() {
			return clausulaActual;
		}
		
		
		public String getTipo(){
			return tipo;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CondicionReglaPseudocodigo other = (CondicionReglaPseudocodigo) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}


}

