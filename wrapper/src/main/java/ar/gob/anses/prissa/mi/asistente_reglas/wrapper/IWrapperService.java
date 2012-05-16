package ar.gob.anses.prissa.mi.asistente_reglas.wrapper;

import java.util.List;
import java.util.Set;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Accion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Condicion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.FilaTablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;


public interface IWrapperService {

		
	public Set<Entidad> getEntidades();
	
	
	/*
	 * Dada una regla (fila de tabla de desicion) devuelve el drl correspondiente*/
	public String buildWithDeclaredFacts(FilaTablaDecision regla);
	
	/*
	 * */
	public String buildRuleWithDeclaredFacts();
	
	/*Devuelve el DRL de la regla de la tabla de desicion */
	public String buildRule();
	
	public String buildRule(FilaTablaDecision fila);
	/*
	 * Genera el DRL file de acuerdo a la ruta declarada*/
	public String buildDRLFile(String path);
	
	/*Dada una tabla de decision retorna todas las acciones (agrupadas que estan involucradas en la regla)*/
	public List<Accion> getAcciones();
	
	public List<Condicion> getCondiciones();
	
	public List<Entidad> getEntidades(List<Condicion> condiciones);
	
	/** 
	 * Convierte las entidades a Facts de DRL
	 * @return Devuelve un String con los FACTS declarados 
	 */
	public String wrappingFacts();

	
}
