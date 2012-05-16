package ar.gov.anses.prissa.asistente.graficador.modeloresumido;


import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Accion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Condicion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Descisor;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.FilaTablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.action.cq.AnclajeRequerimientoInformatico;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.action.cq.EstadoAnclaje;
import ar.gob.anses.prissa.mi.asistente_reglas.services.PersistenceService;
import ar.gov.anses.prissa.asistente.graficador.modelo.TablaVaciaException;
import ar.gov.anses.prissa.asistente.graficador.modeloresumido.jgraph.Colores;
import static ar.gob.anses.prissa.mi.asistente_reglas.seam.action.cq.EstadoAnclaje.*;

@Scope(ScopeType.EVENT)
@Name("fabricaDeTablaModeloResumido")
@AutoCreate
public class FabricaDeTablaMapaResumido {

	@Logger
	private Log log;

	/**
	 * Mapa que relaciona una tabla de decisión con su representación
	 * "graficable". Utilizado para no procesar dos veces la misma tabla.
	 */
	private Map<TablaDecision, TablaMR> tablasCargadas = new HashMap<TablaDecision, TablaMR>();

	private int contador = 0;

	/**
	 * Toma una tablaDecision y la convierte en una tabla apta para ser
	 * graficada por el componente de graficación.
	 * 
	 * @param tablaDecision
	 * @return
	 * @throws TablaVaciaException
	 *             Si la tabla no tiene filas, columnas o acciones.
	 */
	public TablaMR convertirTabla(TablaDecision tablaDecision) {

//		if (contador == 3)
//			return null;
//		contador++;

		log.info("Proceseando tabla: #0", contador);

		log.info("#0 - Convirtiendo la tabla #0", tablaDecision.getNombre());
		TablaMR tablaDecisionGraficador = null;

		if (tablasCargadas.containsKey(tablaDecision)) {

			log.debug("#0 - La tabla #0 ya está cargada", tablaDecision
					.getNombre());
			tablaDecisionGraficador = tablasCargadas.get(tablaDecision);

		} else {

			if (tablaDecision.getFilas() == null
					|| tablaDecision.getFilas().size() <= 0) {
				throw new TablaVaciaExceptionMR("La tabla no tiene filas");
			}

			log.debug("#0 - La tabla tiene #1 filas",
					tablaDecision.getNombre(), tablaDecision.getFilas().size());

			tablaDecisionGraficador = new TablaMR(tablaDecision);
			this.tablasCargadas.put(tablaDecision, tablaDecisionGraficador);

			/*
			 * Cargamos las columnas en base a la primera fila
			 */
			cargarHeadersColumnas(tablaDecision, tablaDecisionGraficador);

			/*
			 * Ahora cargamos las filas
			 */
			cargarFilas(tablaDecision, tablaDecisionGraficador);

		}

		return tablaDecisionGraficador;
	}

	/**
	 * Dada una tabla de decision obtiene los nombres de todas sus columnas y
	 * los carga como encabezados de la tabla graficable.
	 * 
	 * @param tablaDecision
	 * @param tablaDecisionGraficador
	 * @throws TablaVaciaException
	 *             Si la tabla no tiene columnas o acciones.
	 */
	private void cargarHeadersColumnas(TablaDecision tablaDecision,
			TablaMR tablaDecisionGraficador) {

		tablaDecisionGraficador.addColumna(1, " ");
		tablaDecisionGraficador.addColumna(2, "Condiciones");
		tablaDecisionGraficador.addColumna(3, "Acciones");

	}

	/**
	 * Procesa las filas de la tabla de decisión y las carga en la tabla
	 * graficable
	 * 
	 * @param tablaDecision
	 * @param tablaDecisionGraficador
	 */
	private void cargarFilas(TablaDecision tablaDecision,
			TablaMR tablaDecisionGraficador) {

		Set<Condicion> condiciones = new HashSet<Condicion>();
		Set<Accion> acciones = new HashSet<Accion>();		
		fillCondicionesYAcciones(tablaDecision, condiciones, acciones);
		Long idFila = 1l;
		for (Condicion condicion : condiciones) {
			Integer idColumna = 1;

			log.debug("Añadiendo celda (#0, #1) con valor #2", idFila,
					idColumna, "COLOR");
			tablaDecisionGraficador.addCelda(idFila, idColumna,
					resolveColor(condicion));
			idColumna = 2;
			String texto = condicion.getNombre();
			log.debug("Añadiendo celda (#0, #1) con valor #2", idFila,
					idColumna, texto);
			tablaDecisionGraficador.addCelda(idFila, idColumna, texto);

			TablaDecision reglaAsociada = condicion.getRegla();
			/*
			 * True si la columna está asociada a otra regla.
			 */
			Boolean celdaTieneReglaAsociada = (reglaAsociada != null);

			log.debug("#0 - La celda tiene regla asociada? #1", tablaDecision
					.getNombre(), celdaTieneReglaAsociada);

			/*
			 * Cargamos recursivamente la tabla asociada.
			 */
			if (celdaTieneReglaAsociada) {
				TablaMR tablaReglaAsociada = this.convertirTabla(reglaAsociada);
				if (tablaReglaAsociada != null) {
					log.debug("#0 - Regla asociada obtenida #1", tablaDecision
							.getNombre(), tablaReglaAsociada);

					tablaDecisionGraficador.getCelda(idFila, idColumna)
							.vincular(tablaReglaAsociada);
				}
			}

			idFila++;
		}

		idFila = 1l;
		for (Accion accion : acciones) {
			Integer idColumna = 3;
			String texto = accion.getNombre();
			log.debug("Añadiendo celda (#0, #1) con valor #2", idFila,
					idColumna, texto);
			tablaDecisionGraficador.addCelda(idFila, idColumna, texto);
			idFila++;
		}

	}

