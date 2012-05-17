package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Condicion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.CondicionAtributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.CondicionFormula;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Formula;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Funcion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Literal;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.ReglaPseudocodigo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.custom.BaseHome;

@Name("condicionHome")
@Scope(ScopeType.CONVERSATION)
public class CondicionHome extends BaseHome<Condicion> {

	private boolean bandera = false;

	@In(create = true, required = false)
	@Out(required = false)
	Condicion condicion;

	@In(create = true, required = false)
	@Out(required = false)
	CondicionList condicionList;

	@In
	FacesMessages facesMessages;

	@Out(required = false)
	List<Atributo> atributos;

	private String tipoCarga;

	@Out(required = false)
	List<Entidad> entidades;

	@Out(required = false)
	List<Entidad> entidades2;

	private String currentLiteral;
	private String currentLiteralNumero;
	private String currentLiteralBoolean;
	private Date currentLiteralFecha;
	private String auxMenorAUnValor;
	private Date auxMenorAUnaFecha;
	// entidad para el atributo 2
	private Entidad entidadAtributo2;

	private List<Atributo> AtributosEntidad2;
	private Atributo atributo2;
	private String msgErrorLiteral;
	// private List<Literal> literales = new ArrayList<Literal>();

	// public List<Literal> getLiterales() {
	// return literales;
	// }

	// public void setLiterales(List<Literal> literales) {
	// this.literales = literales;
	// }

	@Logger
	Log log;

	@In(create = true, required = false)
	@Out(required = false)
	String nombreReglaTablaDecision = new String();

	@In(create = true, required = false)
	@Out(required = false)
	String nombreFuncion = new String();

	Properties props = null;
	// Formulas
	// ----------------------------------------------------------
	String nombreFormula = "";

	List<Formula> formulas;

	CondicionFormula condformulaSelected = new CondicionFormula();

	Formula formulaSelected;

	public void buscarFormulas(String criterio) {
		if ((criterio.equals("")) || (criterio == null)) {
			Query query = entityManager.createQuery("SELECT f FROM Formula f");
			this.formulas = query.getResultList();

		} else {
			Query query = entityManager
					.createQuery(
							"SELECT f FROM Formula f where f.nombre like concat('%',concat(:criterio,'%'))")
					.setParameter("criterio", criterio);
			this.formulas = query.getResultList();
		}
	}

	public String getNombreFormula() {
		return nombreFormula;
	}

	public void setNombreFormula(String nombreFormula) {
		this.nombreFormula = nombreFormula;
	}

	public String agregarFormula(Formula formula) {
		if (this.operadorLogico == "") {
			facesMessages.add("Debe seleccionar un Operador L\u00F3gico.");
			return null;
		} else if (this.formulaSelected == null) {
			facesMessages.add("Debe seleccionar una f\u00F3rmula.");
			return null;
		}

		condformulaSelected.setOperadorLogico(operadorLogico);
		condformulaSelected.setFormula(formula);

		if (encontrarDuplicado() == false) {
			this.instance.getCondicionFormulaList().add(condformulaSelected);
			condformulaSelected = new CondicionFormula();
			return null;
		} else {
			facesMessages.add("El valor ya se encuentra cargado.");
			return null;
		}
	}

	public Formula getFormulaSelected() {
		return formulaSelected;
	}

	public void setFormulaSelected(Formula formulaSelected) {
		this.formulaSelected = formulaSelected;
	}

	public boolean encontrarDuplicado() {

		boolean band = false;
		for (Iterator iterator = instance.getCondicionFormulaList().iterator(); iterator
				.hasNext();) {
			CondicionFormula condicionFormula2 = (CondicionFormula) iterator
					.next();

			if (condformulaSelected.equals(condicionFormula2) && condformulaSelected.getOperadorLogico().equals(condicionFormula2.getOperadorLogico())) {
				band = true;
				break;
				
			}
		}

		return band;
	}

	// ---------------------------------------------------------
	public List<String> getListaOperadores() {
		List<String> l = new ArrayList<String>();

		l.add("==");

		if (!esBooleano()) {
			l.add("<");
			l.add(">");
			l.add("<=");
			l.add(">=");
			l.add("!=");

			if (!esTexto()) {
				l.add("BETWEEN");
			}
		}
		return l;
	}

