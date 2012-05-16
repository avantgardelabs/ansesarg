package ar.gob.anses.prissa.mi.asistente_reglas.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Accion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Condicion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Descisor;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.FilaTablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.ReglaFilaPseudocodigo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.ReglaPseudocodigo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IVersionable;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IVersionableRegla;
import ar.gob.anses.prissa.mi.asistente_reglas.stuff.DiffMatchPatch;
import ar.gob.anses.prissa.mi.asistente_reglas.stuff.DiffMatchPatch.Diff;
import ar.gob.anses.prissa.mi.asistente_reglas.stuff.DiffMatchPatch.Patch;

@Name("diferenciasService")
@AutoCreate
public class DiferenciasService {
	
	@In EntityManager entityManager;
	
	/**
	 * Obtiene las diferencias de Acciones y Condiciones la Regla 2 respecto de la Regla 1
	 * 
	 * @param idRegla1
	 * @param idRegla2
	 * @return
	 */
	public List<DiferenciasBean> diferencias(Long idRegla1, Long idRegla2, Class<? extends IVersionableRegla> clazz) {
		IVersionableRegla td1 = entityManager.find(clazz, idRegla1);
		IVersionableRegla td2 = entityManager.find(clazz, idRegla2);
		Set<Condicion> condiciones1 = obtenerCondiciones(td1);
		Set<Condicion> condiciones2 = obtenerCondiciones(td2);
		
		List<DiferenciasBean> ret = diferenciasVersionables(condiciones1, condiciones2);
		
		Set<Accion> acciones1 = obtenerAcciones(td1);
		Set<Accion> acciones2 = obtenerAcciones(td2);
		
		ret.addAll(diferenciasVersionables(acciones1, acciones2));
		
		
		return ret;
	}
	
	
	private Set<Accion> obtenerAcciones(IVersionableRegla iv) {
		if (iv instanceof TablaDecision) {
			return obtenerAccionesTablaDecision((TablaDecision)iv);
		}
		if (iv instanceof ReglaPseudocodigo) {
			return obtenerAccionesReglaPseudocodigo((ReglaPseudocodigo)iv);
		}
		return null;
	}


	private Set<Accion> obtenerAccionesReglaPseudocodigo(ReglaPseudocodigo iv) {
		Set<Accion> ret = new HashSet<Accion>();
		List<ReglaFilaPseudocodigo> filas = iv.getReglasFilas();
		for (Iterator<ReglaFilaPseudocodigo>  iterator = filas.iterator(); iterator.hasNext();) {
			ReglaFilaPseudocodigo reglaFilaPseudocodigo = (ReglaFilaPseudocodigo) iterator
					.next();
			ret.addAll(reglaFilaPseudocodigo.getAcciones());
		}
		return ret;
	}


	private Set<Condicion> obtenerCondiciones(IVersionableRegla iv) {
		if (iv instanceof TablaDecision) {
			return obtenerCondicionesTablaDecision((TablaDecision)iv);
		}
		if (iv instanceof ReglaPseudocodigo) {
			return obtenerCondicionesReglaPseudocodigo((ReglaPseudocodigo)iv);
		}
		return null;
	}


	private Set<Condicion> obtenerCondicionesReglaPseudocodigo(
			ReglaPseudocodigo regla) {
		//TODO: No esta implementado aun los objetos condicion en regla por pseudocodigo
		
		return new HashSet<Condicion>();
	}


