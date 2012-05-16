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
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Accion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Condicion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.CondicionAtributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Descisor;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.EntidadFila;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Escenario;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.EscenarioFila;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.FilaTablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.LoteFila;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.LoteSimulacion;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Universo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Usuario;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.ValoresSimples;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.AtributoSimulado;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
import ar.gob.anses.prissa.mi.asistente_reglas.services.PersistenceService;

@Name("simuladorReglasPorLoteService")
@AutoCreate
public class SimuladorReglasPorLoteService {

	@In
	PersistenceService persistenceService;

	@In
	Usuario user;

	@SuppressWarnings("unchecked")
	public Set<Entidad> queryEntidadesRegistradas(TablaDecision tabla) {
		Set<Entidad> entidadesRegistradas = new HashSet<Entidad>();
		for (Iterator iterator = tabla.getFilas().iterator(); iterator
				.hasNext();) {
			FilaTablaDecision fila = (FilaTablaDecision) iterator.next();
			for (Iterator iterator2 = fila.getAcciones().iterator(); iterator2
					.hasNext();) {
				Accion accion = (Accion) iterator2.next();
				entidadesRegistradas.add(accion.getAccionModificaHecho()
						.getEntidad());			
			}			
		}
		return entidadesRegistradas;
	}

	@SuppressWarnings("unchecked")
	public List<LoteSimulacion> buscarLotes(Long id) {
		List<LoteSimulacion> lotesSimulacion = (List<LoteSimulacion>) persistenceService
				.createQuery(
						""
								+ "select e from LoteSimulacion e where (((e.usuario = :user) or (e.visibilidad = true)) and (e.tabla.id = :id)))")
				.setParameter("user", user.getUsername())
				.setParameter("id", id).getResultList();
		return lotesSimulacion;
	}
	
	// Busqueda de Atributos para tablas hijas
	public List<LoteFila> cargarListaLote(TablaDecision tabla) {
		Set<LoteFila> listaLotes = new HashSet<LoteFila>();
		Set<TablaDecision> tablas = new HashSet<TablaDecision>();
		recorreArbol(tabla, listaLotes, tablas);
		List<LoteFila> loteFilas = new ArrayList<LoteFila>(
				listaLotes);	

		return loteFilas;

	}

	// Busqueda de Entidades para tablas hijas
	public List<EntidadFila> cargarListaEntidades(TablaDecision tabla, boolean soloFisico) {
		Set<EntidadFila> listaEntidad = new HashSet<EntidadFila>();
		Set<TablaDecision> tablas = new HashSet<TablaDecision>();
		recorreArbolEntidad(tabla, listaEntidad, tablas, soloFisico);
		List<EntidadFila> entidadFila = new ArrayList<EntidadFila>(listaEntidad);
		return entidadFila;
	}

	void recorreArbol(TablaDecision tabla, Set<LoteFila> setLoteSimulacion,
			Set<TablaDecision> tablas) {

		if (tabla == null)
			return;
		if (tablas.contains(tabla))
			return;
		tablas.add(tabla);
		agregarEscenarioNodo(tabla, setLoteSimulacion);
		Set<TablaDecision> setTabla = buscarTablasHijas(tabla);
		for (Iterator iterator = setTabla.iterator(); iterator.hasNext();) {
			TablaDecision tabla1 = (TablaDecision) iterator.next();
			recorreArbol(tabla1, setLoteSimulacion, tablas);
		}
	}

	void recorreArbolEntidad(TablaDecision tabla,
			Set<EntidadFila> listaEntidades, Set<TablaDecision> tablas, boolean soloFisico) {

		if (tabla == null)
			return;
		if (tablas.contains(tabla))
			return;
		tablas.add(tabla);
		agregarEntidadNodo(tabla, listaEntidades, soloFisico);
		Set<TablaDecision> setTabla = buscarTablasHijas(tabla);
		for (Iterator iterator = setTabla.iterator(); iterator.hasNext();) {
			TablaDecision tabla1 = (TablaDecision) iterator.next();
			recorreArbolEntidad(tabla1, listaEntidades, tablas, soloFisico);
		}
	}

	private void agregarEscenarioNodo(TablaDecision tabla,
			Set<LoteFila> setLoteSimulacion) {

		for (Iterator iterator = tabla.getFilas().iterator(); iterator
				.hasNext();) {
			FilaTablaDecision fila = (FilaTablaDecision) iterator.next();
			for (Iterator iterator2 = fila.getValores().iterator(); iterator2
					.hasNext();) {
				Descisor valor = (Descisor) iterator2.next();
				LoteFila lote = new LoteFila();
				Entidad entidad = new Entidad();
				Atributo atributo = new Atributo();
				entidad = valor.getCondicion().getEntidad();
				// atributo = valor.getCondicion().getAtributo();
				atributo = valor.getCondicion().getAtributo();
				lote.setAtributo(atributo);
				lote.setEntidad(entidad);
				if (atributo.getTipoCarga().equals("FISICO"))
					setLoteSimulacion.add(lote);
			}
		}

	}

