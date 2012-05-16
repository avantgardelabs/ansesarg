package ar.gob.anses.prissa.mi.asistente_reglas.seam.seguridad;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Properties;

import javax.faces.FacesException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Log;
import org.jboss.seam.util.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Usuario;

@Name("autenticador")
public class Autenticador
{
	@Logger Log log;

    private String paginaInicio;
    
    public boolean autenticar()
    {
    	Usuario oUsuario= (Usuario) Contexts.getSessionContext().get("user");

    	String sToken = oUsuario.getToken();
    	log.info("sToken=" + sToken);
    	boolean bRespuesta = false;
		String sResultadoToken;
		
		
		try
		{
			sResultadoToken = new String(Base64.decode(sToken));;
		}
		catch (Exception e) {
			sResultadoToken = null;
		}

		if (sResultadoToken != null)
		{
			log.info("Empiezo a cargar el objeto Usuario");
	    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    	Document dom;
	    
	    	try {
	    		
				DocumentBuilder db = dbf.newDocumentBuilder();
				log.info("1");
				dom = db.parse((new InputSource(new StringReader(sResultadoToken.trim()))));
				log.info("2");
				Element docEle = dom.getDocumentElement();
				NodeList oNodoList = docEle.getElementsByTagName("login");
				log.info("3");
				if(oNodoList != null && oNodoList.getLength() > 0) {
					for(int i = 0 ; i < oNodoList.getLength();i++) {

						Element el = (Element)oNodoList.item(i);

						oUsuario.setUsername(el.getAttribute("username"));
						oUsuario.setMetodoAutenticacion(el.getAttribute("authmethod"));
						
					}
				}
				
				oNodoList = docEle.getElementsByTagName("info");
				
				if(oNodoList != null && oNodoList.getLength() > 0) {
					for(int i = 0 ; i < oNodoList.getLength();i++) {
						
						try
						{
						
							Element el = (Element)oNodoList.item(i);
							
							String sNodo = "nombre";
							if (el.getAttribute("name").trim().equals(sNodo))
							{
								log.info("Entro nodo nombre");
								if (el.getAttribute("value")!= null)
									oUsuario.setNombre(el.getAttribute("value"));
								else
									oUsuario.setNombre(null);
							}
							
							sNodo = "email";
							if (el.getAttribute("name").trim().equals(sNodo))
							{
								log.info("Entro nodo email");
								if (el.getAttribute("value")!= null)
									oUsuario.setEmail(el.getAttribute("value"));
								else
									oUsuario.setEmail(null);
							}
							
							sNodo = "oficina";
							if (el.getAttribute("name").trim().equals(sNodo))
							{
								log.info("Entro nodo oficina");
								if (el.getAttribute("value")!= null)
									oUsuario.setOficina(el.getAttribute("value"));
								else
									oUsuario.setOficina(null);
							}
							
							sNodo = "oficinadesc";
							if (el.getAttribute("name").trim().equals(sNodo))
							{
								log.info("Entro nodo oficinadesc");
								/* TODO: da el siguiente mensaje de error "
								 * (1406) The COM Transaction Integrator LU 6.2 transport failed to 
								 * allocate a conversation.  Verify that host LU CICS2    is active, 
								 * and that the Remote Environment specifies the SNA mode expected 
								 * by th...."*/
								if (el.getAttribute("value")!= null)
								{
									//oUsuario.setOficinaDesc(el.getAttribute("value"));
									oUsuario.setOficinaDesc("Valor asignado por mi");
								}
								else
									oUsuario.setOficinaDesc(null);
							}
							
							sNodo = "ip";
							if (el.getAttribute("name").trim().equals(sNodo))
							{
								log.info("Entro nodo ip");
								if (el.getAttribute("value")!= null)
									oUsuario.setIp(el.getAttribute("value"));
								else
									oUsuario.setIp(null);
							}
							
						}
						catch (Exception ex)
						{
							log.info("Ocurrio el siguiente error: #0",ex.getMessage());
						}
						
					}
				}
				
				log.info("Saco información del nodo group");
				oNodoList = docEle.getElementsByTagName("group");
				
				if(oNodoList != null && oNodoList.getLength() > 0) {
					for(int i = 0 ; i < oNodoList.getLength();i++) {

						Element el = (Element)oNodoList.item(i);

						oUsuario.setRol(el.getAttribute("name"));

					}
				}
				
			}catch(ParserConfigurationException pce)
			{ 
				//NADA
			}
		    catch(IOException ioe) {
		    	//NADA
			}
		    catch(SAXException se) {
		    	//NADA
			}
		    catch(Exception e) {
		    	//NADA
		    }
			
	        log.info("autenticando usuario: #0", oUsuario.getUsername());
	        log.info("autenticando nombre: #0", oUsuario.getNombre());
	        log.info("autenticando metodo de autenticacion: #0", oUsuario.getMetodoAutenticacion());
	        log.info("autenticando email: #0", oUsuario.getEmail());
	        if (oUsuario.getOficina()!=null)
	        	log.info("autenticando oficina: #0", oUsuario.getOficina());
	        else
	        	log.info("autenticando oficina: null");
	        
	        log.info("autenticando oficinadesc: #0", oUsuario.getOficinaDesc());
	        
	        log.info("autenticando ip: #0", oUsuario.getIp());
	        log.info("autenticando rol: #0", oUsuario.getRol());
		}

		if (oUsuario.getUsername() != null)
			bRespuesta = !(oUsuario.getUsername().isEmpty() || oUsuario.getMetodoAutenticacion().isEmpty() || oUsuario.getIp().isEmpty());
		
        log.info("bRespuesta= #0", bRespuesta);
        
        return bRespuesta;
        
    }
    
	public String getPaginaInicio() {
		InputStream dataStream = this.getClass().getResourceAsStream("seguridad.properties");
        
		String sURL = "";
		
        try {
        	
            Properties propURL = new Properties();
        
            if (dataStream==null){            	
            	log.fatal("No se pudo encontrar el archivo de propiedades de seguridad.");
            }
            
            propURL.load(dataStream);
            
            sURL = propURL.getProperty(propURL.getProperty("UBICACION"));
            
            this.setPaginaInicio(sURL);
        }  
         catch (IOException e) {
            throw new FacesException(e.getMessage(), e);
        } 
        finally {
            if (dataStream != null) {
                try {
                    dataStream.close();
                } catch (IOException e) {
                    log.info(e.getMessage(), e);
                }
            }
        }
		
        return paginaInicio;
	}

	public void setPaginaInicio(String paginaInicio) {
		this.paginaInicio = paginaInicio;
	}
	
}