	private List<DiferenciasBean> diferenciasVersionables(Set<? extends IVersionable> vers1, Set<? extends IVersionable> vers2)  {
		//La interseccion de las condiciones son reglas que no se modificaron 
		Set<IVersionable> sinModificacion = new HashSet<IVersionable>(vers1);
		sinModificacion.retainAll(vers2);
		
		//Condiciones eliminadas de la regla 1, aparecen en 1 pero no en la regla 2
		Set<IVersionable> eliminados =  new HashSet<IVersionable>(vers1);
		eliminados.removeAll(vers2);
		
		//Condiciones agregadas a la regla 1, aparecen en la 2 pero no en la regla 1
		Set<IVersionable> agregados =  new HashSet<IVersionable>(vers2);
		agregados.removeAll(vers1);
		
		//Condiciones modificadas, son aquellas reglas que coinciden en el nombre
		//pero difieren la version. Se compara los eliminados con los agregados
		Set<IVersionable> modificadas = new HashSet<IVersionable>();
		for (Iterator<IVersionable> iterator = agregados.iterator(); iterator.hasNext();) {
			IVersionable condicion_agregada = (IVersionable) iterator.next();
			for (Iterator<IVersionable> iterator2 = eliminados.iterator(); iterator2
					.hasNext();) {
				IVersionable condicion_eliminada = (IVersionable) iterator2.next();
				//si los nombres de las condiciones coinciden, entonces no es ni un agregado ni un elminado
				//es una modificada
				if (condicion_agregada.getIdOriginal().equals(condicion_eliminada.getIdOriginal())) {
					modificadas.add(condicion_agregada);
					modificadas.add(condicion_eliminada);
					iterator.remove();
					iterator2.remove();
					break;
				}
			}
		}
		
		List<DiferenciasBean> ret = new ArrayList<DiferenciasBean>();
		ret.addAll(wrapVersionable(sinModificacion, Estado.SIN_MODIFICAR));
		ret.addAll(wrapVersionable(modificadas, Estado.MODIFICADO));
		ret.addAll(wrapVersionable(agregados, Estado.AGREGADO));
		ret.addAll(wrapVersionable(eliminados, Estado.ELIMINADO));
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	private List<DiferenciasBean> wrapVersionable(Set<? extends IVersionable> vers, Estado estado) {
		List<DiferenciasBean> ret = new ArrayList<DiferenciasBean>(vers.size());
		for (Iterator iterator = vers.iterator(); iterator.hasNext();) {
			IVersionable versionable = (IVersionable) iterator.next();
			ret.add(new DiferenciasBean(versionable, estado));
		}
		return ret;
	}
	
	
	private Set<Condicion> obtenerCondicionesTablaDecision(TablaDecision td) {
		Set<Condicion> ret = new HashSet<Condicion>();
		Iterator<FilaTablaDecision> itFTD = td.getFilas().iterator();
		while (itFTD.hasNext()) {
			FilaTablaDecision filaTablaDecision = (FilaTablaDecision) itFTD
					.next();
			Iterator<Descisor> itD = filaTablaDecision.getValores().iterator();
			while (itD.hasNext()) {
				Descisor descisor = (Descisor) itD.next();
				ret.add(descisor.getCondicion());
			}
		}
		return ret;
	}
	
	
	private Set<Accion> obtenerAccionesTablaDecision(TablaDecision td) {
		Set<Accion> ret = new HashSet<Accion>();
		Iterator<FilaTablaDecision> itFTD = td.getFilas().iterator();
		while (itFTD.hasNext()) {
			FilaTablaDecision filaTablaDecision = (FilaTablaDecision) itFTD
					.next();
			ret.addAll(filaTablaDecision.getAcciones());
		}
		return ret;
	}



	public static enum Tipo {
		ACCION   { String value() { return "Accion"; } },
		CONDICION  { String value() { return "Condicion"; } };
		abstract String value();
	}
	
	public static enum Estado {
		ELIMINADO   { 	public String value() { return "Eliminado"; }; 
						public String color() { return "LightCoral"; }; },
		AGREGADO 	{ 	public String value() { return "Agregado"; };
						public String color() { return "Khaki"; }; },
		SIN_MODIFICAR { public String value() { return "Sin Modificar"; };
						public String color() { return "White"; }; },
		MODIFICADO  { 	public String value() { return "Modificado"; };
						public String color() { return "MediumSeaGreen"; }; };
		public abstract String value();
		public abstract String color();
	}
	
	
	@SuppressWarnings("serial")
	public static class DiferenciasBean implements Serializable {
		
		private String estado;
		private String tipo;
		private String nombre;
		private Long version;
		private Long id;
		private Long idOriginal;
		private String color;
		
		public DiferenciasBean(IVersionable entidad, Estado estado) {
			
			if (entidad instanceof Condicion) {
				Condicion cond = (Condicion) entidad;
				this.nombre = cond.getNombre();
				this.tipo = Tipo.CONDICION.value();
				
			}
			else if (entidad instanceof Accion) {
				Accion accion = (Accion) entidad;
				this.nombre = accion.getNombre();
				this.tipo = Tipo.ACCION.value();
			}
			this.id = entidad.getId();
			this.idOriginal = entidad.getIdOriginal();
			this.version = entidad.getVersionNumber();
			this.estado = estado.value();
			this.color = estado.color();
			
		}

		public String getEstado() {
			return estado;
		}

		public String getTipo() {
			return tipo;
		}

		public String getNombre() {
			return nombre;
		}

		public Long getVersion() {
			return version;
		}

		public Long getId() {
			return id;
		}

		public Long getIdOriginal() {
			return idOriginal;
		}
		

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((idOriginal == null) ? 0 : idOriginal.hashCode());
			result = prime * result
					+ ((version == null) ? 0 : version.hashCode());
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
			DiferenciasBean other = (DiferenciasBean) obj;
			if (idOriginal == null) {
				if (other.idOriginal != null)
					return false;
			} else if (!idOriginal.equals(other.idOriginal))
				return false;
			if (version == null) {
				if (other.version != null)
					return false;
			} else if (!version.equals(other.version))
				return false;
			return true;
		}

		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}
	}

	
	public String diferenciasDRL(Long idRegla1, Long idRegla2, Class<? extends IVersionableRegla> clazz) {
		IVersionableRegla td1 = entityManager.find(clazz, idRegla1);
		IVersionableRegla td2 = entityManager.find(clazz, idRegla2);
		String code1 = obtenerCodigoDRL(td1);
		String code2 = obtenerCodigoDRL(td2);
		if (code1.isEmpty()&&code2.isEmpty()) {
			return null;
		}
		DiffMatchPatch dmt = new DiffMatchPatch();
		LinkedList<Diff> diffs = dmt.diff_main(code1, code2, false);
		String ret = dmt.diff_myHtml((LinkedList<Diff>)diffs);
//		LinkedList<Patch> patchs = dmt.patch_make(code1, code2);
//		String ret = dmt.patch_toText(patchs);
		return ret;
	}
	
	private String obtenerCodigoDRL(IVersionableRegla ivr) {
		String ret =  ivr.getCodigoDRL();
		return (ret==null)?"":ret;
	}
}
	

