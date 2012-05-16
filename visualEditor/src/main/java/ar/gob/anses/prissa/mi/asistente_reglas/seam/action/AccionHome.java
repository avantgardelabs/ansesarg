package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Accion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.AccionModificaHechoFormula;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Formula;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Funcion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Parametro;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.ReglaPseudocodigo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.ValorParametro;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.custom.BaseHome;
@Name("accionHome")
@Scope(ScopeType.CONVERSATION)
public class AccionHome extends BaseHome<Accion>{

	private static final long serialVersionUID = 999426568179067865L;

	private boolean modificado = false;

	@Logger
	Log log;

	@In(create = true) 
	EntityManager entityManager;

	@In(create = true,required=false) @Out(required=false)
	AccionList accionList;

	@In FacesMessages facesMessages;

	private String panelBuscarFun;

	@In(create = true,required=false) @Out(required=false)
	private Entidad entidad = new Entidad();

	@In(create = true,required=false) @Out(required=false)
	private Atributo atributo = new Atributo();

	private List<Atributo> atributos;

	private List<Formula> formulas;
	private Formula formula = new Formula();

	private List<TablaDecision> listTablaDecision;
	private List<ReglaPseudocodigo> listRPS;

	private Funcion funcionActual = new Funcion();

	private List<Funcion> listFunciones;

	private List<ValorParametro> listValoresDeLosParametros = new ArrayList<ValorParametro>();

	private boolean opcion;
	private String currentInput; 
	private Date currentFecha;

	@In( create=true, required=false) @Out(required=false)
	private String miOpcion = "LIT";

	@In(create = true, required=false) @Out(required = false)
	String valueOHR = new String("MH");

	@In(create=true, required=false ) @Out(required = false)
	String nombreReglaTablaDecision = new String();

	@In( create=true, required = false) @Out(required = false)
	String nombreFun = new String();

	private TablaDecision tablaDecision;


	@In( create=true, required = false) @Out(required = false)
	java.lang.String valorDeUnParametro = new String();

	private String valorDeUnParametroBool;
	private Parametro param;

	private ArrayList<Accion> resultadosEsperadosPreSeleccionados;

	private Boolean accionCreada = Boolean.FALSE;

	private Boolean asignarAcciones = Boolean.FALSE; 

	/*=========================FUNCIONES PROPIAS DEL CONTROLADOR=================================*/

	public Boolean getAsignarAcciones() {
		return asignarAcciones;
	}

	/**
	 * @return the param
	 */
	public Parametro getParam() {
		return param;
	}

	public void setAsignarAcciones(Boolean asignarAcciones) {
		this.asignarAcciones = asignarAcciones;
	}	

	public ArrayList<Accion> getResultadosEsperadosPreSeleccionados() {
		return resultadosEsperadosPreSeleccionados;
	}

	public void setResultadosEsperadosPreSeleccionados(
			ArrayList<Accion> resultadosEsperadosPreSeleccionados) {
		this.resultadosEsperadosPreSeleccionados = resultadosEsperadosPreSeleccionados;
	}

	public String getValorDeUnParametroBool() {
		return valorDeUnParametroBool;
	}

	public void setValorDeUnParametroBool(String valorDeUnParametroBool) {
		this.valorDeUnParametroBool = valorDeUnParametroBool;
	}

	@Override
	protected Accion createInstance() {
		Accion accion = new Accion();
		this.setAccionCreada(Boolean.FALSE);
		return accion;
	}

	public Accion getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public String getCreatedMessage() { return "Nueva accion #{accionHome.instance.nombre} creada exitosamente"; }
	public String getUpdatedMessage() { return "Accion #{accionHome.instance.nombre} actualizada exitosamente"; }
	public String getDeletedMessage() { return "Accion #{accionHome.instance.nombre} borrada exitosamente"; }

	public void continuar() {
		if(this.instance.getId()==null)
			buscarFormulas();
		log.info("continuar");
	}

	@Override
	public String persist(){
		log.info("PASE POR EL PERSIST");
		log.info(this.instance.getTipoAccion());
		return persist(false);
	}

	public String guardar(){
		log.info("PASE POR GUADAR");
		if(persist(false) != null){
			this.getResultadosEsperadosPreSeleccionados().add(getInstance());
			this.setAccionCreada(Boolean.TRUE);
			if(this.getAsignarAcciones().booleanValue() == true){
				return "/asignarAcciones.xhtml";
			}else{
				return "/agregarFila.xhtml";
			}
		}else{
			return null;
		}
	}

	public Boolean getAccionCreada() {
		return accionCreada;
	}

	public void setAccionCreada(Boolean accionCreada) {
		this.accionCreada = accionCreada;
	}

