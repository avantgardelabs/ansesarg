package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Formula;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Incognita;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
import ar.gob.anses.prissa.mi.asistente_reglas.formulas.CalculadoraExpresion;
import ar.gob.anses.prissa.mi.asistente_reglas.formulas.Funcion;
import ar.gob.anses.prissa.mi.asistente_reglas.services.PersistenceService;

@Name("formulasAction")
@Scope(ScopeType.CONVERSATION)
public class FormulasAction {

	@Logger
	Log log;
	
	private String nombre;
	private String dominio;
	
	//Bandera que indica si la formula fue validada y sea correcta
	private boolean valida;
		
	// 0 - Alta
	// 1 - Editar
	// 2 -  Ver
	private byte selector;
	
	private List<Funcion> funciones;
	
	private Funcion funcion;
	
	private Formula formula2;
	
	private Formula formula;
	
	@In(create = true)
	private EntityManager entityManager;

	
	public String getNombre() {
		return nombre;
	}
	
	private List<Entidad> entidades;

	private Entidad entidad;
	
	@In
	PersistenceService persistenceService;
	
	private List<Atributo> atributos;
	
	private Atributo atributo;
	
	private List<Formula> formulas;
	
	public String ver(Formula formula){
		selector = 2;
		this.setFormula(formula);
		return "/agregarFormula.xhtml";
	}
	
