package ar.gob.anses.prissa.mi.asistente_reglas.seam.action.cq;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Instrumento;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Usuario;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IVersionableRegla;

/**
 * The Class ProcesaXML.
 */
@Scope(ScopeType.CONVERSATION)
@Name("procesaXML")
public class ProcesaXML implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4939196374988097274L;

	/** The log. */
	@Logger
	Log log;

	/** The faces messages. */
	@In
	FacesMessages facesMessages;

	private ArrayList<RegistroSitaci> registrosSitaci;	
	
	/** The req. */
	private RequerimientoInformatico req;
	
	/**
	 * Gets the req.
	 *
	 * @return the req
	 */
	public RequerimientoInformatico getReq() {
		return req;
	}

	/**
	 * Sets the req.
	 *
	 * @param req the new req
	 */
	public void setReq(RequerimientoInformatico req) {
		this.req = req;
	}

	private String convertXMLToString(Document doc) {
		Format format = Format.getPrettyFormat();
		XMLOutputter xmloutputter = new XMLOutputter(format);
		String docStr = xmloutputter.outputString(doc).trim();

		log.info(docStr);

		return docStr;
	}
	
	/**
	 * Genera el XML de muestra de datos
	 *
	 * @return the string
	 */
	public String generarMuestraDatosXML() {

		Document doc = new Document();
		Element datosClearQuest = new Element("datosClearQuest");
		doc.setRootElement(datosClearQuest);
		
		generarConexion(datosClearQuest);
		generarLote(datosClearQuest);

		return convertXMLToString(doc);
	}
	
	/**
	 * Generar anclaje atributos xml.
	 *
	 * @return the string
	 */
	public String generarAnclajeAtributosXML(){

		Document doc = new Document();
		Element datosClearQuest = new Element("datosClearQuest");
		doc.setRootElement(datosClearQuest);
		
		generarConexion(datosClearQuest);
		generarAnclaje(datosClearQuest);

		return convertXMLToString(doc);

	}
	
	/**
	 * Generar pasaje regla xml.
	 *
	 * @return the string
	 */
	public String generarPasajeReglaXML() {

		Document doc = new Document();
		Element datosClearQuest = new Element("datosClearQuest");
		doc.setRootElement(datosClearQuest);
		
		generarConexion(datosClearQuest);
		generarPasaje(datosClearQuest);

		return convertXMLToString(doc);
	} 

	/**
	 * Generar lote.
	 *
	 * @param datosClearQuest the datos clear quest
	 */
	private void generarLote(Element datosClearQuest) {
		Element lotePrueba = new Element("LotePrueba");
		datosClearQuest.addContent(lotePrueba);

		setAsunto(lotePrueba);
		setDescripcion(lotePrueba);
		setJustificacion(lotePrueba);
		setUsuario(lotePrueba);
		setSistemas(lotePrueba);
		setReglas(lotePrueba);
		//setAdjuntos(lotePrueba);
		setSITACI(lotePrueba);
	}
	
	/**
	 * Generar anclaje.
	 *
	 * @param datosClearQuest the datos clear quest
	 */
	private void generarAnclaje(Element datosClearQuest) {
		Element anclajeDato = new Element("AnclajeDato");
		datosClearQuest.addContent(anclajeDato);

		setAsunto(anclajeDato);
		setDescripcion(anclajeDato);
		setJustificacion(anclajeDato);
		setUsuario(anclajeDato);
		setSistemas(anclajeDato);
		setEntidad(anclajeDato);
		setAtributos(anclajeDato);
		setAdjuntos(anclajeDato);
	}

	/**
	 * Generar pasaje.
	 *
	 * @param datosClearQuest the datos clear quest
	 */
	private void generarPasaje(Element datosClearQuest) {
		Element pasajeRegla = new Element("PasajeRegla");
		datosClearQuest.addContent(pasajeRegla);

		setAsunto(pasajeRegla);
		setAprobacion(pasajeRegla);
		setDescripcion(pasajeRegla);
		setJustificacion(pasajeRegla);
		setUsuario(pasajeRegla);
		setSistemas(pasajeRegla);
		setReglasInstrumentos(pasajeRegla);
		setAdjuntos(pasajeRegla);
	}
	
	private void setAtributos(Element anclajeDato) {
		Element descripcion;
		Element uniqueKey;
		Element atributos = new Element("N_Atributos");
		for(Atributo atributo : ((AnclajeRequerimientoInformatico)getReq()).getAtributos()){
			uniqueKey = new Element("uniqueKey");			
			uniqueKey.setAttribute("D_Id", StringUtils.leftPad(atributo.getId().toString(), 5, '0'));
			uniqueKey.setAttribute("campoCQ", "dbid");
			uniqueKey.setAttribute("entidadCQ", "Atributos");
			Element nombre = new Element("D_Nombre");
			nombre.setText(atributo.getNombre());
			uniqueKey.addContent(nombre);
			
			Element tipoDato = new Element("N_TipoDato");
			Element uniqueKey2 = new Element("uniqueKey");			
			uniqueKey2.setAttribute("D_Id", StringUtils.leftPad(atributo.getIdTipoDato().toString(), 5, '0'));
			uniqueKey2.setAttribute("campoCQ", "D_TipoDato");
			uniqueKey2.setAttribute("entidadCQ", "TipoDato");
			Element tipo = new Element("D_TipoDato");
			tipo.setText(atributo.getTipoDato());
			uniqueKey2.addContent(tipo);
			tipoDato.addContent(uniqueKey2);
			uniqueKey.addContent(tipoDato);
			
			descripcion = new Element("D_Descripcion");
			descripcion.setText(atributo.getDescripcion());
			uniqueKey.addContent(descripcion);			
			
			atributos.addContent(uniqueKey);
		}
		anclajeDato.addContent(atributos);
	}

	private void setEntidad(Element anclajeDato) {
		Element descripcion;
		Element uniqueKey;
		AnclajeRequerimientoInformatico req = (AnclajeRequerimientoInformatico)getReq();
		
		Element entidad = new Element("D_NombreEntidad");
		uniqueKey = new Element("uniqueKey");		
		uniqueKey.setAttribute("D_Id", StringUtils.leftPad(req.getEntidad().getId().toString(), 5, '0'));
		uniqueKey.setAttribute("campoCQ", "D_Nombre");
		uniqueKey.setAttribute("entidadCQ", "Entidad");
		Element nombre = new Element("D_Nombre");
		nombre.setText(req.getEntidad().getNombre());
		uniqueKey.addContent(nombre);
		descripcion = new Element("D_Descripcion");
		descripcion.setText(req.getEntidad().getDescripcion());
		uniqueKey.addContent(descripcion);
		entidad.addContent(uniqueKey);
		anclajeDato.addContent(entidad);		
	}

	/**
	 * Setea los adjuntos si los hubiese.
	 *
	 * @param lotePrueba the new adjuntos
	 */
	private void setAdjuntos(Element element) {
		Element adjuntos = new Element("A_AdjuntosReglas");
		adjuntos.setText(getReq().getObservaciones());
		element.addContent(adjuntos);
	}
	
	/**
	 * Setea el SITACI si los hubiese.
	 *
	 * @param lotePrueba the new sitaci
	 */
	private void setSITACI(Element element) {
		Element eSitaci = new Element("D_EnviaSITACI");
		if(this.registrosSitaci.size()<0){
			eSitaci.setText("NO");
			element.addContent(eSitaci);
		}else{
			eSitaci.setText("SI");
			element.addContent(eSitaci);
			Element listaSitaci = new Element("N_ListaSITACI");
			Element registroSitaci;
			Element interiorRegistroSitaci;
			for(RegistroSitaci reg : this.registrosSitaci){
				registroSitaci = new Element("uniqueKey");
				registroSitaci.setAttribute("D_Id", "NA");
				registroSitaci.setAttribute("campoCQ", "dbid");
				registroSitaci.setAttribute("entidadCQ", "SITACI");
				
				//nroSitaci
				interiorRegistroSitaci = new Element("D_NroSITACI");
				interiorRegistroSitaci.setText(reg.getNroSitaci());
				registroSitaci.addContent(interiorRegistroSitaci);
				//DNSorigen
				interiorRegistroSitaci = new Element("D_DNSOrigen");
				interiorRegistroSitaci.setText(reg.getDnsOrigen());
				registroSitaci.addContent(interiorRegistroSitaci);
				//Longitud
				interiorRegistroSitaci = new Element("D_Long");
				interiorRegistroSitaci.setText(String.valueOf(reg.getLongitud()));
				registroSitaci.addContent(interiorRegistroSitaci);
				//cantReg
				interiorRegistroSitaci = new Element("D_CantReg");
				interiorRegistroSitaci.setText(String.valueOf(reg.getCantReg()));
				registroSitaci.addContent(interiorRegistroSitaci);
				//Descripcion
				interiorRegistroSitaci = new Element("D_DescSITACI");
				interiorRegistroSitaci.setText(reg.getDescripcion());
				registroSitaci.addContent(interiorRegistroSitaci);
				//agrego un registro
				listaSitaci.addContent(registroSitaci);
			}
			//Agrego toda la lista al cuerpo del xml
			element.addContent(listaSitaci);
		}		
		
	}
	
	/**
	 * Arma las reglas - padre e hijas - y las adjunta al XML
	 *
	 * @param lotePrueba the new reglas
	 */
	private void setReglas(Element element) {
		MuestraRequerimientoInformatico req = (MuestraRequerimientoInformatico)getReq();
		//regla padre
		Element reglas = new Element("N_Reglas");
		setRegla(req.getRegla(), reglas);
		//subreglas
		if(req.getSubReglas() != null){
			for(IVersionableRegla regla : req.getSubReglas()){
				setRegla(regla, reglas);	
			}
		}
		element.addContent(reglas);
	}

	/**
	 * Arma la regla y la adjunta al XML
	 *
	 * @param regla the regla
	 * @param lotePrueba the lote prueba
	 */
	private void setRegla(IVersionableRegla regla, Element reglas) {
		Element descripcion;
		Element uniqueKey;
		
		uniqueKey = new Element("uniqueKey");		
		uniqueKey.setAttribute("D_Id", StringUtils.leftPad(regla.getId().toString(), 5, '0'));
		uniqueKey.setAttribute("campoCQ", "D_Nombre");
		uniqueKey.setAttribute("entidadCQ", "Regla");
		Element nombre = new Element("D_Nombre");
		nombre.setText(regla.getNombre());
		uniqueKey.addContent(nombre);
		descripcion = new Element("D_Descripcion");
		descripcion.setText(regla.getDescripcion());
		uniqueKey.addContent(descripcion);
		Element tipo = new Element("D_Tipo");
		if(getReq() instanceof MuestraRequerimientoInformatico){
			tipo.setText(((MuestraRequerimientoInformatico)getReq()).getTipoRegla());	
		}else if(getReq() instanceof PasajeRequerimientoInformatico){
			tipo.setText(((PasajeRequerimientoInformatico)getReq()).getTipoRegla());
		}
		uniqueKey.addContent(tipo);
		Element dominio = new Element("D_Dominio");
		dominio.setText(regla.getDominio().getDescripcion());
		uniqueKey.addContent(dominio);
		Element version = new Element("D_Version");
		version.setText(regla.getVersionRegla());
		uniqueKey.addContent(version);
		reglas.addContent(uniqueKey);
	}
	
	private void setReglasInstrumentos(Element element) {
		PasajeRequerimientoInformatico req = (PasajeRequerimientoInformatico)getReq();
		//regla padre
		Element nombreRegla = new Element("D_NombreRegla");
		setRegla(req.getRegla(), nombreRegla);
		element.addContent(nombreRegla);
		
		//subreglas
		Element reglasHijas = new Element("N_ReglasHijas");		
		if(req.getSubReglas() != null){
			for(IVersionableRegla regla : req.getSubReglas()){
				setRegla(regla, reglasHijas);	
			}
		}
		element.addContent(reglasHijas);
		
		//instrumentos normativos
		Element instrumentos = new Element("N_InstrumentosNormativos");
		for(Instrumento instrumento : req.getInstrumentos()){
			setInstrumento(instrumento, instrumentos);	
		}
		element.addContent(instrumentos);
	}

	
	private void setInstrumento(Instrumento instrumento, Element instrumentos) {
		Element descripcion;
		Element uniqueKey;
		
		uniqueKey = new Element("uniqueKey");		
		uniqueKey.setAttribute("D_Id", StringUtils.leftPad(instrumento.getId().toString(), 5, '0'));
		uniqueKey.setAttribute("campoCQ", "dbid");
		uniqueKey.setAttribute("entidadCQ", "InstrumentoNormativo");
		Element norma = new Element("D_Norma");
		norma.setText(instrumento.getDescripcion());
		uniqueKey.addContent(norma);
		descripcion = new Element("D_Descripcion");
		descripcion.setText(instrumento.getTextoExplicativo());
		uniqueKey.addContent(descripcion);
		Element fechaFirma = new Element("F_FechaFirma");
		fechaFirma.setText(instrumento.getFechaFirmaFormat());
		uniqueKey.addContent(fechaFirma);
		Element fechaAplicacion = new Element("F_FechaAplicacion");
		fechaAplicacion.setText(instrumento.getFechaAplicacionFormat());
		uniqueKey.addContent(fechaAplicacion);
		Element fechaVigencia = new Element("F_FechaVigencia");
		fechaVigencia.setText(instrumento.getFechaVigenciaFormat());
		uniqueKey.addContent(fechaVigencia);
		Element fechaFin = new Element("F_FechaFin");
		fechaFin.setText("01/01/2000");
		uniqueKey.addContent(fechaFin);
		instrumentos.addContent(uniqueKey);
		
	}

	/**
	 * Arma el sistema y la adjunta al XML
	 *
	 * @param lotePrueba the new sistemas
	 */
	private void setSistemas(Element element) {
		Element descripcion;
		Element uniqueKey;
		if(getReq().getSistemas() != null){
			Element sistemas = new Element("N_Sistemas");
			for(String sistema : getReq().getSistemas()){
				String[] nombreYDesc = sistema.split("-");				
				uniqueKey = new Element("uniqueKey");
				uniqueKey.setAttribute("D_Nombre", nombreYDesc[0].trim());
				uniqueKey.setAttribute("campoCQ", "D_Nombre");
				uniqueKey.setAttribute("entidadCQ", "Sistemas");
				descripcion = new Element("D_Descripcion");
				descripcion.setText(nombreYDesc[1].trim());
				uniqueKey.addContent(descripcion);
				sistemas.addContent(uniqueKey);			
			}
			element.addContent(sistemas);
		}
	}

	/**
	 * Arma el usuario y lo adjunta al XML
	 *
	 * @param lotePrueba the new usuario
	 */
	private void setUsuario(Element element) {
		Usuario user = (Usuario)Contexts.getSessionContext().get("user");
		
		Element usuario = new Element("N_UsuarioAlta");
		Element uniqueKey = new Element("uniqueKey");
		uniqueKey.setAttribute("login_name", user.getUsername());
		uniqueKey.setAttribute("campoCQ", "login_name");
		uniqueKey.setAttribute("entidadCQ", "users");
		usuario.addContent(uniqueKey);
		element.addContent(usuario);
	}

	/**
	 * Setea la justificacion.
	 *
	 * @param lotePrueba the new justificacion
	 */
	private void setJustificacion(Element element) {
		Element justificacion = new Element("D_JustificacionMultiline");
		justificacion.setText(getReq().getJustificacion());
		element.addContent(justificacion);
	}

	/**
	 * Setea la descripcion.
	 *
	 * @param lotePrueba the new descripcion
	 */
	private void setDescripcion(Element element) {
		Element descripcion = new Element("D_Descripcion");
		descripcion.setText(getReq().getDescripcion());
		element.addContent(descripcion);
	}
	
	private void setAprobacion(Element pasajeRegla){
		Element aprobacion = new Element("D_AprobacionControl");
		aprobacion.setText("NO");
		pasajeRegla.addContent(aprobacion);
	}

	/**
	 * Setea el asunto.
	 *
	 * @param lotePrueba the new asunto
	 */
	private void setAsunto(Element element) {
		Element asunto = new Element("D_Asunto");
		asunto.setText(getReq().getAsunto());
		element.addContent(asunto);
	}

	/**
	 * Genera la conexion y la adjunta al XML
	 *
	 * @param datosClearQuest the datos clear quest
	 */
	private void generarConexion(Element datosClearQuest) {
		Element conexion = new Element("conexion");
		datosClearQuest.addContent(conexion);
		Element nombreDeUsuario = new Element("nombreDeUsuario");
		nombreDeUsuario.setText(PropertiesHelper.getInstance().getProperties("cq").getProperty("conexion.nombreDeUsuario"));
		conexion.addContent(nombreDeUsuario);
		Element password = new Element("password");
		password.setText(PropertiesHelper.getInstance().getProperties("cq").getProperty("conexion.password"));
		conexion.addContent(password);
		Element nombreDeConexion = new Element("nombreDeConexion");
		nombreDeConexion
				.setText(PropertiesHelper.getInstance().getProperties("cq").getProperty("conexion.nombreDeConexion"));
		conexion.addContent(nombreDeConexion);
		Element baseDeDatos = new Element("baseDeDatos");
		baseDeDatos.setText(PropertiesHelper.getInstance().getProperties("cq").getProperty("conexion.baseDeDatos"));
		conexion.addContent(baseDeDatos);
	}

	public void setRegistrosSitaci(ArrayList<RegistroSitaci> registrosSitaci) {
		this.registrosSitaci = registrosSitaci;
	}

	public ArrayList<RegistroSitaci> getRegistrosSitaci() {
		return registrosSitaci;
	}
}