	public String persist(boolean update){
		boolean validacion = true;

		if(this.instance.getNombre() == null){
			log.info("Nombre de la accion es nulo");
			return null;
		}

		if (this.nombreAccionRepetido(update))
			return null;

		if (this.instance.getTipoAccion()==null) {
			log.info("tipo accion null");
			return null;
		}
		else
			log.info("tipo accion: " + this.instance.getTipoAccion());

		if (!this.instance.getTipoAccion().equals("SA") ) {

			if(this.instance.getAccionModificaHecho().getAccionModificaHechoLiteral() == null){
				log.info("accionModificaHechoLiteral es NULL");
				return null;
			}

			if (update){
				try {
					for (Iterator iterator = this.listValoresDeLosParametros.iterator(); iterator.hasNext();) {
						ValorParametro vp = (ValorParametro) iterator.next();
						entityManager.persist(vp);
						log.info("Entro al iterador");
					}
				}catch (NullPointerException e) {
					log.info("La lista de Valores de los Parametros es nula");
				}
			}

			//Modifica Hecho
			if (this.instance.getTipoAccion().equals("MH")) {

				log.info("MH");
				this.instance.setAccionActivaRegla(null);

				if (this.instance.getAccionModificaHecho().getEntidad() == null) {
					log.info("Debe seleccionar una entidad");
					facesMessages.add("Debe seleccionar una entidad");
					validacion = false;
				}

				if (this.instance.getAccionModificaHecho().getAtributo() == null) {
					facesMessages.add("Debe seleccionar un atributo");
					validacion = false;
				}else {
					if (this.instance.getAccionModificaHecho().getAtributo().getTipoCarga().equals("LOGICO"))
						this.instance.getAccionModificaHecho().setPersistible(false);
					log.info(this.instance.getAccionModificaHecho().getTipoModificacion());

				}


				if (this.instance.getAccionModificaHecho().getTipoModificacion().equals("FUN")) {

					this.currentFecha=null;
					this.currentInput=null;
					this.instance.getAccionModificaHecho().setAccionModificaHechoLiteral(null);
					this.instance.getAccionModificaHecho().setAccionModificaHechoFormula(null);
					this.instance.getAccionModificaHecho().getAccionModificaHechoFuncion().setValoresParametros(this.listValoresDeLosParametros);
					this.instance.getAccionModificaHecho().getAccionModificaHechoFuncion().setFuncion(funcionActual);

					/*
					if (this.instance.getAccionModificaHecho().getAccionModificaHechoFuncion().getFuncion() == null) {
						facesMessages.add("Debe seleccionar una funcion que modifique al hecho");
						validacion = false;
					}
					 */
				}
				else if (this.instance.getAccionModificaHecho().getTipoModificacion().equals("LIT")) {

					this.instance.getAccionModificaHecho().setAccionModificaHechoFormula(null);

					this.nombreFun=new String();
					this.funcionActual=null;

					if (this.listFunciones != null) this.listFunciones.clear();

					this.instance.getAccionModificaHecho().setAccionModificaHechoFuncion(null);
					if ( this.obtenerCamposCompletados() == null){
						return null;
					}

					if(this.instance.getAccionModificaHecho().getAccionModificaHechoLiteral().getLiteral() == null){
						log.info("literal es NULL");
						facesMessages.add("Debe ingresar un literal");
						return null;
					}
					if (this.instance.getAccionModificaHecho().getAccionModificaHechoLiteral().getLiteral().equals("")) {
						facesMessages.add("Debe ingresar un literal");
						validacion = false;
					}
				}
				else{
					if(this.instance.getAccionModificaHecho().getTipoModificacion().equals("FORMULA"))

						if(this.getInstance().getAccionModificaHecho().getAccionModificaHechoFormula()==null){
							this.getInstance().getAccionModificaHecho().setAccionModificaHechoFormula(new AccionModificaHechoFormula());
							this.getInstance().getAccionModificaHecho().getAccionModificaHechoFormula().setFormula(new Formula());
						}

					this.nombreFun=new String();
					this.funcionActual=null;

					if (this.listFunciones != null) this.listFunciones.clear();

					this.instance.getAccionModificaHecho().setAccionModificaHechoFuncion(null);
					this.instance.getAccionModificaHecho().setAccionModificaHechoLiteral(null);
					this.instance.getAccionModificaHecho().getAccionModificaHechoFormula().setFormula(this.getFormula());

					//						if(this.instance.getAccionModificaHecho().getAccionModificaHechoFormula().getFormula()==null){
					if(this.formula==null){
						log.info("Formula es NULL");
						facesMessages.add("Debe ingresar una f\u00F3rmula");
						validacion = false;
					}	
				}
			}
			//End Modifica Hecho
			else if (this.instance.getTipoAccion().equals("AR")) {
				this.instance.setAccionModificaHecho(null);


				if (this.instance.getAccionActivaRegla().getReglaPorPseudocodigo() == null && this.instance.getAccionActivaRegla().getTablaDecision() == null) {
					this.instance.getAccionActivaRegla().setReglaPorPseudocodigo(null);
					this.instance.getAccionActivaRegla().setTablaDecision(null);
				}


				if(this.instance.getAccionActivaRegla().getReglaPorPseudocodigo() != null){
					if(this.instance.getAccionActivaRegla().getReglaPorPseudocodigo().getNombre() == null)
						this.instance.getAccionActivaRegla().setReglaPorPseudocodigo(null);
					else
						this.instance.getAccionActivaRegla().setTablaDecision(null);
				}

				if(this.instance.getAccionActivaRegla().getTablaDecision() != null){
					if(this.instance.getAccionActivaRegla().getTablaDecision().getNombre() == null)
						this.instance.getAccionActivaRegla().setTablaDecision(null);
					else
						this.instance.getAccionActivaRegla().setReglaPorPseudocodigo(null);
				}


			}
		}
		else {
			//Sin accion
			this.instance.setAccionActivaRegla(null);
			this.instance.setAccionModificaHecho(null);
			validacion = true;
		}

		if (validacion) {
			if (update) {
				log.info("update: persisto");
				super.update();
				return "/accion.xhtml";
			}
			else {
				log.info("no update: persisto");
				super.persist();
				return "/accion.xhtml";
				//this.getPersistenceContext().save(this.instance);
				//return "success";
				//return super.persist();
			}
		}
		else {
			return null;
		}
	}

