package ar.gob.anses.prissa.mi.asistente_reglas.seam.action.cq;

import java.util.Date;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.log.Log;

@Name("quartzObserver")
public class AnclajeQuartzObserver {
	
	 /** The log. */
	@Logger
	Log log;

    @In(create=true) AnclajeQuartzTask quartzTask;
    @SuppressWarnings("unused")

    @Observer("org.jboss.seam.postInitialization")
    public void observe(){
    	String cron;
        try{
        	//cada 2 segundos
        	if(PropertiesHelper.getInstance().getProperties("cq") == null){
				log.error("No se pudo obtener la variable 'cron.anclaje'. Se usa por defecto '0 0 12 * * ?' todos los dias a las 12 del mediodia");
				cron = "0 0 12 * * ?";
			}else{
				cron = PropertiesHelper.getInstance().getProperties("cq").getProperty("cron.anclaje");
			}
        	QuartzTriggerHandle handle = quartzTask.performTask(new Date(), cron, null);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}