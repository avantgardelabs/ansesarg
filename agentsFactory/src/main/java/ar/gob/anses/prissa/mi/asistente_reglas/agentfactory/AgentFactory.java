package ar.gob.anses.prissa.mi.asistente_reglas.agentfactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
import ar.gob.anses.prissa.mi.asistente_reglas.wrapper.WrapperRulesFactory;

public class AgentFactory {

	@Logger
	private Log log;



	private TablaDecision regla;

	private void generarPOM(String nombre, String workspace) throws Exception {
		String contenido = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n"
				+ " xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\"> \n"
				+ " <modelVersion>4.0.0</modelVersion> \n"
				+ " <parent> \n"
				+ "      <groupId>ar.gov.anses.prissa.asistente2.server</groupId> \n"
				+ "      <artifactId>knowledge-agents</artifactId> \n"
				+ "      <version>1.0</version> \n"
				+ "  </parent> \n"
				+ "  <groupId>ar.gov.anses.prissa.asistente2.server.agentes</groupId> \n"
				+ "  <artifactId>"
				+ nombre
				+ "</artifactId> \n"
				+ "  <packaging>jar</packaging> \n"
				+ "  <name>"
				+ nombre
				+ "</name> \n"
				+ "  <dependencies> \n"
				+ "      <dependency> \n"
				+ "          <groupId>ar.gov.anses.prissa.asistente2.server.agents</groupId> \n"
				+ "          <artifactId>abstract-agent</artifactId> \n"
				+ "          <version>1.0</version> \n"
				+ "          <scope>provided</scope> \n"
				+ "      </dependency> \n"
				+"	<dependency> \n"
				+"		<groupId>ar.gov.anses.prissa.asistente2</groupId> \n"
				+"		<artifactId>ejemplo-modelo</artifactId> \n"
				+"		<version>1.0</version> \n"
				+"		<scope>provided</scope> \n"
				+"	</dependency>\n"
				+ "  </dependencies> \n"
				+ " </project> ";
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			fw = new FileWriter(workspace + "/" + nombre + "/pom.xml");
			pw = new PrintWriter(fw);
			pw.print(contenido);
		} catch (IOException e) {
			log.error(e);
			throw new Exception(
					"Error al intentar crear el archivo POM. Error:"
							+ e.getMessage());
		} finally {
			pw.close();
			fw.close();
		}

	}

	private void generarInterface(String nombreAgente, String path)
			throws Exception {
		String contenido = "package ar.gov.anses.prissa.asistente2.server.agentes."
				+ nombreAgente
				+ ";\n"
				+ "import javax.xml.ws.Holder; \n"
				+ " import ar.gov.anses.prissa.asistente2.messages.Report; \n"
				+ " import ar.gov.anses.prissa.asistente2.server.agents.RuleAgent; \n"
				+ "  \n"
				+ "public interface "
				+ nombreAgente
				+ " extends RuleAgent {  \n"
				+ "    Report execute(Holder<" + nombreAgente + "Facts>  param); \n" + "}";

		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			fw = new FileWriter(path + "/" + nombreAgente + ".java");
			pw = new PrintWriter(fw);
			pw.print(contenido);
		} catch (IOException e) {
			log.error(e);
			throw new Exception(
					"Error al intentar crear la interface del archivo  "
							+ nombreAgente + ".java. Error:" + e.getMessage());
		} finally {

			pw.close();
			fw.close();
		}
	}

	private void generarBean(String dominio, String nombreAgente, String path)
			throws Exception {
		String contenido = "package ar.gov.anses.prissa.asistente2.server.agentes."
				+ nombreAgente
				+ ";\n"
				+ " import javax.ejb.Stateless; \n"
				+ " import javax.jws.WebMethod; \n"
				+ " import javax.jws.WebParam; \n"
				+ " import javax.jws.WebResult; \n"
				+ " import javax.jws.WebParam.Mode; \n"
				+ " import javax.jws.WebService; \n"
				+ " import javax.xml.ws.Holder; \n"
				+ " \n"
				+ "import org.jboss.wsf.spi.annotation.WebContext; \n"
				+ "\n"
				+ "import ar.gov.anses.prissa.asistente2.messages.Report; \n"
				+ "import ar.gov.anses.prissa.asistente2.server.agents.RuleAgentBean; \n"
				+ "\n"
				+ " @Stateless \n"
				+ " @WebService \n"
				+ " @WebContext(contextRoot = " + nombreAgente + "Bean.WEB_CONTEXT_ROOT,  \n"
				+ "            urlPattern = " + nombreAgente + "Bean.WEB_CONTEXT_URL_PATTERN) \n"
				+ " public class " + nombreAgente + "Bean extends RuleAgentBean implements " + nombreAgente + " { \n"
				+ "\n"
				+ " private static final String DOMAIN = \""
				+ dominio
				+ "\"; \n"
				+ "\n"
				+ "private static final String NAME = \""
				+ nombreAgente
				+ "\"; \n"
				+ "\n"
				+ "protected static final String WEB_CONTEXT_ROOT = \"/\" + DOMAIN "
				+ " + NAME; \n"
				+ "\n"
				+ "protected static final String WEB_CONTEXT_URL_PATTERN = \"/\" + NAME; \n"
				+ "\n"
				+ " @WebMethod(action = \"execute\") \n"
				+ " @WebResult(name = \"report\") \n"
				+ " public Report execute(@WebParam(name = \"facts\", mode =  Mode.INOUT)  \n"
				+ "                     Holder<"
				+ nombreAgente
				+ "Facts> facts) { \n"
				+ "\n"
				+ "     return super.executeImpl(facts); \n"
				+ " } \n"
				+ "\n"
				+ " @Override \n"
				+ " public String getDomain() { \n"
				+ "\n"
				+ "   return DOMAIN; \n"
				+ " } \n"
				+ "\n"
				+ " @Override \n"
				+ " public String getName() { \n"
				+ "\n"
				+ " return NAME; \n"
				+ " } \n"
				+ "\n"
				+ " @Override \n"
				+ " public String getPath() { \n"
				+ "\n"
				+ " return WEB_CONTEXT_ROOT + WEB_CONTEXT_URL_PATTERN; \n"
				+ " } \n" + " }";

		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			fw = new FileWriter(path + "/" + nombreAgente + "Bean.java");
			pw = new PrintWriter(fw);
			pw.print(contenido);
		} catch (IOException e) {
			log.error(e);
			throw new Exception(
					"Error al intentar crear la interface del archivo  "
							+ nombreAgente + ".java. Error:" + e.getMessage());
		} finally {
			pw.close();
			fw.close();
		}
	}

	private void generarControllerMBean(String nombreAgente, String path)
			throws Exception {
		String contenido = "package ar.gov.anses.prissa.asistente2.server.agentes."
				+ nombreAgente
				+ ";\n"
				+ "\n"
				+ "import javax.ejb.EJB;\n"
				+ "import org.jboss.annotation.ejb.Management;\n"
				+ "import org.jboss.annotation.ejb.Service;\n"
				+ "import ar.gov.anses.prissa.asistente2.server.agents.RuleAgent;\n"
				+ "import ar.gov.anses.prissa.asistente2.server.agents.RuleAgentController;\n"
				+ "import ar.gov.anses.prissa.asistente2.server.agents.RuleAgentControllerMBean;\n"
				+ "\n"
				+ "@Service\n"
				+ "@Management(RuleAgentController.class)\n"
				+ "public class "
				+ nombreAgente
				+ "ControllerMBean extends RuleAgentControllerMBean {\n"
				+ "    @EJB\n"
				+ "    private "
				+ nombreAgente
				+ " agent;\n"
				+ "\n"
				+ "     @Override\n"
				+ "    public RuleAgent getRuleAgent() {\n"
				+ "       return this.agent;\n" + "     }\n" + "}";

		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			fw = new FileWriter(path + "/" + nombreAgente
					+ "ControllerMBean.java");
			pw = new PrintWriter(fw);
			pw.print(contenido);
		} catch (IOException e) {
			log.error(e);
			throw new Exception(
					"Error al intentar crear el ControllerMBean del archivo  "
							+ nombreAgente + "ControllerMBean.java. Error:"
							+ e.getMessage());
		} finally {
			pw.close();
			fw.close();
		}
	}

	private String generarEntidades(TablaDecision tabla) {
		String cadena = "";
		WrapperRulesFactory wrapper = new WrapperRulesFactory(tabla);
		wrapper.setLog(log);
		wrapper.buildRule();
		Set<Atributo> listaTemp = wrapper.getAtributosInvolucrados();

		ArrayList<Entidad> listaEntidades = new ArrayList();
		
		for (Atributo a:listaTemp){
			if (!listaEntidades.contains(a.getEntidad())){
				listaEntidades.add(a.getEntidad());
				log.info("Entidad: " + a.getEntidad().getDescripcion() );
			}
		}
		
		for (Entidad e : listaEntidades) {
			cadena += "private " + e.getNombre() + " "
					+ e.getNombre() + ";" + "\n"
					+ " public " + e.getNombre() + " get"
					+ e.getNombre() + "() { " + " return "
					+ e.getNombre() + "; }\n";

			cadena += " public void set" + e.getNombre() + "("
					+ e.getNombre() + " valor) { " + " this."
					+ e.getNombre() + "=valor;}\n";

		}

		return cadena;

	}

	private void generarFacts(TablaDecision tabla, String nombreAgente,
			String path) throws Exception {
		String contenido = "" +
				"	package ar.gov.anses.prissa.asistente2.server.agentes."
				+ nombreAgente
				+ ";\n"
				+ "\n"
				+ "import ar.gov.anses.prissa.asistente2.server.agents.RuleAgentFacts;\n"
				+ "\n"
				+ "import ar.gov.anses.prissa.asistente.modelosemantico.*;\n"
				+ "\n" 
				+ "public class "
				+ nombreAgente
				+ "Facts extends RuleAgentFacts {\n"
				+ "\n"
				+ generarEntidades(tabla) + "}";

		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			fw = new FileWriter(path + "/" + nombreAgente + "Facts.java");
			pw = new PrintWriter(fw);
			pw.print(contenido);
		} catch (IOException e) {
			log.error(e);
			throw new Exception(
					"Error al intentar crear el Facts del archivo  "
							+ nombreAgente + "Facts.java. Error:"
							+ e.getMessage());
		} finally {
			pw.close();
			fw.close();
		}
	}

	private void generarDevProperties(String nombreAgente, String workspace)
			throws Exception {
		String contenido = " ";

		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			fw = new FileWriter(workspace + "/" + nombreAgente
					+ "/src/main/config/dev.properties");
			pw = new PrintWriter(fw);
			pw.print(contenido);
		} catch (IOException e) {
			log.error(e);
			throw new Exception("Error al crear el dev.properties");
		} finally {
			pw.close();
			fw.close();
		}
	}

	public void generarChangeSet(String nombreAgente, String workspace)
			throws Exception {
		String contenido = " <?xml version='1.0' encoding='UTF-8' ?>\n"
				+ " <change-set xmlns='http://drools.org/drools-5.0/change-set'\n"
				+ "             xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'\n"
				+ " xs:schemaLocation='http://drools.org/drools-5.0/change-set' >\n"
				+ "   <add>\n" + "     <resource source=\"${host.url}/" +nombreAgente +".drl\" \n"
				+ "  type=\"DRL\"/>\n" + "   </add>\n" + " </change-set>";

		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			fw = new FileWriter(workspace + "/" + nombreAgente
					+ "/src/main/resources/META-INF/change-set.xml");
			pw = new PrintWriter(fw);
			pw.print(contenido);
		} catch (IOException e) {
			log.error(e);
			throw new Exception("Error el change-set");
		} finally {
			pw.close();
			fw.close();
		}
	}

	public void generar(TablaDecision tabla, String workspace) throws Exception {
		regla = tabla;
		String nombreTmp = tabla.getDominio().getDescripcion()
				.replaceAll(" ", "")
				+ tabla.getNombre().replaceAll(" ", "");
		
		String nombreAgente=nombreTmp.replaceAll("[^A-Za-z_0-9]","");
	
				
				
		log.info("Generando el agente: " + nombreAgente);
		String path = workspace + "/" + nombreAgente
				+ "/src/main/java/ar/gov/anses/prissa/asistente2/agentes/"
				+ nombreAgente;
		File f = new File(path);
		f.mkdirs();
		f = new File(workspace + "/" + nombreAgente + "/src/main/config");
		f.mkdirs();

		f = new File(workspace + "/" + nombreAgente
				+ "/src/main/resources/META-INF");
		f.mkdirs();

		generarPOM(nombreAgente, workspace);
		generarInterface(nombreAgente, path);
		generarBean(tabla.getDominio().getDescripcion().replaceAll(" ", ""),
				nombreAgente, path);
		generarControllerMBean(nombreAgente, path);
		generarFacts(tabla, nombreAgente, path);
		generarDevProperties(nombreAgente, workspace);
		generarChangeSet(nombreAgente, workspace);
	}
	
	public void generarSVNFile(String svnServer,String workspace) { 
		
		String nombreTmp = regla.getDominio().getDescripcion()
				.replaceAll(" ", "")
				+ regla.getNombre().replaceAll(" ", "");
		
		String nombreAgente=nombreTmp.replaceAll("[^A-Za-z_0-9]","");
		Format formatter = new SimpleDateFormat("yyyymmddHHmmss");
		String version = formatter.format(new Date());
		String path = workspace + "/" + nombreAgente + "/svnLoader.sh";
		
		String commands=" " +
				"	svn update \n" 
				+ " svn copy . " + svnServer + "/tags/" + nombreAgente + "/" + version + " \n";
		
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			fw = new FileWriter(path);
			pw = new PrintWriter(fw);
			pw.print(commands);
		} catch (IOException e) {
			log.error(e);
			
		} finally {
			pw.close();
			try {
				fw.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
		
		
	
		
		
	}

	public AgentFactory() {

	}

	public Log getLog() {
		return log;
	}

	public void setLog(Log log) {
		this.log = log;
	}

}
