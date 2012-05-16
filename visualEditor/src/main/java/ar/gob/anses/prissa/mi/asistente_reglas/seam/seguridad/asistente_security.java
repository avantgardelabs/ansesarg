package ar.gob.anses.prissa.mi.asistente_reglas.seam.seguridad;

import java.io.IOException;
import java.io.StringReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;
import org.jboss.seam.servlet.ContextualHttpServletRequest;
import org.jboss.seam.util.Base64;
import org.jboss.seam.web.AbstractResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Usuario;

@BypassInterceptors
@Scope(ScopeType.APPLICATION)
@Name("securitySrv")
public class asistente_security extends AbstractResource{

	@Logger
	Log log;
	
	@Override
	public void getResource(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		
		new ContextualHttpServletRequest(request){
			@Override
			public void process() throws IOException
			{
				doWork(request,response);
			}
		}.run();
		
	}

	@Override
	public String getResourcePath() {
		
		return "/securitySrv";
	}
	
	private void doWork(HttpServletRequest request, HttpServletResponse response) {
		
		String sToken = request.getParameter("token");
		Usuario oUsuario = new Usuario();

		oUsuario.setToken(sToken);
		
		Contexts.getSessionContext().set("user", oUsuario);

		Identity.instance().login();
        
        if (Identity.instance().isLoggedIn())
			try {
					response.sendRedirect("../../home.seam");				
			} catch (IOException e1) {
				log.info("Error=" + e1.getMessage());
			}
		else
			try {
				log.info("Acceso Denegado");
				response.sendRedirect("../../acceso_denegado.seam");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
}
