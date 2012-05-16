package ar.gob.anses.prissa.mi.asistente_reglas.seam.action.graficador;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JComponent;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jdom.Element;
import org.jgraph.JGraph;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gov.anses.prissa.asistente.graficador.GraficadorModeloResumido;
import ar.gov.anses.prissa.asistente.graficador.Graficar;
import ar.gov.anses.prissa.asistente.graficador.modelo.FabricaDeTabla;
import ar.gov.anses.prissa.asistente.graficador.modelo.Tabla;
import ar.gov.anses.prissa.asistente.graficador.modelo.TablaVaciaException;
import ar.gov.anses.prissa.asistente.graficador.modeloresumido.FabricaDeTablaMapaResumido;
import ar.gov.anses.prissa.asistente.graficador.modeloresumido.TablaMR;
import ar.gov.anses.prissa.asistente.graficador.modeloresumido.TablaVaciaExceptionMR;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

@Scope(ScopeType.EVENT)
@Name("graficador")
public class GraficadorAction implements Serializable {

    private static final long serialVersionUID = -7238424286542867312L;

    @Logger
    private Log log;
    
    @In 
    private FacesMessages facesMessages;
    
    
    @In(create=true)
    @Out(required=true)
    private FabricaDeTabla fabricaDeTabla;
    
    @In(create=true)
    @Out(required=true)
    private FabricaDeTablaMapaResumido fabricaDeTablaModeloResumido;
    
    /**
     * Obtiene la representación gráfica de la tabla recibida y redirige a la 
     * página correspondiente para mostrarla por pantalla.
     * 
     * @param tablaDecision
     * @return
     */
    public void graficar(TablaDecision tablaDecision) {

        log.debug("Exportando a PDF: #0", tablaDecision.getNombre());
        
        try {
            
            /* construye del componente gráfico */
            JComponent componente = this.generarComponente(tablaDecision);

            /* genera el pdf en un archivo temporal */
            File archivoTemporal = File.createTempFile("regla-",".jpg");

            this.exportarJpg(componente, archivoTemporal);
            
            this.escribirRespuesta(archivoTemporal, tablaDecision.getNombre() + ".jpg");
            
            archivoTemporal.delete();
            
        } catch (TablaVaciaException tve) {
            
            facesMessages.add("La tabla '#0' no tiene filas", tablaDecision.getNombre());
            
        } catch (Exception e) {
            
            facesMessages.add("Se provocó un error generando pdf para la tabla '#0", 
                               tablaDecision.getNombre() + "'");
            
            log.error("Error generando PDF. Causado por " + 
                      e.getClass().getName() + ": " + e.getMessage());
            log.debug("Exception", e);
        }
        
    }
    
    /**
     * Obtiene la representación gráfica de la tabla recibida y redirige a la 
     * página correspondiente para mostrarla por pantalla.
     * 
     * @param tablaDecision
     * @return
     */
    public void graficarMapaResumido(TablaDecision tablaDecision) {

        log.debug("Exportando a PDF: #0", tablaDecision.getNombre());
        
        try {
            
            /* construye del componente gráfico */
            JComponent componente = this.generarComponenteMapaResumido(tablaDecision);

            /* genera el pdf en un archivo temporal */
            File archivoTemporal = File.createTempFile("regla-",".jpg");

            this.exportarJpg(componente, archivoTemporal);
            
            this.escribirRespuesta(archivoTemporal, tablaDecision.getNombre() + ".jpg");
            
            archivoTemporal.delete();
            
        } catch (TablaVaciaExceptionMR tve) {
            
            facesMessages.add("La tabla '#0' no tiene filas", tablaDecision.getNombre());
            
        } catch (Exception e) {
            
            facesMessages.add("Se provocó un error generando pdf para la tabla '#0", 
                               tablaDecision.getNombre() + "'");
            
            log.error("Error generando PDF. Causado por " + 
                      e.getClass().getName() + ": " + e.getMessage(), e);
            log.debug("Exception", e);
        }
        
    }


    /**
     * Genera un pdf a partir de componente en archivo.
     * 
     * @param componente
     * @param archivo
     * @throws DocumentException 
     * @throws FileNotFoundException 
     */
    private void exportarJpg(JComponent componente, File archivo) throws FileNotFoundException, DocumentException {
        
        Document documento = null;
        Dimension size = componente.getSize();
    	BufferedImage bi = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
    	Graphics2D g2d = bi.createGraphics();
    	g2d.setColor(Color.WHITE);
    	g2d.fillRect(0, 0, size.width, size.height);
        componente.addNotify();
        componente.validate();
    	componente.paintComponents(g2d);           	        	
        		
        	try {
				ImageIO.write(bi,"jpg",archivo);			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            
         finally {
        	g2d.dispose();        
            if (documento != null) {
                
                try {
                    
                    documento.close();
                    
                } catch (Exception e) {
                    
                    /* Ignorada */
                    log.debug("Exception cerrando documento", e);
                }
            }
        }
    }
    
    /**
     * Escribe en la respuesta http el contenido de archivo.
     * 
     * @param archivo
     * @param nombre
     * @throws IOException
     */
    private void escribirRespuesta(File archivo, String nombre) throws IOException {
        
        FileChannel jpg = null;
        WritableByteChannel respuesta = null;
        
        try {
            
            /* configura la respuesta http */
            HttpServletResponse response = (HttpServletResponse)FacesContext
                                                        .getCurrentInstance()
                                                            .getExternalContext()
                                                                .getResponse();
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition","attachment;filename=" + 
                               URLEncoder.encode(nombre,"UTF-8"));            
            response.setContentLength((int)archivo.length());
    
            /* escribe el archivo temporal en la respuesta http */
            jpg = new FileInputStream(archivo).getChannel();
            respuesta = Channels.newChannel(response.getOutputStream());
            jpg.transferTo(0, jpg.size(), respuesta);
            
        } catch (IOException ioe) {
            
            log.error("Error escribiendo pdf en la respuesta. Causado por " +  
                      ioe.getClass().getName() + ": "  + ioe.getMessage());
            log.debug("Exception", ioe);
            
        } finally {
            
            this.close(jpg);
            this.close(respuesta);
        }
        
        FacesContext.getCurrentInstance().responseComplete();
    }

    private void close(Channel channel) {
        
        try {
            
            if (channel != null) {
                channel.close();
            }
            
        } catch (IOException ioe) {
            
            /* Ignorado */
            log.debug("Error cerrando channel", ioe);
        }
    }
    
    /**
     * A partir de una tabla de decisión obtiene su representación 
     * como un JComponent
     * 
     * @param tablaDecision
     * @return
     */
    private JComponent generarComponente(TablaDecision tablaDecision) {
        
        Tabla tablaDecisionGraficador = fabricaDeTabla.convertirTabla(tablaDecision);

        Graficar graficar = new Graficar();
        graficar.setTabla(tablaDecisionGraficador);
        graficar.procesar();
        
        return graficar.getGrafico();
    }
    
    /**
     * A partir de una tabla de decisión obtiene su representación 
     * como un JComponent
     * 
     * @param tablaDecision
     * @return
     */
    private JComponent generarComponenteMapaResumido(TablaDecision tablaDecision) {
        
       TablaMR tablaDecisionGraficador = fabricaDeTablaModeloResumido.convertirTabla(tablaDecision);

       GraficadorModeloResumido graficador = new GraficadorModeloResumido();
 
       graficador.setTabla(tablaDecisionGraficador);
       graficador.procesar();
        
       return graficador.getGrafico();
    }

    

}