	private boolean nombreAccionRepetido(boolean update) {
		List<Accion> acciones = accionList.getMyResultList();
		log.info("la longitud de accionlist es " + accionList.getMaxResults());
		for (Accion a: acciones){
			if(a.getNombre().toUpperCase().trim().equals(this.instance.getNombre().toUpperCase().trim())){
				if (update) {
					if(!a.getId().equals(this.instance.getId())) {
						facesMessages.add("El nombre de la accion ya esta siendo utilizado. Modifiquelo para poder guardar. Editando");
						return true;
					}
				}
				else {
					facesMessages.add("El nombre de la accion ya esta siendo utilizado. Modifiquelo para poder guardar.");
					return true;
				}
			}	
		}
		return false;
	}

	private String obtenerCamposCompletados() {

		String currentLiteral = "";
		if (this.currentInput!= null && this.currentInput!= "") {

			if (esTexto()) currentLiteral = this.currentInput;

			if (esNumero()){
				try {
					Double.parseDouble(this.currentInput);
				} catch (NumberFormatException e) {
					facesMessages.add("El valor literal ingresado no es valido. Debe ingresar solamente un numero.");
					this.currentInput="";
					this.currentFecha = null;
					return null;
				}
				currentLiteral = this.currentInput;
			}

			if (esBooleano()) currentLiteral = this.currentInput;
			this.instance.getAccionModificaHecho().getAccionModificaHechoLiteral().setLiteral(currentLiteral);
		}
		Date fechaDeHoy = new Date();
		if ( this.currentFecha != null ) {
			log.info("currentFecha no es null");

			if (esFecha()) currentLiteral = DateFormat.getDateInstance(DateFormat.MEDIUM).format(this.currentFecha);
			this.instance.getAccionModificaHecho().getAccionModificaHechoLiteral().setLiteral(currentLiteral);
		}
		return currentLiteral;
	}

	@Override
	public String update() {
		return persist(true);
	}



	@Override
	public String remove() {

		Query query = getPersistenceContext().createQuery("" +
				"SELECT" +
				" tablaDecision FROM TablaDecision as tablaDecision join tablaDecision.filas as filas join filas.acciones as accion" +
		" WHERE accion = :acc")
		.setParameter("acc", this.instance);

		List<TablaDecision> reglasUsanAcciones = (List<TablaDecision>)query.getResultList();

		if(reglasUsanAcciones.size() != 0){
			log.info("No se puede eliminar la condicion debido a que esta siendo usada por una regla por asistente");
			facesMessages.add("No se puede eliminar la condicion debido a que esta siendo usada por una regla por asistente");
			return null;
		}


		query = getPersistenceContext().createQuery("" +
				"SELECT" +
				" reglaPseudocodigo FROM ReglaPseudocodigo as reglaPseudocodigo join reglaPseudocodigo.acciones as accion" +
		" WHERE accion = :acc")
		.setParameter("acc", this.instance);

		List<ReglaPseudocodigo> reglasPseudoUsanAcciones = (List<ReglaPseudocodigo>)query.getResultList();

		if(reglasPseudoUsanAcciones.size() != 0){
			log.info("No se puede eliminar la condicion debido a que esta siendo usada por una regla por pseudocodigo");
			facesMessages.add("No se puede eliminar la condicion debido a que esta siendo usada por una regla por pseudocodigo");
			return null;
		}


		entityManager.clear();
		return super.remove();


	}