	public List<Formula> getFormulas() {
		return formulas;
	}

	public void setFormulas(List<Formula> formulas) {
		this.formulas = formulas;
	}

	public CondicionFormula getCondformulaSelected() {
		return condformulaSelected;
	}

	public void setCondformulaSelected(CondicionFormula condformulaSelected) {
		this.condformulaSelected = condformulaSelected;
	}

	public String getNombreReglaTablaDecision() {
		return nombreReglaTablaDecision;
	}

	public void setNombreReglaTablaDecision(String nombreReglaTablaDecision) {
		this.nombreReglaTablaDecision = nombreReglaTablaDecision;
	}

	public String getNombreFuncion() {
		return nombreFuncion;
	}

	public void setNombreFuncion(String nombreFuncion) {
		this.nombreFuncion = nombreFuncion;
	}

	private boolean modificado = false;

	@In(create = true, required = false)
	@Out(required = false)
	private String operadorLogico;

	@In(create = true)
	private EntityManager entityManager;

	private List<TablaDecision> listTablaDecision;

	public List<TablaDecision> getListTablaDecision() {
		return listTablaDecision;
	}

	public void setListTablaDecision(List<TablaDecision> listTablaDecision) {
		this.listTablaDecision = listTablaDecision;
	}

	public List<ReglaPseudocodigo> getListRPS() {
		return listRPS;
	}

	public void setListRPS(List<ReglaPseudocodigo> listRPS) {
		this.listRPS = listRPS;
	}

	private List<ReglaPseudocodigo> listRPS;

	private List<Funcion> listFunciones;

	private boolean seleccionActivado;

	private static final long serialVersionUID = -5507163986931035275L;

	public String getCurrentLiteral() {
		return currentLiteral;
	}

	public void setCurrentLiteral(String currentLiteral) {
		this.currentLiteral = currentLiteral;
	}

	public void setCurrentLiteralNumero(String currentLiteralNumero) {
		this.currentLiteralNumero = currentLiteralNumero;
	}

	public String getCurrentLiteralNumero() {
		return currentLiteralNumero;
	}

	public String getCurrentLiteralBoolean() {
		return currentLiteralBoolean;
	}

	public void setCurrentLiteralBoolean(String currentLiteralBoolean) {
		this.currentLiteralBoolean = currentLiteralBoolean;
	}

	public Date getCurrentLiteralFecha() {
		return currentLiteralFecha;
	}

	public void setCurrentLiteralFecha(Date currentLiteralFecha) {
		this.currentLiteralFecha = currentLiteralFecha;
	}

	public void setCondicionId(Long id) {
		setId(id);
	}

	public Long getCondicionId() {
		return (Long) getId();
	}

	@Override
	protected Condicion createInstance() {
		Condicion condicion = new Condicion();
		condicion.setCondicionAtributoList(new ArrayList<CondicionAtributo>());
		condicion.setCondicionFormulaList(new ArrayList<CondicionFormula>());
		entidadAtributo2 = new Entidad();
		atributo2 = new Atributo();
		return condicion;
	}

	public void wire() {
		getInstance();
		log.info("wire");
		try {
			if (this.instance.getAtributo().getTipoCarga().equals("LOGICO"))
				log.info("wire2");
			if (this.instance.getRegla() != null
					|| this.instance.getReglaPsC() != null) {
				log.info("wire2.1");
				this.setTipoCarga("REG");
			} else {
				if (this.instance.getFuncion() != null) {
					log.info("wire2.2");
					this.setTipoCarga("FUN");
				}
			}
			if (this.instance.getRegla() == null
					&& this.instance.getReglaPsC() == null
					&& this.instance.getFuncion() == null) {
				this.setTipoCarga("REG");
				if ((instance.getId() != 0L) && (bandera == false))
					fijarValorPosibleEdicion();
			}
		} catch (NullPointerException e) {
		}
	}

	public boolean isWired() {
		return true;
	}

	public Condicion getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public void setValorDelTipoDeCarga() {
		log.info("De tipo " + this.tipoCarga);
	}

	/*
	 * El literal para el operador logico BETWEEN se guarda con formato
	 * cotaInferior:cotaSuperior
	 */

