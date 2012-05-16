package ar.gob.anses.prissa.mi.asistente_reglas.seam.action.cq;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.log.Log;

public class PropertiesHelper {
	
	private static PropertiesHelper instance = null;
	
	private Properties properties;
	
	@Logger
	Log log;
	
	public Properties getProperties(String name) {
		name = name + ".properties";
		if(properties == null){
			URL url = getClass().getResource(name);
			if (url != null) {
				try {
					InputStream in = url.openStream();
					properties = new Properties();
					properties.load(in);
				} catch (IOException ioe) {
					log.info("NO se encuentra el archivo de propiedades " + name);
					log.info("No fue posible inicializar el cron de anclaje debido a errores internos.");					
					return null;
				}
			} else {
				log.error("NO se encuentra el archivo de propiedades " + name);
				log.error("No fue posible inicializar el cron de anclaje debido a errores internos.");				
				return null;
			}
		}
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public static PropertiesHelper getInstance() {
		if (PropertiesHelper.instance == null) {
			PropertiesHelper.instance = new PropertiesHelper();
        }

        return PropertiesHelper.instance;
	}

	public static void setInstance(PropertiesHelper instance) {
		PropertiesHelper.instance = instance;
	}
	
    public PropertiesHelper() {
    	PropertiesHelper.instance = this;
    }

}