	public void cancel() {
		log.info("Cancelar");
	}

	public void view(){
		log.info(this.entidad.getNombre());
	}

	public List<Atributo> getAtributos(){
		if(this.instance.getAccionModificaHecho() == null){
			log.info("La entidad AccionModificaHecho es nula");
			return null;
		}

		if(this.instance.getAccionModificaHecho().getEntidad() == null){
			log.info("La entidad dentro de AccionModificaHecho es nula");
			return null;
		}

		log.warn("Buscando los Atributos de entidad " + this.instance.getAccionModificaHecho().getEntidad());


		if (this.instance.getAccionModificaHecho().getEntidad().getNombre() == null){
			log.info("El nombre de la entidad dentro de AccionModificaHecho es nula");
			return null;
		}


		Query query = entityManager.createQuery("" +
				"SELECT" +
				" atributo FROM Atributo as atributo" +
		" WHERE atributo.entidad=:ent")
		.setParameter("ent", this.instance.getAccionModificaHecho().getEntidad());

		this.atributos = query.getResultList();
		return this.atributos;
	}

	public void setValorOpcionAM(){
		log.info("Seteado el estado de las opciones de ACTIVA REGLA O MODIFICA HECHO(radio button) "+this.instance.getTipoAccion());
	}

	public String getValueOHR() {
		return this.valueOHR==null?"MH":this.valueOHR;
	}

	public void setValueOHR(String valueOHR) {
		this.valueOHR = valueOHR;
		log.info("Seteado el valor de valueOHR a "+this.valueOHR);
	}



	public void setValorOpcionLF(){
		log.info("Seteado el estado de las opciones de LITERAL O FUNCION "+ this.instance.getAccionModificaHecho().getTipoModificacion());
	}

	public String getMiOpcion() {
		return this.miOpcion==null?"LIT":this.miOpcion;
	}

	public void setMiOpcion(String miOpcion) {
		this.miOpcion = miOpcion;
		log.info("seteamos opcion a "+this.miOpcion);
	}

	public void actualizaNombre(){
		log.info("Actualizando nombre");
		//log.info("Actulizado el nombre de la regla: "+this.nombreReglaTablaDecision);
	}

	public void actualizaNombreFuncion(){
		log.info("Actulizado el nombre de la funcion utilizado en la busqueda: "+this.nombreFun);
	}

	public void actualizaNombreValor(){
		log.info("Actulizado el valor del parametro para la funcion: "+this.valorDeUnParametro);
	}

	public void buscarReglasDecision(){
		if(this.nombreReglaTablaDecision == null){
			log.info("El nombre para la busqueda de reglas es NULL");
			return;
		}

		log.info("Buscando las reglas cuyo nombre sean o contengan a " + this.nombreReglaTablaDecision);

		Query query = entityManager.createQuery("" +
				"SELECT" +
				" tablaDecision FROM TablaDecision as tablaDecision" +
		" WHERE upper(tablaDecision.nombre) like :patron and lastVersion = true")
		.setParameter("patron", "%"+this.nombreReglaTablaDecision.toUpperCase()+"%");

		this.listTablaDecision = query.getResultList();


		query = entityManager.createQuery("" +
				"SELECT" +
				" reglaPseudocodigo FROM ReglaPseudocodigo as reglaPseudocodigo" +
		" WHERE upper(reglaPseudocodigo.nombre) like :patron and lastVersion = true")
		.setParameter("patron", "%"+this.nombreReglaTablaDecision.toUpperCase()+"%");


		this.listRPS = query.getResultList();

		if(listTablaDecision.size() == 0)
			log.info("LA LISTA NO REGLAS TD TIENEN NINGUN ELEMENTO");

		if(listRPS.size() == 0)
			log.info("LA LISTA DE RPS NO TIENEN NINGUN ELEMENTO");

	}

	public void buscarFunciones(){
		if(this.nombreFun == null){
			log.info("El nombre para la busqueda de funciones es NULL");
			return;
		}

		if(this.instance.getAccionModificaHecho() == null || 
				this.instance.getAccionModificaHecho().getAtributo() == null || 
				this.instance.getAccionModificaHecho().getAtributo().getNombre() == null){
			//facesMessages.add("Se debe seleccionar Entidad y Atributo antes de realizar la busqueda.");
			return;
		}

		log.info("Buscando las fuciones cuyo nombre sean o contengan a "+this.nombreFun);

		Query query = entityManager.createQuery("" +
				"SELECT" +
				" funcion FROM Funcion as funcion" +
		" WHERE upper(funcion.nombre) like :patron and upper(funcion.tipoDato) = :tipo")
		.setParameter("patron", "%"+this.nombreFun.toUpperCase()+"%")
		.setParameter("tipo",this.instance.getAccionModificaHecho().getAtributo().getTipoDato().toUpperCase());

		this.listFunciones = query.getResultList();
	}

