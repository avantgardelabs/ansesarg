package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import ar.gob.anses.prissa.mi.asistente_reglas.seam.action.SystemProperties;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Remove;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletResponse;

import org.drools.agent.AgentEventListener;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

import ar.gob.anses.prissa.mi.asistente_reglas.agentfactory.AgentFactory;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Accion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Condicion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.CondicionAtributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.CondicionFormula;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Descisor;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Dominio;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.FilaTablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Instrumento;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Literal;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Usuario;
import ar.gob.anses.prissa.mi.asistente_reglas.services.PersistenceService;
import ar.gob.anses.prissa.mi.asistente_reglas.wrapper.WrapperRulesFactory;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

@SuppressWarnings("serial")
@Name("editor")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class EditorAction implements Serializable {

	// @In EntityManager entityManager;
	@In
	PersistenceService persistenceService;

	@In
	FacesMessages facesMessages;

	@In
	EntityManager entityManager;

	@In(create = true)
	@Out(required = false)
	TablaDecision tablaDesicion;

	private ArrayList<Condicion> condicionesDisponibles = new ArrayList<Condicion>();

	// private ArrayList<Condicion> condicionesSeleccionadasTmp=new
	// ArrayList<Condicion>();
	@In(required = false)
	@Out(required = false)
	List<Condicion> condicionesSeleccionadas;
	private Dominio dominioFiltroCondiciones;
	@Out(required = false)
	ArrayList<FilaTablaDecision> filas;
	private ArrayList<Accion> resultadosEsperadosDisponibles;

	private ArrayList<Accion> resultadosEsperadosPreSeleccionados;
	private ArrayList<Accion> resultadosEsperadosSeleccionados;

	@In(create = true)
	@Out(required = true)
	private AccionHome accionHome;

	public AccionHome getAccionHome() {
		return accionHome;
	}

	public void setAccionHome(AccionHome accionHome) {
		this.accionHome = accionHome;
	}

	@In(required = false)
	@Out(required = false)
	FilaTablaDecision filaAAgregar;
	@Out(required = false)
	ArrayList<FilaTablaDecision> fakeFila;

	private TreeNode rootNodeInstrumento = null;
	private TreeNode nodoSeleccionadoInstrumento = null;

	private ArrayList<Dominio> dominiosRegistrados;
	private Condicion condicion;

	/* Se refiere a cual fue la ultima pagina que llamo al asistente */
	private String ultimoLlamado;

	@DataModel
	private Dominio dominio;

	@Logger
	Log log;

	private String nameInstrument = new String("");

	private ArrayList<Instrumento> listInstruments = new ArrayList<Instrumento>();

	private boolean filaEditada = false;

	private List<FilaTablaDecision> filasAAsignarAcciones = new ArrayList<FilaTablaDecision>();

	public List<FilaTablaDecision> getFilasAAsignarAcciones() {
		return filasAAsignarAcciones;
	}

	public void setFilasAAsignarAcciones(List<FilaTablaDecision> filasAAsignarAcciones) {
		this.filasAAsignarAcciones = filasAAsignarAcciones;
	}

	public boolean validar() {
		return true;
	}

	@SuppressWarnings("unchecked")
	private void cargarRegla(TablaDecision t) {
		condicionesSeleccionadas = new ArrayList();
		resultadosEsperadosSeleccionados = new ArrayList();
		filas = new ArrayList();
		fakeFila = new ArrayList();
		tablaDesicion = t;
		try {
			Set condicionesSinRepetir = new HashSet();
			Set accionesSinRepetir = new HashSet();
			List<FilaTablaDecision> filas = t.getFilas();
			if (filas != null) {
				for (int i = 0; i < filas.size(); i++) {
					FilaTablaDecision fila = filas.get(i);
					List<Descisor> listaD = new ArrayList(fila.getValores());
					for (int j = 0; j < listaD.size(); j++) {
						Descisor d = (Descisor) listaD.get(j);
						if (d.getCondicion() != null) {
							condicionesSinRepetir.add(d.getCondicion());
						}
					}
					List<Accion> acciones = fila.getAcciones();
					if (acciones != null) {
						for (int j = 0; j < acciones.size(); j++) {
							Accion accion = acciones.get(j);
							accionesSinRepetir.add(accion);
						}
					}
				}
			}
			condicionesSeleccionadas.addAll(condicionesSinRepetir);
			Collections.sort(condicionesSeleccionadas);
			resultadosEsperadosSeleccionados.addAll(accionesSinRepetir);

			filtrarCondicion();

		} catch (IndexOutOfBoundsException e) {
		}
	}

	public String reglaAEditar(TablaDecision t) {
		cargarRegla(t);
		return "/detalle_regla.xhtml";
	}

	public String reglaAVer(TablaDecision t) {
		cargarRegla(t);
		return "/detalle_regla_read.xhtml";
	}

	@SuppressWarnings("unchecked")
	@Begin
	public String init() {

		condicionesSeleccionadas = new ArrayList();
		resultadosEsperadosSeleccionados = new ArrayList();
		filas = new ArrayList();
		fakeFila = new ArrayList();

		tablaDesicion.setInstrumentos(new ArrayList<Instrumento>());

		return "/detalle_regla.xhtml";

	}

	public String enConstruccion() {
		return null;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Condicion> getCondicionesDisponibles() {
		return condicionesDisponibles;
	}

	public void setCondicionesDisponibles(ArrayList<Condicion> condiciones) {
		this.condicionesDisponibles = condiciones;
	}

	public ArrayList<Accion> getResultadosEsperadosDisponibles() {
		return resultadosEsperadosDisponibles;
	}

	public void setResultadosEsperadosDisponibles(ArrayList<Accion> resultadosEsperadosDisponibles) {
		this.resultadosEsperadosDisponibles = resultadosEsperadosDisponibles;
	}

	public ArrayList<Accion> getResultadosEsperadosSeleccionados() {
		return resultadosEsperadosSeleccionados;
	}

	public void setResultadosEsperadosSeleccionados(ArrayList<Accion> resultadosEsperadosSeleccionados) {
		this.resultadosEsperadosSeleccionados = resultadosEsperadosSeleccionados;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Dominio> getDominiosRegistrados() {

		dominiosRegistrados = (ArrayList<Dominio>) persistenceService.createQuery(
				"" + "	select dominio from Dominio as Dominio order by Dominio.descripcion").getResultList();

		return dominiosRegistrados;
	}

	public void setDominiosRegistrados(ArrayList<Dominio> dominiosRegistrados) {
		this.dominiosRegistrados = dominiosRegistrados;
	}

	public String establecerCondiciones() {
		return "resultado_esperado.xhtml";
	}

	public String establecerAcciones() {
		return "editor.xhtml";
	}

	public Condicion getCondicion() {
		return condicion;
	}

	public void setCondicion(Condicion condicion) {
		this.condicion = condicion;
	}

	public Dominio getDominio() {
		return dominio;
	}

	public void setDominio(Dominio dominio) {
		this.dominio = dominio;
	}

	public ArrayList<Accion> getResultadosEsperadosPreSeleccionados() {
		if (accionHome != null && accionHome.getAccionCreada() == Boolean.TRUE) {
			this.setResultadosEsperadosPreSeleccionados(this.accionHome.getResultadosEsperadosPreSeleccionados());
			this.setResultadosEsperadosSeleccionados(new ArrayList<Accion>(this.accionHome.getResultadosEsperadosPreSeleccionados()));
			accionHome.setAccionCreada(Boolean.FALSE);
		}

		return resultadosEsperadosPreSeleccionados;
	}

	public void setResultadosEsperadosPreSeleccionados(ArrayList<Accion> resultadosEsperadosPreSeleccionados) {
		this.resultadosEsperadosPreSeleccionados = resultadosEsperadosPreSeleccionados;
	}

	public String llamarNuevoDominio(String quienLlama) {

		this.ultimoLlamado = quienLlama;
		return "nuevo_dominio.xhtml";
	}

	/** Persistimos la condiciÔøΩn en la BBDD */
	public void guardarCondicion() {
		persistenceService.save(this.condicion);
	}

	/* Persistimos el nuevo dominio */
	public String guardarDominio() {
		persistenceService.save(this.dominio);
		return ultimoLlamado;
	}

	public Dominio getDominioFiltroCondiciones() {
		return dominioFiltroCondiciones;
	}

	public void setDominioFiltroCondiciones(Dominio dominioFiltroCondiciones) {
		this.dominioFiltroCondiciones = dominioFiltroCondiciones;
	}

	@Remove()
	public void destroy() {

	}

	public void exportar() throws DocumentException, IOException {

		this.setNameInstrument(this.getTablaDesicion().getNombre());

		String literal = this.obtenerLiteral();

		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

		response.setContentType("application/octet-stream");
		response.addHeader("Content-Disposition", "attachment;filename=" + this.nameInstrument.replace(" ", "%20") + ".pdf");

		Document documento = new Document();
		FileOutputStream ficheroPdf;
		ficheroPdf = new FileOutputStream("ficheroTablaDecision.pdf");
		PdfWriter.getInstance(documento, ficheroPdf);
		documento.open();
		documento.add(new Paragraph(literal));
		documento.close();

		InputStream in = new FileInputStream("ficheroTablaDecision.pdf");
		byte[] data = new byte[in.available()];
		in.read(data);

		response.setContentLength(data.length);
		OutputStream output = response.getOutputStream();
		output.write(data);
		output.flush();
		output.close();
		FacesContext.getCurrentInstance().responseComplete();

	}

	private String obtenerLiteral() {

		String cadena = "Reglas por asistente\n\n";
		int i = 0;
		for (FilaTablaDecision fila : tablaDesicion.getFilas()) {
			i++;
			cadena = cadena + "\n regla_" + i + "\n";
			cadena = cadena + "\t condiciones";
			for (Descisor dec : fila.getValores()) {
				try {
					if (dec.getValor() != null) {
						cadena = cadena + "\n \t\t\t   " + dec.getCondicion().getNombre() + " con valor ";
						if (dec.getValor().getOperadorLogico().equals("!="))
							cadena = cadena + "distinto a ";
						else if (dec.getValor().getOperadorLogico().equals(">"))
							cadena = cadena + "mayor a ";
						else if (dec.getValor().getOperadorLogico().equals("<"))
							cadena = cadena + "menor a ";
						else if (dec.getValor().getOperadorLogico().equals(">="))
							cadena = cadena + "mayor o igual a ";
						else if (dec.getValor().getOperadorLogico().equals("<="))
							cadena = cadena + "menor o igual a ";
						cadena = cadena + dec.getValor().getNombre();
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
			cadena = cadena + "\n \t acciones";
			for (Accion ac : fila.getAcciones()) {
				cadena = cadena + "\n \t\t\t   " + ac.getNombre();
			}
			cadena = cadena + "\n end \n\n";
		}
		log.debug(cadena);
		return cadena;
	}

	public String filtrarAccion() {

		Query query = persistenceService.createQuery(
				"" + "SELECT" + " Accion FROM Accion as Accion" + " WHERE Accion.removed = false and Accion.dominio=:dom").setParameter("dom",
				dominioFiltroCondiciones);

		this.resultadosEsperadosDisponibles = (ArrayList<Accion>) query.getResultList();
		this.resultadosEsperadosDisponibles.removeAll(this.resultadosEsperadosSeleccionados);

		return null;
	}

	public String filtrarCondicion() {

		log.debug("filtra condicion");
		Query query = persistenceService.createQuery(
				"" + "SELECT" + " condicion FROM Condicion as Condicion" + " WHERE Condicion.removed = false and Condicion.dominio=:dom")
				.setParameter("dom", dominioFiltroCondiciones);

		condicionesDisponibles = (ArrayList<Condicion>) query.getResultList();
		condicionesDisponibles.removeAll(this.condicionesSeleccionadas);
		Collections.sort(condicionesDisponibles);

		log.debug("tamanio cond disponibles: " + this.condicionesDisponibles.size() + "tamanio cond selecc " + this.condicionesSeleccionadas.size());
		filtrarAccion();
		return null;
	}

	public void actualizarCondiciones() {
		ArrayList<Condicion> condicionesABorrar = new ArrayList<Condicion>();
		for (Condicion cond : condicionesDisponibles) {
			if (!cond.getDominio().equals(dominioFiltroCondiciones)) {
				condicionesABorrar.add(cond);
			}
		}
		condicionesDisponibles.removeAll(condicionesABorrar);

		Collections.sort(condicionesDisponibles);
		Collections.sort(condicionesSeleccionadas);
		log.debug("Se actualizo la lista de condiciones");
	}

	public void actualizarAcciones() {
		ArrayList<Accion> accionesABorrar = new ArrayList<Accion>();
		for (Accion accion : resultadosEsperadosDisponibles) {
			if (!accion.getDominio().equals(dominioFiltroCondiciones)) {
				accionesABorrar.add(accion);
			}
		}
		resultadosEsperadosDisponibles.removeAll(accionesABorrar);
		log.debug("Se actualizo la lista de acciones");
	}

	private void setearFechaYAutor() {
		// fecha
		tablaDesicion.setFecha(new Date(System.currentTimeMillis()));

		// autor
		Usuario user = (Usuario) Contexts.getSessionContext().get("user");
		if (user.getNombre() != null && !user.getNombre().trim().equals("")) {
			tablaDesicion.setAutor(user.getNombre());
		} else {
			tablaDesicion.setAutor(user.getUsername());
		}
	}

	public String guardarBorrador() {

		setearFechaYAutor();

		if (!filaEditada) {
			tablaDesicion = (TablaDecision) persistenceService.save(tablaDesicion);
		} else {
			tablaDesicion = (TablaDecision) persistenceService.update(tablaDesicion);
			filaEditada = false;
		}

		FacesMessages.instance().add(new FacesMessage("La regla ha sido guardada en formato borrador"));
		return "/tablaDecisionList.xhtml";
	}

	public void guardarBorradorFila() {

		setearFechaYAutor();

		if (!filaEditada) {
			tablaDesicion = (TablaDecision) persistenceService.save(tablaDesicion);
		} else {
			tablaDesicion = (TablaDecision) persistenceService.update(tablaDesicion);
			filaEditada = false;
		}

		FacesMessages.instance().add(new FacesMessage("La regla ha sido guardada en formato borrador"));
	}

	public void versionarTabla() {

		tablaDesicion = (TablaDecision) persistenceService.versioning(tablaDesicion);
		FacesMessages.instance().add(new FacesMessage("La regla ha sido versionada correctamente"));
	}

	@End
	public String cancelar() {
		return "/tablaDecisionList.xhtml";
	}

	public String agregarFila() {

		fakeFila = new ArrayList<FilaTablaDecision>();

		ArrayList juegoCondiciones = new ArrayList();
		filaAAgregar = new FilaTablaDecision();

		List<Descisor> _list = new ArrayList();
		Descisor d;

		log.debug("tamanio cond seleccionadas: " + this.condicionesSeleccionadas.size());
		for (int i = 0; i < this.condicionesSeleccionadas.size(); i++) {
			d = new Descisor();
			d.setCondicion(condicionesSeleccionadas.get(i));
			_list.add(d);
		}

		filaAAgregar.setValores(_list);

		fakeFila.add(filaAAgregar);
		this.setResultadosEsperadosPreSeleccionados(new ArrayList<Accion>(this.getResultadosEsperadosSeleccionados()));

		return "/agregarFila.xhtml";
	}

	@SuppressWarnings("unchecked")
	public String generarFilas() {
		log.debug("generarFilas");
		this.setFilasAAsignarAcciones(new ArrayList<FilaTablaDecision>());
		tablaDesicion.setFilas(new ArrayList<FilaTablaDecision>());
		FilaTablaDecision fila = null;
		List<Descisor> valores;
		Descisor d;
		if (this.condicionesSeleccionadas.size() == 1) {
			Condicion cond = this.condicionesSeleccionadas.get(0);
			if ((cond.getCondicionAtributoList().isEmpty()) && (cond.getCondicionFormulaList().isEmpty())) {
				for (Literal lit : cond.getLiterales()) {
					fila = new FilaTablaDecision();
					valores = new ArrayList<Descisor>();
					d = new Descisor();
					d.setValor(lit);
					d.setCondicion(cond);
					valores.add(d);
					fila.setValores(valores);
					tablaDesicion.getFilas().add(fila);
				}
				log.debug("Se generaron " + cond.getLiterales().size() + " filas");
			}
			// En caso de que sea = atributo de otra entidad o Formula
			else {
				if (cond.getCondicionAtributoList().isEmpty() == false) {
					for (CondicionAtributo condAtr : cond.getCondicionAtributoList()) {
						fila = new FilaTablaDecision();
						valores = new ArrayList<Descisor>();
						d = new Descisor();
						d.setValorCondicionAtributo(condAtr);
						d.setCondicion(cond);
						valores.add(d);
						fila.setValores(valores);
						tablaDesicion.getFilas().add(fila);
					}
				} else {
					for (CondicionFormula condForm : cond.getCondicionFormulaList()) {
						fila = new FilaTablaDecision();
						valores = new ArrayList<Descisor>();
						d = new Descisor();
						d.setValorCondicionFormula(condForm);
						d.setCondicion(cond);
						valores.add(d);
						fila.setValores(valores);
						tablaDesicion.getFilas().add(fila);
					}
				}
			}
		} else {
			long cantFilas = 1;
			Literal literal = new Literal();
			List<List<Literal>> listaLiterales = new ArrayList();
			List<List<CondicionAtributo>> listaCondicionAtributo = new ArrayList();
			List<List<CondicionFormula>> listaCondicionFormula = new ArrayList();

			for (Condicion cond : this.condicionesSeleccionadas) {
				if ((cond.getCondicionAtributoList().isEmpty()) && (cond.getCondicionFormulaList().isEmpty())) {
					List<Literal> literales = new ArrayList(cond.getLiterales());
					literales.add(literal);
					listaLiterales.add(literales);
					cantFilas *= literales.size();
				}
				// En caso de que sea = atributo de otra entidad
				else {
					if (cond.getCondicionAtributoList().isEmpty() == false) {
						List<CondicionAtributo> condAtrList = new ArrayList(cond.getCondicionAtributoList());
						condAtrList.add(new CondicionAtributo());
						listaCondicionAtributo.add(condAtrList);
						cantFilas *= condAtrList.size();
					} else {
						if (cond.getCondicionFormulaList().isEmpty() == false) {
							List<CondicionFormula> condForm = new ArrayList(cond.getCondicionFormulaList());
							condForm.add(new CondicionFormula());
							listaCondicionFormula.add(condForm);
							cantFilas *= condForm.size();
						}
					}
				}
				// si supera las 300 filas, se cancela el proceso y se informa
				// al usuario
				if (cantFilas > 300) {
					log.debug("Se cancela la generacion de filas.");
					log.debug("Cantidad de filas a generar: mas de " + cantFilas + ".");
					FacesMessages.instance().add(new FacesMessage("Se intentaron generar mÔøΩs de 300 filas. Se cancelo el proceso."));
					return null;
				}

			}

			// se descuenta la fila con valores en blanco
			cantFilas -= 1;
			log.debug("Cantidad de filas a generar: " + cantFilas + ".");

			List<Object> resultado = new ArrayList<Object>();
			resultado.addAll(listaLiterales);
			resultado.addAll(listaCondicionAtributo);
			resultado.addAll(listaCondicionFormula);
			Collections.reverse(resultado);

			List<?> result = cartesianProduct((List[]) resultado.toArray(new List[resultado.size()]));

			for (List<Object> literales : (List<List<Object>>) result) {
				fila = new FilaTablaDecision();
				valores = new ArrayList<Descisor>();
				int i = 0;
				for (Object obj : (List<Object>) literales) {

					if (obj instanceof Literal) {
						if (((Literal) obj).getId() != null) {
							d = new Descisor();
							d.setValor((Literal) obj);
							d.setCondicion(condicionesSeleccionadas.get(i));
							valores.add(d);
						}
					} else {
						if (obj instanceof CondicionAtributo) {
							if (((CondicionAtributo) obj).getId() != null) {
								d = new Descisor();
								d.setValorCondicionAtributo((CondicionAtributo) obj);
								d.setCondicion(condicionesSeleccionadas.get(i));
								valores.add(d);
							}
						} else {
							if (((CondicionFormula) obj).getId() != null) {
								d = new Descisor();
								d.setValorCondicionFormula((CondicionFormula) obj);
								d.setCondicion(condicionesSeleccionadas.get(i));
								valores.add(d);
							}
						}
					}
					i++;
				}

				fila.setValores(valores);
				tablaDesicion.getFilas().add(fila);
			}

			// se quita la ultima fila que contiene todos los literales vacios.
			tablaDesicion.getFilas().remove(tablaDesicion.getFilas().size() - 1);
			log.debug("El producto cartesiano dio un total de " + tablaDesicion.getFilas().size() + " filas");
		}
		ordenarTablaDecision(this.getTablaDesicion().getFilas(), this.getCondicionesSeleccionadas());
		guardarBorradorFila();
		return "/editor.xhtml";
	}

	public static List<List<Object>> cartesianProduct(List<?>... lists) {
		if (lists.length < 2)
			throw new IllegalArgumentException("Can't have a product of fewer than two sets (got " + lists.length + ")");

		return _cartesianProduct(0, lists);
	}

	private static List<List<Object>> _cartesianProduct(int index, List<?>... lists) {
		List<List<Object>> ret = new ArrayList<List<Object>>();
		if (index == lists.length) {
			ret.add(new ArrayList<Object>());
		} else {
			for (Object obj : lists[index]) {
				for (List<Object> set : _cartesianProduct(index + 1, lists)) {
					set.add(obj);
					ret.add(set);
				}
			}
		}
		return ret;
	}

	public void addRemoveFila(FilaTablaDecision fila, Integer nroFila) {

		/*
		 * Si ya estaba en la lista entonces se lo va a sacar de la lista porque
		 * el usuario destildo la opcion de mover
		 */
		if (filasAAsignarAcciones.contains(fila)) {
			log.debug("Se desmarco la fila " + (nroFila + 1));
			filasAAsignarAcciones.remove(fila);
			return;
		}
		log.debug("Se marco la fila " + (nroFila + 1));
		filasAAsignarAcciones.add(fila);
	}

	public String asignarAcciones() {

		if (this.getFilasAAsignarAcciones().size() == 0) {
			FacesMessages.instance().add(new FacesMessage("Debe seleccionar cual/es es/son la/s fila/s a asignar Acciones"));
			return null;
		}

		fakeFila = new ArrayList<FilaTablaDecision>();
		filaAAgregar = new FilaTablaDecision();
		fakeFila.add(filaAAgregar);
		this.setResultadosEsperadosPreSeleccionados(new ArrayList<Accion>(this.getResultadosEsperadosSeleccionados()));
		filaEditada = true;
		accionHome.setAsignarAcciones(Boolean.TRUE);

		accionHome.setResultadosEsperadosPreSeleccionados(this.getResultadosEsperadosPreSeleccionados());

		return "/asignarAcciones.xhtml";
	}

	public String crearAccion() {
		accionHome.setResultadosEsperadosPreSeleccionados(this.getResultadosEsperadosPreSeleccionados());
		accionHome.setInstance(accionHome.createInstance());
		return "/crearAccion.xhtml";
	}

	public String guardarAcciones() {

		if (tablaDesicion.getFilas() == null) {
			tablaDesicion.setFilas(new ArrayList<FilaTablaDecision>());
			log.debug("No existe la tabla de desicion y se crea por primera vez");
		}

		if (filaAAgregar.getAcciones().size() == 0) {
			FacesMessages.instance().add(
					new FacesMessage("Debe indicar cual/es es/son la/s accion/es a ejecutar fruto de la evaluacion de la condicion"));
			return null;
		}

		log.debug("Tam tabla " + tablaDesicion.getFilas().size());

		for (FilaTablaDecision fila : this.getFilasAAsignarAcciones()) {
			fila.setAcciones(filaAAgregar.getAcciones());
			fila.setMensajeOperadorUdai(filaAAgregar.getMensajeOperadorUdai());
			fila.setMensajeUsuarioWEB(filaAAgregar.getMensajeUsuarioWEB());
			fila.setObservacion(filaAAgregar.getObservacion());
		}
		log.debug("Cantidad de Filas editadas: " + this.getFilasAAsignarAcciones().size());

		guardarBorradorFila();

		this.setFilasAAsignarAcciones(new ArrayList<FilaTablaDecision>());

		return "/editor.xhtml";
	}

	public String guardarFila() {

		if (filas == null)
			filas = new ArrayList();

		if (tablaDesicion.getFilas() == null) {
			tablaDesicion.setFilas(new ArrayList<FilaTablaDecision>());
			log.debug("No existe la tabla de desicion y se crea por primera vez");
		}

		if (filaAAgregar.getAcciones().size() == 0) {
			FacesMessages.instance().add(
					new FacesMessage("Debe indicar cual/es es/son la/s accion/es a ejecutar fruto de la evaluacion de la condicion"));
			return null;
		}

		log.debug("Tam tabla " + tablaDesicion.getFilas().size());

		if (!filaEditada) {
			/*if (tablaDesicion.getFilas().contains(filaAAgregar)) {
				log.debug("La fila ya existe");
				facesMessages.add("La fila ya existe.");
			} else {
				log.debug("La fila no existe, se agrega.");
				tablaDesicion.getFilas().add(filaAAgregar);
				log.debug("Fila a agregar en la tabla: " + filaAAgregar.toString());
				guardarBorradorFila();
			}*/
			
			
			//TODO: Se quita el control de repeticion de fila para evaluar mas adelante. 
			
			log.debug("La fila no existe, se agrega.");
			tablaDesicion.getFilas().add(filaAAgregar);
			log.debug("Fila a agregar en la tabla: " + filaAAgregar.toString());
			guardarBorradorFila();
		} else {
			log.debug("Fila a editar en la tabla: " + filaAAgregar.toString());
			guardarBorradorFila();
		}

		return "/editor.xhtml";
	}

	public void eliminarFila(FilaTablaDecision fila) {

		FilaTablaDecision t;

		for (int i = 0; i < tablaDesicion.getFilas().size(); i++) {
			t = (FilaTablaDecision) tablaDesicion.getFilas().get(i);
			if (t == fila) {
				tablaDesicion.getFilas().remove(i);
				break;
			}
		}
	}

	@Begin(join = true)
	public String editarFila(FilaTablaDecision fila) {
		if (fila == null)
			return null;
		if (this.getTablaDesicion().getFilas() == null)
			return null;
		if (this.getTablaDesicion().getFilas().contains(fila)) {
			fakeFila = new ArrayList<FilaTablaDecision>();
			int indice = this.getTablaDesicion().getFilas().indexOf(fila);
			this.filaAAgregar = this.getTablaDesicion().getFilas().get(indice);
			this.setResultadosEsperadosPreSeleccionados(new ArrayList<Accion>(this.getResultadosEsperadosSeleccionados()));
			if (this.filaAAgregar.getAcciones() != null) {
				this.getResultadosEsperadosPreSeleccionados().removeAll(this.filaAAgregar.getAcciones());
			}
			filaEditada = true;
			accionHome.setAsignarAcciones(Boolean.FALSE);
			fakeFila.add(filaAAgregar);
			return "/agregarFila.xhtml";
		} else
			return null;
	}

	public String proximoPaso(String pasoActual) {

		if (pasoActual.equals("DETALLEREGLA")) {
			if (this.tablaDesicion.getInstrumentos().size() == 0) {
				facesMessages.add("Debe seleccionar al menos un instrumento normativo.");
				return null;
			}
			// [DFB] grabar borrador
			guardarBorrador();
			return "/condiciones.xhtml";
		}

		if (pasoActual.equals("CONDICION")) {
			// this.actualizarCondicionesSeleccionadas();
			if (this.getCondicionesSeleccionadas().isEmpty()) {
				facesMessages.add("Debe seleccionar por lo menos una condicion.");
				return null;
			}
			// [DFB] grabar borrador
			guardarBorrador();
			return "/resultado_esperado.xhtml";
		}
		if (pasoActual.equals("RESULTADOREGLA")) {

			if ((resultadosEsperadosSeleccionados == null) || (resultadosEsperadosSeleccionados.size() == 0)) {
				FacesMessages.instance().add(new FacesMessage("Debe seleccionar las acciones posibles resultantes de las reglas"));
				return null;
			}
			this.setResultadosEsperadosPreSeleccionados(new ArrayList<Accion>(this.getResultadosEsperadosSeleccionados()));

			ordenarTablaDecision(this.getTablaDesicion().getFilas(), this.getCondicionesSeleccionadas());

			// [DFB] grabar borrador
			guardarBorrador();
			return "/editor.xhtml";
		}
		return null;
	}

	public void ordenarTablaDecision(List<FilaTablaDecision> filas, List<Condicion> condicionesSeleccionadas) {
		// List<FilaTablaDecision> filas = this.getTablaDesicion().getFilas();
		if (filas != null) {
			for (FilaTablaDecision fila : filas) {
				List<Descisor> valores = fila.getValores();
				List<Descisor> valoresABorrar = new ArrayList<Descisor>();
				for (Descisor valor : valores) {
					if (!condicionesSeleccionadas.contains(valor.getCondicion())) {
						valoresABorrar.add(valor);
					}
				}
				valores.removeAll(valoresABorrar);
				for (Condicion cond : condicionesSeleccionadas) {
					boolean found = false;
					for (Descisor valor : valores) {
						if (valor.getCondicion().equals(cond)) {
							found = true;
						}
					}
					if (!found) {
						Descisor d = new Descisor();
						d.setCondicion(cond);
						valores.add(d);
					}
				}
				Collections.sort(valores);
			}
		}
	}

	public String anteriorPaso(String pasoActual) {

		if (pasoActual.equals("CONDICION"))
			return "/detalle_regla.xhtml";

		if (pasoActual.equals("EDITOR"))
			return "/resultado_esperado.xhtml";

		if (pasoActual.equals("RESULTADOREGLA"))
			return "/condiciones.xhtml";

		return null;
	}

	public String cancelarFila() {
		return "/editor.xhtml";
	}

	public String cancelarFilas() {
		this.setFilasAAsignarAcciones(new ArrayList<FilaTablaDecision>());
		return "/editor.xhtml";
	}

	public String getUltimoLlamado() {
		return ultimoLlamado;
	}

	public void setUltimoLlamado(String ultimoLlamado) {
		this.ultimoLlamado = ultimoLlamado;
	}

	public void processSelectionInstrumento(NodeSelectedEvent event) {
		HtmlTree tree = (HtmlTree) event.getComponent();

		TreeNode currentNode = tree.getModelTreeNode(tree.getRowKey());

		nodoSeleccionadoInstrumento = currentNode;
	}

	public TreeNode getTreeNodeInstrumento() {
		if (rootNodeInstrumento == null) {
			loadTreeInstrumento();
		}
		return rootNodeInstrumento;
	}

	private void loadTreeInstrumento() {
		List<Instrumento> instrumentos = new InstrumentoList().getResultList();
		rootNodeInstrumento = new TreeNodeImpl<Instrumento>();

		if (instrumentos.isEmpty()) {
			TreeNodeImpl<Instrumento> carpetaRaiz = new TreeNodeImpl<Instrumento>();

			Instrumento i = new Instrumento();
			i.setDescripcion("Instrumentos Normativos");
			i.setEsCarpeta(true);

			carpetaRaiz.setData(i);

			rootNodeInstrumento.addChild("root", carpetaRaiz);
			return;
		}
		log.debug("se ejecuta bien la carga del arbol");

		addNodesInstrumento(null, rootNodeInstrumento, instrumentos);
	}

	private void addNodesInstrumento(Instrumento padre, TreeNode<Instrumento> node, List<Instrumento> instrumentos) {
		for (Instrumento i : instrumentos) {
			if (i.getPadre() == padre && i.getEsCarpeta() && i.getActivo() != "N") {
				TreeNodeImpl<Instrumento> nodeImpl = new TreeNodeImpl<Instrumento>();
				nodeImpl.setData(i);
				node.addChild(i.getDescripcion(), nodeImpl);
				addNodesInstrumento(i, nodeImpl, instrumentos);
			}
		}
	}

	private void addInstrumento(TreeNodeImpl<Instrumento> nodo) {
		if (nodo.isLeaf()) {
			// agrego el instrumento porque es una hoja

			if (!this.tablaDesicion.getInstrumentos().contains(nodo.getData()))
				this.tablaDesicion.getInstrumentos().add(nodo.getData());
		} else {
			Iterator<Map.Entry<Object, TreeNode<Instrumento>>> it = nodo.getChildren();
			while (it.hasNext()) {

				addInstrumento((TreeNodeImpl<Instrumento>) it.next().getValue());
			}
		}

	}

	public void addInstrumentoSeleccionado() {
		if (nodoSeleccionadoInstrumento == null)
			return;
		if (nodoSeleccionadoInstrumento.getData() == null)
			return;
		addInstrumento((TreeNodeImpl<Instrumento>) nodoSeleccionadoInstrumento);
	}

	public void addInst(Instrumento i) {

		if (this.tablaDesicion.getInstrumentos().contains(i))
			return;

		this.tablaDesicion.getInstrumentos().add(i);
	}

	public TablaDecision getTablaDesicion() {
		return tablaDesicion;
	}

	public void setTablaDesicion(TablaDecision tablaDesicion) {
		this.tablaDesicion = tablaDesicion;
	}

	public void eliminarInstrumentoSeleccionado(Instrumento instrumento) {
		this.tablaDesicion.getInstrumentos().remove(instrumento);
	}

	public void setNameInstrument(String nameInstrument) {
		this.nameInstrument = nameInstrument;
	}

	public String getNameInstrument() {
		return nameInstrument;
	}

	public void setListInstruments(ArrayList<Instrumento> listInstruments) {
		this.listInstruments = listInstruments;
	}

	public ArrayList<Instrumento> getListInstruments() {
		return listInstruments;
	}

	public boolean isExistent() {
		return (tablaDesicion.getId() != null);
	}

	public void actualizarNombre() {
		// TODO este metodo se sigue usando???}
	}

	public void viewInstOfFolderSelected() {

		if (nodoSeleccionadoInstrumento == null) {
			log.debug("Debe seleccionar una carpeta");
			return;
		}

		Query query = persistenceService.createQuery(
				"" + "SELECT" + " instrumento FROM Instrumento as instrumento"
						+ " WHERE instrumento.padre = :inst and instrumento.esCarpeta = false and activo <>'N'").setParameter("inst",
				((Instrumento) nodoSeleccionadoInstrumento.getData()));

		this.listInstruments = (ArrayList<Instrumento>) query.getResultList();

	}

	public void searchNormativeInstrument() {

		log.debug("Buscado los intrumentos normativos de la carpeta seleccionada. Los resultados de la busqueda se dejaran en una variable de instancia");

		Query query = persistenceService.createQuery(
				"" + "SELECT" + " instrumento FROM Instrumento as instrumento"
						+ " WHERE instrumento.descripcion like :patron and instrumento.esCarpeta = false and activo <>'N'").setParameter("patron",
				"%" + nameInstrument + "%");

		this.listInstruments = (ArrayList<Instrumento>) query.getResultList();
		log.debug("El tama√±o de la lista de la busqueda es " + listInstruments.size());
	}

	public List<Condicion> getCondicionesSeleccionadas() {
		return condicionesSeleccionadas;
	}

	public void setCondicionesSeleccionadas(List<Condicion> condicionesSeleccionadas) {
		this.condicionesSeleccionadas = condicionesSeleccionadas;
	}

	WrapperRulesFactory factory;

	public void getDRLFile(TablaDecision tabla) throws IOException {
		factory = new WrapperRulesFactory(tabla);
		factory.setLog(log);
		
		String regla =factory.buildRule();
		log.debug(regla);
		crearArchivoDrl();
		presentarArchivo(regla);
		
	}

	public void crearArchivoDrl() {
		File archivo = null;
		BufferedWriter bufferedWriter = null;
		String dir = "";
		String resultado = factory.buildRule();
		String nombreRegla = factory.getTablaDecision().getNombre().trim();

		try {
			SystemProperties sys = (SystemProperties) Component.getInstance(SystemProperties.class);

			dir = sys.getDirectorioGestorArchivosDRL();

			log.debug("OBTUVO EL DIRECTORIO: " + dir + File.separator + nombreRegla + ".drl");
			
			String nombreArchivo=dir + File.separator + nombreRegla + ".drl";
			archivo = new File(nombreArchivo);

			bufferedWriter = new BufferedWriter(new FileWriter(archivo));
			bufferedWriter.write(resultado);
			//presentarArchivo(resultado);
			
		} catch (FileNotFoundException ex) {
			log.error(ex);
		} catch (IOException ex) {
			log.error(ex);
		} finally {
			// Close the BufferedWriter
			try {
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @param regla
	 * @throws IOException
	 */
	public void presentarArchivo(String regla) throws IOException{
		  FileChannel drl = null;
	      WritableByteChannel respuesta = null;
	        
	        try {
	            
	            /* configura la respuesta http */
	            HttpServletResponse response = (HttpServletResponse)FacesContext
	                                                        .getCurrentInstance()
	                                                            .getExternalContext()
	                                                                .getResponse();
	            response.setContentType("text");
	            
	        
	            FacesContext facesContext = FacesContext.getCurrentInstance();
	            OutputStream responseStream = ((HttpServletResponse)facesContext.getExternalContext().getResponse()).getOutputStream();
	            
	            String reglaFinal=  "\n\npackage ar.org.anses.prissa.reglas;\n" +
	            		"\n" +
	            		"import ar.gov.anses.prissa.asistente.modelosemantico.*; \n" +
	    	    		"import ar.gob.anses.prissa.mi.asistente_reglas.simulador.MensajeSistema;\n" +
	    	    		"import java.util.*;" +
	    	    		"import ar.gob.anses.prissa.mi.asistente_reglas.excepciones.RuleException;\n\n" + 
	    	    		
	    	    		"global ar.gov.anses.prissa.asistente2.messages.Report report;\n" +
	    	    		"global RuleException exception;\n" +
	    	    		"\n\n\n" + regla;
	            
	            log.debug("Regla a enviar al browser: " + reglaFinal);
	            responseStream.write(reglaFinal.getBytes());
	            
	          
	            
	        } catch (IOException ioe) {
	            
	            log.error("Error escribiendo drl en la respuesta. Causado por " +  
	                      ioe.getClass().getName() + ": "  + ioe.getMessage());
	            log.debug("Exception", ioe);
	            
	        } finally {
	            if (drl!=null)
	            	drl.close();
	            
	            if (respuesta!=null)
	            	respuesta.close();	            
	        }
	        
	        FacesContext.getCurrentInstance().responseComplete();
		
	}
	
	/**
	 * Genera un ruleAgent con la tabla de desicion proporcionada. 
	 * @param tabla
	 */
	public void generarAgente(TablaDecision tabla) throws Exception{
		SystemProperties sys = (SystemProperties) Component.getInstance(SystemProperties.class);

		String workspace = sys.getWorkSpaceAgentes();
		try {
			AgentFactory agente = new AgentFactory();
			agente.setLog(log);
			agente.generar(tabla, workspace);
			agente.generarSVNFile(sys.getSVNServer(), workspace);
			FacesMessages.instance().add(new FacesMessage("El agente ha sido generado existosamente "));
		} catch (Exception e) {
			throw e;
			//log.error(e);
			//FacesMessages.instance().add(new FacesMessage("Ha ocurrido un error al crear el agente. Error: " + e.getMessage()));
		}
		
		

	}

}
