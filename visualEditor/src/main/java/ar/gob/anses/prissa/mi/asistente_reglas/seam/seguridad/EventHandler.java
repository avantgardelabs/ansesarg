package ar.gob.anses.prissa.mi.asistente_reglas.seam.seguridad;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.Log;
import org.jboss.seam.ScopeType;
import org.jboss.seam.security.Identity;

@Name("org.jboss.seam.security.facesSecurityEvents")
@Scope(ScopeType.APPLICATION)
@Install(precedence = Install.APPLICATION, classDependencies = "javax.faces.context.FacesContext")
@BypassInterceptors
@Startup
public class EventHandler extends org.jboss.seam.security.FacesSecurityEvents {
	
	@Logger
	Log log;
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 4743367273945458180L;

	@Override @Observer(Identity.EVENT_LOGIN_SUCCESSFUL)
    public void addLoginSuccessfulMessage() {
    }    
	
}