	public void addLiteral() {
		msgErrorLiteral = null;
		String opLog = "";
		String currentLiteral = "";

		if ((this.operadorLogico == null) || (this.operadorLogico.isEmpty())) {
			facesMessages.add("Debe seleccionar un operador logico");
			return;
		}

		if (props == null) {
			URL url = getClass().getResource("Parametros.properties");

			if (url != null) {
				try {
					InputStream in = url.openStream();
					props = new Properties();
					props.load(in);
				} catch (IOException ioe) {
					log.info("NO se encuentra el archivo de propiedades Parametros.properties");
					facesMessages
							.add("No fue posible setear el valor del properties.");
					return;
				}
			}
		}
		if (esVacio()) {
			currentLiteral = props.getProperty("condicion.valor");
		} else {

			if (esTexto())
				currentLiteral = this.currentLiteral;

			if (esNumero()) {
				double cotaSup = -999999;
				double cotaInf = 999999;

				try {
					cotaInf = Double.parseDouble(this.currentLiteralNumero);
					if (operadorLogico.equals("BETWEEN"))
						cotaSup = Double.parseDouble(this.auxMenorAUnValor);
				} catch (NumberFormatException e) {
					// facesMessages.add("el valor ingresado no es valido. Debe ingresar solamente un numero");
					msgErrorLiteral = "<ul><li>El valor ingresado no es valido. Debe ingresar solamente un numero</li></ul>";
					return;
				}
				if (operadorLogico.equals("BETWEEN"))
					if (cotaInf > cotaSup) {
						facesMessages
								.add("Orden de los limites de comparacion incorrectos");
						return;
					}

				if ((this.auxMenorAUnValor != null)
						&& (!this.auxMenorAUnValor.equals("")))
					currentLiteral = cotaInf + ":" + cotaSup;
				else
					currentLiteral = this.currentLiteralNumero;
			}

			if (esBooleano())
				currentLiteral = this.currentLiteralBoolean;

			if (esFecha()) {
				String fechaSup = "", fechaInf = "";
				Calendar cal = Calendar.getInstance();
				cal.setTime(getCurrentLiteralFecha());
				fechaInf = cal.get(Calendar.DATE) + "/"
						+ (cal.get(Calendar.MONTH) + 1) + "/"
						+ cal.get(Calendar.YEAR);
				if (operadorLogico.equals("BETWEEN")) {
					if (auxMenorAUnaFecha == null) {
						facesMessages
								.add("Debe especificar una fecha limite superior");
						return;
					}
					if (this.currentLiteralFecha.before(auxMenorAUnaFecha)) {
						Calendar aux = Calendar.getInstance();
						aux.setTime(auxMenorAUnaFecha);
						fechaSup = ":" + aux.get(Calendar.DATE) + "/"
								+ (aux.get(Calendar.MONTH) + 1) + "/"
								+ aux.get(Calendar.YEAR);
					} else {
						facesMessages
								.add("Orden de las fechas limite incorrecto");
						return;
					}
				}
				currentLiteral = fechaInf + fechaSup;
			}
		}
		opLog = this.operadorLogico.trim();
		this.setModificado(true);
		log.info(currentLiteral);

		if (currentLiteral == null || currentLiteral.trim().equals("")) {
			msgErrorLiteral = "<ul><li>No puede crearse un literal con valor vacio</li></ul>";
			return;
		}

		for (Literal lit : this.instance.getLiterales()) {
			if (lit.getNombre().trim().equals(currentLiteral.trim())
					&& lit.getOperadorLogico().equals(this.operadorLogico)) {
				log.info("No se permiten literales de mismo nombre");
				msgErrorLiteral = "<ul><li>No se pueden generar literales con mismo nombre y mismo operador logico</li></ul>";
				return;
			}
		}

		log.warn("se instancia nuevo literal");
		Literal l = new Literal();
		// l.setCondicion(this.instance);
		l.setNombre(currentLiteral);
		if (esVacio()) {
			l.setDescripcion("Vacio / Nulo");
		} else {
			l.setDescripcion(currentLiteral);
		}
		l.setOperadorLogico(opLog);

		List<Literal> pila = new ArrayList<Literal>();
		pila.add(l);
		pila.addAll(this.instance.getLiterales());
		this.instance.setLiterales(pila);

		this.currentLiteral = "";
		this.currentLiteralBoolean = "";
		this.currentLiteralFecha = null;
		this.currentLiteralNumero = "";
		this.auxMenorAUnValor = "";
		this.auxMenorAUnaFecha = null;

		// this.getInstance().getLiterales().add(l);
		this.generarPseudoCodigo();
	}

