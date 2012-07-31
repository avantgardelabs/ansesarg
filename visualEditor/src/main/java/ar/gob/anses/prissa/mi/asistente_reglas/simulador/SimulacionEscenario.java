package ar.gob.anses.prissa.mi.asistente_reglas.simulador;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;

import org.jboss.seam.ScopeType;

import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Accion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Descisor;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.FilaTablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Funcion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Parametro;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
import ar.gob.anses.prissa.mi.asistente_reglas.wrapper.WrapperRulesFactory;
import ar.gov.anses.prissa.asistente.modelosemantico.ControlRegla;
import ar.gov.anses.prissa.asistente.modelosemantico.Persona;
import ar.gov.anses.prissa.asistente.modelosemantico.Solicitud_RTI;

@Name("simuladorEscenario")
@Scope(ScopeType.CONVERSATION)
public class SimulacionEscenario implements Serializable{
	
	private MensajeSistema mensajeSalida = new MensajeSistema(); 
	
	@Out(required=false)
	private TablaDecision tablaSeleccionada; 
	
	@Out(required=false)
	List<Persona> personas; 

	@Out(required=false)
	List<MensajeSistema> listaMensajes = new ArrayList<MensajeSistema>();
	
	public MensajeSistema getMensajeSalida() {
		return mensajeSalida;
	}

	public void setMensajeSalida(MensajeSistema mensajeSalida) {
		this.mensajeSalida = mensajeSalida;
	}

	@In(required=false,create=true)
	@Out(required=false)
	Persona persona;
	
	@In(required=false,create=true)
	@Out(required=false)
	Solicitud_RTI solicitud_rti;
	
	@In
	EntityManager entityManagerMMX;
	
	@In
	EntityManager entityManager;
	
	@Logger
	Log log;
	
	private List<FilaTablaDecision> filasGeneradas; 
	
	private Set<Funcion> vFunciones= new HashSet<Funcion>();
	
	public void consultar() {
		personas= entityManagerMMX.createQuery("select p from Persona p where cuil = :cuil").setParameter("cuil", persona.getCuil()).getResultList();
		persona = new Persona();
		log.info("Cantidad de personas consultadas: " + personas.size());
		
	}
	
	public void simular(Persona p) throws Exception {
		
		File inputFile = new File(this.getClass().getResource("/").getPath() + "escenario.drl");
		FileInputStream fis = new FileInputStream(inputFile);
		ControlRegla controlRegla = new ControlRegla();
		
		PackageBuilder builder = new PackageBuilder();
		builder.addPackageFromDrl(new InputStreamReader(fis));
		
		Package pkg = builder.getPackage();
		
		RuleBase ruleBase  = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage(pkg);
		
		StatefulSession session = ruleBase.newStatefulSession();
	
		FactHandle perHandle= session.insert(p);
		
		log.info("Fecha de nacimiento: " + p.getFecha_nacimiento().toString());
		log.info("Fecha de solicitud: " + solicitud_rti.getFecha_solicitud().toString());
		
		FactHandle solHandle= session.insert(solicitud_rti);
		
		FactHandle controlHandle= session.insert(controlRegla);
		
		listaMensajes = new ArrayList<MensajeSistema>();
		
		FactHandle fM = session.insert(listaMensajes);
		
		session.fireAllRules();
		listaMensajes = (List<MensajeSistema>)session.getObject(fM);
		
		
		log.info("Mensajes emitidos:" + listaMensajes.size());
	
	}
	
	public void agregarPersona() {
		consultar();
		persona = new Persona();
	}
	
	
	public void makeRule(TablaDecision t) throws Exception, IOException {
		WrapperRulesFactory factory = new WrapperRulesFactory();
		factory.setTablaDecision(t);
		factory.setLog(log);
		//log.info(t.getFilas().size());
		
		log.info(factory.buildRule() + factory.funcionesHelpers() + factory.writeFunction()) ;
	}
	
	public String generarRegla(TablaDecision t) throws Exception, IOException {
		
		
		List<FilaTablaDecision> filas =t.getFilas();
		FilaTablaDecision fila;
		
		filasGeneradas=new ArrayList();
	
		tablaSeleccionada = t; 
		
		String regla = "\npackage ar.org.anses.prissa.reglas;\n";
	    regla= regla + "\n\nimport ar.gov.anses.prissa.asistente.modelosemantico.*; \n" +
	    		"import ar.gob.anses.prissa.mi.asistente_reglas.simulador.MensajeSistema;\n" +
	    		"import java.util.*;" +
	    		"import ar.gob.anses.prissa.mi.asistente_reglas.excepciones.RuleException;\n\n" + 
	    		
	    		"global ar.gov.anses.prissa.asistente2.messages.Report report;\n" +
	    		"\n\n\n";
		
		regla = regla + ruleWrapper(t) + funcionesHelpers();	
		
		regla = regla + "\n" + writeFunction();
	
		log.info(this.getClass().getResource("/").getPath());
		
		File outputFile = new File(this.getClass().getResource("/").getPath() + "escenario.drl");
		FileOutputStream fos = new FileOutputStream(outputFile);
		fos.write(regla.getBytes());
		fos.close();
		
		log.info(regla);
		
		
		return "/simulacionEscenario.xhtml";
		
		
	}
	