	private void agregarEntidadNodo(TablaDecision tabla,
			Set<EntidadFila> listaEntidades) {

		agregarEntidadNodo(tabla, listaEntidades, true);

	}

	private void agregarEntidadNodo(TablaDecision tabla,
			Set<EntidadFila> listaEntidades, boolean soloFisico) {
		if (soloFisico) {
			for (Iterator iterator = tabla.getFilas().iterator(); iterator
					.hasNext();) {
				FilaTablaDecision fila = (FilaTablaDecision) iterator.next();
				for (Iterator iterator2 = fila.getValores().iterator(); iterator2
						.hasNext();) {
					Descisor valor = (Descisor) iterator2.next();
					AtributoSimulado atributo = new AtributoSimulado(valor
							.getCondicion().getAtributo());
					Entidad entidad = valor.getCondicion().getEntidad();

					if (atributo.getAtributo().getTipoCarga().equals("FISICO")) {
						this.agregarEntidadYAtributos(entidad, atributo, listaEntidades);
						//repetimos el proceso para los atributos presentes en la
						//lista de condicionAtributo que tiene la clase condicion
						//Si son del tipo comparacion
						System.out.println("Simulacion: Agrego por comparacion");
						this.agregarEntidadesComparacion(valor.getCondicion().getCondicionAtributoList(), listaEntidades);
						
					}
				}
			}
		} else {
			System.out.println("DEBUG: PRIMER PEDIDO DE DATOS");
			for (Iterator iterator = tabla.getFilas().iterator(); iterator
					.hasNext();) {
				FilaTablaDecision fila = (FilaTablaDecision) iterator.next();
				for (Iterator iterator2 = fila.getValores().iterator(); iterator2
						.hasNext();) {
					Descisor valor = (Descisor) iterator2.next();
					AtributoSimulado atributo = new AtributoSimulado(valor
							.getCondicion().getAtributo());
					Entidad entidad = valor.getCondicion().getEntidad();
					
					System.out.println("Simulacion: Agrego atributos relacionados");
					this.agregarEntidadYAtributos(entidad, atributo, listaEntidades);
					
					//repetimos el proceso para los atributos presentes en la
					//lista de condicionAtributo que tiene la clase condicion
					//Si son del tipo comparacion
					System.out.println("Simulacion: Agrego por comparacion");
					this.agregarEntidadesComparacion(valor.getCondicion().getCondicionAtributoList(), listaEntidades);
									
					
				}
			}
		}

	}
	private void agregarEntidadYAtributos(Entidad entidad , AtributoSimulado atributo, Set<EntidadFila> listaEntidades){
		
		boolean nuevaEntidad = true;

		for (EntidadFila entFil : listaEntidades) {
			if (entFil.getEntidad().equals(entidad)) {
				nuevaEntidad = false;
				// No sÈ porque me trae varias veces el mismo
				// atributo
				if (!entFil.getAtributosInvolucrados().contains(
						atributo)) {
					entFil.getAtributosInvolucrados().add(atributo);
					System.out.println("Simulacion: Nuevo atributo " + atributo.getAtributo().getNombre() + " tipo carga " + atributo.getAtributo().getTipoCarga());					
				}
			}
		}					
		
		if (nuevaEntidad) {
			System.out.println("Simulacion: Nueva entidad " + entidad.getNombre() + " " + entidad.getVersionNumber() + " la lista cuenta con " + listaEntidades.size() + " entidades");
			System.out.println("Simulacion: Nuevo atributo " + atributo.getAtributo().getNombre() + " tipo carga " + atributo.getAtributo().getTipoCarga());			
			EntidadFila entidadFila = new EntidadFila();
			entidadFila.getAtributosInvolucrados().add(atributo);
			entidadFila.setEntidad(entidad);
			listaEntidades.add(entidadFila);
		}
	}
	
	private void agregarEntidadesComparacion(List<CondicionAtributo> listaCondicionAtributos, Set<EntidadFila> listaEntidades ){
		
		for(CondicionAtributo conAtr : listaCondicionAtributos){
			if(conAtr.getAtributo2().getTipoCarga().equals("FISICO")){			
			AtributoSimulado atributo = new AtributoSimulado(conAtr.getAtributo2());
			Entidad entidad = conAtr.getAtributo2().getEntidad();
			
			this.agregarEntidadYAtributos(entidad, atributo, listaEntidades);
			}
		}
	}

