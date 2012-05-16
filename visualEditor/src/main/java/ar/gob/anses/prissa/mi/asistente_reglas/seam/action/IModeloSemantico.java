package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import javax.ejb.Local;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;

@Local
public interface IModeloSemantico {
	
	public void agregarEntidad(Entidad entidad);

}