	public boolean isModificado() {
		return modificado;
	}

	public void setModificado(boolean modificado) {
		log.info("Seteo modificado: " + modificado);
		try {
			log.info("tipo del atributo: "
					+ this.instance.getAtributo().getTipoDato()
					+ " y su tpo de carga es: "
					+ this.instance.getAtributo().getTipoCarga());
		} catch (Exception e) {
		}
		this.modificado = modificado;
		this.operadorLogico = "";
	}

	public void generarPseudoCodigo() {
		log.warn("Se genera pseudoCodigo");
		// this.persist();
		// return "/condicion.xhtml";
	}

	public void eliminarLiteral(Literal l) {

		this.setModificado(true);
		log.warn("Elimino Literal");
		this.instance.getLiterales().remove(l);

		msgErrorLiteral = null;
	}

	@Override
	public String remove() {

		Query query = getPersistenceContext()
				.createQuery(
						""
								+ "SELECT"
								+ " tablaDecision FROM TablaDecision as tablaDecision join tablaDecision.filas as filas join filas.valores as valores"
								+ " WHERE valores.condicion = :cond")
				.setParameter("cond", this.instance);

		List<TablaDecision> reglasUsanConds = (List<TablaDecision>) query
				.getResultList();

		if (reglasUsanConds.size() != 0) {
			log.info("No se puede eliminar la condicion debido a que esta siendo usada por una regla");
			facesMessages
					.add("No se puede eliminar la condicion debido a que esta siendo usada por una regla");
			return null;
		}

		return super.remove();
	}

	@SuppressWarnings("unchecked")
	public List<Atributo> getAtributos() {

		if (this.instance.getEntidad() == null) {
			log.info("entidad es nulo");
			return null;
		}

		log.warn("Buscando los Atributos de entidad "
				+ this.instance.getEntidad().getNombre());

		if (this.instance.getEntidad().getNombre() == null)
			return null;

		Query query = getPersistenceContext().createQuery(
				"" + "SELECT" + " atributo FROM Atributo as atributo"
						+ " WHERE atributo.entidad=:ent").setParameter("ent",
				this.instance.getEntidad());

		this.atributos = query.getResultList();
		return this.atributos;
	}

	public List<Entidad> getEntidades() {
		return new EntidadList().getMyResultList();
	}

	public String persist() {

		// initCondicionAtributo();
		// verifico que el nombre de Condicion no esta siendo usado
		List<Condicion> condiciones = condicionList.getMyResultList();

		for (Condicion c : condiciones) {
			if ((c.getNombre().toUpperCase().trim()
					.equals(this.instance.getNombre().toUpperCase().trim()) && (c
					.getId() != this.instance.getId()))) {
				facesMessages
						.add("El nombre de la condicion ya esta siendo utilizado. Modifiquelo para guardar");
				return null;
			}
		}
		// Verifico si se trata de Formulas
		if (this.instance.getVacio().equals("formula")) {
			this.instance.setFuncion(null);
			this.instance.setReglaPsC(null);
			this.instance.setRegla(null);
			this.instance.setLiterales(null);
			this.instance.setCondicionAtributoList(null);
			return super.persist();
		}

		// Verifico si se trata de comparacion de Atributos
		if (this.instance.getVacio().equals("atributo")) {
			this.instance.setFuncion(null);
			this.instance.setReglaPsC(null);
			this.instance.setRegla(null);
			this.instance.setLiterales(null);
			this.instance.setCondicionFormulaList(null);
			return super.persist();
		}

		if (this.instance.getAtributo().getTipoCarga().equals("FISICO")) {
			this.instance.setFuncion(null);
			this.instance.setReglaPsC(null);
			this.instance.setRegla(null);
			this.instance.setCondicionAtributoList(null);
			this.instance.setCondicionFormulaList(null);
			if (this.instance.getLiterales().size() > 0) {
				return super.persist();
			}
			facesMessages
					.add("La condicion debe contenter al menos un literal");
			return null;
		} else {
			if (this.tipoCarga == null) {
				this.instance.setFuncion(null);
				this.instance.setCondicionAtributoList(null);
				this.instance.setCondicionFormulaList(null);
				if (this.instance.getLiterales().size() > 0) {
					return super.persist();
				}
				facesMessages
						.add("La condicion debe contenter al menos un literal");
				return null;
			}

			if (this.tipoCarga.equals("REG")) {
				this.instance.setFuncion(null);
				this.instance.setCondicionAtributoList(null);
				this.instance.setCondicionFormulaList(null);
				if (this.instance.getLiterales().size() > 0) {
					return super.persist();
				}
				facesMessages
						.add("La condicion debe contenter al menos un literal");
				return null;

				// facesMessages.add("la condicion debe contener una regla");
				// return null;
			} else {
				this.instance.setRegla(null);
				this.instance.setReglaPsC(null);
				this.instance.setCondicionAtributoList(null);
				this.instance.setCondicionFormulaList(null);
				if (this.instance.getLiterales().size() > 0) {
					return super.persist();
				}
				facesMessages
						.add("La condicion debe contenter al menos un literal");
				return null;
			}
		}

	}

