package ar.gob.anses.prissa.mi.asistente_reglas.services.impl;

import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Accion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.AccionActivaRegla;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.AccionModificaHecho;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.AccionModificaHechoLiteral;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.utils.CloneUtil;

@Name("servPersistAccion")
public class PersistenceServiceAccionImpl extends
		PersistenceServiceVersionImpl<Accion> {

	@Override
	protected Accion cloneEntity(Accion entidad) {
		Accion newEntidad = super.cloneEntity(entidad);
		//FIXME: al momento no funciona la alta y edicion de accion activa por regla
		//se deberia probar parte del codigo comentado abajo para permitir el versionado
		newEntidad.setAccionActivaRegla(null);

//		if (entidad.getAccionActivaRegla() != null) {
//			AccionActivaRegla acr = (AccionActivaRegla) CloneUtil
//					.cloneEntity(entidad.getAccionActivaRegla());
//			acr.setId(null);
//			newEntidad.setAccionActivaRegla(acr);
//			if (acr.getReglaPorPseudocodigo()!=null&&acr.getReglaPorPseudocodigo().getId()!=null) {
//				acr.setReglaPorPseudocodigo(null);
//			}
//			if (acr.getTablaDecision()!=null&&acr.getTablaDecision().getId()!=null) {
//				acr.setTablaDecision(null);
//			}
//		}
		//END FIXME
		
		if ((entidad.getAccionModificaHecho() != null) &&(entidad.getAccionModificaHecho().getId() != null)) {
			AccionModificaHecho amh = (AccionModificaHecho) CloneUtil
					.cloneEntity(entidad.getAccionModificaHecho());
			amh.setId(null);
			newEntidad.setAccionModificaHecho(amh);
			
			//TODO: No funciona todavia este featire
			amh.setAccionModificaHechoFuncion(null);
			
			if (amh.getAccionModificaHechoLiteral()!=null && amh.getAccionModificaHechoLiteral().getId()!=null) {
				AccionModificaHechoLiteral amhl = (AccionModificaHechoLiteral) CloneUtil.cloneEntity(entidad.getAccionModificaHecho().getAccionModificaHechoLiteral());
				amhl.setId(null);
				amh.setAccionModificaHechoLiteral(amhl);
			} else {
				amh.setAccionModificaHechoLiteral(null);
			}
			
		} else {
			newEntidad.setAccionModificaHecho(null);
		}
		
		return newEntidad;
	}

}
