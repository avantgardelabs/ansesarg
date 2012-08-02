package ar.gob.anses.prissa.mi.asistente_reglas.wrapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Accion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Condicion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.CondicionAtributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Descisor;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.FilaTablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Formula;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Funcion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Incognita;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Parametro;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;

/*
 import ar.gob.anses.prissa.mi.asistente_reglas.formulas.CalculadoraExpresion;
 import ar.gob.anses.prissa.mi.asistente_reglas.seam.action.FormulasAction;
 */

@Name("wrapperRulesFactory")
@Scope(ScopeType.CONVERSATION)
public class WrapperRulesFactory implements IWrapperService {

	private Log log;

	@In(create = true)
	private EntityManager entityManager;

	private TablaDecision tablaDecision;

	private String reglasHijas = "";
	private Set<Funcion> vFunciones = new HashSet<Funcion>();
	private Set<TablaDecision> vReglasHijas = new HashSet<TablaDecision>();
	private String contraCondicion = "";

	/**
	 * Representa las formulas en condiciones, ya sean que vienen directamente
	 * de las condiciones o bien desde las acciones
	 */
	private String formulasCondiciones = "";

	String strCondiciones = "";

	/**
	 * Representa el valor de las condiciones wrapeardas de la fila de tabla de
	 * desicion (por fila) y utilizada luego por el loguer
	 */
	private String condicionesDSL = "";

	/**
	 * Son las entidades que estan dentro de condiciones que evaluan otras
	 * entidades y deberian ser declaradas
	 */
	Set<Entidad> entidadesADeclarar = new HashSet<Entidad>();

	/**
	 * Indica cuales son las Entidades a declarar fruto del parsing de las
	 * acciones
	 **/
	Set<Entidad> entidadesADeclararEnAccion = new HashSet<Entidad>();

	private Set<Entidad> vEntidades = new HashSet<Entidad>();

	/**
	 * Representa la coleccion de atributos que hay que declarar como variables
	 * 
	 */
	private Set<Atributo> atributosADeclararComoVariables = new HashSet<Atributo>();

	/**
	 * Representa las acciones en formato DSL luego del proceso de wrapping
	 */
	private String accionesDSL;

	/**
	 * Indica cuales son los operadores lexicos en formato RegExp
	 */
	public static String OPERADORES_LEXICOS_REGEXP = "(^ |^\\(|^\\)|^\\+|^\\-|^\\/|^\\*)*";

	/**
	 * Indica los valores posibles que pueden tomar las incognitas en formato
	 * RegExp
	 */
	public static String VALORES_POSIBLES_INCOGNITAS_REGEXP = "[a-zA-Z]";

	private Set<Atributo> atributosInvolucrados = new HashSet<Atributo>();

	/**
	 * Reprenta las condiciones en formato de metadata
	 */
	private Set<MetaDataCondicion> metaDataCondiciones = new HashSet<MetaDataCondicion>();
	
	
	
	
	
	
	public Set<Atributo> getAtributosInvolucrados() {
		return atributosInvolucrados;
	}

	public void setAtributosInvolucrados(Set<Atributo> atributosInvolucrados) {
		this.atributosInvolucrados = atributosInvolucrados;
	}

	public WrapperRulesFactory(TablaDecision tabla) {
		tablaDecision = tabla;
	}