	@SuppressWarnings("unchecked")
	public List<Atributo> getAtributosEntidad2() {

		if (this.entidadAtributo2 == null) {
			log.info("Entidad es nulo");
			return null;
		}

		// log.debug("Buscando los Atributos de entidad 2 " +
		// this.instance.getEntidad().getNombre());

		if (this.entidadAtributo2.getNombre() == null) {
			log.debug("El nombre de la Entidad2 Atributo es NULL");
			return null;
		}

		Query query = getPersistenceContext()
				.createQuery(
						""
								+ "SELECT"
								+ " atributo FROM Atributo as atributo"
								+ " WHERE (atributo.entidad=:ent AND atributo.tipoDato = :tDato )")
				.setParameter("ent", entidadAtributo2)
				.setParameter("tDato", instance.getAtributo().getTipoDato());

		this.AtributosEntidad2 = query.getResultList();

		log.debug("Cantidad de atributos de la entidad2 "
				+ AtributosEntidad2.size() + " Entidad Consultada: "
				+ this.entidadAtributo2.getNombre());
		return this.AtributosEntidad2;
	}

	public String update() {

		// verifico que el nombre de Condicion no esta siendo usado
		List<Condicion> condiciones = condicionList.getMyResultList();

		for (Condicion c : condiciones) {
			if ((c.getNombre().toUpperCase().trim()
					.equals(this.instance.getNombre().toUpperCase().trim()) && (c
					.getId() != this.instance.getId()))) {
				facesMessages
						.add("El nombre de la condicion ya esta siendo utilizado. Modifiquelo para guardar");
				return null;
			}
		}

		if (this.instance.getAtributo().getTipoCarga().equals("FISICO")) {
			this.instance.setFuncion(null);
			this.instance.setReglaPsC(null);
			this.instance.setRegla(null);
			if (this.instance.getLiterales().size() > 0) {
				return super.persist();
			}
			facesMessages
					.add("La condicion debe contenter al menos un literal");
			return null;
		} else {
			if (this.tipoCarga.equals("REG")) {
				this.instance.setFuncion(null);
				this.instance.setLiterales(null);
				if (this.instance.getRegla() != null
						|| this.instance.getReglaPsC() != null)
					return super.update();
				facesMessages.add("la condicion debe contener una regla");
				return null;
			} else {
				this.instance.setRegla(null);
				this.instance.setReglaPsC(null);
				this.instance.setLiterales(null);
				if (this.instance.getFuncion() != null)
					return super.update();
				facesMessages.add("La condicion debe tener una función");
				return null;
			}
		}
	}

	public String getCreatedMessage() {
		return this.isManaged() ? "Condicion #{condicionHome.instance.nombre} actualizada exitosamente"
				: "Nueva condicion #{accionHome.instance.nombre} creada exitosamente";
	}

	public String getUpdatedMessage() {
		return "Condicion #{condicionHome.instance.nombre} actualizada exitosamente";
	}

	public String getDeletedMessage() {
		return "Condicion #{condicionHome.instance.nombre} borrada exitosamente";
	}

	public void setMsgErrorLiteral(String msgErrorLiteral) {
		this.msgErrorLiteral = msgErrorLiteral;
	}

	public String getMsgErrorLiteral() {
		return msgErrorLiteral;
	}

