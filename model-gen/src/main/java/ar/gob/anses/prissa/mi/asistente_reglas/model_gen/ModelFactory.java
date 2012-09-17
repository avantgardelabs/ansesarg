package ar.gob.anses.prissa.mi.asistente_reglas.model_gen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;

@Name("modelFactory")
@AutoCreate
public class ModelFactory {

	
	private EntityManager entityManager;
	
	private Log log; 
	private Entidad entidad; 
	
	
	private String path;
	
	
	

	public static ModelFactory createFactory(){
		return new ModelFactory();
	}
	
	
	
	
	private String getHeader() { 
		String result="\n package ar.gov.anses.prissa.asistente.modelosemantico;\n" +
				"import java.util.Date;\n";
		
		return result;
	}
	
	private String getClassName(String name){ 
		String result="";
		
		result+="public class " + name + " { ";
		return result; 
		
	}
	
	private String getClassProperties(Entidad entidad) { 
		String resultado="";
		
		for (Atributo a:entidad.getAtributos()){
			resultado+="\n private " + getTipoDato(a.getTipoDato()) + " " + a.getNombre()  +";\n\n" ;
			resultado +=getAccessors(a);
		}
		
		return resultado;
		
	}
	
	private String getAccessors(Atributo a) {
		String resultado="";
		String atributo=a.getNombre().substring(0,1);
		atributo=atributo.toUpperCase() + a.getNombre().substring(1);
		resultado+="public " + getTipoDato(a.getTipoDato()) + " get" + atributo + "() { return " + a.getNombre() + "; }";
		resultado+="\npublic void set" + atributo + "(" + getTipoDato(a.getTipoDato()) + " valor) { " + a.getNombre() + "=valor;}\n";
		return resultado;
		
	}
	
	private String getTipoDato(String tipoDato){
		
		String tipo="";
		
		if (tipoDato.equals("TEXTO"))
			tipo= "String";
		
		if (tipoDato.equals("NUMERO"))
			tipo= "Double";
		
		
		if (tipoDato.equals("BOOLEANO"))
			tipo= "Boolean";
		
		
		if (tipoDato.equals("FECHA"))
			tipo= "Date";
		
		
		return tipo;
		
	}
	
	public void generate()  {
		log.info("Generando el modelo de dominio");
		String src=path + "/modeldominio/src/main/java/ar/gov/anses/prissa/asistente/modelosemantico";
		log.info("Source Paht: " + src);
		File f=new File(src);
		f.mkdirs();
		
		FileWriter clase=null;
		PrintWriter pw=null;
		String datosArchivo= getHeader();
		
		List<Entidad> listaEntidades = (List<Entidad>)entityManager.createQuery("select e from Entidad as e").getResultList();
		
		for (Entidad e:listaEntidades){
			datosArchivo=getHeader() + getClassName(e.getNombre()) + "\n\n";
			datosArchivo+=getClassProperties(e) + "}";
			try {
				clase = new FileWriter(src + "/" + e.getNombre() + ".java");
				pw=new PrintWriter(clase);
				pw.print(datosArchivo);
			} catch (IOException e1) {
				log.error(e1);
			} finally{
				try {
					clase.close();
					log.info("Se genero la clase: " + e.getNombre());
					pw.close();
				} catch (IOException e1) {
					log.error(e1);
				}
			}
			
			
			
		}
		
		generatePOM();
		
		
	}
	
	private void generatePOM() { 
		String src=path + "/modeldominio";
		
		String pom="<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n"+
				"xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\"> \n" +
				"\t\t<modelVersion>4.0.0</modelVersion>\n" +
				"	<groupId>ar.gov.anses.prissa.asistente</groupId>\n" +
				"	<artifactId>modelosemantico</artifactId>\n" +
				"   <version>1.0</version>" + 
				"	<packaging>jar</packaging>\n" +
				"	<name>Model</name>\n" +
				"</project>";
		
		FileWriter clase=null;
		PrintWriter pw=null;
		try {
			clase = new FileWriter(src +"/pom.xml");
			pw=new PrintWriter(clase);
			pw.print(pom);
		} catch (IOException e) {
			log.error(e);
		}finally { 
			try {
				clase.close();
				pw.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
		
		
		
	}
	
	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}


	public EntityManager getEntityManager() {
		return entityManager;
	}


	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}


	public Log getLog() {
		return log;
	}


	public void setLog(Log log) {
		this.log = log;
	}
	
	

	
}