	public String buildDRLFile(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * NO UTILIZAR.
	 */
	@Deprecated
	public WrapperRulesFactory() {

	}

	public String buildRule() {

		String resultado = "";

		for (FilaTablaDecision fila : tablaDecision.getFilas()) {
			resultado = resultado + buildRule(fila);
			condicionesDSL = "";
			accionesDSL = "";
			contraCondicion = "";
			strCondiciones="";
		}

		//log.debug("Cantidad de atributos involucrados: " + atributosInvolucrados.size());

		for (Atributo a : atributosInvolucrados) {
			log.debug("Atributo Involucrado: " + a.getEntidad().getNombre() + "." + a.getNombre());
		}

		
		
		return reglasHijas + resultado + writeFunction();

	}

	public String buildRule(FilaTablaDecision fila) {

		String regla = "";
		String updatesObjetos = "";

		entidadesADeclarar = new HashSet<Entidad>();
		vEntidades = new HashSet<Entidad>();

		String nombreRegla = "regla_" + tablaDecision.getNombre() + "_" + fila.getId();
		strCondiciones = "\t" + wrapCondiciones(fila);

		
		//log.debug("**************** Condiciones generadas antes de su tratamiento Fila:   " +fila.getId() +" ************************");
		//log.debug(strCondiciones);
		
		// strCondiciones = strCondiciones + "\t\t eval(!isExecuted(\"" +
		// tablaDecision.getNombre() + "_" + fila.getId() + "\",$control))\n";

		if (fila.getAcciones() != null) {

			//log.debug("Cantidad de Entidades en acciones: " + getEntidadesEnAcciones(fila.getAcciones()).size());
			for (Entidad ent : getEntidadesEnAcciones(fila.getAcciones())) {
				if (ent != null) {
					updatesObjetos = updatesObjetos + "\n\t\t update($" + ent.getNombre() + ");";
				}
			}
		}

		String strAcciones = "";

		boolean encontrado = false;
		String emptyFacts = "";

		for (Entidad ent1 : entidadesADeclarar) {
			for (Entidad ent2 : vEntidades) {
				if (ent1.equals(ent2)) {
					encontrado = true;
					break;
				}
			}

			if (!encontrado)
				emptyFacts = emptyFacts + "\t$" + ent1.getNombre() + ":" + ent1.getNombre() + "( )" + "\n";

			encontrado = false;
		}

		//log.debug("Valor de EmptyFacts: " + emptyFacts);

		strCondiciones = emptyFacts + strCondiciones;

		if (fila.getAcciones() != null) {
			strAcciones = wrapAcciones(fila.getAcciones());
			accionesToDSL(fila.getAcciones());
		}

		emptyFacts = "";

		for (Entidad ent1 : entidadesADeclararEnAccion) {
			for (Entidad ent2 : vEntidades) {
				if (ent1.equals(ent2)) {
					encontrado = true;
					break;
				}
			}

			if (!encontrado)
				emptyFacts = emptyFacts + "\t$" + ent1.getNombre() + ":" + ent1.getNombre() + "( )" + "\n";

			encontrado = false;
		}

		strCondiciones = emptyFacts + strCondiciones;

		
		analizarContracondiciones(fila.getAcciones());

		
		declararVariablesEnCondicion();
		

		String traza = "\t\treport.addMessage(\"0\").addDescription(\"traza\",\"" + "REGLA: " + tablaDecision.getNombre() + " -" + condicionesDSL
				+ accionesDSL + "\"); \n";

		traza += "\t\treport.addMessage(\"0\").addDescription(\"UDAI\",\"" + fila.getMensajeOperadorUdai() + "\");\n";
		traza += "\t\treport.addMessage(\"0\").addDescription(\"CIUDADANO\",\"" + fila.getMensajeUsuarioWEB() + "\");\n";
		traza += "\t\treport.addMessage(\"0\").addDescription(\"SISTEMAS\",\"" + fila.getObservacion() + "\");\n";

		String cadenaLog = "\n\t\t//log.debug(\" Regla ejecutada  " + tablaDecision.getNombre() + fila.getId() + "\");";

		regla = "\t\n rule '" + nombreRegla + "'\n\twhen\n\t\t \n" + strCondiciones + formulasCondiciones + contraCondicion + " \t then\n"
				+ strAcciones;

		// regla = regla + "\n\t\tagregarRegla(\"" + tablaDecision.getNombre() +
		// "_" + fila.getId() + "\",$control);";
		regla = regla + updatesObjetos;
		regla += "\n" + traza + "\n";
		// regla = regla + "\n\t\t update($control);";

		regla = regla + cadenaLog + "\n\t\t" + elevarException(fila.isElevaExcepcion()) + "\n\tend\n";

		return regla;
	}

	public String buildRuleWithDeclaredFacts() {
		// TODO Auto-generated method stub
		return null;
	}

	public String buildWithDeclaredFacts(FilaTablaDecision regla) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Accion> getAcciones() {
		List<Accion> acciones = new ArrayList<Accion>();

		for (FilaTablaDecision fila : tablaDecision.getFilas()) {
			for (Accion accion : fila.getAcciones()) {
				if (!acciones.contains(accion)) {
					acciones.add(accion);
				}
			}
		}

		return acciones;
	}

	public Set<Entidad> getEntidadesEnAcciones(List<Accion> acciones) {
		if (acciones == null)
			return null;

		// List<Entidad> entidades = new ArrayList<Entidad>();

		Set<Entidad> entidades = new HashSet<Entidad>();

		for (Accion accion : acciones) {
			if (accion.getAccionModificaHecho() != null) {
				if (accion.getAccionModificaHecho().getAtributo() != null) {
					if (!entidades.contains(accion.getAccionModificaHecho().getAtributo().getEntidad())) {
						entidades.add(accion.getAccionModificaHecho().getAtributo().getEntidad());
					}
				}

			}
		}
		return entidades;

	}

	public List<Condicion> getCondiciones() {

		List<Condicion> condiciones = new ArrayList<Condicion>();

		for (FilaTablaDecision fila : tablaDecision.getFilas()) {
			for (Descisor d : fila.getValores()) {
				if (!condiciones.contains(d.getCondicion())) {
					condiciones.add(d.getCondicion());
				}
			}
		}

		return condiciones;
	}

	/**
	 * Devuelve una lista total de entidades en la tabla de desicion
	 */
	public Set<Entidad> getEntidades() {
		Set<Entidad> entidades = vEntidades;

		for (Atributo a : atributosInvolucrados) {
			if (!entidades.contains(a.getEntidad())) {
				entidades.add(a.getEntidad());
			}
		}

		return entidades;
	}

	public List<Entidad> getEntidades(List<Condicion> condiciones) {

		List<Entidad> entidades = new ArrayList<Entidad>();

		for (Condicion condicion : condiciones) {
			if (!entidades.contains(condicion.getEntidad())) {
				entidades.add(condicion.getEntidad());
				if (condicion.getCondicionAtributoList() != null) {
					for (CondicionAtributo condAtributo : condicion.getCondicionAtributoList()) {
						if (!entidades.contains(condAtributo.getAtributo2().getEntidad())) {
							entidades.add(condAtributo.getAtributo2().getEntidad());
						}
					}
				}
			}
		}

		return entidades;
	}

	private String getFunctionHeader(Descisor d) {
		String pseudocodigo = "";
		Funcion f = d.getCondicion().getFuncion();
		pseudocodigo = f.getNombre() + "(";
		String accessor = "";
		String sEntidad = "";

		boolean pongocoma = false;

		for (Parametro p : f.getParametros()) {
			if (pongocoma) {
				pseudocodigo += ",";
			}
			pongocoma = true;

			if (p.getAtributo().getTipoDato().equals("BOOLEANO")) {
				accessor = "is";
			} else {
				accessor = "get";
			}

			String _metodo = p.getAtributo().getNombre().substring(0, 1);
			_metodo = _metodo.toUpperCase() + p.getAtributo().getNombre().substring(1);

			pseudocodigo += "$" + p.getEntidad().getNombre() + "." + accessor + _metodo + "( )";

			/*
			 * Agrego la entidad a la lista de entidades utilizadas si es que no
			 * se encuentra en dicho vector y adem‚àö¬∞s agrego la inicializaci‚àö‚â•n
			 * de la variable en DRL
			 */

			sEntidad = "$" + p.getEntidad().getNombre() + ": " + p.getEntidad().getNombre() + "( )\n\t\t";
		}

		pseudocodigo += ")";

		return sEntidad + "eval(" + pseudocodigo + d.getValor().getOperadorLogico() + d.getValor().getNombre() + ")";
	}

	public List<Funcion> getFunciones() {
		List<Accion> acciones = getAcciones();

		return null;
	}

	private String wrapCondiciones(FilaTablaDecision fila) {

		ArrayList<ArrayList> descisores = new ArrayList<ArrayList>();
		ArrayList<Descisor> grupo = new ArrayList<Descisor>();
		metaDataCondiciones = new HashSet<MetaDataCondicion>();
		ArrayList tkGrupo;
		String condiciones = "";

		boolean encontrado = false;

		for (Descisor d : fila.getValores()) {
			// Tratamiento del primer elemento
			if (descisores.size() == 0) {
				grupo.add(d);
				descisores.add(grupo);
			} else {
				for (int i = 0; i < descisores.size(); i++) {

					encontrado = false;

					tkGrupo = (ArrayList) descisores.get(i);
					Descisor muestra = (Descisor) tkGrupo.get(0);
					if (muestra.getCondicion().getEntidad().equals(d.getCondicion().getEntidad())) {
						tkGrupo.add(d);
						descisores.set(i, tkGrupo);
						encontrado = true;

						break;
					}
				}
				if (!encontrado) {
					tkGrupo = new ArrayList<Descisor>();
					tkGrupo.add(d);
					descisores.add(tkGrupo);
				}
			}

		}

		for (ArrayList resultado : descisores) {
			condiciones = condiciones + wrapRow(resultado, fila.getAcciones());
		}
		
		//log.debug("Condiciones de la Fila: " + fila.getId());
		List<MetaDataCondicion> listaOrdenada = this.ordenarMetadataCondiciones(metaDataCondiciones);
		
		String condicionesFinales ="";
		for (MetaDataCondicion m: listaOrdenada  ){
			//log.debug("Linea condicion: " + m.getCondicion());
			condicionesFinales+= m.getCondicion();
		}

		//return condiciones;
		
		return condicionesFinales;

	}

	/**
	 * Transforma/Wrappea una formula a codigo DRL. En la presente version solo
	 * se soportan combinatorias simples para MAX, MIN, SUMATORIA, CONTAR,
	 * REDONDEO
	 * 
	 * @param formula
	 *            Formula a analizar
	 * @param tipo
	 *            Region donde se aplica, puede ser ACCION o CONDICION
	 * @param fx
	 *            Es un objeto formula donde se almacena el cuerpo de la formula
	 *            wrapeada. Esto es para propagar el valor por referencia del
	 *            parametro
	 * @param atrInvolucrado
	 *            Es el atributo involucrado en la formula (condicion o accion)
	 *            para determinar si se esta trabajando con la misma entidad o
	 *            una distinta.
	 * @return Devuelve el codigo DRL Generado
	 */
	private String wrapFormula(Formula formula, String tipo, Formula fx, Entidad entInvolucrada) {
		String cadena = "";
		String incognita = "";

		String cadenaResultante = "";

		//log.debug("Cuerpo de la formula a analizar: " + formula.getCuerpo());

		if (formula.getCuerpo() != null) {

			if (formula.getCuerpo().indexOf("SUMATORIA") != -1) {
				//log.debug("Funcion transformara la funcion SUMATORIA " + formula.getCuerpo().indexOf("SUMATORIA"));
				incognita = formula.getCuerpo().substring(formula.getCuerpo().indexOf("SUMATORIA") + 10, formula.getCuerpo().length() - 1);

			}

			if (formula.getCuerpo().indexOf("MIN") != -1) {
				//log.debug("Funcion transformara la funcion MIN " + formula.getCuerpo().indexOf("MIN"));
				incognita = formula.getCuerpo().substring(formula.getCuerpo().indexOf("MIN") + 4, formula.getCuerpo().length() - 1);

			}

			if (formula.getCuerpo().indexOf("MAX") != -1) {
				//log.debug("Funcion transformara la funcion MAX " + formula.getCuerpo().indexOf("MAX"));
				incognita = formula.getCuerpo().substring(formula.getCuerpo().indexOf("MAX") + 4, formula.getCuerpo().length() - 1);

			}

			if (formula.getCuerpo().indexOf("CONTAR") != -1) {
				//log.debug("Funcion transformara la funcion CONTAR " + formula.getCuerpo().indexOf("CONTAR"));
				incognita = formula.getCuerpo().substring(formula.getCuerpo().indexOf("CONTAR") + 7, formula.getCuerpo().length() - 1);

			}

			if (formula.getCuerpo().indexOf("PROMEDIO") != -1) {
				//log.debug("Funcion transformara la funcion PROMEDIO " + formula.getCuerpo().indexOf("PROMEDIO"));
				incognita = formula.getCuerpo().substring(formula.getCuerpo().indexOf("PROMEDIO") + 9, formula.getCuerpo().length() - 1);

			}
		}

		// Hemos allado una incognita a evaluar
		if (!incognita.equals("")) {
			for (Incognita i : formula.getIncognitas()) {
				if (i.getCodigo() == incognita.charAt(0)) {

					//log.debug("La incognita encontrada es: " + i.getCodigo() + " y el valor de la misma es: " + i.getAtributo().getNombre());

					if (!atributosInvolucrados.contains(i.getAtributo())) {
						atributosInvolucrados.add(i.getAtributo());
					}

					if (formula.getCuerpo().indexOf("SUMATORIA") != -1) {
						fx.setCuerpo(wrapSumatoria(i.getAtributo(), i.getAtributo().getEntidad().getNombre() + i.getAtributo().getNombre()));
						// if (tipo.equals("ACCION"))
						return "$Sumatoria" + i.getAtributo().getEntidad().getNombre() + i.getAtributo().getNombre();

					}

					if (formula.getCuerpo().indexOf("MIN") != -1) {
						fx.setCuerpo(wrapMin(i.getAtributo(), i.getAtributo().getEntidad().getNombre() + i.getAtributo().getNombre()));
						// if (tipo.equals("ACCION"))
						return "$Min" + i.getAtributo().getEntidad().getNombre() + i.getAtributo().getNombre();
					}

					if (formula.getCuerpo().indexOf("MAX") != -1) {
						fx.setCuerpo(wrapMax(i.getAtributo(), i.getAtributo().getEntidad().getNombre() + i.getAtributo().getNombre()));

						return "$Max" + i.getAtributo().getEntidad().getNombre() + i.getAtributo().getNombre();
					}

					if (formula.getCuerpo().indexOf("CONTAR") != -1) {
						fx.setCuerpo(wrapContar(i.getAtributo(), i.getAtributo().getEntidad().getNombre() + i.getAtributo().getNombre()));
						// if (tipo.equals("ACCION"))
						return "$Cantidad" + i.getAtributo().getEntidad().getNombre() + i.getAtributo().getNombre();

					}

					if (formula.getCuerpo().indexOf("PROMEDIO") != -1) {
						fx.setCuerpo(wrapAvg(i.getAtributo(), i.getAtributo().getEntidad().getNombre() + i.getAtributo().getNombre()));
						// if (tipo.equals("ACCION"))
						return "$Avg" + i.getAtributo().getEntidad().getNombre() + i.getAtributo().getNombre();

					}

				}
			}

		}

		// Se asume que no hay formulas complejas (sumatoria, max, etc) por lo
		// cual se analiza formulas simples
		if (incognita.equals("")) {

			if (formula.getCuerpo() != null) {

				// Por algun motivo que solo Dios sabe, no incluye el primer
				// caracter cuando se trata de un parentesis...why?
				if (formula.getCuerpo().subSequence(0, 1).equals("(")) {
					cadenaResultante += "(";
				}

				for (String s : formula.getCuerpo().split(WrapperRulesFactory.OPERADORES_LEXICOS_REGEXP)) {
					//log.debug("Resultado del Split: " + s);
					if (!s.matches(WrapperRulesFactory.VALORES_POSIBLES_INCOGNITAS_REGEXP))
						cadenaResultante += s;
					else {
						Incognita inc = buscarIncognita(s, formula);

						if (!atributosInvolucrados.contains(inc.getAtributo())) {
							atributosInvolucrados.add(inc.getAtributo());
						}

						if (inc != null) {

							if (tipo.equals("CONDICION"))

								if (!entInvolucrada.equals(inc.getEntidad()))
									cadenaResultante += "$" + inc.getEntidad().getNombre() + inc.getAtributo().getNombre();
								else
									cadenaResultante += inc.getAtributo().getNombre();
							else {
								String _metodo = inc.getAtributo().getNombre().substring(0, 1);
								_metodo = _metodo.toUpperCase() + inc.getAtributo().getNombre().substring(1);
								cadenaResultante += "$" + inc.getEntidad().getNombre() + ".get" + _metodo + "()";
								// cadenaResultante += "$" +
								// inc.getEntidad().getNombre() + "." +
								// inc.getAtributo().getNombre();
							}
						} else { // Se asume que es una constante
							cadenaResultante += s;
						}
					}

				}

				log.debug("Cadena Resultante del analisis lexico: " + cadenaResultante);

				cadena = cadenaResultante;

			}

		}

		return cadena;
	}

	/**
	 * Busca una incognita dentro de una formula
	 * 
	 * @param inc
	 *            Incognita/Variable a buscar
	 * @param fx
	 *            Formula donde se realizara la busqueda
	 * @return Devuelve la incognita encontrada o nulo en caso contrario. Podria
	 *         tratarse de una constante ?
	 */
	private Incognita buscarIncognita(String inc, Formula fx) {
		Incognita incEncontrada = null;

		for (Incognita i : fx.getIncognitas()) {
			if (String.valueOf(i.getCodigo()).equals(inc)) {
				if (!entidadesADeclarar.contains(i.getEntidad())) {
					entidadesADeclarar.add(i.getEntidad());
				}
				incEncontrada = i;
				break;
			}
		}

		return incEncontrada;

	}

	private String analizarTermino(String termino, Formula formula, String tipo) {
		String cadena = "";
		boolean encontrado = false;

		// Se agrega factor de correccion al tratamiento del termino WTF?
		if (termino.length() > 1
				&& (termino.substring(0, 1).equals("(") || termino.substring(0, 1).equals(")") || termino.substring(0, 1).equals("+")
						|| termino.substring(0, 1).equals("-") || termino.substring(0, 1).equals("/") || termino.substring(0, 1).equals("*"))) {
			termino = termino.substring(1);
			//log.debug("Termino trabajado: " + termino);
		}

		for (Incognita i : formula.getIncognitas()) {

			if (!atributosInvolucrados.contains(i.getAtributo())) {
				atributosInvolucrados.add(i.getAtributo());
			}

			if (String.valueOf(i.getCodigo()).equals(termino)) {

				if (!entidadesADeclarar.contains(i.getEntidad())) {
					entidadesADeclarar.add(i.getEntidad());
				}

				if (tipo.equals("CONDICION"))
					cadena = "$" + i.getEntidad().getNombre() + "." + i.getAtributo().getNombre();
				else {
					String _metodo = i.getAtributo().getNombre().substring(0, 1);
					_metodo = _metodo.toUpperCase() + i.getAtributo().getNombre().substring(1);
					cadena = "$" + i.getEntidad().getNombre() + ".get" + _metodo + "()";
				}

				encontrado = true;
				break;
			}

		}

		if (!encontrado) {

			if (termino.equals("(") || termino.equals(")")) {
				cadena = analizarTermino(termino.substring(1), formula, tipo);
			} else {
				//log.debug("Termino no encontrado: " + termino);
				cadena = termino;
			}

		}

		return cadena;

	}

	/**
	 * Wrapea una fila de una tabla de desicion
	 * 
	 * @param resultado
	 * @param acciones
	 *            Lista de acciones
	 * @return Devuelve el codigo DRL de la regla wrappeada
	 */
	public String wrapRow(ArrayList resultado, List<Accion> acciones) {
		String cadena = "";
		String strOperador = "";
		boolean primerCiclo = true;
		String sFuncion = "";
		String condicionEnFuncion = "";
		WrapperRulesFactory factory;
		String formulasEnCondiciones = "";
		
		MetaDataCondicion metaCondicion=new MetaDataCondicion();
		
		/** 
		 * conjunto de atributos que intervienen en una condicion (por comparacion)
		 */
		Set<Atributo> atributosEnCondicion =new HashSet<Atributo>();

		// cadena="$" +
		// ((Descisor)((ArrayList)descisores.get(0)).get(0)).getCondicion().getEntidad().getNombre()
		// + ": " +
		// ((Descisor)((ArrayList)descisores.get(0)).get(0)).getCondicion().getEntidad().getNombre()
		// + "(";
		
		metaCondicion.setEntidadDeclarada(((Descisor) resultado.get(0)).getCondicion().getEntidad());

		cadena = "$" + ((Descisor) resultado.get(0)).getCondicion().getEntidad().getNombre() + ":"
				+ ((Descisor) resultado.get(0)).getCondicion().getEntidad().getNombre() + "(";

		// Guardamos en una variable el momento inicial de la condicion para
		// evitar el Objeto(&& algo
		String cadenaInicial = cadena;

		condicionesDSL = "";
		condicionToDSL(resultado);

		for (int i = 0; i < resultado.size(); i++) {

			Descisor des = (Descisor) resultado.get(i);

			// Se agrega un condicionante para que excluya los valores N/A de
			// los descisores
			if ((!primerCiclo && !cadena.equals(cadenaInicial) && des.getCondicion().getFuncion() == null && des.getValorCondicionAtributo() != null && des
					.getValorCondicionFormula() == null) || (des.getValor() != null && !primerCiclo && !cadena.equals(cadenaInicial)))
				cadena = cadena + " && ";

		/*	log.debug("Valor analizado: " + des.getCondicion().getNombre() + " Y el valor es: " + des.getValor());

			log.debug("Valor de Valor: " + des.getValor());
			log.debug("Funcion: " + des.getCondicion().getFuncion());
			log.debug("Condicion atributo: " + des.getValorCondicionAtributo());
			log.debug("Condicion Formula: " + des.getValorCondicionFormula());*/

			if (!vEntidades.contains(des.getCondicion().getEntidad())) {
				vEntidades.add(des.getCondicion().getEntidad());
			}

			/* Analisis de reglas hijas */
			if (des.getCondicion().getRegla() != null) {
				// factory = new WrapperRulesFactory();

				factory = new WrapperRulesFactory();
				factory.setTablaDecision(des.getCondicion().getRegla());
				factory.setLog(log);

				if (!vReglasHijas.contains(des.getCondicion().getRegla())) {
					vReglasHijas.add(des.getCondicion().getRegla());
					reglasHijas = reglasHijas + factory.buildRule();
				}
			}

			if (des.getCondicion().getAtributo().getTipoCarga().equals("LOGICO") && des.getCondicion().getRegla() == null
					&& des.getCondicion().getFuncion() == null) {
				// throw new
				// Exception("La regla  no es simulable debido a que posee una o mas condiciones (por ejemplo la condicion '"+
				// des.getCondicion().getNombre() +"') " +
				// " que evaluan atributos 'LOGICOS' y que no poseen una funcion y/o regla que los hidrate");
				log.fatal("La regla  no es simulable debido a que posee una o mas condiciones (por ejemplo la condicion '"
						+ des.getCondicion().getNombre() + "') "
						+ " que evaluan atributos 'LOGICOS' y que no poseen una funcion y/o regla que los hidrate");

			}

			/* Cuando una condicion es evaluada por una funcion */
			if (des.getCondicion().getFuncion() != null) {

				//log.debug(String.format("Nombre de la funci‚Äö√Ñ√Æn: %s", des.getCondicion().getFuncion().getNombre()));
				//log.debug(String.format("Cuerpo de la funci‚Äö√Ñ√Æn: %s", des.getCondicion().getFuncion().getCuerpo()));

				for (Parametro parametro : des.getCondicion().getFuncion().getParametros()) {
					//log.debug(String.format("Nombre del parametro: %s, descripci‚Äö√Ñ√Æn del parametro %s, entidad del parametro %s",
							//parametro.getNombre(), parametro.getAtributo().getDescripcion(), parametro.getEntidad().getNombre()));
				}
				if (!vFunciones.contains(des.getCondicion().getFuncion())) {
					vFunciones.add(des.getCondicion().getFuncion());
				}
				sFuncion = writeFunctionHeader(des.getCondicion().getFuncion(), des.getValor().getOperadorLogico(), des.getValor().getNombre(),
						"CONDICIONES", vEntidades);
				condicionEnFuncion = condicionEnFuncion + "\t\t" + sFuncion + "\n";
				//log.debug("El valor de condicionenfuncion es: " + condicionEnFuncion);
				// condicionesEvaluadas = condicionesEvaluadas + "\t" +
				// sFuncion;
			}

			if (des.getValor() != null) {

				if (!atributosInvolucrados.contains(des.getCondicion().getAtributo())) {
					atributosInvolucrados.add(des.getCondicion().getAtributo());
				}

				// Para el caso que la condicion compare contra un texto
				if (des.getCondicion().getAtributo().getTipoDato().equals("TEXTO") && (des.getCondicion().getFuncion() == null)) {
					String _valorString = des.getValor().getNombre();
					String _condicionFormateada = "";
					if (_valorString.equals("VALORNULO")) {
						_condicionFormateada = des.getCondicion().getAtributo().getNombre() + "==null";
					} else {
						_condicionFormateada = des.getCondicion().getAtributo().getNombre() + des.getValor().getOperadorLogico() + "\""
								+ des.getValor().getNombre() + "\"";
					}
					cadena = cadena + strOperador + _condicionFormateada;
				}

				// Para el caso que la condicion compare contra un numero
				if (des.getCondicion().getAtributo().getTipoDato().equals("NUMERO") && (des.getCondicion().getFuncion() == null)) {
					String _valorNumerico = des.getValor().getNombre();
					String _condicionFormateada = "";
					if (_valorNumerico.equals("VALORNULO")) {
						_condicionFormateada = des.getCondicion().getAtributo().getNombre() + "==null";
					} else {
						_condicionFormateada = des.getCondicion().getAtributo().getNombre() + des.getValor().getOperadorLogico()
								+ des.getValor().getNombre();
					}
					cadena = cadena + strOperador + _condicionFormateada;
				}

				// Para el caso que la condicion compare contra un booleano
				if (des.getCondicion().getAtributo().getTipoDato().equals("BOOLEANO") && (des.getCondicion().getFuncion() == null)) {
					String _valorBooleano = des.getValor().getNombre();

					if (_valorBooleano.equals("VERDADERO")) {
						_valorBooleano = "true";
					} else {
						_valorBooleano = "false";
					}
					cadena = cadena + strOperador + des.getCondicion().getAtributo().getNombre() + des.getValor().getOperadorLogico()
							+ _valorBooleano;
				}

				if (des.getCondicion().getAtributo().getTipoDato().equals("FECHA") && (des.getCondicion().getFuncion() == null)) {
					String _valorString = des.getValor().getNombre();
					String _condicionFormateada = "";
					if (_valorString.equals("VALORNULO")) {
						_condicionFormateada = des.getCondicion().getAtributo().getNombre() + "==null";
					}else
                    {
                        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MMM-yyyy");
                        try
                        {
                              _condicionFormateada = "\"" + formatoFecha.format(new Date(new SimpleDateFormat("dd/MM/yyyy").parse(_valorString).getTime())) + "\"";
                        }
                        catch (java.text.ParseException ex)
                        {
                       
                        }
                       
                  }
                 
                  cadena = cadena + strOperador + des.getCondicion().getAtributo().getNombre() + des.getValor().getOperadorLogico()
                  + _condicionFormateada;

					
				 
				}
			}

			if (des.getValorCondicionAtributo() != null) {

				if (!entidadesADeclarar.contains(des.getValorCondicionAtributo().getAtributo2().getEntidad())) {
					entidadesADeclarar.add(des.getValorCondicionAtributo().getAtributo2().getEntidad());
				}

				if (!atributosInvolucrados.contains(des.getCondicion().getAtributo())) {
					atributosInvolucrados.add(des.getCondicion().getAtributo());
				}

				if (!atributosInvolucrados.contains(des.getValorCondicionAtributo().getAtributo2())) {
					atributosInvolucrados.add(des.getValorCondicionAtributo().getAtributo2());
				}

				String termino1 = des.getCondicion().getAtributo().getNombre();

				if (!atributosADeclararComoVariables.contains(des.getValorCondicionAtributo().getAtributo2())) {
					atributosADeclararComoVariables.add(des.getValorCondicionAtributo().getAtributo2());
				}

				String termino2 = "$" + des.getValorCondicionAtributo().getAtributo2().getEntidad().getNombre()
						+ des.getValorCondicionAtributo().getAtributo2().getNombre();
				
				
				
				atributosEnCondicion.add(des.getValorCondicionAtributo().getAtributo2());

				// Estamos trabajando con la misma entidad
				if (des.getValorCondicionAtributo().getAtributo2().getEntidad().getId() == des.getCondicion().getAtributo().getEntidad().getId()) {
					cadena = cadena + des.getValorCondicionAtributo().getOperadorLogico() + termino2;
				} else {

					cadena = cadena + termino1 + des.getValorCondicionAtributo().getOperadorLogico() + termino2;
				}

			}

			// Formula
			if (des.getValorCondicionFormula() != null) {

				Formula fx = new Formula();

				if (!atributosADeclararComoVariables.contains(des.getCondicion().getAtributo())) {
					atributosADeclararComoVariables.add(des.getCondicion().getAtributo());
				}

				formulasEnCondiciones = wrapFormula(des.getValorCondicionFormula().getFormula(), "CONDICION", fx, des.getCondicion().getAtributo()
						.getEntidad());

				//log.debug("Cuerpo de la formula en la condicion: " + fx.getCuerpo());

				if (des.getValorCondicionFormula().getFormula().getCuerpo().indexOf("SUMATORIA") == -1
						&& des.getValorCondicionFormula().getFormula().getCuerpo().indexOf("CONTAR") == -1
						&& des.getValorCondicionFormula().getFormula().getCuerpo().indexOf("MAX") == -1
						&& des.getValorCondicionFormula().getFormula().getCuerpo().indexOf("MIN") == -1
						&& des.getValorCondicionFormula().getFormula().getCuerpo().indexOf("PROMEDIO") == -1) {

					
					
					// if ( des.getCondicion().getAtributo().)
					cadena = (fx.getCuerpo() == null ? "" : fx.getCuerpo()) + "\n\t" + cadena + des.getCondicion().getAtributo().getNombre()
							+ des.getValorCondicionFormula().getOperadorLogico() + "(" + formulasEnCondiciones + ")";
				} else {
					
					if (!primerCiclo && !cadena.equals(cadenaInicial))
						cadena = cadena + ", ";
					
					cadena = (fx.getCuerpo() == null ? "" : fx.getCuerpo()) + "\n\t" + cadena + des.getCondicion().getAtributo().getNombre()
							+ des.getValorCondicionFormula().getOperadorLogico() + formulasEnCondiciones;
				}

			}
			
			
			primerCiclo = false;
		}

		metaCondicion.setAtributosDeclarados(atributosEnCondicion);
		metaCondicion.setCondicion( cadena + " " + exclusionCondicion(acciones, ((Descisor) resultado.get(0)).getCondicion().getEntidad().getNombre()) + ")\t\n"
				+ condicionEnFuncion + "\t\n");
		
		metaDataCondiciones.add(metaCondicion);
		
		return cadena + " " + exclusionCondicion(acciones, ((Descisor) resultado.get(0)).getCondicion().getEntidad().getNombre()) + ")\t\n"
				+ condicionEnFuncion + "\t\n";

	}

	public Set<Entidad> getEntidadesADeclarar() {
		return entidadesADeclarar;
	}

	public void setEntidadesADeclarar(Set<Entidad> entidadesADeclarar) {
		this.entidadesADeclarar = entidadesADeclarar;
	}

	public Log getLog() {
		return log;
	}

	public void setLog(Log log) {
		this.log = log;
	}

	/**
	 * Transforma una lista de descisores en los elementos del WHEN de la regla
	 * */

	@SuppressWarnings("unused")
	private String wrapCondiciones(List<Descisor> descisores) {

		String cadena = "";
		String sFuncion = "";
		String funciones = "";

		Comparator<Descisor> ORDER_BY_ID = new Comparator<Descisor>() {
			public int compare(Descisor d1, Descisor d2) {
				return d2.getId().compareTo(d1.getId());
			}
		};
		Collections.sort(descisores, ORDER_BY_ID);

		Object comparacion = null;
		Descisor d = null;
		String strOperador = "";

		condicionToDSL(descisores);

		for (int i = 0; i < descisores.size(); i++) {
			d = (Descisor) descisores.get(i);

			if (d.getCondicion().getFuncion() != null) {
				sFuncion = getFunctionHeader(d);
				funciones = funciones + "\t\t" + sFuncion + "\n";
			}

			if (!d.getCondicion().getEntidad().equals(comparacion)) {
				if (!cadena.equals("")) {
					cadena = cadena + ")";
				}
				cadena = cadena + "\n\t" + "$" + d.getCondicion().getEntidad().getNombre() + ":" + d.getCondicion().getEntidad().getNombre() + "(";
				strOperador = "";
			}

			if (d.getValor() != null) {

				// Para el caso que la condicion compare contra un texto
				if (d.getCondicion().getAtributo().getTipoDato().equals("TEXTO") && (d.getCondicion().getFuncion() == null)) {

					String _valorString = d.getValor().getNombre();
					String _condicionFormateada = "";
					if (_valorString.equals("VALORNULO")) {
						_condicionFormateada = d.getCondicion().getAtributo().getNombre() + "==null";
					} else {
						_condicionFormateada = d.getCondicion().getAtributo().getNombre() + d.getValor().getOperadorLogico() + "\""
								+ d.getValor().getNombre() + "\"";
					}
					cadena = cadena + strOperador + _condicionFormateada;
				}

				// Para el caso que la condicion compare contra un numero
				if (d.getCondicion().getAtributo().getTipoDato().equals("NUMERO") && (d.getCondicion().getFuncion() == null)) {
					String _valorNumerico = d.getValor().getNombre();
					String _condicionFormateada = "";
					if (_valorNumerico.equals("VALORNULO")) {
						_condicionFormateada = d.getCondicion().getAtributo().getNombre() + "==null";
					} else {
						_condicionFormateada = d.getCondicion().getAtributo().getNombre() + d.getValor().getOperadorLogico()
								+ d.getValor().getNombre();
					}
					cadena = cadena + "\t\t" + d.getCondicion().getEntidad().getNombre() + "(" + _condicionFormateada + ")\n";
				}

				// Para el caso que la condicion compare contra un booleano
				if (d.getCondicion().getAtributo().getTipoDato().equals("BOOLEANO") && (d.getCondicion().getFuncion() == null)) {
					String _valorBooleano = d.getValor().getNombre();

					if (_valorBooleano.equals("VERDADERO")) {
						_valorBooleano = "true";
					} else {
						_valorBooleano = "false";
					}
					cadena = cadena + "\t\t" + d.getCondicion().getEntidad().getNombre() + "(" + d.getCondicion().getAtributo().getNombre()
							+ d.getValor().getOperadorLogico() + _valorBooleano + ")\n";
				}
			}
			strOperador = " && ";
		}
		return cadena + funciones;
	}

	private String exclusionCondicion(List<Accion> acciones, String entidad) {

		String cadena = "";

		for (Accion a : acciones) {
			/*
			 * if (a.getTipoAccion().equals("MH") &&
			 * a.getAccionModificaHecho().getAccionModificaHechoFormula() ==
			 * null &&
			 * a.getAccionModificaHecho().getEntidad().getNombre().equals
			 * (entidad)) {
			 * 
			 * cadena = cadena + " && " +
			 * a.getAccionModificaHecho().getAtributo().getNombre() + "==null";
			 * 
			 * }
			 */

			if (a.getTipoAccion().equals("MH") && a.getAccionModificaHecho().getEntidad().getNombre().equals(entidad)) {

				cadena = cadena + " && " + a.getAccionModificaHecho().getAtributo().getNombre() + "==null";

			}
		}

		return cadena;
	}

	/**
	 * Wrapea/Transforma una lista de acciones a codigo DRL
	 * 
	 * @param acciones
	 *            Lista de acciones a 'wrappear'
	 * @return Devuelve una cadena con el resultado del wrapeo
	 */
	private String wrapAcciones(List<Accion> acciones) {

		String cadena = "";

		for (Accion a : acciones) {

			if (a.getTipoAccion().equals("MH")) {

				
				if (!entidadesADeclarar.contains(a.getAccionModificaHecho().getAtributo().getEntidad())
						&& !entidadesADeclararEnAccion.contains(a.getAccionModificaHecho().getAtributo().getEntidad())) {
					entidadesADeclararEnAccion.add(a.getAccionModificaHecho().getAtributo().getEntidad());
				}

				// Se carga una variable local con todos los atributos que
				// estan
				// involucrados en la regla
				/*if (!atributosInvolucrados.contains(a.getAccionModificaHecho().getAtributo()) && a.getAccionModificaHecho().getAtributo().getTipoCarga().equals("LOGICO")) {
					atributosInvolucrados.add(a.getAccionModificaHecho().getAtributo());
				}*/
								
				//log.debug("Entidad a declarar en la modificacion de un hecho: " + a.getAccionModificaHecho().getAtributo().getEntidad().getNombre());

				String _valorLiteral = a.getAccionModificaHecho().getAccionModificaHechoLiteral().getLiteral();

				if (a.getTipoAccion().equals("MH") && a.getAccionModificaHecho().getAccionModificaHechoFormula() == null
						&& a.getAccionModificaHecho().getAccionModificaHechoFuncion().getFuncion() == null) {

					String _metodo = a.getAccionModificaHecho().getAtributo().getNombre().substring(0, 1);
					_metodo = _metodo.toUpperCase() + a.getAccionModificaHecho().getAtributo().getNombre().substring(1);

					if (a.getAccionModificaHecho().getAtributo().getTipoDato().equals("BOOLEANO")) {
						if (_valorLiteral.equals("VERDADERO")) {
							_valorLiteral = "true";
						} else {
							_valorLiteral = "false";
						}
						cadena = cadena + "\t\t$" + a.getAccionModificaHecho().getEntidad().getNombre() + ".set" + _metodo + "(" + _valorLiteral
								+ ");";

						/*
						 * cadena = cadena + "\t\t$" +
						 * a.getAccionModificaHecho().getEntidad().getNombre() +
						 * "." +
						 * a.getAccionModificaHecho().getAtributo().getNombre()
						 * + "="+ _valorLiteral + ";";
						 */
					}

					if (a.getAccionModificaHecho().getAtributo().getTipoDato().equals("NUMERO")) {
						_valorLiteral = a.getAccionModificaHecho().getAccionModificaHechoLiteral().getLiteral();
						cadena = cadena + "\t\t$" + a.getAccionModificaHecho().getEntidad().getNombre() + ".set" + _metodo + "("
								+ Double.parseDouble(_valorLiteral) + ");";
						/*
						 * cadena = cadena + "\t\t$" +
						 * a.getAccionModificaHecho().getEntidad().getNombre() +
						 * "." +
						 * a.getAccionModificaHecho().getAtributo().getNombre()
						 * + "=" + + Double.parseDouble(_valorLiteral) + ";";
						 */

					}

					if (a.getAccionModificaHecho().getAtributo().getTipoDato().equals("TEXTO")) {

						cadena = cadena + "\t\t$" + a.getAccionModificaHecho().getEntidad().getNombre() + ".set" + _metodo + "(\""
								+ a.getAccionModificaHecho().getAccionModificaHechoLiteral().getLiteral() + "\");";

						/*
						 * cadena = cadena + "\t\t$" +
						 * a.getAccionModificaHecho().getEntidad().getNombre() +
						 * "." +
						 * a.getAccionModificaHecho().getAtributo().getNombre()
						 * + "\"" +
						 * a.getAccionModificaHecho().getAccionModificaHechoLiteral
						 * ().getLiteral() + "\";";
						 */
					}

				}

				String _metodo = a.getAccionModificaHecho().getAtributo().getNombre().substring(0, 1);
				_metodo = _metodo.toUpperCase() + a.getAccionModificaHecho().getAtributo().getNombre().substring(1);

				// Un hecho modificado por una funcion.
				if (a.getTipoAccion().equals("MH") && a.getAccionModificaHecho().getAccionModificaHechoFuncion().getFuncion() != null) {

					if (!vFunciones.contains(a.getAccionModificaHecho().getAccionModificaHechoFuncion().getFuncion())) {
						vFunciones.add(a.getAccionModificaHecho().getAccionModificaHechoFuncion().getFuncion());
					}
					String sFuncion = writeFunctionHeader(a.getAccionModificaHecho().getAccionModificaHechoFuncion().getFuncion(), "", "",
							"ACCIONES", vEntidades);

					cadena += "\t\t $" + a.getAccionModificaHecho().getEntidad().getNombre() + ".set" + _metodo + "(" + sFuncion + ");";
					// cadena += "\t\t $" +
					// a.getAccionModificaHecho().getEntidad().getNombre() + "."
					// +a.getAccionModificaHecho().getAtributo().getNombre()+
					// "=" + sFuncion + ";";

				}

				// Accion modificada por una formula
				if (a.getTipoAccion().equals("MH") && a.getAccionModificaHecho().getAccionModificaHechoFormula() != null) {

					Formula fx = new Formula();

					/*
					 * cadena = cadena + "\t\t$" +
					 * a.getAccionModificaHecho().getEntidad().getNombre() + "."
					 * + a.getAccionModificaHecho().getAtributo().getNombre() +
					 * "=" + wrapFormula(a.getAccionModificaHecho().
					 * getAccionModificaHechoFormula().getFormula(), "ACCION",
					 * fx, a .getAccionModificaHecho().getEntidad()) + ";";
					 */

					cadena = cadena
							+ "\t\t$"
							+ a.getAccionModificaHecho().getEntidad().getNombre()
							+ ".set"
							+ _metodo
							+ "("
							+ wrapFormula(a.getAccionModificaHecho().getAccionModificaHechoFormula().getFormula(), "ACCION", fx, a
									.getAccionModificaHecho().getEntidad()) + ");";

					strCondiciones += (fx.getCuerpo() == null ? "" : fx.getCuerpo()) + "\n\t";

					//log.debug("Cuerpo de la formula en Accion:" + fx.getCuerpo());
				}

				// Se agrega caracteres de presentacion
				cadena += "\t\t\n";
				// mergeContraCondicion(a.getAccionModificaHecho().getAtributo());
			}
		}

		return cadena;
	}

	private void analizarContracondiciones(List<Accion> acciones) {

		for (Accion a : acciones) {
			if (a.getTipoAccion().equals("MH")) {
				mergeContraCondicion(a.getAccionModificaHecho().getAtributo());
			}
		}
	}

	/**
	 * Agrega una condicion de filtado a formulas especiales
	 * 
	 * @param atr
	 *            Atributo a buscar en las condiciones
	 * @return
	 */
	private String agregarCondcionFiltrado(Atributo atr) {

		
		int inicio=0;
		String condicionFiltrado = "";
		String patronBusqueda = "$" + atr.getEntidad().getNombre() + ":" + atr.getEntidad().getNombre() + "(";

		if (!strCondiciones.equals("")) {
			//log.debug("El patron de busqueda es: " + patronBusqueda);
		    inicio = strCondiciones.indexOf(patronBusqueda) + patronBusqueda.length();
			if (strCondiciones.indexOf(patronBusqueda) !=-1){
				String primeraParte = strCondiciones.substring(inicio);
				int fin = primeraParte.indexOf(")");
				condicionFiltrado = primeraParte.substring(0, primeraParte.indexOf(")"));

				if (condicionFiltrado == null || condicionFiltrado.trim().equals(""))
					condicionFiltrado = "";
			}
		}

		return extraerDeclaracionVariables(condicionFiltrado);
	}

	
	/**
	 * Funcion que permite extraer (separar) aquellas cosas que son declaracion de variables de las que son 'atributos de filtrado'
	 * @param cadena
	 * @return
	 */
	private String extraerDeclaracionVariables(String cadena) {
		int inicio=cadena.indexOf("$");
		if (inicio!=-1){
			//Verificamos si hay otra variable, en cuyo caso hacemos recursiva
			if (cadena.substring(inicio+1).indexOf("$")!=-1){
				return extraerDeclaracionVariables(cadena.substring(cadena.substring(inicio+1).indexOf("$")+1));
			}
			else { //Significa que no hay mas declaracion de variables 
				if (cadena.substring(inicio+1).indexOf(",")!=-1){//significa que desde aqui vienen los atributos
					//log.debug("El valor del indice cuando hay atributos: " +cadena.substring(inicio+1).indexOf(",") +1 );
					return cadena.substring(cadena.substring(inicio+1).indexOf(",")+1);
				}
				else{//Significa ques una cadena que solo tiene declaracion de variables 
					return "";
				}
					
			}
		}
		else { 
			return cadena;
		}
	}
	

	
	/**
	 * Wrapea la funcion SUMATORIA en una funcion drl accumulate y deposita el
	 * resultado en una variable de clase llamada formulasCondiciones
	 * 
	 * @param atr
	 *            Atributo que se evalua en la sumatoria
	 * @param var
	 *            Nombre que adquiere la variable que luego se utiliza (en el
	 *            caso de las acciones) para la modificacion del hecho
	 */
	public String wrapSumatoria(Atributo atr, String var) {

		String cadena = "\n\t$" + "Sumatoria" + var  + ": Double() \n\t\t" + "from accumulate(" + atr.getEntidad().getNombre() + "( " + agregarCondcionFiltrado(atr)
				+ " $valor:" + atr.getNombre() + "),\n\t\t" + "sum($valor))";
		
		if (strCondiciones.indexOf(cadena)!=-1)
			return "";
		else
			return cadena;
	}

	/**
	 * Wrapea/Transforma funciones MAX en codigo DRL. Luego del proceso deja en
	 * formulasCondiciones el resultado
	 * 
	 * @param atr
	 *            Atributo a evaluar
	 * @param var
	 *            nombre de la variable asignada
	 */
	public String wrapMax(Atributo atr, String var) {
		String cadena =  "\n\t $" + "Max" + var  + ": Double() from accumulate(" + atr.getEntidad().getNombre() + "(" + agregarCondcionFiltrado(atr) + "$" + var + ":"
				+ atr.getNombre() + "), \n\t\t" + "	max( $" + var + ")) \n\t\t ";
		if (strCondiciones.indexOf(cadena)!=-1)
			return "";
		else
			return cadena;
	}

	/**
	 * Wrapea/Transforma funciones MIN en codigo DRL. Luego del proceso deja en
	 * formulasCondiciones el resultado
	 * 
	 * @param atr
	 *            Atributo a evaluar
	 * @param var
	 *            nombre de la variable asignada
	 */
	public String wrapMin(Atributo atr, String var) {
		String cadena = "\n\t $" + "Min" + var  + ": Double() from accumulate(" + atr.getEntidad().getNombre() + "(" + agregarCondcionFiltrado(atr) + "$" + var + ":"
				+ atr.getNombre() + "), \n\t\t" + "	min( $" + var + ")) \n\t\t ";
		
		if (strCondiciones.indexOf(cadena)!=-1)
			return "";
		else
			return cadena;
	}

	/**
	 * Wrapea/Transforma funciones AVG en codigo DRL. Luego del proceso deja en
	 * formulasCondiciones el resultado
	 * 
	 * @param atr
	 *            Atributo a evaluar
	 * @param var
	 *            nombre de la variable asignada
	 */
	public String wrapAvg(Atributo atr, String var) {
		String cadena= "\n\t $" + "Avg" + var  + ": Double() from accumulate(" + atr.getEntidad().getNombre() + "(" + agregarCondcionFiltrado(atr) + "$" + var + ":"
				+ atr.getNombre() + "), \n\t\t" + "	average( $" + var + ")) \n\t\t ";
		if (strCondiciones.indexOf(cadena)!=-1)
			return "";
		else
			return cadena;
		
		
	}

	/**
	 * Wrapea/Transfoma una pseudo funcion CONTAR
	 * 
	 * @param atr
	 *            Atributo a evaluar
	 * @param var
	 *            Nombre de la variable contada
	 */
	private String wrapContar(Atributo atr, String var) {
		String cadena = "\n\t$" + "Cantidad" + var  + ": Double() \n\t\t" + "from accumulate(" + atr.getEntidad().getNombre() + "(" + agregarCondcionFiltrado(atr)
				+ "$valor:" + atr.getNombre() + "),\n\t\t" + "init(double " + var + " =0;),\n\t\t" + "	action(" + var + "+=1;),\n\t\t" + "reverse( "
				+ var + " -= 1; ),\n\t\t" + "	result(" + var + "))\n";
		
		if (strCondiciones.indexOf(cadena)!=-1)
			return "";
		else
			return cadena;
	}

	/**
	 * Metodo que declara las variables en condiciones cuando se hace
	 * comparacion con atributo de otras entidades
	 */
	private void declararVariablesEnCondicion() {

		String aReemplazar = "";
		String aBuscar = "";
		String variable = "";
		String resultado = "";

		for (Atributo atr : atributosADeclararComoVariables) {

			if (!atributosInvolucrados.contains(atr)) {
				atributosInvolucrados.add(atr);
			}

			variable = "\\$" + atr.getEntidad().getNombre() + atr.getNombre() + ":" + atr.getNombre();

			//log.debug("Variable a definir por comparacion de atributo: " + variable);
			
			//log.debug("valor del indice: " + strCondiciones.indexOf(atr.getEntidad().getNombre() + ":" + atr.getEntidad().getNombre() + "()"));
			// Buscamos por objetos vacios
			/*if (strCondiciones.indexOf(atr.getEntidad().getNombre() + ":" + atr.getEntidad().getNombre() + "( )") != -1)
			{
				aBuscar =    "\\$" +  atr.getEntidad().getNombre() + ":" + atr.getEntidad().getNombre() + "\\( \\)";
				aReemplazar = atr.getEntidad().getNombre() + ":" + atr.getEntidad().getNombre() + "\\(" + variable + "\\) ";
			} else {*/
				if (( strCondiciones.indexOf(atr.getEntidad().getNombre() + ":" + atr.getEntidad().getNombre() + "( )") != -1) || (strCondiciones.indexOf(atr.getEntidad().getNombre() + ":" + atr.getEntidad().getNombre() + "()") != -1)){
					aBuscar =  "\\$" + atr.getEntidad().getNombre() + "\\:" + atr.getEntidad().getNombre() + "\\( \\)";
					aReemplazar =   "\\$" + atr.getEntidad().getNombre() + "\\:" + atr.getEntidad().getNombre() + "\\(" + variable + "\\) ";
					//log.debug("Se reemplazara con: " + "\\$" +atr.getEntidad().getNombre() + "\\:" + atr.getEntidad().getNombre() + "\\(" + variable);
					
					//log.debug("Buscamos: " + aBuscar + "reemplazamos por:" + aReemplazar);
					String resultante = strCondiciones.replaceAll(aBuscar, aReemplazar);

					//log.debug("Resultante: " + resultante);
					strCondiciones = resultante;
					
					/*if (atr.getEntidad().getNombre().equals("RelacionParentesco")){
						log.debug("--------------Buscamos por la declaracion del objeto vacio-------------------------------");
						log.debug("Cadena de analisis: " + strCondiciones);
						log.debug("A buscar: " + aBuscar);
						log.debug("Reemplazamos con: " + aReemplazar);
						log.debug("Resultado final: " + strCondiciones.replaceAll(aBuscar, aReemplazar));
						log.debug("--------------Resultado del String codiciones -------------------------------");
						log.debug(strCondiciones);
					}*/
					
					
					
				}
				else
				{
					
					aBuscar =     "\\$" + atr.getEntidad().getNombre() + "\\:" + atr.getEntidad().getNombre() + "\\(";
					aReemplazar =   "\\$" +atr.getEntidad().getNombre() + "\\:" + atr.getEntidad().getNombre() + "\\(" + variable + ", ";
					
					//log.debug("Buscamos: " + aBuscar + "reemplazamos por:" + aReemplazar);
					String resultante = strCondiciones.replaceAll(aBuscar, aReemplazar);

					//log.debug("Resultante: " + resultante);
					strCondiciones = resultante;
					/*
					if (atr.getEntidad().getNombre().equals("RelacionParentesco")){
						log.debug("------------------Buscamos por la declaracion del objeto con datos-----------------------------------------");
						log.debug("Cadena de analisis: " + strCondiciones);
						log.debug("A buscar: " + aBuscar);
						log.debug("Reemplazamos con: " + aReemplazar);
						log.debug("Resultado final: " + strCondiciones.replaceAll(aBuscar, aReemplazar));
						log.debug("--------------Resultado del String codiciones -------------------------------");
						log.debug(strCondiciones);
					}*/
					
				}
				
			//}

			

			aReemplazar = "";
			aBuscar = "";

		}

	}

	/**
	 * Mergea la contra-condicion encontrada en las acciones a fin de evitar el
	 * loopeo. Se hace a nivel de cadena porque no son procesos concurrente el
	 * wrap de las acciones de las condiciones sino que uno es anterior al otro.
	 * Siempre se contradice a null
	 * 
	 * @param atr
	 *            Atributo a Contradecir
	 * 
	 */
	private void mergeContraCondicion(Atributo atr) {

		
		if (!atributosInvolucrados.contains(atr)) {
			//atributosInvolucrados.add(atr);
		}
		

		String aReemplazar = "";
		String aBuscar = "";

		//log.debug("mergeContraCondicion: Valor del strCondiciones");

		//log.debug("mergeContraCondicion: Valor de indexOf cuando se trata de una entidad sin parametros: "
				//+ strCondiciones.indexOf(atr.getEntidad().getNombre() + ":" + atr.getEntidad().getNombre() + "()"));

		// Buscamos por objetos vacios
		if (strCondiciones.indexOf(atr.getEntidad().getNombre() + ":" + atr.getEntidad().getNombre() + "( )") != 0) {
			aBuscar = "\\$" + atr.getEntidad().getNombre() + ":" + atr.getEntidad().getNombre() + "\\( \\)";
			aReemplazar = "\\$" + atr.getEntidad().getNombre() + ":" + atr.getEntidad().getNombre() + "\\(" + atr.getNombre() + "==null\\) ";
		} else {
			aBuscar = "\\$" + atr.getEntidad().getNombre() + ":" + atr.getEntidad().getNombre() + "\\(";
			aReemplazar = "\\$" + atr.getEntidad().getNombre() + ":" + atr.getEntidad().getNombre() + "\\(" + atr.getNombre() + "==null && ";
		}

		String resultante = strCondiciones.replaceAll(aBuscar, aReemplazar);

		strCondiciones = resultante;

	}

	public String wrappingFacts() {
		// TODO Auto-generated method stub
		return null;
	}

	public String funcionesHelpers() {

		String cadena = "";
		return cadena;
	}

	/**
	 * Escribe el header de la funcion
	 * 
	 * @param f
	 *            Funcion a ser escrita
	 * @param operadorLogico
	 *            Operador logico que representa la funcion
	 * @param valor
	 *            Valor
	 * @param modo
	 *            CONDICIONES para escribir las formulas para condiciones o
	 *            ACCIONES para escribir las formulas en las acciones
	 * @param vEntidades
	 *            Entidades que estan involucradas en la funcion
	 * @return
	 */
	public String writeFunctionHeader(Funcion f, String operadorLogico, String valor, String modo, Set<Entidad> vEntidades) {

		String pseudocodigo = "";
		// Funcion f = d.getCondicion().getFuncion();
		pseudocodigo = f.getNombre() + "(";
		String accessor = "";
		String sEntidad = "";

		boolean pongocoma = false;

		for (Parametro p : f.getParametros()) {
			if (pongocoma) {
				pseudocodigo += ",";
			}
			pongocoma = true;

			if (p.getAtributo().getTipoDato().equals("BOOLEANO")) {
				accessor = "is";
			} else {
				accessor = "get";
			}

			String _metodo = p.getAtributo().getNombre().substring(0, 1);
			_metodo = _metodo.toUpperCase() + p.getAtributo().getNombre().substring(1);

			pseudocodigo += "$" + p.getEntidad().getNombre() + "." + accessor + _metodo + "()";

			/*
			 * Agrego la entidad a la lista de entidades utilizadas si es que no
			 * se encuentra en dicho vector y adem‚àö¬∞s agrego la inicializaci‚àö‚â•n
			 * de la variable en DRL
			 */
			if (!vEntidades.contains(p.getEntidad())) {
				vEntidades.add(p.getEntidad());
				sEntidad = "$" + p.getEntidad().getNombre() + ": " + p.getEntidad().getNombre() + "()\n\t\t";
			}

			// Se carga una variable local con todos los atributos que estan
			// involucrados en la regla
			if (!atributosInvolucrados.contains(p.getAtributo())) {
				atributosInvolucrados.add(p.getAtributo());
			}
		}

		pseudocodigo += ")";

		if (modo.equals("CONDICIONES")) {
			return sEntidad + "eval(" + pseudocodigo + operadorLogico + valor + ")";
		} else {
			return pseudocodigo;
		}

	}

	/**
	 * Escribe las funciones declaradas en el vector vFunciones
	 * 
	 * @return Devuelve el conjunto de funciones wrapeadas
	 */
	public String writeFunction() {
		String retType = "";
		String pseudocodigo = "";
		String drl = "";

		for (Funcion f : vFunciones) {

			if (f.getTipoDato().equals("NUMERO")) {
				retType = " java.lang.Double ";
			} else if (f.getTipoDato().equals("TEXTO")) {
				retType = " java.lang.String ";
			} else if (f.getTipoDato().equals("FECHA")) {
				retType = " java.util.Date ";
			} else if (f.getTipoDato().equals("BOOLEANO")) {
				retType = " boolean ";
			}

			pseudocodigo += "function" + retType + f.getNombre() + "(";

			boolean pongocoma = false;

			for (Parametro p : f.getParametros()) {
				if (pongocoma) {
					pseudocodigo += ",";
				}
				pongocoma = true;

				if (p.getAtributo().getTipoDato().equals("NUMERO")) {
					retType = " java.lang.Double ";
				} else if (p.getAtributo().getTipoDato().equals("TEXTO")) {
					retType = " java.lang.String ";
				} else if (p.getAtributo().getTipoDato().equals("FECHA")) {
					retType = " java.util.Date ";
				} else if (p.getAtributo().getTipoDato().equals("BOOLEANO")) {
					retType = " boolean ";
				}

				pseudocodigo += retType + p.getNombre();
			}

			pseudocodigo += ") {\n";
			pseudocodigo += "/*\n" + f.getDescripcion() + "\n*/";
			pseudocodigo += "\n" + f.getCuerpo() + "\n}";

			drl = drl + pseudocodigo;
			pseudocodigo = "";

		}
		//log.debug("Funciones: " + drl);

		return drl;
	}

	public Set<Entidad> getvEntidades() {
		return vEntidades;
	}

	public void setvEntidades(Set<Entidad> vEntidades) {
		this.vEntidades = vEntidades;
	}

	public TablaDecision getTablaDecision() {
		return tablaDecision;
	}

	public void setTablaDecision(TablaDecision tablaDecision) {
		this.tablaDecision = tablaDecision;
	}

	/**
	 * Este metodo convierte una fila de una tabla de desicion en un DSL
	 * 
	 * @param fila
	 *            Fila de la tabla de desicion que sera wrapeada
	 */
	private void filaToDSL(FilaTablaDecision fila) {

		// TODO: Falta construir el metodo filaToDSL

	}

	private String convertirOperadorLogico(String operador) {
		if (operador.equals("==") || operador.equals("=")) {
			return " ES IGUAL A ";
		}

		if (operador.equals("!=")) {
			return " ES DISTINTO A ";
		}

		if (operador.equals(">=")) {
			return " ES MAYOR O IGUAL A ";
		}

		if (operador.equals(">")) {
			return " ES MAYOR ";
		}

		if (operador.equals("<")) {
			return " ES MENOR ";
		}

		if (operador.equals("<=")) {
			return " ES MENOR O IGUAL ";
		}

		// log.fatal("Operador logico " + operador + "  no definido");
		return " Atencion, el operador:  " + operador + " no esta definido";

	}

	/**
	 * Convierte una lista de condiciones a una escritura coloquial DSL. Luego
	 * de la conversion actualiza una propiedad de la factory llamada
	 * condicionesDSL
	 * 
	 * @param descisores
	 *            Lista de descisoares
	 */
	private void condicionToDSL(List<Descisor> descisores) {
		/*
		 * condicionesDSL += " CUANDO exista la entidad " + ((Descisor)
		 * descisores.get(0)).getCondicion().getEntidad().getNombre() +
		 * " cuyas condiciones son que  ";
		 */

		condicionesDSL += " CUANDO ";

		int contador = 0;

		for (Descisor d : descisores) {
			if (d.getValor() != null) {
				condicionesDSL += " " + d.getCondicion().getAtributo().getNombre() + convertirOperadorLogico(d.getValor().getOperadorLogico()) + " "
						+ d.getValor().getNombre();
			}

			if (d.getValorCondicionFormula() != null) {
				String atributo = d.getCondicion().getAtributo().getNombre();
				String operadorLogico = " se Aplica ";
				String formula = d.getValorCondicionFormula().getFormula().getNombre();

				condicionesDSL += " " + atributo + " " + operadorLogico + formula;
			}

			if (d.getValorCondicionAtributo() != null) {

				String termino1 = d.getCondicion().getAtributo().getNombre();

				String termino2 = d.getValorCondicionAtributo().getAtributo2().getEntidad().getNombre() + " "
						+ d.getValorCondicionAtributo().getAtributo2().getNombre();

				// Estamos trabajando con la misma entidad
				if (d.getValorCondicionAtributo().getAtributo2().getEntidad().getId() == d.getCondicion().getAtributo().getEntidad().getId()) {
					condicionesDSL += " " + d.getValorCondicionAtributo().getOperadorLogico() + " " + termino2;
				} else {

					condicionesDSL += " " + termino1 + d.getValorCondicionAtributo().getOperadorLogico() + " " + termino2;
				}

			}
			
			contador++;
			if (contador<descisores.size()){
				condicionesDSL+=" Y ";
			}

		}

	}

	/**
	 * Convierte una lista de acciones a DSL
	 * 
	 * @param acciones
	 *            Lista de acciones a convertir
	 */
	private void accionesToDSL(List<Accion> acciones) {
		String valor = "";

		accionesDSL = " ENTONCES ";

		for (Accion a : acciones) {
			if (a.getTipoAccion().equals("MH")) {

				if (a.getAccionModificaHecho().getAccionModificaHechoLiteral() != null) {
					accionesDSL += "  actualizar " + a.getAccionModificaHecho().getAtributo().getNombre() + " de la entidad "
							+ a.getAccionModificaHecho().getAtributo().getEntidad().getNombre() + " EN  "
							+ a.getAccionModificaHecho().getAccionModificaHechoLiteral().getLiteral();
				}

			}

			if (a.getAccionModificaHecho() != null && a.getTipoAccion().equals("MH")
					&& a.getAccionModificaHecho().getAccionModificaHechoFormula() != null) {
				accionesDSL += " CON EL RESULTADO DE  llamar a la formula "
						+ a.getAccionModificaHecho().getAccionModificaHechoFormula().getFormula().getNombre();
			}

			if (a.getAccionModificaHecho() != null && a.getTipoAccion().equals("MH")
					&& a.getAccionModificaHecho().getAccionModificaHechoFuncion().getFuncion() != null) {

				accionesDSL += " CON EL RESULTADO DE  llamar a la funcion "
						+ a.getAccionModificaHecho().getAccionModificaHechoFuncion().getFuncion().getNombre();
			}
		}

	}

	/**
	 * Genera el String para elevar la excepcion en la regla
	 * 
	 * @param elevar
	 *            Booleano que determina si se eleva o no la excepion
	 * @return Devuelve la cadena
	 */
	private String elevarException(boolean elevar) {

		if (elevar)
			return "exception= new RuleException(\"Se ha interrumpido la ejecucion del programa\");";
		else
			return "";
	}
	
	/**
	 * Ordena de acuerdo al orden de declaracion de la metadata
	 * @param condiciones
	 * @return
	 */
	private List<MetaDataCondicion> ordenarMetadataCondiciones(Set<MetaDataCondicion> condiciones){
		List<MetaDataCondicion> resultante = new ArrayList<MetaDataCondicion>();
		
		
		//Recorremos la primmera vez para ordenar aquellos que no tienen interdendencia entre si en primer lugar
		for (MetaDataCondicion m: condiciones){
			//log.debug("DRL De Condicion: " + m.getCondicion() + " Cantidad de elementos:: " + m.getAtributosDeclarados().size());
			if (m.getAtributosDeclarados().size()==0){
				resultante.add(m);
				//log.debug("Declaracion de condicion sin atributos declarados" + m.getCondicion());
			}
		}
		
		
		//Recorremos la segunda vez para incorporar el resto
		for (MetaDataCondicion m: condiciones){
			if (m.getAtributosDeclarados().size()!=0){
				resultante.add(m);
				//log.debug("Declaracion de condicion CON atributos declarados" + m.getCondicion());
			}
		}
		
		//NOTA: Evaluar que pasa para los distintos valores de N (cuando es distinto de cero) 
		return resultante;
		
		
	}

}