	public String editar(Formula formula){
		selector = 1;
		this.setFormula(formula);
		return "/agregarFormula.xhtml";
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDominio() {
		return dominio;
	}

	public void setDominio(String dominio) {
		this.dominio = dominio;
	}
	
	public Formula getFormula() {
		return formula;
	}

	public void setFormula(Formula formula) {
		this.formula = formula;
	}
	
	public Formula getFormula2() {
		return formula2;
	}

	public void setFormula2(Formula formula2) {
		this.formula2 = formula2;
	}

	public void buscar(){
		Query query = entityManager.createQuery("SELECT f FROM Formula f");
		this.formulas = query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Atributo> getAtributos() {
		
		if(this.getEntidad() == null){
			log.info("entidad es nulo");
			return null;
	}
		
		log.debug("Buscando los Atributos de entidad " + this.getEntidad().getNombre());
		
		if (this.getEntidad().getNombre() == null) return null;
		
		Query query = entityManager.createQuery("" +
				"SELECT" +
				" atributo FROM Atributo as atributo" +
				" WHERE (atributo.entidad=:ent AND atributo.tipoDato = 'NUMERO')")
				.setParameter("ent", this.getEntidad());
		
		this.atributos = query.getResultList();
		return this.atributos;
	}
	
	public List<Entidad> getEntidades() {
		return new EntidadList().getMyResultList();
	}
	
	public Entidad getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	public List<Formula> getFormulas() {
		return formulas;
	}

	public void setFormulas(List<Formula> formulas) {
		this.formulas = formulas;
	}

	public Atributo getAtributo() {
		return atributo;
	}

	public void setAtributo(Atributo atributo) {
		this.atributo = atributo;
	}

	public Funcion getFuncion() {
		return funcion;
	}

	public void setFuncion(Funcion funcion) {
		this.funcion = funcion;
	}

	public byte getSelector() {
		return selector;
	}

	public void setSelector(byte selector) {
		this.selector = selector;
	}

	public boolean isValida() {
		return valida;
	}

	public void setValida(boolean valida) {
		this.valida = valida;
	}

	public List<Funcion> getFunciones() {
		return funciones;
	}

	public void setFunciones(List<Funcion> funciones) {
		this.funciones = funciones;
	}

	public void init(){
//		this.formula.setIncognitas(new ArrayList<Incognita>());
		inicializaListaFunciones();
		funcion = new Funcion();
		funcion.setCuerpo(new String());
		funcion.setNombre(new String());
	}
	
	public String limpiar(){
		limpiarDatos();
		return "/formulas.xhtml";
	}
	
	public void buscarFormula(String criterio, String criterio2){
		if(((criterio == "")||(criterio==null))&&((criterio2 == "")||(criterio2==null)))
			buscar();
		else{
			if(((criterio!= "")||(criterio!=null))&&((criterio2 == "")||(criterio2==null))){
		Query query = entityManager.createQuery("SELECT f FROM Formula f where f.nombre like concat('%',concat(:criterio,'%'))").setParameter("criterio", criterio);
		this.formulas = query.getResultList();
			}
			else{
				if(((criterio== "")||(criterio==null))&&((criterio2 != "")||(criterio2!=null))){
					Query query = entityManager.createQuery("SELECT f FROM Formula f where f.dominio.descripcion like concat('%',concat(:criterio,'%'))").setParameter("criterio", criterio2);
					this.formulas = query.getResultList();
						}
					
					else{
						if(((criterio!= "")||(criterio!=null))&&((criterio2 != "")||(criterio2!=null))){
							Query query = entityManager.createQuery("SELECT f FROM Formula f where (f.dominio.descripcion like concat('%',concat(:criterio2,'%'))) and (f.nombre like concat('%',concat(:criterio,'%')))").setParameter("criterio", criterio).setParameter("criterio2", criterio2);
							this.formulas = query.getResultList();
								}
						}
					}
				}
	}
	
	public String crearFormula(){
		formula = new Formula();
		formula.setCuerpo(new String());
		selector = 0;
		return "/agregarFormula.seam";
	}
	
	
	public String salir(){
		return "/home.seam";
	}
	
	@End(beforeRedirect = true)
	public String guardar(){
		if(validarNombre()== true){
			if(validarNombreDuplicado(formula.getNombre())==true){
				if(valida == true){
					this.persistenceService.save(formula);
					this.limpiarDatos();
					return "/formulas.seam";
				}
				else{
					FacesMessages.instance().add(
		     				new FacesMessage("La f\u00F3rmula no ha sido validada"));
					return null;
				}
			}
			else{
				FacesMessages.instance().add(
	     				new FacesMessage("El nombre de la f\u00F3rmula ya se encuentra cargado"));
				return null;
			}
		}
		else{
			FacesMessages.instance().add(
     				new FacesMessage("El nombre de la f\u00F3rmula debe tener al menos 3 caracteres"));
			return null;
		}
	}
	
	@End(beforeRedirect = true)
	public String cancelar(){
		limpiarDatos();
		return "/formulas.seam";
	}
	
	public void agregarIncognita(){
		Incognita incognita = new Incognita();
		if((atributo!=null)&&(entidad!=null)){
		incognita.setAtributo(atributo);
		incognita.setEntidad(entidad);
		if(validarIncognitaDuplicada(incognita)==true){
		incognita.setCodigo((char)(obtenerCodigoMasalto()+1));
		formula.getIncognitas().add(incognita);
		}
		else{
			FacesMessages.instance().add(
     				new FacesMessage("Ya se encuentra cargada otra incógnita con la misma entidad y el mismo atributo."));
			}
		}
	}
	
	public char obtenerCodigoMasalto(){
		char max = 64;
		for (Iterator iterator = formula.getIncognitas().iterator(); iterator.hasNext(); ) {
		    Incognita incognita = (Incognita) iterator.next();
		    if(incognita.getCodigo()> max)
		    	max = incognita.getCodigo();
		}
		return max;
	}
	
	
//	public void agregarAFormula(Atributo atributo){
//		if(atributo!=null){
//			this.formula.setCuerpo(this.formula.getCuerpo() + atributo.getEntidad().getNombre() + "." + atributo.getNombre());
//			agregarIncognita();
//		}
//	}
	
	public void agregarAFormula(Incognita incognita){
		if(atributo!=null){
			this.formula.setCuerpo(this.formula.getCuerpo() + incognita.getCodigo());
		}
	}
		
	public void agregarFuncionAFormula(Funcion funcion2){
		if((funcion2.getCuerpo()!="")&&(funcion2.getNombre()!="")){
			this.formula.setCuerpo(this.formula.getCuerpo() + funcion2.getCuerpo());
		}
	}
	
	public void agregarFormulaAFormula(Formula formula2){
		if(formula2!=null){
			this.formula.setCuerpo(this.formula.getCuerpo() + formula2.getNombre());
		}
	}
	
	public void calculcar(){
		CalculadoraExpresion calculadoraExpresion = new CalculadoraExpresion();
		CalculadoraExpresion.setIncognitas(formula.getIncognitas());
		valida = calculadoraExpresion.calcularExpresion((formula.getCuerpo()));
	}
	
//	public String convertirVariable(String formula){
//		String formulaFinal;
//		formulaFinal = formula;
//		for (Iterator iterator = incognitas.iterator(); iterator.hasNext(); ) {
//		    Incognita inc = (Incognita) iterator.next();
//		    if(formulaFinal.contains(inc.getEntidad().getNombre() + "." + inc.getAtributo().getNombre()))
//		    	formulaFinal = formulaFinal.replace(inc.getEntidad().getNombre() + "." + inc.getAtributo().getNombre(), "1");
//		}
//		return formulaFinal;
//	}
	
	public void inicializaListaFunciones(){
		this.funciones = new ArrayList<Funcion>();

		/*Funcion redondear = new Funcion();
		redondear.setNombre("Redondear");
		redondear.setCuerpo("REDONDEAR(valor)");
		funciones.add(redondear);*/

		/*Funcion raiz = new Funcion();
		raiz.setNombre("Raiz Cuadrada");
		raiz.setCuerpo("RAIZ(valor)");
		funciones.add(raiz);*/
		
		Funcion contar = new Funcion();
		contar.setNombre("Contar");
		contar.setCuerpo("CONTAR(valor)");
		funciones.add(contar);

		Funcion maximo = new Funcion();
		maximo.setNombre("Maximo");
		maximo.setCuerpo("MAX(valor)");
		funciones.add(maximo);

		Funcion minimo = new Funcion();
		minimo.setNombre("Minimo");
		minimo.setCuerpo("MIN(valor)");
		funciones.add(minimo);
		
		Funcion promedio = new Funcion();
		promedio.setNombre("Promedio");
		promedio.setCuerpo("PROMEDIO(valor)");
		funciones.add(promedio);

		Funcion sumatoria = new Funcion();
		sumatoria.setNombre("Sumatoria");
		sumatoria.setCuerpo("SUMATORIA(valor)");
		funciones.add(sumatoria);
	   
	}

	public boolean validarNombre(){
		if(formula.getNombre().length()>=3)
			return true;
		return false;
	}

	public boolean validarNombreDuplicado(String nombre){
			
			//Si es Edicion no Realiza la Validación
			if(selector==1)
				return true;
			List<Formula> formulas;
			Query query = entityManager.createQuery("SELECT f FROM Formula f where f.nombre = :nombre").setParameter("nombre", nombre);
			formulas = query.getResultList();
			if((formulas!=null)&&(formulas.size()>0))
				return false;
		return true;
	}
	
	public boolean validarIncognitaDuplicada(Incognita incognita){
		for (Iterator iterator = formula.getIncognitas().iterator(); iterator.hasNext(); ) {
		    Incognita incognita2 = (Incognita) iterator.next();
		    if((incognita2.getAtributo().equals(incognita.getAtributo()))&&(incognita2.getEntidad().equals(incognita.getEntidad())))
		    	return false;		    
		}
		return true;
	}
	public void limpiarDatos(){
		nombre = "";
		dominio="";
		if(formulas!=null)
		this.formulas = null;
		entidad=null;
		atributo=null;
		valida = false;
		formula2 = null;
		funcion = null;
	}
}
