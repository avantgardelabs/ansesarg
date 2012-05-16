
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
 *         &lt;element name="crearRegistroEnClearQuestResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "crearRegistroEnClearQuestResult"
})
@XmlRootElement(name = "crearRegistroEnClearQuestResponse")
public class CrearRegistroEnClearQuestResponse {

    protected String crearRegistroEnClearQuestResult;

    /**
     * Gets the value of the crearRegistroEnClearQuestResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCrearRegistroEnClearQuestResult() {
        return crearRegistroEnClearQuestResult;
    }

    /**
     * Sets the value of the crearRegistroEnClearQuestResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCrearRegistroEnClearQuestResult(String value) {
        this.crearRegistroEnClearQuestResult = value;
    }

}