	public boolean hayMsgErrorLiteral() {
		return !(msgErrorLiteral == null || msgErrorLiteral.equals(""));
	}

	public void cancel() {
		// this.getEntityManager().clear();
	}

	public boolean esVacio() {
		return Boolean.valueOf(this.instance.getVacio());
	}

	public boolean esTexto() {
		if (this.instance.getAtributo() == null) {
			return false;
		}
		return this.instance.getAtributo().getTipoDato().equals("TEXTO");
	}

	public boolean esNumero() {
		if (this.instance.getAtributo() == null) {
			return false;
		}
		return this.instance.getAtributo().getTipoDato().equals("NUMERO");
	}

	public boolean esBooleano() {
		if (this.instance.getAtributo() == null) {
			return false;
		}
		return this.instance.getAtributo().getTipoDato().equals("BOOLEANO");
	}

	public boolean esFecha() {
		if (this.instance.getAtributo() == null) {
			return false;
		}
		return this.instance.getAtributo().getTipoDato().equals("FECHA");
	}

	public boolean muestraLiteral() {
		return esTexto() || esNumero() || esBooleano() || esFecha();
	}

	public void setAuxMenorAUnValor(String auxMenorAUnValor) {
		this.auxMenorAUnValor = auxMenorAUnValor;
	}

	public String getAuxMenorAUnValor() {
		return auxMenorAUnValor;
	}

	public void setAuxMenorAUnaFecha(Date auxMenorAUnaFecha) {
		this.auxMenorAUnaFecha = auxMenorAUnaFecha;
	}

	public Date getAuxMenorAUnaFecha() {
		return auxMenorAUnaFecha;
	}

	public void setTipoCarga(String tipoCarga) {
		this.tipoCarga = tipoCarga;
	}

	public String getTipoCarga() {
		return tipoCarga;
	}

	public void setAtributosEntidad2(List<Atributo> atributosEntidad2) {
		AtributosEntidad2 = atributosEntidad2;
	}

	public Atributo getAtributo2() {
		return atributo2;
	}

	public void setAtributo2(Atributo atributo2) {
		this.atributo2 = atributo2;
	}

	public void reachServer() {
		log.info("Mensaje de prueba");
	}

	public Entidad getEntidadAtributo2() {
		return entidadAtributo2;
	}

	public void setEntidadAtributo2(Entidad entidadAtributo2) {
		this.entidadAtributo2 = entidadAtributo2;
	}

	public void buscarReglasDecision() {
		if (this.nombreReglaTablaDecision == null) {
			log.info("El nombre para la busqueda de reglas es NULL");
			this.nombreReglaTablaDecision = "";
		}

		log.info("Buscando las reglas cuyo nombre sean o contengan a "
				+ this.nombreReglaTablaDecision);

		Query query = entityManager
				.createQuery(
						""
								+ "SELECT"
								+ " tablaDecision FROM TablaDecision as tablaDecision"
								+ " WHERE upper(tablaDecision.nombre) like :patron and lastVersion = true")
				.setParameter("patron",
						"%" + this.nombreReglaTablaDecision.toUpperCase() + "%");

		this.listTablaDecision = query.getResultList();

		query = entityManager
				.createQuery(
						""
								+ "SELECT"
								+ " reglaPseudocodigo FROM ReglaPseudocodigo as reglaPseudocodigo"
								+ " WHERE upper(reglaPseudocodigo.nombre) like :patron and lastVersion = true")
				.setParameter("patron",
						"%" + this.nombreReglaTablaDecision.toUpperCase() + "%");

		this.listRPS = query.getResultList();

		if (listTablaDecision.size() == 0)
			log.info("LA LISTA NO REGLAS TD TIENEN NINGUN ELEMENTO");
		else
			log.info("LA LISTA NO REGLAS TD SI tiene elementos");

		if (listRPS.size() == 0)
			log.info("LA LISTA DE RPS NO TIENEN NINGUN ELEMENTO");
	}

	public void setReglaPS(ReglaPseudocodigo reglaPS) {

		if (reglaPS == null) {
			log.info("Regla por pseudocodigo es NULL (parametro que llega dps de seleccionar");
			return;
		}

		this.instance.setReglaPsC(reglaPS);
		this.instance.setRegla(null);

		this.listTablaDecision = null;
		this.listFunciones = null;
		this.listRPS = null;
	}