	public void setReglaTablaDecision(TablaDecision tablaDecision){

		if(tablaDecision == null){
			log.info("tablaDecision es NULL (parametro que llega dps de seleccionar");
			return;
		}

		if(this.instance.getAccionActivaRegla() == null){
			log.info("AccionActivaRegla es NULL");
			return;
		}

		this.instance.getAccionActivaRegla().setTablaDecision(tablaDecision);
		this.instance.getAccionActivaRegla().setReglaPorPseudocodigo(null);
		this.tablaDecision = tablaDecision;

		log.info("seteamos la regla de tabla de decision elegida en el pop-pup: "+this.tablaDecision==null?"Tabla decision es NULL":this.tablaDecision.getNombre());
	}

	public void setReglaPS(ReglaPseudocodigo reglaPS) {

		if(reglaPS == null){
			log.info("Regla por pseudocodigo es NULL (parametro que llega dps de seleccionar");
			return;
		}

		if(this.instance.getAccionActivaRegla() == null) {
			log.info("AccionActivaRegla es NULL");
			return;
		}

		this.instance.getAccionActivaRegla().setReglaPorPseudocodigo(reglaPS);
		this.instance.getAccionActivaRegla().setTablaDecision(null);
	}

	private void proof(){

		Query query = entityManager.createQuery("" +
				"SELECT" +
		" valorParametro FROM ValorParametro as valorParametro");

		for (Iterator iterator = query.getResultList().iterator(); iterator.hasNext();) {
			ValorParametro vp = (ValorParametro) iterator.next();
			log.info(vp.getValor()+" - param: "+vp.getParametro().getNombre()+"  -  "+vp.getParametro().getDescripcion());
		}
	}

	public void setFuncion(Funcion funcion){
		//proof();
		if(funcion == null){
			log.info("La funcion es NULL");
			return;
		}

		if (this.instance.getAccionModificaHecho().getAccionModificaHechoFuncion() == null){
			log.info("AccionModificaHechoFuncion es NULL");
			return;
		}

		funcionActual = funcion;

		/*
		 * Chequeo al setear la funcion para la accion si es que este ya tenia una.
		 * Y ademas que si la tenia y era igual a la que seteo entonces solo borros los parametros
		 * correspondientes de la base. Si no es igual ademas de los parametros se tengo q sacar
		 * la referencia a la funcion anterior.
		 */
		if (this.instance.getAccionModificaHecho().getAccionModificaHechoFuncion().getFuncion() != null &&
				this.instance.getAccionModificaHecho().getAccionModificaHechoFuncion().getFuncion().equals(funcion)){
			log.info("La funcion elegida es la misma que la anterior");
			return;
		}else if(this.instance.getAccionModificaHecho().getAccionModificaHechoFuncion().getFuncion() != null  &&
				!this.instance.getAccionModificaHecho().getAccionModificaHechoFuncion().getFuncion().equals(funcion)){
			for (Iterator iterator = this.instance.getAccionModificaHecho().getAccionModificaHechoFuncion().getValoresParametros().iterator(); iterator.hasNext();) {
				ValorParametro vp = (ValorParametro) iterator.next();
				entityManager.remove(vp);
			}

			this.instance.getAccionModificaHecho().getAccionModificaHechoFuncion().setValoresParametros(null);
			this.listValoresDeLosParametros.clear();
		}

		this.instance.getAccionModificaHecho().getAccionModificaHechoFuncion().setFuncion(funcion);
		if(this.instance.getAccionModificaHecho().getAccionModificaHechoFuncion().getFuncion() == null){
			log.info("La funcion que nos llego para setearle a la accion es nula");
			return;
		}

		if(this.instance.getAccionModificaHecho().getAccionModificaHechoFuncion().getFuncion().getNombre() == null){
			log.info("La el nombre de la funcion que nos llego para setearle a la accion es nula");
			return;
		}


		//log.info("seteamos la funcion elegida en el pop-pup: "+this.instance.getAccionModificaHecho().getAccionModificaHechoFuncion().getFuncion().getNombre());
	}

