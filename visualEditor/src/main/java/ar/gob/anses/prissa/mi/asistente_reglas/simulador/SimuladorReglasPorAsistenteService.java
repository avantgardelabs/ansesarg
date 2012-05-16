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
import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Universo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Usuario;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.ValoresSimples;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.AtributoSimulado;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
import ar.gob.anses.prissa.mi.asistente_reglas.services.PersistenceService;
import ar.gob.anses.prissa.mi.asistente_reglas.wrapper.WrapperRulesFactory;

@Name("simuladorReglasPorAsistenteService")
@AutoCreate
public class SimuladorReglasPorAsistenteService {

	@In
	PersistenceService persistenceService;

	@In
	Usuario user;

	@Logger
	Log log;

	@SuppressWarnings("unchecked")
	public Set<Entidad> queryEntidadesRegistradas(TablaDecision tabla) {
		Set<Entidad> entidadesRegistradas = new HashSet<Entidad>();
		for (Iterator iterator = tabla.getFilas().iterator(); iterator.hasNext();) {
			FilaTablaDecision fila = (FilaTablaDecision) iterator.next();
			for (Iterator iterator2 = fila.getAcciones().iterator(); iterator2.hasNext();) {
				Accion accion = (Accion) iterator2.next();

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
		}
		return entidadesRegistradas;
	}

	@SuppressWarnings("unchecked")
	public List<Escenario> buscarEscenarios(Long id) {
		List<Escenario> escenarios = (List<Escenario>) persistenceService
				.createQuery("" + "select e from Escenario e where (((e.usuario = :user) or (e.visibilidad = true)) and (e.tabla.id = :id)))")
				.setParameter("user", user.getUsername()).setParameter("id", id).getResultList();
		return escenarios;
	}

	// Busqueda de Atributos para tablas hijas
	public List<EscenarioFila> cargarListaEscenario(TablaDecision tabla) {
		// TODO: SI ESTE CODIGO NO SE USA HAY QUE BORRARLO
		// List<EscenarioFila> listaEscenario = new ArrayList<EscenarioFila>();
		// for (Iterator iterator = regla.getAcciones().iterator();
		// iterator.hasNext(); ) {
		// Accion accion = (Accion) iterator.next();
		// Escenario escenario = new Escenario();
		// escenario.setEntidad(accion.getAccionModificaHecho().getEntidad());
		// escenario.setAtributo(accion.getAccionModificaHecho().getAtributo());
		// listaEscenario.add(escenario);
		// }
		// for (Iterator iterator = entidadesRegistradas.iterator();
		// iterator.hasNext(); ) {
		// Entidad entidad = (Entidad) iterator.next();
		// for (Iterator iterator2 = entidad.getAtributos().iterator();
		// iterator2.hasNext(); ) {
		// Atributo atributo = (Atributo)iterator2.next();
		// EscenarioFila escenario = new EscenarioFila();
		// escenario.setEntidad(entidad);
		// escenario.setAtributo(atributo);
		// listaEscenario.add(escenario);
		// }
		// }
		// 2 ok
		// Set<EscenarioFila> listaEscenario = new HashSet<EscenarioFila>();
		//
		// for (Iterator iterator = tabla.getFilas().iterator();
		// iterator.hasNext(); ) {
		// FilaTablaDecision fila = (FilaTablaDecision)iterator.next();
		// for (Iterator iterator2 = fila.getAcciones().iterator();
		// iterator2.hasNext(); ) {
		// fila.getValores().get(0).getCondicion().getRegla().getFilas();
		//
		// fila.getValores().get(0).getCondicion().get
		// Accion accion = (Accion) iterator2.next();
		// EscenarioFila escenario = new EscenarioFila();
		// Entidad entidad = new Entidad();
		// Atributo atributo = new Atributo();
		// entidad = accion.getAccionModificaHecho().getEntidad();
		// atributo = accion.getAccionModificaHecho().getAtributo();
		// escenario.setAtributo(atributo);
		// escenario.setEntidad(entidad);
		// listaEscenario.add(escenario);
		//
		// }
		// }
		// List<EscenarioFila> escenarioFilas = new
		// ArrayList<EscenarioFila>(listaEscenario);
		// return escenarioFilas;

		Set<EscenarioFila> listaEscenario = new HashSet<EscenarioFila>();
		Set<TablaDecision> tablas = new HashSet<TablaDecision>();
		recorreArbol(tabla, listaEscenario, tablas);
		List<EscenarioFila> escenarioFilas = new ArrayList<EscenarioFila>(listaEscenario);
		return escenarioFilas;

	}

	// Busqueda de Entidades para tablas hijas
	public List<EntidadFila> cargarListaEntidades(TablaDecision tabla, boolean soloFisico) {
		Set<EntidadFila> listaEntidad = new HashSet<EntidadFila>();
		Set<TablaDecision> tablas = new HashSet<TablaDecision>();
		recorreArbolEntidad(tabla, listaEntidad, tablas, soloFisico);
		List<EntidadFila> entidadFila = new ArrayList<EntidadFila>(listaEntidad);
		return entidadFila;
	}

	void recorreArbol(TablaDecision tabla, Set<EscenarioFila> setEscenario, Set<TablaDecision> tablas) {

		if (tabla == null)
			return;
		if (tablas.contains(tabla))
			return;
		tablas.add(tabla);
		agregarEscenarioNodo(tabla, setEscenario);
		Set<TablaDecision> setTabla = buscarTablasHijas(tabla);
		for (Iterator iterator = setTabla.iterator(); iterator.hasNext();) {
			TablaDecision tabla1 = (TablaDecision) iterator.next();
			recorreArbol(tabla1, setEscenario, tablas);
		}
	}

	void recorreArbolEntidad(TablaDecision tabla, Set<EntidadFila> listaEntidades, Set<TablaDecision> tablas, boolean soloFisico) {

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

	private void agregarEscenarioNodo(TablaDecision tabla, Set<EscenarioFila> setEscenario) {

		for (Iterator iterator = tabla.getFilas().iterator(); iterator.hasNext();) {
			FilaTablaDecision fila = (FilaTablaDecision) iterator.next();
			for (Iterator iterator2 = fila.getValores().iterator(); iterator2.hasNext();) {
				Descisor valor = (Descisor) iterator2.next();
				EscenarioFila escenario = new EscenarioFila();
				Entidad entidad = new Entidad();
				Atributo atributo = new Atributo();
				entidad = valor.getCondicion().getEntidad();
				// atributo = valor.getCondicion().getAtributo();
				atributo = valor.getCondicion().getAtributo();
				escenario.setAtributo(atributo);
				escenario.setEntidad(entidad);
				if (atributo.getTipoCarga().equals("FISICO"))
					setEscenario.add(escenario);
			}
		}

	}

	private void agregarEntidadNodo(TablaDecision tabla, Set<EntidadFila> listaEntidades) {

		agregarEntidadNodo(tabla, listaEntidades, true);

	}

	/**
	 * 
	 * Metodo que carga las listas que son usados en el simulador virtual.
	 * 
	 * @param tabla
	 *            Tabla de decision a ser wrappeada
	 * @param soloFisico
	 *            Determina si solo se cargan los atributos fisicos o todos.
	 */
	private void agregarEntidadNodo(TablaDecision tabla, boolean soloFisico) {

		WrapperRulesFactory factory = new WrapperRulesFactory(tabla);
		factory.setLog(log);
		AtributoSimulado atributo;
		Entidad entidad;

		if (soloFisico) {
			for (Atributo atr : factory.getAtributosInvolucrados()) {
				atributo = new AtributoSimulado(atr);
				entidad = atr.getEntidad();
				if (atr.getTipoCarga().equals("FISICO")) {

				}
			}
		} else {

		}
	}

	private void agregarEntidadNodo(TablaDecision tabla, Set<EntidadFila> listaEntidades, boolean soloFisico) {

		WrapperRulesFactory factory = new WrapperRulesFactory(tabla);
		factory.setLog(log);
		factory.buildRule();
		AtributoSimulado atributo;
		Entidad entidad;

		if (soloFisico) {

			for (Atributo atr : factory.getAtributosInvolucrados()) {
				atributo = new AtributoSimulado(atr);
				entidad = atr.getEntidad();
				if (atr.getTipoCarga().equals("FISICO")) {
					this.agregarEntidadYAtributos(entidad, atributo, listaEntidades);
					// repetimos el proceso para los atributos presentes en la
					// lista de condicionAtributo que tiene la clase condicion
					// Si son del tipo comparacion
					log.debug("Simulacion: Agrego por comparacion");
					// this.agregarEntidadesComparacion(valor.getCondicion().getCondicionAtributoList(),
					// listaEntidades);

					//agregarEntidadYAtributos(entidad, atributo, listaEntidades);
				}
			}

			/*
			 * for (Iterator iterator = tabla.getFilas().iterator(); iterator
			 * .hasNext();) { FilaTablaDecision fila = (FilaTablaDecision)
			 * iterator.next(); for (Iterator iterator2 =
			 * fila.getValores().iterator(); iterator2 .hasNext();) { Descisor
			 * valor = (Descisor) iterator2.next(); AtributoSimulado atributo =
			 * new AtributoSimulado(valor .getCondicion().getAtributo());
			 * Entidad entidad = valor.getCondicion().getEntidad();
			 * 
			 * if (atributo.getAtributo().getTipoCarga().equals("FISICO")) {
			 * this.agregarEntidadYAtributos(entidad, atributo, listaEntidades);
			 * //repetimos el proceso para los atributos presentes en la //lista
			 * de condicionAtributo que tiene la clase condicion //Si son del
			 * tipo comparacion
			 * System.out.println("Simulacion: Agrego por comparacion");
			 * this.agregarEntidadesComparacion
			 * (valor.getCondicion().getCondicionAtributoList(),
			 * listaEntidades);
			 * 
			 * } } }
			 */
		} else {
			log.debug("DEBUG: PRIMER PEDIDO DE DATOS");

			for (Atributo atr : factory.getAtributosInvolucrados()) {
				atributo = new AtributoSimulado(atr);
				entidad = atr.getEntidad();

				this.agregarEntidadYAtributos(entidad, atributo, listaEntidades);

				log.debug("Simulacion: Agrego por comparacion");
				// this.agregarEntidadesComparacion(valor.getCondicion().getCondicionAtributoList(),
				// listaEntidades);

				//agregarEntidadYAtributos(entidad, atributo, listaEntidades);

			}

			/*
			 * for (Iterator iterator = tabla.getFilas().iterator(); iterator
			 * .hasNext();) { FilaTablaDecision fila = (FilaTablaDecision)
			 * iterator.next(); for (Iterator iterator2 =
			 * fila.getValores().iterator(); iterator2 .hasNext();) { Descisor
			 * valor = (Descisor) iterator2.next(); AtributoSimulado atributo =
			 * new AtributoSimulado(valor .getCondicion().getAtributo());
			 * Entidad entidad = valor.getCondicion().getEntidad();
			 * 
			 * System.out.println("Simulacion: Agrego atributos relacionados");
			 * this.agregarEntidadYAtributos(entidad, atributo, listaEntidades);
			 * 
			 * //repetimos el proceso para los atributos presentes en la //lista
			 * de condicionAtributo que tiene la clase condicion //Si son del
			 * tipo comparacion
			 * System.out.println("Simulacion: Agrego por comparacion");
			 * this.agregarEntidadesComparacion
			 * (valor.getCondicion().getCondicionAtributoList(),
			 * listaEntidades);
			 * 
			 * 
			 * } }
			 */
		}

	}

	private void agregarEntidadYAtributos(Entidad entidad, AtributoSimulado atributo, Set<EntidadFila> listaEntidades) {

		boolean nuevaEntidad = true;

		for (EntidadFila entFil : listaEntidades) {
			if (entFil.getEntidad().equals(entidad)) {
				nuevaEntidad = false;
				// No s� porque me trae varias veces el mismo
				// atributo
				if (!entFil.getAtributosInvolucrados().contains(atributo)) {
					entFil.getAtributosInvolucrados().add(atributo);
					System.out.println("Simulacion: Nuevo atributo " + atributo.getAtributo().getNombre() + " tipo carga "
							+ atributo.getAtributo().getTipoCarga());
				}
			}
		}

		if (nuevaEntidad) {
			System.out.println("Simulacion: Nueva entidad " + entidad.getNombre() + " " + entidad.getVersionNumber() + " la lista cuenta con "
					+ listaEntidades.size() + " entidades");
			System.out.println("Simulacion: Nuevo atributo " + atributo.getAtributo().getNombre() + " tipo carga "
					+ atributo.getAtributo().getTipoCarga());
			EntidadFila entidadFila = new EntidadFila();
			entidadFila.getAtributosInvolucrados().add(atributo);
			entidadFila.setEntidad(entidad);
			listaEntidades.add(entidadFila);
		}
	}

	private void agregarEntidadesComparacion(List<CondicionAtributo> listaCondicionAtributos, Set<EntidadFila> listaEntidades) {

		for (CondicionAtributo conAtr : listaCondicionAtributos) {
			if (conAtr.getAtributo2().getTipoCarga().equals("FISICO")) {
				AtributoSimulado atributo = new AtributoSimulado(conAtr.getAtributo2());
				Entidad entidad = conAtr.getAtributo2().getEntidad();

				this.agregarEntidadYAtributos(entidad, atributo, listaEntidades);
			}
		}
	}

	private Set<TablaDecision> buscarTablasHijas(TablaDecision tabla) {
		Set<TablaDecision> lista = new HashSet<TablaDecision>();
		for (Iterator iterator = tabla.getFilas().iterator(); iterator.hasNext();) {
			FilaTablaDecision fila = (FilaTablaDecision) iterator.next();
			for (Iterator iterator2 = fila.getValores().iterator(); iterator2.hasNext();) {
				Descisor valor = (Descisor) iterator2.next();
				if (valor.getCondicion().getRegla() != null)
					lista.add(valor.getCondicion().getRegla());
			}
		}
		return lista;
	}

	// Busqueda de Atributos para tablas hijas

	public List<EscenarioFila> cargarListaEscenarioFilaDatos(List<EscenarioFila> listaEscenario, Escenario escenario) {
		List<EscenarioFila> listaARemover = new ArrayList<EscenarioFila>();
		Set<EscenarioFila> set = new HashSet<EscenarioFila>();
		set.addAll(escenario.getEscenarioFila());
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

	public void guardarEscenario(List<EscenarioFila> listaEscenario, Escenario escenario, TablaDecision tabla) {
		// this.escenario.setEscenarioFila(this.listaEscenario);
		List<EscenarioFila> filas = new ArrayList<EscenarioFila>();
		for (Iterator iterator = listaEscenario.iterator(); iterator.hasNext();) {
			EscenarioFila escenarioFila = (EscenarioFila) iterator.next();
			if ((escenarioFila.getUniverso() != null) || (escenarioFila.getValoresSimples() != null)) {
				escenarioFila.setEscenario(escenario);
				filas.add(escenarioFila);
			}
		}
		escenario.setEscenarioFila(filas);
		escenario.setUsuario(user.getUsername());
		escenario.setTabla(tabla);
		persistenceService.save(escenario);
		listaEscenario.clear();
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

	public boolean validarDuplicadosUniverso(EscenarioFila escenarioSelected, Universo universo1) {
		boolean band = false;
		for (Iterator iterator = escenarioSelected.getUniverso().iterator(); iterator.hasNext();) {
			Universo universo2 = (Universo) iterator.next();
			if (universo2.equals(universo1)) {
				band = true;
				break;
			}
		}
		return band;
	}

	public boolean validarDuplicadosValoresSimples(EscenarioFila escenarioSelected, ValoresSimples valoresSimples1) {
		boolean band = false;
		for (Iterator iterator = escenarioSelected.getValoresSimples().iterator(); iterator.hasNext();) {
			ValoresSimples valoresSimples2 = (ValoresSimples) iterator.next();
			if (valoresSimples2.equals(valoresSimples1)) {
				band = true;
				break;
			}
		}
		return band;
	}

}
