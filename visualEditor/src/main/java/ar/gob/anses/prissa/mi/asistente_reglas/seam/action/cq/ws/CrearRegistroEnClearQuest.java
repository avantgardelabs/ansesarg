
package ar.gob.anses.prissa.mi.asistente_reglas.seam.action.cq.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="textoXML" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nombreDeArchivo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="datosDeArchivo" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "textoXML",
    "nombreDeArchivo",
    "datosDeArchivo"
})
@XmlRootElement(name = "crearRegistroEnClearQuest")
public class CrearRegistroEnClearQuest {

    protected String textoXML;
    protected String nombreDeArchivo;
    protected byte[] datosDeArchivo;

    /**
     * Gets the value of the textoXML property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTextoXML() {
        return textoXML;
    }

    /**
     * Sets the value of the textoXML property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTextoXML(String value) {
        this.textoXML = value;
    }

    /**
     * Gets the value of the nombreDeArchivo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombreDeArchivo() {
        return nombreDeArchivo;
    }

    /**
     * Sets the value of the nombreDeArchivo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombreDeArchivo(String value) {
        this.nombreDeArchivo = value;
    }

    /**
     * Gets the value of the datosDeArchivo property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getDatosDeArchivo() {
        return datosDeArchivo;
    }

    /**
     * Sets the value of the datosDeArchivo property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setDatosDeArchivo(byte[] value) {
        this.datosDeArchivo = ((byte[]) value);
    }

}