	public void addValorParametro(Parametro parametro){

		this.param = parametro;
		ValorParametro valorParametro = new ValorParametro();

		if(this.param == null){
			log.info("Param es nulo");
			return;
		}

		if(this.valorDeUnParametro == null){
			log.info("El valor del parametro es null");
			return;
		}

		if(this.param.getAtributo().getTipoDato().equals("NUMERO")){
			try {
				Double.parseDouble(this.valorDeUnParametro);
			} catch (NumberFormatException e) {
				facesMessages.add("Ud. solamente puede ingresar como valor del parametro un numero");
				return;
			}

		}else if(this.param.getAtributo().getTipoDato().equals("BOOLEANO")){
			log.info("booleano " + this.valorDeUnParametroBool);
			if(!this.valorDeUnParametroBool.equals("VERDADERO") && !this.valorDeUnParametroBool.equals("FALSO")){
				facesMessages.add("Ud. solamente puede ingresar como valor del parametro VERDADERO o FALSO");
				return;
			}
			this.valorDeUnParametro = this.valorDeUnParametroBool;

		}else if(this.param.getAtributo().getTipoDato().equals("FECHA")){

			log.info("Parametro tipo fecha");
			String dateValue = new String(this.valorDeUnParametro);
			String[] vec = dateValue.split("/");
			Integer dia=null,mes=null,anio=null;
			boolean flag = false;

			if(vec.length != 3){
				facesMessages.add("Ud. solamente puede ingresar como valor del parametro una fecha con formato: dd/mm/aaaa");
				return;
			}

			if(vec[0].length() != 2 || vec[1].length() != 2 || vec[2].length() != 4){
				facesMessages.add("Ud. solamente puede ingresar como valor del parametro una fecha con formato: dd/mm/aaaa");
				return;
			}

			try {
				dia = Integer.parseInt(vec[0]);
				mes = Integer.parseInt(vec[1]);
				anio = Integer.parseInt(vec[2]);
			} catch (NumberFormatException e) {
				facesMessages.add("Ud. solamente puede ingresar como valor del parametro una fecha con formato: dd/mm/aaaa");
				return;
			}

			if(dia < 0 || dia > 31){
				facesMessages.add("El rango del dia debe ser entre 0 y 31");
				flag = true;
			}

			if(mes < 0 || mes > 12){
				facesMessages.add("El rango del mes debe ser entre 1 y 12");
				flag = true;
			}

			if(anio < 1900 || anio > 3000){
				facesMessages.add("El rango del año debe ser entre 1900 y 3000");
				flag = true;
			}

			String dateStr = anio.toString() + "/" + mes.toString() + "/" + dia.toString();
			Date testDate = null;
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

				testDate = sdf.parse(dateStr);

				log.info(sdf.format(testDate));
				log.info(testDate);
				log.info("" + testDate.getDate() + " " + testDate.getMonth() + " " + (testDate.getYear() + 1900));

				if (!(testDate.getDate() == dia && testDate.getMonth()+1 == mes && testDate.getYear()+1900 == anio)) {
					facesMessages.add("La fecha  no es valida");
					return;
				}

			}
			catch (ParseException pe) {
				facesMessages.add("La fecha " + dateStr + " no es valida");
				return;
			}


			if(flag){
				facesMessages.add("Ud. solamente puede ingresar como valor del parametro una fecha con formato: dd/mm/aaaa");
				return;
			}

		}

		log.info(this.valorDeUnParametro);
		valorParametro.setValor(this.valorDeUnParametro);
		valorParametro.setParametro(this.param);

		//vemos si el parametro existe en la lista
		for (ValorParametro vp : listValoresDeLosParametros) {
			if (vp.getParametro().equals(this.param)) {
				log.info("Encontramos UN PARAMETRO IGUAL");
				ValorParametro v = this.listValoresDeLosParametros.get(listValoresDeLosParametros.indexOf(vp));
				v.setValor(new String(this.valorDeUnParametro));
				return;
			}
		}

		if(this.listValoresDeLosParametros.add(valorParametro)){
			log.info("INSERTAMOS BIEN EN LA LISTA Y SU LONGITUD ES "+this.listValoresDeLosParametros.size());
			return;
		}

		this.valorDeUnParametro = "";

