package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Accion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Condicion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Descisor;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.FilaTablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;

@Scope(ScopeType.CONVERSATION)
@Name("fichaRegla")
public class FichaReglaAsistente implements Serializable{

	
	private static final long serialVersionUID = -3624835134151838393L;

	@Logger
	Log log; 
	
	@Out(required=false)
	TablaDecision tablaD;
	
	@Out(required=false)
	List<Condicion> condiciones;
	
	@Out(required=false)
	List<Accion> acciones;
	
	@Out(required=false)
	List<Entidad> entidades;
	
	@Out(required=false)
	Entidad entConsultada;
	
	@Out(required=false)
	List<TablaDecision> subreglas;
	
	@In(create=true)
	@Out(required=true)
	private EditorAction editor;
	
	public void consultarEntidad(Entidad e){
		entConsultada=e;
	}
	
	public void contraerEntidad(){
		entConsultada=null;
	}
	
	//Metodo de inicializacion.
	public String init(TablaDecision t) {	
		tablaD = t;	
		
		FilaTablaDecision fila;
		Descisor d; 
		
		condiciones = new ArrayList<Condicion>();
		acciones = new ArrayList<Accion>();
		entidades = new ArrayList<Entidad>();
		Set<Entidad> entidadesSet = new HashSet<Entidad>();
		subreglas = new ArrayList<TablaDecision>();
		
		
		for (int cantFilas=0;cantFilas < t.getFilas().size();cantFilas++){
		
			fila=t.getFilas().get(cantFilas);
			
			for (int i=0; i<fila.getValores().size();i++){
				d=(Descisor)fila.getValores().get(i);
				if (!condiciones.contains(d.getCondicion())){
					condiciones.add(d.getCondicion());
					if (d.getCondicion().getRegla()!=null){
						if (!subreglas.contains(d.getCondicion().getRegla()) && 
								!t.equals(d.getCondicion().getRegla())){
							log.info("Regla a agregar: " + d.getCondicion().getRegla().getNombre());
							subreglas.add(d.getCondicion().getRegla());
						}
					}
				}
				
				if (!entidades.contains(d.getCondicion().getEntidad())){
					entidadesSet.add(d.getCondicion().getEntidad());
				}
				
				for (int j=0;j<fila.getAcciones().size();j++){
					if (!acciones.contains(fila.getAcciones().get(j))){
						acciones.add(fila.getAcciones().get(j));
						
						
						
						if (fila.getAcciones().get(j).getAccionModificaHecho()!=null){
							if (!entidades.contains(fila.getAcciones().get(j).getAccionModificaHecho().getEntidad())){
								if (fila.getAcciones().get(j).getAccionModificaHecho().getAtributo()!=null)
									entidadesSet.add(fila.getAcciones().get(j).getAccionModificaHecho().getEntidad());
							}
						}
					}
						
				}			
			}
		
		}
		entidades.addAll(entidadesSet);
		
		editor.ordenarTablaDecision(tablaD.getFilas(), condiciones);		
		
		return "/fichaReglaAsistente.xhtml";
	}
}
