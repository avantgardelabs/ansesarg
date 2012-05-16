package ar.gob.anses.prissa.mi.asistente_reglas.simulador;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.drools.builder.KnowledgeBuilder;

import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.command.CommandFactory;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.EntidadFila;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.FilaTablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Funcion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.AtributoSimulado;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.action.SimulacionReglaPorLoteAction;
import ar.gob.anses.prissa.mi.asistente_reglas.excepciones.RuleException;
import ar.gob.anses.prissa.mi.asistente_reglas.wrapper.WrapperRulesFactory;
import ar.gov.anses.prissa.asistente2.messages.Message;
import ar.gov.anses.prissa.asistente2.messages.Report;

@Name("simuladorVirtual")
@Scope(ScopeType.CONVERSATION)
public class SimuladorVirtual implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7582410758818506597L;

	
	
	@Logger
	Log log;

	@In
	private SimuladorReglasPorAsistenteService simuladorReglasPorAsistenteService;

	@Out(required = false)
	private TablaDecision tablaSeleccionada;
	
	@Out(required=false)
	private Report report;

	private List<FilaTablaDecision> filasGeneradas;
	private List<EntidadFila> listaEntidades;
	private List<EntidadFila> listaEntidadesFisicas;
	private List<EntidadFila> listaEntidadesCargadas;
	private EntidadFila entidadSeleccionada;
	private String msjError;
	
	private WrapperRulesFactory factory;

	
	
	private Set<Funcion> vFunciones = new HashSet<Funcion>();

	
	public String generarRegla(TablaDecision t) throws Exception, IOException {

		tablaSeleccionada = t;
		limpiar();
		
		this.setListaEntidades(simuladorReglasPorAsistenteService
				.cargarListaEntidades(t, false));
		this.setListaEntidadesFisicas(simuladorReglasPorAsistenteService
				.cargarListaEntidades(t, true));
		
		this.entidadSeleccionada = null;
		this.listaEntidadesCargadas = new ArrayList<EntidadFila>();

		return "/simulacionVirtualPaso2ReglaPorLote.seam";

	}
	
	/**
	 * Limpia los objetos de la conversacion
	 */
	public void limpiar(){
		report=null;
		listaEntidadesCargadas= new ArrayList<EntidadFila>();
	}

	public String cargarEntidad(EntidadFila ef) {
		this.entidadSeleccionada = ef;

		return "/simulacionVirtualPaso2ReglaPorLote.seam";
	}

	public String guardar(){
		int cantidadErrores = 0;
				for(AtributoSimulado atrSim : this.entidadSeleccionada.getAtributosInvolucrados()){
					try {
						atrSim.validar();
					} catch (Exception e) {
						cantidadErrores++;
					}
				}
			if(cantidadErrores < 1){			
				this.msjError = null;
				EntidadFila nuevaEntidad = new EntidadFila();
				for(AtributoSimulado atr: this.entidadSeleccionada.getAtributosInvolucrados()){
					nuevaEntidad.getAtributosInvolucrados().add((AtributoSimulado)atr.clone());
					atr.vaciar(); 
				}
				nuevaEntidad.setEntidad(this.entidadSeleccionada.getEntidad());
				
				this.listaEntidadesCargadas.add(nuevaEntidad);
				//this.listaEntidades.remove(this.entidadSeleccionada);
				
				borrarTablaDetalles();
				
			}else if(cantidadErrores == 1){
				this.msjError = "Un atributo con error, favor de corregirlo";
			}else{
				this.msjError = "Varios atributos con error, favor de corregirlos";
			}
		

			
			return "/simulacionVirtualPaso2ReglaPorLote.seam";  
	}

	public void simular() throws Exception {

		factory = new WrapperRulesFactory(tablaSeleccionada);
		factory.setLog(log);
		String rules = factory.buildRule();
		
		String encabezado = generarEncabezadoReglaParaSimular("ar.gov.anses.prissa.rules");
		String cuerpo = generarCuerpoReglaParaSimular("");
		
		

		/*log.info(encabezado);
		log.info(cuerpo);*/
		
		String finalDrl="" +
				"	\n" +
				"	";
		finalDrl = finalDrl+encabezado+cuerpo+rules;
		
		log.info(finalDrl);
		
	
		encenderMotor(finalDrl);
		
		

	}

	public String editarEntidadCargada(EntidadFila ef) {
		this.entidadSeleccionada = ef;
		this.listaEntidadesCargadas.remove(ef);
		return "/simulacionVirtualPaso2ReglaPorLote.seam";
	}

	public String eliminarEntidadCargada(EntidadFila ef) {
		this.listaEntidadesCargadas.remove(ef);

		for (AtributoSimulado at : ef.getAtributosInvolucrados()) {
			at.vaciar();
		}
		return "/simulacionVirtualPaso2ReglaPorLote.seam";
	}

	public void borrarTablaDetalles() {
		this.entidadSeleccionada = null;
	}

	public String generarEncabezadoReglaParaSimular(String paquete) {
		SimpleDateFormat sdf = new SimpleDateFormat(
				"dd MMMMM yyyy ' a las ' hh:mm ");
		String encabezadoRegla = "#Creado el " + sdf.format(new Date());
		if (paquete.isEmpty()) {
			encabezadoRegla = "package "
					+ this.getClass().getPackage().getName() + ";\n";
		} else {
			encabezadoRegla = "package " + paquete + ";\n";
		}
		
		encabezadoRegla += "import java.util.HashMap;\n" +
							"import ar.gob.anses.prissa.mi.asistente_reglas.excepciones.RuleException;\n\n" +
							"global  org.jboss.seam.log.Log log;\n" +
							"global ar.gov.anses.prissa.asistente2.messages.Report reporte;\n\n";
		// Para cada entidad hacemos la declaracion de sus atributos
		/*for (EntidadFila enti : this.listaEntidades) {
			encabezadoRegla += "declare " + enti.getEntidad().getNombre()
					+ " \n";
			encabezadoRegla += escribirDeclaracionEntidad(enti);
			encabezadoRegla += "end\n";
		}*/
		
		for (Entidad enti : factory.getEntidades()) {
			encabezadoRegla += "declare " + enti.getNombre()
					+ " \n";
			encabezadoRegla += escribirDeclaracionEntidad(enti);
			encabezadoRegla += "end\n";
		}
		
		

		return encabezadoRegla;
	}

	public String escribirDeclaracionEntidad(Entidad enti) {
		String declaracion = "";
		// Listado de los atributos en formato 'nombre' : 'tipoDato'
		for (Atributo atr : enti.getAtributos()) {
			declaracion += "\t\t" + atr.getNombre() + " : ";

			if (atr.getTipoDato().equals("NUMERO")) {
				declaracion += " Double  \n";
			} else if (atr.getTipoDato().equals("TEXTO")) {
				declaracion += " String \n";
			} else if (atr.getTipoDato().equals("FECHA")) {
				declaracion += " java.util.Date \n";
			} else if (atr.getTipoDato().equals("BOOLEANO")) {
				declaracion += " Boolean \n";
			}

		}

		return declaracion;
	}

	public String generarCuerpoReglaParaSimular(String nombreRegla) {
		int cantEntidadesCargadas = this.listaEntidadesCargadas.size();
		String cuerpoRegla = "";
		if (!nombreRegla.isEmpty()) {
			cuerpoRegla = "\nrule \"" + nombreRegla + "\"\n";
		} else {
			cuerpoRegla = "\nrule \"" + this.tablaSeleccionada.getNombre()
					+ "\"\n";
		}
		//cuerpoRegla += "\tdialect \"java\"\n";
		cuerpoRegla += "\twhen\n" +
				"\t\t$ini : HashMap()\n " +
				"	\tthen \n";
		for (EntidadFila enti : this.listaEntidadesCargadas) {
			cuerpoRegla += "\t\t" + escribirInstanciaEntidad(enti, "instEntidad"
					+ cantEntidadesCargadas);
			cantEntidadesCargadas--;
		}
		
		
		
		for (Entidad e:factory.getEntidades()){
			boolean encontrado=false;
			for (EntidadFila enti:this.listaEntidades){
				log.debug("\n");
				log.debug("Entidad1: " + e.getNombre() + " .... Comparar con: " + enti.getEntidad().getNombre());
				if(e.equals(enti.getEntidad())){
					encontrado=true;
					log.debug("Hemos encontrado coincidencia con la entidad:" + e.getNombre());
					break;
				}
			}
			
			log.debug(" Salimos del for");
			
			if (!encontrado){
				cuerpoRegla += "\t\t insert(new " + e.getNombre() + "());\n";
			}
		}
		
		
		cuerpoRegla += "\t\tretract($ini);\n";
		cuerpoRegla += "end\n";
		return cuerpoRegla;
	}

	public String escribirInstanciaEntidad(EntidadFila enti,
			String nombreEntidad) {
		String retorno = "";
		// Creo una instancia de la clase en la variable instEntidad
		retorno += enti.getEntidad().getNombre() + " " + nombreEntidad
				+ " = new " + enti.getEntidad().getNombre() + "();\n";
		// Le agrego valor a los atributos de esta entidad
		for (AtributoSimulado atr : enti.getAtributosInvolucrados()) {
			
			if (atr.getAtributo().getTipoDato().equals("TEXTO")){
				retorno += nombreEntidad + ".set"
				+ camelizar(atr.getAtributo().getNombre()) + "(\""
				+ atr.getValor() + "\");\n";
			}else if(atr.getAtributo().getTipoDato().equals("BOOLEANO") ){
				if (((String)atr.getValor()).equals("VERDADERO")){
					retorno += nombreEntidad + ".set"
					+ camelizar(atr.getAtributo().getNombre()) + "("
					+  "true" + ");\n";
				}
				else{
					retorno += nombreEntidad + ".set"
					+ camelizar(atr.getAtributo().getNombre()) + "("
					+  "false" + ");\n";
				}
					
			}else if  (atr.getAtributo().getTipoDato().equals("FECHA")){
				retorno += nombreEntidad + ".set"
						+ camelizar(atr.getAtributo().getNombre()) + "("
						+ "new java.util.Date( Long.parseLong(\"" + atr.getCurrentLiteralFecha().getTime() +"\"))"+ ");\n";
			}
			else{
				retorno += nombreEntidad + ".set"
				+ camelizar(atr.getAtributo().getNombre()) + "("
				+  atr.getValor() + ");\n";
			}
			
			
		}

		retorno += "\t\tinsert(" + nombreEntidad + ");\n";

		return retorno;
	}

	public String camelizar(String cadena) {
		char primerCaracter = Character.toUpperCase(cadena.charAt(0));
		cadena = cadena.substring(1);
		cadena = primerCaracter + cadena;

		return cadena;
	}

	public void setListaEntidades(List<EntidadFila> listaEntidades) {
		this.listaEntidades = listaEntidades;
	}

	public List<EntidadFila> getListaEntidades() {
		return listaEntidades;
	}

	public List<Atributo> getTodosLosAtributosInvolucrados() {
		List<Atributo> retorno = new ArrayList<Atributo>();

		return retorno;
	}

	public void setEntidadSeleccionada(EntidadFila entidadSeleccionada) {
		this.entidadSeleccionada = entidadSeleccionada;
	}

	public EntidadFila getEntidadSeleccionada() {
		return entidadSeleccionada;
	}

	public void setListaEntidadesCargadas(
			List<EntidadFila> listaEntidadesCargadas) {
		this.listaEntidadesCargadas = listaEntidadesCargadas;
	}

	public List<EntidadFila> getListaEntidadesCargadas() {
		return listaEntidadesCargadas;
	}

	public void setMsjError(String msjError) {
		this.msjError = msjError;
	}

	public String getMsjError() {
		return msjError;
	}

	public void setListaEntidadesFisicas(List<EntidadFila> listaEntidadesFisicas) {
		this.listaEntidadesFisicas = listaEntidadesFisicas;
	}

	public List<EntidadFila> getListaEntidadesFisicas() {
		return listaEntidadesFisicas;
	}

	public TablaDecision getTablaSeleccionada() {
		return tablaSeleccionada;
	}

	public void setTablaSeleccionada(TablaDecision tablaSeleccionada) {
		this.tablaSeleccionada = tablaSeleccionada;
	}
	
	private void encenderMotor(String rule) throws Exception{
		
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		//kbuilder.add(ResourceFactory.newClassPathResource("Sample.drl"), ResourceType.DRL);
		//kbuilder.add(ResourceFactory.newClassPathResource("Ramirito.drl"), ResourceType.DRL);
		//kbuilder.add(ResourceFactory.newClassPathResource("Prueba.drl"), ResourceType.DRL);
		//kbuilder.add(ResourceFactory.newClassPathResource("Declare.drl"), ResourceType.DRL);
		
		String Regla = "package com.sample \n " + 
			"rule 'fake'\n " + 
			"no-loop true\n " +
		    "when\n "+ 
		"eval(true)\n " +
		"then\n " +
		"System.out.println(\"Se ejecuto la regla fake\");\n"+
		"end";
		
		kbuilder.add(ResourceFactory.newByteArrayResource(rule.getBytes()), ResourceType.DRL);
		
		//kbuilder.add(ResourceFactory.newUrlResource("http://10.2.16.43:8080/jboss-brms/org.drools.guvnor.Guvnor/package/ar.org.anses.prissa.reglas/LATEST.drl"), ResourceType.DRL);
		KnowledgeBuilderErrors errors = kbuilder.getErrors();
		if (errors.size() > 0) {
			for (KnowledgeBuilderError error: errors) {
				log.error(error);
			}
			throw new IllegalArgumentException("Could not parse knowledge.");
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		
		
		//StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
		
		org.drools.runtime.StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
		
		//KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
		// go !
		
		 report = new Report();
				
		
		ksession.setGlobal("reporte", report);
		ksession.setGlobal("log", log);
		//ksession.fireAllRules();
		
		try {
			ksession.execute(new HashMap());
			
			if (report.getMessages().size() == 0)
				report.addMessage("0").addDescription("traza","No se ejecuto ninguna regla.");
			
		}catch(Exception re){
			report.addMessage("0").addDescription("traza","Ocurrio el siguiente error: " + re.getMessage());
		}
		
		
//		ksession.execute(Arrays.asList( new Object[] { oPer } ));
		
		//logger.close();
		
		
		
	}

}