	private String writeRule(String nombreTabla, FilaTablaDecision f) throws Exception  {
		String regla="";		
		String condicionesEvaluadas="";
		String reglasHijas="";
		String nombreRegla="regla_" + nombreTabla + "_" + f.getId();
		String objetoAgenda="";
		String sFuncion = "";
		Set<Entidad> vEntidades = new HashSet<Entidad>();
		
		regla =  regla + "\nrule '" + nombreRegla + "'\n";
		regla = regla + "\tno-loop true \n";
		//regla = regla + "\t activation-group \"" + nombreRegla +   "\"\n";
		regla = regla + "\twhen \n";
		regla = regla + "\t\t$control:ControlRegla() \n";
		regla = regla + "\t\t$lista:ArrayList() \n";
		
		for(Accion a:f.getAcciones()){				
			if (a.getTipoAccion().equals("MH")){
				
				//Agrego la entidad a la lista de entidades utilizadas si es que no se encuentra en dicho vector 
				if (!vEntidades.contains(a.getAccionModificaHecho().getEntidad())){
					vEntidades.add(a.getAccionModificaHecho().getEntidad());
				}
				
				regla= regla + "\t\t$" + a.getAccionModificaHecho().getEntidad().getNombre() + ": " + a.getAccionModificaHecho().getEntidad().getNombre()  + "()\n";
				objetoAgenda= a.getAccionModificaHecho().getEntidad().getNombre();
			}
		}
		condicionesEvaluadas="";
		
		for (Descisor d:f.getValores()){
		
			if (d.getCondicion().getRegla()!=null){				
				reglasHijas= reglasHijas + ruleWrapper(d.getCondicion().getRegla());				
			}
			
			if (d.getCondicion().getAtributo().getTipoCarga().equals("LOGICO") && d.getCondicion().getRegla()==null && d.getCondicion().getFuncion()==null){
				throw new Exception("La regla " + nombreRegla + " no es simulable debido a que posee una o mas condiciones (por ejemplo la condicion '"+ d.getCondicion().getNombre() +"') " +
						" que evaluan atributos 'LOGICOS' y que no poseen una funcion y/o regla que los hidrate");
			}
			
			
			if (d.getValor()!=null){
				
				if (d.getCondicion().getFuncion()!=null){
					
					log.info(String.format("Nombre de la funci�n: %s",d.getCondicion().getFuncion().getNombre()));
					log.info(String.format("Cuerpo de la funci�n: %s",d.getCondicion().getFuncion().getCuerpo()));
					
					for ( Parametro parametro:d.getCondicion().getFuncion().getParametros()){
						log.info(String.format("Nombre del parametro: %s, descripci�n del parametro %s, entidad del parametro %s", parametro.getNombre(),parametro.getAtributo().getDescripcion(),parametro.getEntidad().getNombre()));
					}
					
					if (!vFunciones.contains(d.getCondicion().getFuncion())){						
						vFunciones.add(d.getCondicion().getFuncion());
					}
					
					sFuncion = writeFunctionHeader(d, vEntidades);
					
					regla = regla + "\t\t" + sFuncion + "\n";
					
					condicionesEvaluadas = condicionesEvaluadas + "\t" + sFuncion;
					
				}
				
				if (d.getCondicion().getAtributo().getTipoDato().equals("TEXTO") && (d.getCondicion().getFuncion()==null)){
					
					String _valorString=d.getValor().getNombre();
					
					String _condicionFormateada="";
					
					if (_valorString.equals("VALORNULO")){
						_condicionFormateada=d.getCondicion().getAtributo().getNombre()+"==null";
					}
					else {
						_condicionFormateada=d.getCondicion().getAtributo().getNombre() + d.getValor().getOperadorLogico() + "\"" + d.getValor().getNombre() + "\"";
					}
					
					regla = regla + "\t\t" + d.getCondicion().getEntidad().getNombre() + "(" +_condicionFormateada+")\n";						
					condicionesEvaluadas = condicionesEvaluadas + "\t" + d.getCondicion().getEntidad().getNombre() + "(" + d.getCondicion().getAtributo().getNombre() + d.getValor().getOperadorLogico() + "\"" + d.getValor().getNombre() + "\")";						
					
				}
				
				if (d.getCondicion().getAtributo().getTipoDato().equals("NUMERO") && (d.getCondicion().getFuncion()==null)){
					String _valorNumerico=d.getValor().getNombre();
					
					String _condicionFormateada="";
					
					if (_valorNumerico.equals("VALORNULO")){
						_condicionFormateada=d.getCondicion().getAtributo().getNombre()+"==null";
					}
					else {
						_condicionFormateada=d.getCondicion().getAtributo().getNombre() + d.getValor().getOperadorLogico() + "\"" + d.getValor().getNombre() + "\"";
					}
					
					regla = regla + "\t\t" + d.getCondicion().getEntidad().getNombre() + "(" + _condicionFormateada + ")\n";						
					condicionesEvaluadas = condicionesEvaluadas + "\t" + d.getCondicion().getEntidad().getNombre() + "(" + d.getCondicion().getAtributo().getNombre() + d.getValor().getOperadorLogico() +  d.getValor().getNombre() + ")";						
				}
				
				if (d.getCondicion().getAtributo().getTipoDato().equals("BOOLEANO") && (d.getCondicion().getFuncion()==null)){
					String _valorBooleano = d.getValor().getNombre();
					
					if (_valorBooleano.equals("VERDADERO")){
						_valorBooleano="true";
					}
					else {
						_valorBooleano="false";
					}
					
					regla = regla + "\t\t" + d.getCondicion().getEntidad().getNombre() + "(" + d.getCondicion().getAtributo().getNombre() + d.getValor().getOperadorLogico() +   _valorBooleano + ")\n";						
					condicionesEvaluadas = condicionesEvaluadas + "\t" + d.getCondicion().getEntidad().getNombre() + "(" + d.getCondicion().getAtributo().getNombre() + d.getValor().getOperadorLogico() +   _valorBooleano + ")";						
				}
			}
				
		}
		regla = regla + "\t\t eval(!isExecuted(\"" + nombreRegla + "\",$control))\n";
		regla = regla + "\tthen\n";
		for(Accion a:f.getAcciones()){		
			
			String _valorLiteral = a.getAccionModificaHecho().getAccionModificaHechoLiteral().getLiteral();
				
			if (a.getTipoAccion().equals("MH")){
				
				String _metodo = a.getAccionModificaHecho().getAtributo().getNombre().substring(0, 1);
				_metodo = _metodo.toUpperCase() + a.getAccionModificaHecho().getAtributo().getNombre().substring(1);
				
				if (_valorLiteral.equals("VERDADERO")){
					_valorLiteral="true";
				}
				else {
					_valorLiteral="false";
				}
				
				if (a.getAccionModificaHecho().getAtributo().getTipoDato().equals("TEXTO")){
				
					regla= regla + "\t\t$" + a.getAccionModificaHecho().getEntidad().getNombre() + ".set" + _metodo + "(\"" + a.getAccionModificaHecho().getAccionModificaHechoLiteral().getLiteral() + "\");";
				}
				else{
					regla= regla + "\t\t$" + a.getAccionModificaHecho().getEntidad().getNombre() + ".set" + _metodo + "(" + _valorLiteral + ");";
				}
			}
			/*
			msjSalida =  "Para la Entidad: " + a.getAccionModificaHecho().getEntidad().getNombre() 
				+ " en el  Atributo: " + a.getAccionModificaHecho().getAtributo().getNombre()  + " se establece el  Valor: " + a.getAccionModificaHecho().getAccionModificaHechoLiteral().getLiteral();
			
			regla = regla + "\n\t$m.setMensaje(\"" + msjSalida + "\");";*/
			
			f.setMensajeOperadorUdai( f.getMensajeOperadorUdai().replace("\"","'"));
			f.setObservacion( f.getObservacion().replace("\"","'"));
			f.setMensajeUsuarioWEB(f.getMensajeUsuarioWEB().replace("\"","'"));
			
			condicionesEvaluadas= condicionesEvaluadas.replace("\"","'");
			condicionesEvaluadas= condicionesEvaluadas.replace("\n","");
		
			
			f.setMensajeOperadorUdai( f.getMensajeOperadorUdai().replace("\n",""));
			f.setObservacion( f.getObservacion().replace("\n",""));
			f.setMensajeUsuarioWEB(f.getMensajeUsuarioWEB().replace("\n",""));
			
			f.setMensajeOperadorUdai( f.getMensajeOperadorUdai().replace("\r",""));
			f.setObservacion( f.getObservacion().replace("\r",""));
			f.setMensajeUsuarioWEB(f.getMensajeUsuarioWEB().replace("\r",""));
			
			
			
			regla = regla + "\n\t\t MensajeSistema msgSistema = new MensajeSistema(); ";
			regla = regla + "\n\t\t msgSistema.setMensaje(\"" + nombreRegla + "\");";
			regla = regla + "\n\t\t msgSistema.setMensajeUDAI(\"" +  f.getMensajeOperadorUdai() + "\");";
			regla = regla + "\n\t\t msgSistema.setMensajeCiudadano(\"" + f.getMensajeUsuarioWEB() + "\");";
			regla = regla + "\n\t\t msgSistema.setObservacion(\"" + f.getObservacion() + "\");";
			regla = regla + "\n\t\t msgSistema.setCondicionesEvaluadas(\"" + condicionesEvaluadas + "\");";
			regla = regla + "\n\t\t $lista.add(msgSistema);";
			regla = regla + "\n\t\tSystem.out.println(\"Se activo regla regla_" + f.getId() + "\");";
		}
		regla = regla + "\n\t\tagregarRegla(\"" + nombreRegla + "\",$control);";
		regla = regla + "\n\t\t update($"+objetoAgenda+");";
		regla = regla + "\n\t\t update($control);";
		
		regla = regla + "\n\tend\n";
		
		String drlTotal = reglasHijas + regla;
		
		return drlTotal;
	}
	
