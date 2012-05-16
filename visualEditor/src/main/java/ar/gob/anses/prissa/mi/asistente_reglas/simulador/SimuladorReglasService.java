package ar.gob.anses.prissa.mi.asistente_reglas.simulador;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Accion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Escenario;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.EscenarioFila;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.ReglaPseudocodigo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Universo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Usuario;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.ValoresSimples;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
import ar.gob.anses.prissa.mi.asistente_reglas.services.PersistenceService;

@Name("simuladorReglasService")
@AutoCreate
public class SimuladorReglasService {
	
	@In
	PersistenceService persistenceService;
	
	@In Usuario user;
	
	@SuppressWarnings("unchecked")
	public Set<Entidad> queryEntidadesRegistradas(ReglaPseudocodigo regla) {
		Set<Entidad> entidadesRegistradas = new HashSet<Entidad>(0);
			for (Iterator iterator = regla.getAcciones().iterator(); iterator.hasNext(); ) {
				Accion accion = (Accion) iterator.next();
				
				//EDO
				if (accion.getAccionModificaHecho().getId() !=null)
				{
					entidadesRegistradas.add(accion.getAccionModificaHecho().getEntidad());
				}
				else
				{
					accion.setAccionModificaHecho(null);
				}
				
			}
		return entidadesRegistradas;
	}
	
	@SuppressWarnings("unchecked")
	public List<Escenario> buscarEscenarios(Long id){
		List<Escenario> escenarios = (List<Escenario>)persistenceService.createQuery("" +
		"select e from Escenario e where (((e.usuario = :user) or (e.visibilidad = true)) and (e.regla.id = :id)))").setParameter("user", user.getUsername()).setParameter("id", id).getResultList();
		return escenarios;
	}
	
	public List<EscenarioFila> cargarListaEscenario(ReglaPseudocodigo regla){
		Set<EscenarioFila> listaEscenario = new HashSet<EscenarioFila>();
		List<Accion> acciones = regla.getAcciones();
		for (Iterator iterator = acciones.iterator(); iterator.hasNext(); ) {
			Accion accion = (Accion) iterator.next();
			EscenarioFila escenario = new EscenarioFila();
			Entidad entidad = new Entidad();
			Atributo atributo = new Atributo();
			entidad = accion.getAccionModificaHecho().getEntidad();
			atributo = accion.getAccionModificaHecho().getAtributo();
			escenario.setAtributo(atributo);
			escenario.setEntidad(entidad);
			listaEscenario.add(escenario);
		}
		
		
//		for (Iterator iterator = entidadesRegistradas.iterator(); iterator.hasNext(); ) {
//		Entidad entidad = (Entidad) iterator.next();
//			for (Iterator iterator2 = entidad.getAtributos().iterator(); iterator2.hasNext(); ) {
//				Atributo atributo = (Atributo)iterator2.next();
//				EscenarioFila escenario = new EscenarioFila();
//				escenario.setEntidad(entidad);
//				escenario.setAtributo(atributo);
//				listaEscenario.add(escenario);
//			}
		
		List<EscenarioFila> escenarioFilas = new ArrayList<EscenarioFila>(listaEscenario);
		return escenarioFilas;
	}
	
	public List<EscenarioFila> cargarListaEscenarioFilaDatos(List<EscenarioFila> listaEscenario, Escenario escenario){
		List<EscenarioFila>listaARemover= new ArrayList<EscenarioFila>();
		Set<EscenarioFila>set = new HashSet<EscenarioFila>();
		set.addAll(escenario.getEscenarioFila());	
		set.addAll(listaEscenario);
		listaARemover.addAll(set);
		listaEscenario=listaARemover;
		return listaARemover;
	}
	
	public void guardarEscenario(List<EscenarioFila> listaEscenario, Escenario escenario, ReglaPseudocodigo regla){
		List<EscenarioFila> filas = new ArrayList<EscenarioFila>();
		for (Iterator iterator = listaEscenario.iterator(); iterator.hasNext(); ) {
			EscenarioFila escenarioFila = (EscenarioFila) iterator.next();
			if((escenarioFila.getUniverso()!=null)||(escenarioFila.getValoresSimples()!=null)){
				escenarioFila.setEscenario(escenario);
				filas.add(escenarioFila);
			}
		}
		escenario.setEscenarioFila(filas);
		escenario.setUsuario(user.getUsername());
		escenario.setRegla(regla);
		persistenceService.save(escenario);
		listaEscenario.clear();
	}
	
	public Date convertirADate(String texto){
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Date aux = new Date();
		try {
			aux = df.parse(texto);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return aux;
	}
	
	public boolean validarDuplicadosUniverso(EscenarioFila escenarioSelected, Universo universo1){
		boolean band = false;
			for (Iterator iterator = escenarioSelected.getUniverso().iterator(); iterator.hasNext(); ) {
			Universo universo2 = (Universo) iterator.next();
				if(universo2.equals(universo1)){
					band=true;
					break;
				}
	    }
		return band;
		}
		
		public boolean validarDuplicadosValoresSimples(EscenarioFila escenarioSelected, ValoresSimples valoresSimples1){
			boolean band = false;
				for (Iterator iterator = escenarioSelected.getValoresSimples().iterator(); iterator.hasNext(); ) {
				ValoresSimples valoresSimples2 = (ValoresSimples) iterator.next();
					if(valoresSimples2.equals(valoresSimples1)){
						band=true;
						break;
					}
		    }
			return band;
			}
	
	
}