		log.info("INSERTAMOS MAL EN LA LISTA Y SU LONGITUD ES "+this.listValoresDeLosParametros.size());
	}

	public String getValorDelParametro(Parametro parametro){

		if(parametro == null){
			log.info("El parametro utlizado para la busqueda del valor correspondiente es NULL");
			return null;
		}

		log.info("Longitud de la lista: "+this.listValoresDeLosParametros.size());

		for (Iterator iterator = this.listValoresDeLosParametros.iterator(); iterator.hasNext();) {
			ValorParametro unValor = (ValorParametro) iterator.next();
			log.info(unValor.getValor()+" "+unValor.getId());
			if(unValor.getParametro().getId() == parametro.getId()){
				valorDeUnParametro= unValor.getValor();
				return valorDeUnParametro;
			}
		}

		log.info("No se encontro ningun valor asociado al parametro: "+parametro.getNombre());
		return null;
	}

	public String getNombreRegla(){

		if(this.instance.getAccionActivaRegla().getTablaDecision() != null && this.instance.getAccionActivaRegla().getTablaDecision().getNombre() != null){
			return this.instance.getAccionActivaRegla().getTablaDecision().getNombre();
		}
		else if(this.instance.getAccionActivaRegla().getReglaPorPseudocodigo() != null && this.instance.getAccionActivaRegla().getReglaPorPseudocodigo().getNombre() != null){
			return this.instance.getAccionActivaRegla().getReglaPorPseudocodigo().getNombre();
		}

		return "";
	}

	public void putParametro(Parametro parametro){
		if(parametro == null){
			log.info("EL PARAMETRO DE LA FUNCION NUNCA LLEGO!");
			return;
		}

		if("BOOLEANO".equals(parametro.getAtributo().getTipoDato())){
			this.setValorDeUnParametroBool(getValorDelParametro(parametro));			
		}else{
			this.setValorDeUnParametro(getValorDelParametro(parametro));
		}

		log.info("Seteado el parametro de la fucion: "+parametro.getNombre() + "con tipo de dato " + parametro.getAtributo().getTipoDato());
		this.param = parametro;
		return;
	}

	public void setHabilitarPersistible(){
		log.info("cambiamos el estado del check ahora es ");

	}

	private String ayuda = new String("");

	public String getAyuda(){
		if (this.param == null || this.param.getAtributo() == null) {
			log.info("param es NULL");
			return "";
		}

		if(this.param.getAtributo().getTipoDato().equals("TEXTO"))
			return "Ud. puede ingresar cualquier tipo de texto";
		else if(this.param.getAtributo().getTipoDato().equals("NUMERO"))
			return "Ud. puede ingresar solamente un numero real";
		else if(this.param.getAtributo().getTipoDato().equals("BOOLEANO"))
			return "Ud. solamente puede ingresar VERDADERO o FALSO";
		else
			return "Ud. solamente puede ingresar una fecha con el siguiente formato: dd/mm/aaaa";
	}

	/*=========================GETTERS Y SETTERS=================================================*/
	public boolean getValidado() {
		return this.instance.getDominio() != null && !this.instance.getNombre().equals("") && !this.instance.getDescripcion().equals("");
	}

	public void setAccionId(Long id) {
		setId(id);
	}

	public Long getAccionId(){
		return (Long) getId();
	}

	public void wire() {

		getInstance();

		if(isManaged()){
			buscarFormulas();

			if(instance.getAccionModificaHecho() != null)
			{
				if(instance.getAccionModificaHecho().getAccionModificaHechoFormula()!=null)
					this.setFormula(instance.getAccionModificaHecho().getAccionModificaHechoFormula().getFormula());

				if(this.instance.getAccionModificaHecho().getAccionModificaHechoFuncion().getValoresParametros() != null
						&& this.instance.getAccionModificaHecho().getAccionModificaHechoFuncion().getValoresParametros().size() != 0){
					this.listValoresDeLosParametros = this.instance.getAccionModificaHecho().getAccionModificaHechoFuncion().getValoresParametros();
				}

				try{
					this.funcionActual = this.instance.getAccionModificaHecho().getAccionModificaHechoFuncion().getFuncion();
				}
				catch (Exception e) {
				}
			}
		}else{
			if(this.instance.getAccionModificaHecho() != null && this.instance.getAccionModificaHecho().getTipoModificacion() != null){
				this.instance.getAccionModificaHecho().setTipoModificacion("LIT");
			}
			else
				log.info("Tipo de Modificacion para ACCION MODIFCA HECHO ES NULL");
		}
	}

	public boolean isWired() {
		return true;
	}


	public void setModificado(boolean modificado) {
		log.info("Se modifico y llego al controlador");
		this.modificado = modificado;
	}

	public void entidadModificada(){

		this.setModificado(true);
		try {
			this.instance.getAccionModificaHecho().setAtributo(null);
		}catch (NullPointerException e) {
			// TODO: handle exception
		}
	}

	public void atributoModificado(){
		this.currentInput="";
		this.currentFecha=null;
		this.funcionActual=null;
		this.nombreFun="";
		this.instance.getAccionModificaHecho().setPersistible(false);
		this.listValoresDeLosParametros.clear();

		if (this.instance.getAccionModificaHecho().getAtributo()!=null){
			log.info("Estoy siendo editado");
			try {
				this.instance.getAccionModificaHecho().getAccionModificaHechoLiteral().setLiteral(null);
				log.info("entro al try para limpiar literal...");
			}catch (NullPointerException e) {}
			try {
				if (!this.instance.getAccionModificaHecho().getAccionModificaHechoFuncion().getFuncion()
						.getTipoDato().equals(this.instance.getAccionModificaHecho().getAtributo().getTipoDato())) {
					this.instance.getAccionModificaHecho().getAccionModificaHechoFuncion().setFuncion(null);
				}
			} catch (NullPointerException e) {}
			try {
				listFunciones.clear();
				log.info("Limpio funciones");
			} catch (NullPointerException e) {}
		}
	}

	public boolean isModificado() {
		return modificado;
	}

	public Entidad getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	public Atributo getAtributo() {
		return atributo;
	}

	public void setAtributo(Atributo atributo) {
		this.atributo = atributo;
	}

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

	public String getNombreReglaTablaDecision() {
		return nombreReglaTablaDecision;
	}

	public void setNombreReglaTablaDecision(String nombreReglaTablaDecision) {
		this.nombreReglaTablaDecision = nombreReglaTablaDecision;
	}

	public String getNombreFun() {
		return nombreFun;
	}

	public void setNombreFun(String nombreFun) {
		this.nombreFun = nombreFun;
	}

	public List<Funcion> getListFunciones() {
		return listFunciones;
	}

	public void setListFunciones(List<Funcion> listFunciones) {
		this.listFunciones = listFunciones;
	}

	public List<ValorParametro> getListValoresDeLosParametros() {
		return listValoresDeLosParametros;
	}

	public void setListValoresDeLosParametros(
			List<ValorParametro> listValoresDeLosParametros) {
		this.listValoresDeLosParametros = listValoresDeLosParametros;
	}

	public String getValorDeUnParametro() {
		return valorDeUnParametro;
	}

	public void setValorDeUnParametro(String valorParametro) {
		this.valorDeUnParametro = valorParametro;
	}

	public boolean muestraLiteral() {
		return esTexto() || esNumero() || esBooleano() || esFecha();
	}

	public boolean esTexto() {
		
		try {
			return this.instance.getAccionModificaHecho().getAtributo().getTipoDato().equals("TEXTO");
		}
		catch (NullPointerException e) {
			return false;
		} 
	}

	public boolean esNumero() {
		
		try {
			return this.instance.getAccionModificaHecho().getAtributo().getTipoDato().equals("NUMERO");
		}
		catch (NullPointerException e) {
			return false;
		}
	}

	public boolean esBooleano() {
		
		if (this.instance.getAccionModificaHecho().getAtributo() == null) {
			return false;
		}
		return this.instance.getAccionModificaHecho().getAtributo().getTipoDato().equals("BOOLEANO");
	}

	public boolean esFecha() {

		if (this.instance.getAccionModificaHecho().getAtributo() == null) {
			return false;
		}
		return this.instance.getAccionModificaHecho().getAtributo().getTipoDato().equals("FECHA");
	}


	public Formula getFormula() {
		return formula;
	}

	public void setFormula(Formula formula) {
		this.formula = formula;
	}

	public void setCurrentInput(String currentInput) {
		this.currentInput = currentInput;
	}

	public String getCurrentInput() {
		return currentInput;
	}

	public void setCurrentFecha(Date currentFecha) {
		this.currentFecha = currentFecha;
	}

	public Date getCurrentFecha() {
		return currentFecha;
	}

	public String getTipoDeAccionActual() {

		String tipoAccion="";
		String abreviatura=this.instance.getTipoAccion();
		if (abreviatura.equals("SA"))
			tipoAccion = "Sin accion";
		if (abreviatura.equals("MH"))
			tipoAccion = "Modifica un hecho";
		if (abreviatura.equals("AR"))
			tipoAccion = "Activa regla";
		return tipoAccion;
	}

	public void setFuncionActual(Funcion funcionActual) {
		this.funcionActual = funcionActual;
	}

	public Funcion getFuncionActual() {
		return funcionActual;
	}

	public void setPanelBuscarFun(String panelBuscarFun) {
		this.panelBuscarFun = panelBuscarFun;
		log.info("STipo modificacion :" + this.instance.getAccionModificaHecho().getTipoModificacion());
	}

	public String getPanelBuscarFun() {
		return panelBuscarFun;
	}

	public List<Formula> getFormulas() {
		return formulas;
	}

	public void setFormulas(List<Formula> formulas) {
		this.formulas = formulas;
	}

	public void buscarFormulas(){
		Query query = entityManager.createQuery("SELECT f FROM Formula f where f.dominio = :dom").setParameter("dom", this.getInstance().getDominio());
		this.formulas = query.getResultList();
	}
}