	private String funcionesHelpers() {
		String cadena = "\nfunction boolean isExecuted (String ruleName, ControlRegla control){ \n" + 
						"	\treturn control.existeRegla(ruleName); \n" + 
						"	} \n\n" +
						"	function void agregarRegla(String ruleName, ControlRegla control){ \n" +
						"	\t         control.agregarRegla(ruleName); \n" +
						"	} \n";
		return cadena;
	}
	
	
	
	private String writeFunctionHeader(Descisor d, Set<Entidad> vEntidades) {
		
		String pseudocodigo="";		
		Funcion f = d.getCondicion().getFuncion();
		pseudocodigo = f.getNombre() + "(";
		String accessor="";
		String sEntidad="";
		
		boolean pongocoma = false;
		
		for (Parametro p : f.getParametros()) {
			if (pongocoma) {
				pseudocodigo += ",";
			}
			pongocoma = true;
			
			if (p.getAtributo().getTipoDato().equals("BOOLEANO")) {
				accessor = "is";
			}
			else {
				accessor="get";
			}
			
			String _metodo = p.getAtributo().getNombre().substring(0, 1);
			_metodo = _metodo.toUpperCase() + p.getAtributo().getNombre().substring(1);
			
			
			pseudocodigo += "$" + p.getEntidad().getNombre() + "." + accessor + _metodo + "()";
			
			/*Agrego la entidad a la lista de entidades utilizadas si es que no se encuentra en dicho vector y adem�s agrego la
			 * inicializaci�n de la variable en DRL
			*/
			if (!vEntidades.contains(p.getEntidad())){						
				vEntidades.add(p.getEntidad());
				sEntidad= "$" + p.getEntidad().getNombre() + ": " + p.getEntidad().getNombre()  + "()\n\t\t";
			}
		}
		
		pseudocodigo += ")";
		
		return  sEntidad + "eval(" + pseudocodigo + d.getValor().getOperadorLogico() + d.getValor().getNombre() +")";
	}
	