	public void setReglaTablaDecision(TablaDecision tablaDecision) {

		if (tablaDecision == null) {
			log.info("tablaDecision es NULL (parametro que llega dps de seleccionar");
			return;
		}

		this.instance.setRegla(tablaDecision);
		this.instance.setReglaPsC(null);

		this.listTablaDecision = null;
		this.listFunciones = null;
		this.listRPS = null;
	}

	public void initCondicionAtributo() {
		// atributo2.setCondicionAtributo(instance.getCondicionAtributo());
		// instance.getCondicionAtributo().getAtributo().setCondicionAtributo(instance.getCondicionAtributo());

		// instance.getCondicionAtributo().setOperadorLogico(operadorLogico);
		List<Literal> listaLiteral = new ArrayList<Literal>();
		// listaLiteral
		Literal verdadero = new Literal();
		verdadero.setDescripcion("Verdadero");
		verdadero.setNombre("Verdadero");
		verdadero.setOperadorLogico(operadorLogico);
		Literal falso = new Literal();
		falso.setDescripcion("Falso");
		falso.setNombre("Falso");
		falso.setOperadorLogico(operadorLogico);
		listaLiteral.add(verdadero);
		listaLiteral.add(falso);
		instance.setLiterales(listaLiteral);

		// if(!instance.getVacio().equals("atributo"))
		// instance.getCondicionAtributo().setAtributo2(null);
		// else
		// instance.getCondicionAtributo().setAtributo2(atributo2);
	}

	public void buscarFunciones() {

		if (this.nombreFuncion == null) {
			log.info("El nombre para la busqueda de funciones es NULL");
			return;
		}
		Query query = entityManager
				.createQuery(
						""
								+ "SELECT"
								+ " funcion FROM Funcion as funcion"
								+ " WHERE upper(funcion.nombre) like :patron and upper(funcion.tipoDato) = :tipo")
				.setParameter("patron",
						"%" + this.nombreFuncion.toUpperCase() + "%")
				.setParameter("tipo",
						this.instance.getAtributo().getTipoDato().toUpperCase());

		this.setListFunciones(query.getResultList());
		log.info("La lista de fuciones es de tamanio " + listFunciones.size());
	}

	public void setListFunciones(List<Funcion> listFunciones) {
		this.listFunciones = listFunciones;
	}

	public List<Funcion> getListFunciones() {
		return listFunciones;
	}

	public void setFuncion(Funcion funcion) {

		if (funcion == null) {
			log.info("La funcion es NULL");
			return;
		}
		this.instance.setFuncion(funcion);

		this.listTablaDecision = null;
		this.listFunciones = null;
		this.listRPS = null;
	}

	public String nombreAtributo(Atributo atributo) {
		return atributo.getEntidad().getNombre() + "." + atributo.getNombre();
	}

	public String agregarCondicionAtributo() {
		CondicionAtributo condicionAtributo = new CondicionAtributo();
		condicionAtributo.setAtributo2(atributo2);
		condicionAtributo.setOperadorLogico(operadorLogico);
		boolean band = false;
		for (Iterator iterator = instance.getCondicionAtributoList().iterator(); iterator
				.hasNext();) {
			CondicionAtributo condicionAtributo2 = (CondicionAtributo) iterator
					.next();

			if (condicionAtributo.getAtributo2().equals(
					condicionAtributo2.getAtributo2())  && condicionAtributo.getOperadorLogico().equals(condicionAtributo2.getOperadorLogico()))
				band = true;
		}
		if (band == false)
			instance.getCondicionAtributoList().add(condicionAtributo);
		else
			facesMessages.add("El valor ya se encuentra cargado.");
		return null;
	}

	public void fijarValorPosibleEdicion() {

		if (instance.getLiterales().isEmpty() == true)
			instance.setVacio("true");
		else
			instance.setVacio("false");

		if (instance.getCondicionAtributoList().isEmpty() != true)
			instance.setVacio("atributo");

		if (instance.getCondicionFormulaList().isEmpty() != true)
			instance.setVacio("formula");
		bandera = true;
	}

	public List<Entidad> getEntidades2() {
		return new EntidadList().getMyResultList();
	}

	public void setEntidades2(List<Entidad> entidades2) {
		this.entidades2 = entidades2;
	}

}