	private Set<TablaDecision> buscarTablasHijas(TablaDecision tabla) {
		Set<TablaDecision> lista = new HashSet<TablaDecision>();
		for (Iterator iterator = tabla.getFilas().iterator(); iterator
				.hasNext();) {
			FilaTablaDecision fila = (FilaTablaDecision) iterator.next();
			for (Iterator iterator2 = fila.getValores().iterator(); iterator2
					.hasNext();) {
				Descisor valor = (Descisor) iterator2.next();
				if (valor.getCondicion().getRegla() != null)
					lista.add(valor.getCondicion().getRegla());
			}
		}
		return lista;
	}

	// Busqueda de Atributos para tablas hijas
	@Logger
	Log log;	
	public List<LoteFila> cargarListaLoteFilaDatos(
			List<LoteFila> listaEscenario, LoteSimulacion loteSimulacion) {
		List<LoteFila> listaARemover = new ArrayList<LoteFila>();
		Set<LoteFila> set = new HashSet<LoteFila>();
		set.addAll(loteSimulacion.getLoteFila());
		set.addAll(listaEscenario);
		listaARemover.addAll(set);		
		listaEscenario = listaARemover;		
		return listaARemover;
		// Map<Long, EscenarioFila> map = new HashMap<Long,EscenarioFila>();
		// for (Iterator iterator = listaEscenario.iterator();
		// iterator.hasNext(); ) {
		// EscenarioFila fila = (EscenarioFila)iterator.next();
		// map.put(fila.getId(), fila);
		// }
		//		
		// for (Iterator iterator = escenario.getEscenarioFila().iterator();
		// iterator.hasNext(); ) {
		// EscenarioFila fila = (EscenarioFila)iterator.next();
		// map.put(fila.getId(), fila);
		// }
		// listaEscenario.clear();
		// listaEscenario.addAll(map.values());

		// for (Iterator iterator3 = escenario.getEscenarioFila().iterator();
		// iterator3.hasNext(); ) {
		// EscenarioFila filaDatos1 = (EscenarioFila)iterator3.next();
		// listaEscenario.add(filaDatos1);
		// }
		//		
		// for (Iterator iterator = listaEscenario.iterator();
		// iterator.hasNext(); ) {
		// EscenarioFila fila = (EscenarioFila)iterator.next();
		// for (Iterator iterator2 = escenario.getEscenarioFila().iterator();
		// iterator2.hasNext(); ) {
		// EscenarioFila filaDatos = (EscenarioFila)iterator2.next();
		// if(((fila.getAtributo().equals(filaDatos.getAtributo()))&&(fila.getEntidad().equals(filaDatos.getEntidad())))&&(fila.getId()==null))
		// listaARemover.add(fila);
		// }
		// }
		// listaEscenario.removeAll(listaARemover);
	}
	
	public void guardarLote(List<LoteFila> listaLote,
			LoteSimulacion lote, TablaDecision tabla) {
		// this.escenario.setEscenarioFila(this.listaEscenario);
		List<LoteFila> filas = new ArrayList<LoteFila>();
		for (Iterator iterator = listaLote.iterator(); iterator.hasNext();) {
			LoteFila loteFila = (LoteFila) iterator.next();
//			if ((loteFila.getLoteValores() != null)) {	
				loteFila.setLoteSimulacion(lote);
				filas.add(loteFila);
//			}
		}
		lote.setLoteFila(filas);
		lote.setUsuario(user.getUsername());
		lote.setTabla(tabla);
		persistenceService.save(lote);		
		listaLote.clear();
	}
		
	public Date convertirADate(String texto) {
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

	public boolean validarDuplicadosUniverso(EscenarioFila escenarioSelected,
			Universo universo1) {
		boolean band = false;
		for (Iterator iterator = escenarioSelected.getUniverso().iterator(); iterator
				.hasNext();) {
			Universo universo2 = (Universo) iterator.next();
			if (universo2.equals(universo1)) {
				band = true;
				break;
			}
		}
		return band;
	}

	public boolean validarDuplicadosValoresSimples(
			EscenarioFila escenarioSelected, ValoresSimples valoresSimples1) {
		boolean band = false;
		for (Iterator iterator = escenarioSelected.getValoresSimples()
				.iterator(); iterator.hasNext();) {
			ValoresSimples valoresSimples2 = (ValoresSimples) iterator.next();
			if (valoresSimples2.equals(valoresSimples1)) {
				band = true;
				break;
			}
		}
		return band;
	}

}