	private String writeFunction() {
		String retType="";
		String pseudocodigo="";
		String drl="";
		
		for (Funcion f:vFunciones){
			
			
			if (f.getTipoDato().equals("NUMERO")) {
				retType = " long ";
			}
			else if (f.getTipoDato().equals("TEXTO")) {
				retType = " java.lang.String ";
			}
			else if (f.getTipoDato().equals("FECHA")) {
				retType = " java.util.Date ";
			}
			else if (f.getTipoDato().equals("BOOLEANO")) {
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
					retType = " long ";
				}
				else if (p.getAtributo().getTipoDato().equals("TEXTO")) {
					retType = " java.lang.String ";
				}
				else if (p.getAtributo().getTipoDato().equals("FECHA")) {
					retType = " java.util.Date ";
				}
				else if (p.getAtributo().getTipoDato().equals("BOOLEANO")) {
					retType = " boolean ";
				}
				
				pseudocodigo += retType + p.getNombre();
			}
			
			pseudocodigo += ") {\n";
			pseudocodigo += "/*\n" + f.getDescripcion() + "\n*/";
			pseudocodigo += "\n" + f.getCuerpo() + "\n}";
			
			drl=drl + pseudocodigo;
			pseudocodigo="";
			
		}
		
		log.info("Funciones: " + drl);
				
		
		return drl;
	}
		
	private String ruleWrapper(TablaDecision t)throws Exception {
			
		String regla="";
		
		
		for (FilaTablaDecision f: t.getFilas()){
			if (!filasGeneradas.contains(f)){
				regla = regla + writeRule(t.getNombre(), f);
				filasGeneradas.add(f);
			}
					
		}
		
		return regla; 
		
	}
	
	@End(beforeRedirect=true)
	public String volver() {
		return "/tablaDecisionList.seam";
	}
}
