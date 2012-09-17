package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.Properties;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.log.Log;

@Name("systemProperties")
@Scope(ScopeType.APPLICATION)
@Startup
public class SystemProperties implements Serializable {

    private static final long serialVersionUID = -4713584943017270174L;

    @Logger
	public Log log;

	public static final String KEY_CONFIG_FILE = "asistente_reglas.config.file";
	public static final String KEY_JBOSS_CONF_URL = "jboss.server.home.dir";
	public static final String KEY_USER_HOME = "user.home";
	public static final String DEFAULT_CONFIG_FILE = "asistente_reglas.properties";

	private String configFilePath = "";

	// private File configFile;

	private Properties config;

	@Create
	public void init() {
		// System.getProperties().list(System.out);
		// busco el archivo de configuracion en la siguiente variable
		configFilePath = System.getProperty(KEY_CONFIG_FILE);

		// sino existe lo busco en el directorio de jboss o home de usuario
		if (configFilePath == null) {
			String dirBase = System.getProperty(KEY_JBOSS_CONF_URL);

			// sino esta definido el directorio de jboss uso el directorio home
			if (dirBase == null) {
				dirBase = System.getProperty(KEY_USER_HOME);
			} else {
				dirBase = dirBase + File.separator + "conf";
			}

			configFilePath = dirBase + File.separator + DEFAULT_CONFIG_FILE;
		}

		// Obtengo el archivo de configuracion
		File configFile = new File(configFilePath);
		config = new Properties();
		if (!configFile.exists()) {
			// Sino existe el arhivo de configuracion usa el default del
			// classpath
			try {
				log.warn("No se encuentra archivo de configuraci�n "
						+ configFile.getAbsolutePath());
				log.warn("Se usar�n archivo de configuraci�n por defecto: "
						+ DEFAULT_CONFIG_FILE);
				InputStream is = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream(DEFAULT_CONFIG_FILE);
				Reader reader = new InputStreamReader(is);
				config.load(reader);
				log.info("Archivo de configuraci�n cargado correctamente!");
			} catch (IOException e) {
				log.error("No se puede cargar archivo de configuracion "
						+ DEFAULT_CONFIG_FILE, e);
			}
			return;
		} else {
			// cargo el archivo definido en config
			log.info("Se usa archivo de configuraci�n: "
					+ configFile.getAbsolutePath());
			try {
				Reader fin = new FileReader(configFile);
				config.load(fin);
				log.info("Archivo de configuraci�n cargado correctamente!");
			} catch (IOException e) {
				log.error("No se puede cargar archivo de configuracion "
						+ configFile.getAbsolutePath(), e);
			}
		}
		config.list(System.out);
		// log.info(this.toString());

	}


	public String getDirectorioGestorArchivosDRL() {
		String local = config.getProperty("directorio.gestor.archivo");
		return getDirectorioBase() + File.separator + local;
	}  
	
	public String getWorkSpaceAgentes() { 
		return  config.getProperty("agentes.workspace");
		
	}
   
	public String getDirectorioBase() {
		String directorioBase = config.getProperty("directorio.base",
				"asistente_reglas");
		String ret = null;
		if (!new File(directorioBase).isAbsolute()) {
			String base_home = System.getProperty("user.home");
			ret = base_home + File.separator + "asistente_reglas";
		} else {
			ret = directorioBase;
		}
		return ret;
	}

}