	private void fillCondicionesYAcciones(TablaDecision regla,
			Set<Condicion> condiciones, Set<Accion> acciones) {

		for (FilaTablaDecision fila : regla.getFilas()) {
			acciones.addAll(fila.getAcciones());
			for (Descisor decisor : fila.getValores()) {
				condiciones.add(decisor.getCondicion());
			}

		}

	}

	private Color resolveColor(Condicion c) {
		boolean reglaAsociada = esNutridaPorRegla(c);
		boolean funcionAsociada = esNutridaPorFuncion(c);
		boolean atributoAnclado = (c.getAtributo().isAnclado() == null) ? false
				: c.getAtributo().isAnclado().booleanValue();
		boolean atributoLogico = c.getAtributo().getTipoCarga()
				.equals("LOGICO");

		boolean atributoPedidoAnclajeAmarillo = false;
		boolean atributoPedidoAnclajeVerde = false;
		boolean atributoPedidoAnclajeNaranja = false;
		boolean atributoPedidoAnclajeCeleste = false;
		boolean atributoPedidoAnclajeGlobal = false;

		if (!atributoAnclado) {
			EstadoAnclaje ea = getEstadoAnclaje(c.getAtributo());
			if (ea != null) {
				atributoPedidoAnclajeAmarillo = (ea == Ingresado
						|| ea == En_Revision_Analista
						|| ea == En_Analisis_Gerencia
						|| ea == En_Aceptacion_Coord || ea == En_Ejecucion
						|| ea == En_Gestion_Buss || ea == En_Despliegue_Test);
				atributoPedidoAnclajeVerde = (ea == En_Verificacion_Usr_Test);
				atributoPedidoAnclajeCeleste = (ea == En_Aprobacion_Gerencia
						|| ea == En_Despliegue_Prod
						|| ea == En_Verificacion_Usr_Prod);
				atributoPedidoAnclajeNaranja = (ea == Cancelado);
				atributoPedidoAnclajeGlobal = (atributoPedidoAnclajeAmarillo
						|| atributoPedidoAnclajeVerde
						|| atributoPedidoAnclajeCeleste
						|| atributoPedidoAnclajeNaranja);
			}

		}

		if (atributoLogico && !reglaAsociada) {
			return Colores.ROJO.color;
		}
		if (!atributoLogico
				&& (atributoPedidoAnclajeNaranja || !atributoPedidoAnclajeGlobal)) {
			return Colores.NARANJA.color;
		}
		if (!atributoLogico && atributoPedidoAnclajeAmarillo) {
			return Colores.AMARILLO.color;
		}
		if ((atributoLogico && reglaAsociada)
				|| (!atributoLogico && atributoPedidoAnclajeCeleste)) {
			return Colores.CELESTE.color;
		}
		if (atributoLogico && !funcionAsociada) {
			return Colores.VIOLETA.color;
		}
		if (!atributoLogico && atributoPedidoAnclajeVerde) {
			return Colores.VERDE.color;
		}
		return Color.WHITE;
	}

	@In
	PersistenceService persistenceService;

	@SuppressWarnings("unchecked")
	private EstadoAnclaje getEstadoAnclaje(Atributo attr) {

		if (attr == null)
			return null;

		Query query = persistenceService
				.createQuery("select a from AnclajeRequerimientoInformatico a left join a.atributos att where att.id = :attrId");
		query.setParameter("attrId", attr.getId());
		List<AnclajeRequerimientoInformatico> ancList = query.getResultList();
		if (ancList.isEmpty())
			return null;
		else {
			return ancList.get(0).getEstado();
		}
	}

	/**
	 * Indica si la condicion c es nutrida por regla
	 * 
	 * @param c Condicion
	 * @return true si la condicion es nutrida por regla, o false en caso contrario
	 */
	private boolean esNutridaPorRegla(Condicion c) {
		// FIXME deberia cambiarse el codigo de este metodo cuando este
		// desarrollado un metodo de la clase Condicion que indique si se nutre
		// por regla o función
		return (c.getRegla() != null);
	}

	/**
	 * Indica si la condicion c es nutrida por función
	 * 
	 * @param c Condicion
	 * @return true si la condicion es nutrida por función, o false en caso contrario
	 */
	private boolean esNutridaPorFuncion(Condicion c) {
		// FIXME deberia cambiarse el codigo de este metodo cuando este
		// desarrollado un metodo de la clase Condicion que indique si se nutre
		// por regla o función
		return (c.getFuncion() != null);
	}

	public void setLog(Log log) {
		this.log = log;
	}

	public Log getLog() {
		return log;
	}
}