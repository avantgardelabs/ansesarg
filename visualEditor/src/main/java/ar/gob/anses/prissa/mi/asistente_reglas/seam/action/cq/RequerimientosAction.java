package ar.gob.anses.prissa.mi.asistente_reglas.seam.action.cq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.RandomUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Descisor;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.FilaTablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Instrumento;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.ReglaPseudocodigo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Usuario;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.action.cq.ws.AsistenteDeReglas;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.action.cq.ws.AsistenteDeReglasSoap;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.custom.BaseHome;
import ar.gob.anses.prissa.mi.asistente_reglas.services.PersistenceService;

/**
 * The Class RequerimientosAction.
 */
@Scope(ScopeType.CONVERSATION)
@Name("requerimientosAction")
@SuppressWarnings("serial")
@AutoCreate
public class RequerimientosAction extends BaseHome<RequerimientoInformatico> {
		
	/** The modificado. */
	private boolean modificado = false;

	/** The entity manager. */
	@In(create = true)
	EntityManager entityManager;

	/** The faces messages. */
	@In
	FacesMessages facesMessages;
	
	@In	PersistenceService persistenceService;
	
	private MuestraRequerimientoInformatico muestraRequerimientoInformatico;
	
	private AnclajeRequerimientoInformatico anclajeRequerimientoInformatico;
	
	private PasajeRequerimientoInformatico pasajeRequerimientoInformatico;

	/** The lista sistemas disponibles. */
	private ArrayList<String> listaSistemasDisponibles;

	/** The sistemas seleccionados. */
	private ArrayList<String> sistemasSeleccionados;
	
	/** The lista tipo req. */
	private ArrayList<SelectItem> listaTipoReq;
	
	private TipoRequerimiento tipoReq;

	/** The log. */
	@Logger
	Log log;

	/** The procesa xml. */
	@In(create = true)
	@Out(required = true)
	private ProcesaXML procesaXML;

	/** The red local. */
	Boolean entorno = null;

	/** The nombre regla tabla decision. */
	@In(create = true, required = false)
	@Out(required = false)
	String nombreReglaTablaDecision = new String();

	/** The list tabla decision. */
	private List<TablaDecision> listTablaDecision;

	/** The list rps. */
	private List<ReglaPseudocodigo> listRPS;
	

	private ArrayList<RegistroSitaci> registrosSitaci;	
	private RegistroSitaci registroSitaciEditando = new RegistroSitaci();
	private RegistroSitaci registroSitaciParaEditar = new RegistroSitaci();
	
	
	private String nombreRegla;
	private String nombreReglaSeleccionada;

	/**
	 * @return the muestraRequerimientoInformatico
	 */
	public MuestraRequerimientoInformatico getMuestraRequerimientoInformatico() {
		return muestraRequerimientoInformatico;
	}

	/**
	 * @param muestraRequerimientoInformatico the muestraRequerimientoInformatico to set
	 */
	public void setMuestraRequerimientoInformatico(
			MuestraRequerimientoInformatico muestraRequerimientoInformatico) {
		this.muestraRequerimientoInformatico = muestraRequerimientoInformatico;
	}

	/**
	 * @return the anclajeRequerimientoInformatico
	 */
	public AnclajeRequerimientoInformatico getAnclajeRequerimientoInformatico() {
		return anclajeRequerimientoInformatico;
	}

	/**
	 * @param anclajeRequerimientoInformatico the anclajeRequerimientoInformatico to set
	 */
	public void setAnclajeRequerimientoInformatico(
			AnclajeRequerimientoInformatico anclajeRequerimientoInformatico) {
		this.anclajeRequerimientoInformatico = anclajeRequerimientoInformatico;
	}
	
	public PasajeRequerimientoInformatico getPasajeRequerimientoInformatico() {
		return pasajeRequerimientoInformatico;
	}

	public void setPasajeRequerimientoInformatico(
			PasajeRequerimientoInformatico pasajeRequerimientoInformatico) {
		this.pasajeRequerimientoInformatico = pasajeRequerimientoInformatico;
	}

	public RequerimientoInformatico getRequerimiento(){
		RequerimientoInformatico req = null;
		if(tipoReq == TipoRequerimiento.MUESTRA_DE_DATOS){
			req = getMuestraRequerimientoInformatico();
		}else if(tipoReq == TipoRequerimiento.ANCLAJE_DE_ATRIBUTOS){
			req = getAnclajeRequerimientoInformatico();
		}else if(tipoReq == TipoRequerimiento.PASAJE_DE_REGLAS){
			req = getPasajeRequerimientoInformatico();
		}
		return req;
	}

	/**
	 * Wire.
	 * Se ejecuta siempre que se invoca generarRequerimiento.seam
	 */
	public void wire() {
		this.armarRequerimientoBase();
		getEntorno();
	}
	
	public void wireBuscarRegla(){
		log.info("Buscar Regla Muestra");
	}
	
	public void wireBuscarReglaPasaje(){
		log.info("Buscar Regla Pasaje");
	}

	/**
	 * Solicitud muestra datos.
	 *
	 * @return the string
	 */
	public String solicitudMuestraDatos() {
		if(getNombreRegla() == null){
			facesMessages.add("Debe seleccionar una regla.");
			return null;
		}

		getMuestraRequerimientoInformatico().setAsunto("Solicitud de Muestra de Datos");
		getMuestraRequerimientoInformatico().setReglaId(getMuestraRequerimientoInformatico().getRegla().getId());
		this.setTipoReq(TipoRequerimiento.MUESTRA_DE_DATOS);
		this.wire();
		return "/Rational/generarRequerimientoMuestra.xhtml";
	}
	
	public String solicitudAnclajeAtributos(Entidad entidad) {
		if(atributos.size() == 0){
			facesMessages.add("Debe seleccionar al menos un atributo.");
			return null;
		}
		setAnclajeRequerimientoInformatico(new AnclajeRequerimientoInformatico());
		getAnclajeRequerimientoInformatico().setAsunto("Solicitud de Anclaje de Atributos");
		getAnclajeRequerimientoInformatico().setEstado(EstadoAnclaje.INICIADO);
		this.setTipoReq(TipoRequerimiento.ANCLAJE_DE_ATRIBUTOS);
		getAnclajeRequerimientoInformatico().setEntidad(entidad);
		this.wire();
		return "/Rational/generarRequerimiento.xhtml";
	}
	
	public String solicitudPasajeRegla() {
		if(getNombreRegla() == null){
			facesMessages.add("Debe seleccionar una regla.");
			return null;
		}

		getPasajeRequerimientoInformatico().setAsunto("Solicitud de Pasaje de Regla");
		getPasajeRequerimientoInformatico().setReglaId(getPasajeRequerimientoInformatico().getRegla().getId());
		this.setTipoReq(TipoRequerimiento.PASAJE_DE_REGLAS);
		this.wire();
		return "/Rational/generarRequerimiento.xhtml";
	}


	private Boolean getEntorno() {
		if(entorno == null){
			if(PropertiesHelper.getInstance().getProperties("cq") == null){
				log.error("No se pudo obtener la variable 'entorno.anses'. Se usa por defecto 'true'");
				entorno = Boolean.TRUE;
			}else{
				entorno = Boolean.valueOf(PropertiesHelper.getInstance().getProperties("cq").getProperty("entorno.anses"));
			}
		}
		return entorno;
	}

	/**
	 * Armar requerimiento base.
	 */
	public void armarRequerimientoBase() {
		Date d = new Date();
		getRequerimiento().setFechaSolicitud(d);
		this.cargarComboListaTipoReq();
		this.cargarListaSistemasInformaticos();
	}

	/**
	 * Cargar lista sistemas informaticos.
	 */
	public void cargarListaSistemasInformaticos() {		
		if (getEntorno()) {
			if(listaSistemasDisponibles == null){
				listaSistemasDisponibles = new ArrayList<String>();				
				ConexionBaseCQ base = new ConexionBaseCQ();
				Connection conn = base.dbConnect();				
				Statement stmt;
				/** TODO Pasar a archivo properties **/
				String queryTiposDeRequerimientos = "SELECT T1.d_nombre, T1.d_descripcion FROM AnsesCQMaster.sistema T1 where T1.dbid <> 0";
				try {
					stmt = conn.createStatement();
					ResultSet result = stmt.executeQuery(queryTiposDeRequerimientos);
					while (result.next()) {
						String sistema = result.getString("d_nombre") + " - " + result.getString("d_descripcion");
						listaSistemasDisponibles.add(sistema);
					}
					stmt.close();
					conn.close();
					log.info("La lista de sistemas disponibles se cargo");
				} catch (SQLException e) {
					log.info("La lista de sistemas disponibles no se pudo cargar");
					e.printStackTrace();
				}
			}else{
				log.info("La lista de sistemas disponibles ya estaba cargada");
			}

		} else {
			if(listaSistemasDisponibles == null){
				listaSistemasDisponibles = new ArrayList<String>();
				for (int i = 1; i < 10; i++) {				
					listaSistemasDisponibles.add("nombreSistema" + i + " - descripcionSistema" + i);
				}
			}
		}
	}

	/**
	 * Cargar combo lista tipo req.
	 */
	public void cargarComboListaTipoReq() {
		if (getEntorno()) {
			if(listaTipoReq == null){
				listaTipoReq = new ArrayList<SelectItem>();
				ConexionBaseCQ base = new ConexionBaseCQ();
				Connection conn = base.dbConnect();
				Statement stmt;
				/** TODO Pasar a archivo properties **/
				String queryTiposDeRequerimientos = "SELECT T1.d_nombre, T1.d_titulo, T1.d_descripcion, T6.d_nombre, T1.d_aplicariesgos "
						+ "FROM AnsesCQMaster.tiporequerimiento T1, AnsesCQMaster.area T6 "
						+ "WHERE T1.n_gerenciaejecutora = T6.dbid and (T1.dbid <> 0)";

				try {
					stmt = conn.createStatement();
					ResultSet result = stmt.executeQuery(queryTiposDeRequerimientos);

					while (result.next()) {
						SelectItem tipoReq = new SelectItem(result.getString("d_nombre") + " - " + result.getString("d_titulo")); 
						listaTipoReq.add(tipoReq);						
					}
					stmt.close();
					conn.close();
					log.info("Se cargo la lista de tipos de req");
				} catch (SQLException e) {
					log.info("No se pudo cargar la lista de tipos de req.");
					e.printStackTrace();
				}
			}else{
				log.info("La lista de tipos de req ya estaba cargada");
			}
		} else {
			if(listaTipoReq == null){
				listaTipoReq = new ArrayList<SelectItem>();
				for (int i = 1; i < 10; i++) {
					listaTipoReq.add(new SelectItem("nombreTR" + i + " - tituloTR" + i));				
				}	
			}
		}
	}

	/**
	 * Enviar req by file.
	 *
	 * @return the string
	 */
	@Deprecated
	public String enviarReqByFile() {
		String xml = "";

		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;

		try {
			archivo = new File(
					"C:\\anses\\workspace\\asistente_reglas_v2.0\\src\\main\\resources\\lote.xml");
			fr = new FileReader(archivo);
			br = new BufferedReader(fr);

			String linea;
			while ((linea = br.readLine()) != null)
				xml += linea;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fr) {
					fr.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		AsistenteDeReglas a = new AsistenteDeReglas();
		AsistenteDeReglasSoap b = a.getAsistenteDeReglasSoap();
		getRequerimiento().setResultado(b.crearRegistroEnClearQuest(xml, "", null));
		return "/Rational/pruebaLote.xhtml";
	}
	
	private boolean validar(){
		boolean valido = true;
		if(getSistemasSeleccionados() == null || (getSistemasSeleccionados()!= null && getSistemasSeleccionados().size()==0)){
			valido = false;
			facesMessages.add("Debe seleccionar al menos un sistema.");
		}
		if("".equals(getRequerimiento().getDescripcion())){
			valido = false;
			facesMessages.add("La descripcion es obligatoria.");
		}
		if("".equals(getRequerimiento().getJustificacion())){
			valido = false;
			facesMessages.add("La justificacion es obligatoria.");
		}
		return valido;
	}

	/**
	 * Envia el mensaje de Muestra de Datos a CQ
	 *
	 * @return the string
	 */
	public String enviarReqByGeneratedXML() {
		if(!validar()){
			return null;
		}
		String ret = "";
		String xml = "";
		String rta = "";
		
		if(esMuestraDeDatos()){
			procesaXML.setRegistrosSitaci(this.registrosSitaci);
			//obtencion subreglas			
			if(getMuestraRequerimientoInformatico().getReglaAsistente() != null){
				TablaDecision t = getMuestraRequerimientoInformatico().getReglaAsistente();
				List<TablaDecision> subReglas = new ArrayList<TablaDecision>();
				generarSubreglas(t, subReglas, true);
				getMuestraRequerimientoInformatico().setSubReglas(subReglas);
			}
		}else if(esAnclajeDeAtributos()){
			getAnclajeRequerimientoInformatico().setAtributos(atributos);
		}else if(esPasajeDeReglas()){
			if(getPasajeRequerimientoInformatico().getReglaAsistente() != null){
				TablaDecision t = getPasajeRequerimientoInformatico().getReglaAsistente();
				List<TablaDecision> subReglas = new ArrayList<TablaDecision>();
				generarSubreglas(t, subReglas, true);
				getPasajeRequerimientoInformatico().setSubReglas(subReglas);
				List<Instrumento> instrumentos = new ArrayList<Instrumento>();
				generarInstrumentosNormativos(t, subReglas, instrumentos);
				getPasajeRequerimientoInformatico().setInstrumentos(instrumentos);
			}
		}	
		
		getRequerimiento().setSistemas(this.getSistemasSeleccionados());		
		Usuario user = (Usuario)Contexts.getSessionContext().get("user");
		getRequerimiento().setUsuario(user.getUsername());
		if(!namesAttachs.isEmpty()){
			//solo se permite 1 archivo adjunto
			getRequerimiento().setPathAdjunto(namesAttachs.get(0));
		}
		
		procesaXML.setReq(getRequerimiento());
		if(esMuestraDeDatos()){			
			xml = procesaXML.generarMuestraDatosXML();
		}else if(esAnclajeDeAtributos()){
			xml = procesaXML.generarAnclajeAtributosXML();
		}else if(esPasajeDeReglas()){
			xml = procesaXML.generarPasajeReglaXML();
		}		

		if (getEntorno()) {
			AsistenteDeReglas a = new AsistenteDeReglas();
			AsistenteDeReglasSoap b = a.getAsistenteDeReglasSoap();
			rta = b.crearRegistroEnClearQuest(xml, fileName, myfile);
		} else {
			// SIMULO CONEXION AL CQ
			rta = "MOCK";
		}
		
		getRequerimiento().setResultado(rta);
		persistenceService.save(getRequerimiento());
		
		if (getEntorno()) {
			if(rta.startsWith("ADM")){
				//exito
				log.info("Requerimiento '" + rta + "' generado exitosamente" );
				facesMessages.add("Requerimiento '" + rta + "' generado exitosamente" );
			}else{
				//error
				log.error("No se pudo generar el requerimiento." );
				log.error(rta);
				facesMessages.add("No se pudo generar el requerimiento. ERROR: " + rta );
			}
		}else{
			log.info("Requerimiento '" + rta + "' generado exitosamente" );
			facesMessages.add("Requerimiento '" + rta + "' generado exitosamente" );
		}
		
		if(esMuestraDeDatos()){
			this.setNombreRegla(null);
			this.setMuestraRequerimientoInformatico(new MuestraRequerimientoInformatico());
			ret = "/Rational/buscarRegla.xhtml";
		}else if(esAnclajeDeAtributos()){
			ret = "/Rational/buscarEntidad.xhtml";
		}else if(esPasajeDeReglas()){
			this.setNombreRegla(null);
			this.setPasajeRequerimientoInformatico(new PasajeRequerimientoInformatico());
			ret = "/Rational/buscarReglaPasaje.xhtml";
		}
		setNamesAttachs(new ArrayList<String>());
		return ret;
	}

	private void generarInstrumentosNormativos(TablaDecision t,
			List<TablaDecision> subReglas, List<Instrumento> instrumentos) {
		for(Instrumento instrumento : t.getInstrumentos()){
			if(!instrumentos.contains(instrumento)){
				instrumentos.add(instrumento);
			}
		}
		if(!subReglas.isEmpty()){
			for(TablaDecision regla : subReglas){
				for(Instrumento instrumento : regla.getInstrumentos()){
					if(!instrumentos.contains(instrumento)){
						instrumentos.add(instrumento);
					}
				}
			}
		}	
	}

	/**
	 * Genera las subreglas.
	 *
	 * @param t the t
	 * @param subReglas the subReglas
	 * @return the subreglas
	 */
	private void generarSubreglas(TablaDecision t, List<TablaDecision> subReglas, boolean isFirstTime) {
		FilaTablaDecision fila;
		Descisor d;			 
		for (int cantFilas=0;cantFilas < t.getFilas().size();cantFilas++){				
			fila=t.getFilas().get(cantFilas);				
			for (int i=0; i<fila.getValores().size();i++){
				d=(Descisor)fila.getValores().get(i);					
				if (d.getCondicion().getRegla()!=null){
					//control que evita loop infinito
					if(d.getCondicion().getRegla().getId() != t.getId()){
						if(!subReglas.contains(d.getCondicion().getRegla()) && 
								!t.equals(d.getCondicion().getRegla())){
							log.info("Regla a agregar: " + d.getCondicion().getRegla().getNombre());
							subReglas.add(d.getCondicion().getRegla());
							//recursivo
							generarSubreglas(d.getCondicion().getRegla(), subReglas, false);	
						}						
					}					
				}					
			}
		}
		if(isFirstTime && subReglas.contains(t)){
			subReglas.remove(t);
		}
	}

	/**
	 * Gets the sistemas seleccionados.
	 *
	 * @return the sistemas seleccionados
	 */
	public ArrayList<String> getSistemasSeleccionados() {
		return sistemasSeleccionados;
	}

	/**
	 * Sets the sistemas seleccionados.
	 *
	 * @param sistemasSeleccionados the new sistemas seleccionados
	 */
	public void setSistemasSeleccionados(
			ArrayList<String> sistemasSeleccionados) {
		this.sistemasSeleccionados = sistemasSeleccionados;
	}

	/**
	 * Gets the lista sistemas disponibles.
	 *
	 * @return the lista sistemas disponibles
	 */
	public ArrayList<String> getListaSistemasDisponibles() {
		return listaSistemasDisponibles;
	}

	/**
	 * Sets the lista sistemas disponibles.
	 *
	 * @param listaSistemasDisponibles the new lista sistemas disponibles
	 */
	public void setListaSistemasDisponibles(
			ArrayList<String> listaSistemasDisponibles) {
		this.listaSistemasDisponibles = listaSistemasDisponibles;
	}

	/**
	 * Abrir archivo.
	 *
	 * @param arch the arch
	 */
	public void abrirArchivo(String arch) {

		int index = arch.lastIndexOf("/");
		String nombreArch = arch.substring(index + 1);
		try {
			FileInputStream archivo = new FileInputStream(arch);
			log.info(arch);
			int longitud = archivo.available();
			byte[] datos = new byte[longitud];
			archivo.read(datos);

			HttpServletResponse response = (HttpServletResponse) FacesContext
					.getCurrentInstance().getExternalContext().getResponse();
			response.setContentType("application/force-download");
			response.setHeader("Content-Disposition", "attachment;filename="
					+ nombreArch);
			response.setContentLength(datos.length);

			ServletOutputStream output = response.getOutputStream();
			output.write(datos);
			output.flush();
			output.close();
			FacesContext.getCurrentInstance().responseComplete();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Delete file uploaded.
	 *
	 * @param nameFileUploaded the name file uploaded
	 */
	public void deleteFileUploaded(String nameFileUploaded) {

		try {
			File file = new File(nameFileUploaded);
			if (!file.delete()) {
				facesMessages
						.add("El archivo a eliminar no existe actualmente.");
				log.debug("El archivo a eliminar no existe actualmente");
			} else {
				File directorio = file.getParentFile();
				if (directorio.isDirectory()
						&& directorio.listFiles().length == 0) {
					if (!directorio.delete())
						log
								.debug("La carpeta a eliminar no existe actualmente");
				}
			}
		} catch (SecurityException e) {
			facesMessages
					.add("No fue posible eliminar el archivo elegido por razones de seguridad.");
			log
					.info("No se cuentan con los permisos suficientes para eliminar el archivo");
			return;
		}

		this.getNamesAttachs().remove(nameFileUploaded);
		this.fileName = "";
		this.myfile = null;
	}

	/**
	 * Actualizar sistemas.
	 */
	public void actualizarSistemas() {
		log.info("Se actualizo la lista de sistemas");
	}

	/**
	 * Guardar archivo.
	 *
	 * @return the string
	 */
	public String guardarArchivo() {
		return "/Rational/generarRequerimiento.xhtml";
	}

	/**
	 * Listener.
	 *
	 * @param event the event
	 * @throws Exception the exception
	 */
	public void listener(UploadEvent event) throws Exception {
		if (myfile != null) {
			log.info("Solo se permite adjuntar un archivo. Para cambiar el archivo adjunto debe eliminar el actual.");
			facesMessages.add("Solo se permite adjuntar un archivo. Para cambiar el archivo adjunto debe eliminar el actual.");
			return;
		} else {
			UploadItem uploadItem = event.getUploadItem();
			File uploadedFile = null;
			if(PropertiesHelper.getInstance().getProperties("cq") == null){
				return;
			}

			if (uploadItem.isTempFile()) {
				uploadedFile = uploadItem.getFile();
			} else {
				log.info("isTemp es false. Este caso no esta contemplado");
				myfile = uploadItem.getData();
			}

			String path = PropertiesHelper.getInstance().getProperties("cq").getProperty("path.destino");
			String fileName = uploadItem.getFileName();

			// me fijo si es de windows
			if (fileName.contains("\\")) {
				String[] rutawin = fileName.split("\\\\");
				fileName = rutawin[rutawin.length - 1];
			}
			this.setFileName(fileName);
			
			if (path.charAt(path.length() - 1) != '/')
				path = path.concat("/");

			path = path.concat(RandomUtils.nextLong() + "/");

			log.info("El path para dejar los archivos es: " + path);

			log.info(fileName);
			log.info(uploadItem.getFile().getName());

			try {
				// crear directorio
				(new File(path)).mkdir();

				File file = new File(path, fileName);
				FileOutputStream out = new FileOutputStream(file);

				FileInputStream in = new FileInputStream(uploadedFile);

				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				myfile = buf;

				in.close();
				out.close();
			} catch (IOException e) {
				log.info("No fue posible adjuntar el archivo para el nuevo requerimiento debido a errores internos.");
				log.info("Problemas internos en el servidor para copiar el archivo a un directorio");
				facesMessages.add("No fue posible adjuntar el archivo para el nuevo requerimiento debido a errores internos.");
				// TODO se deberia lanzar una exception...
				return;
			}

			this.getNamesAttachs().add(path + fileName);
			log.info("Se ha cargado exitosamente el archivo " + fileName
					+ " En el directorio " + path);
		}
	}
	
	
	public void guardarRegistroSitaci(){
		if(this.registrosSitaci == null){
			this.registrosSitaci = new ArrayList<RegistroSitaci>();
		}
		this.registrosSitaci.add(this.registroSitaciEditando);
		this.registroSitaciEditando = new RegistroSitaci();
//		return "exito" ;
	}
	
	public void guardarRegistroEditado(){
		System.out.println("LO ACTUALIZO?" + this.registroSitaciParaEditar.getDescripcion());
	}
	
	public void eliminarRegistroSitaci(){
		this.registrosSitaci.remove(this.registroSitaciParaEditar);		
	}

	/** The myfile. */
	private byte[] myfile;

	/** The names attachs. */
	private List<String> namesAttachs = new ArrayList<String>();

	/** The cant files allowed. */
	private int cantFilesAllowed = 1;

	/** The file name. */
	private String fileName = new String("");

	/**
	 * Gets the file name.
	 *
	 * @return the file name
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the file name.
	 *
	 * @param fileName the new file name
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Gets the myfile.
	 *
	 * @return the myfile
	 */
	public byte[] getMyfile() {
		return myfile;
	}

	/**
	 * Sets the file.
	 *
	 * @param myfile the new file
	 */
	public void setFile(byte[] myfile) {
		this.myfile = myfile;
	}

	/**
	 * Gets the cant files allowed.
	 *
	 * @return the cant files allowed
	 */
	public int getCantFilesAllowed() {
		return cantFilesAllowed;
	}

	/**
	 * Sets the cant files allowed.
	 *
	 * @param cantFilesAllowed the new cant files allowed
	 */
	public void setCantFilesAllowed(int cantFilesAllowed) {
		this.cantFilesAllowed = cantFilesAllowed;
	}

	/**
	 * Gets the names attachs.
	 *
	 * @return the names attachs
	 */
	public List<String> getNamesAttachs() {
		return namesAttachs;
	}

	/**
	 * Sets the names attachs.
	 *
	 * @param namesAttachs the new names attachs
	 */
	public void setNamesAttachs(List<String> namesAttachs) {
		this.namesAttachs = namesAttachs;
	}

	/**
	 * Solo nombre.
	 *
	 * @param path the path
	 * @return the string
	 */
	public String soloNombre(String path) {
		File fichero = new File(path);
		return fichero.getName();
	}

//	/**
//	 * Gets the sistemas disponibles.
//	 *
//	 * @return the sistemas disponibles
//	 */
//	public ArrayList<SistemaInformatico> getSistemasDisponibles() {
//		return sistemasDisponibles;
//	}
//
//	/**
//	 * Sets the sistemas disponibles.
//	 *
//	 * @param sistemasDisponibles the new sistemas disponibles
//	 */
//	public void setSistemasDisponibles(
//			ArrayList<SistemaInformatico> sistemasDisponibles) {
//		this.sistemasDisponibles = sistemasDisponibles;
//	}

	/**
	 * Checks if is modificado.
	 *
	 * @return true, if is modificado
	 */
	public boolean isModificado() {
		return modificado;
	}

	/**
	 * Sets the modificado.
	 *
	 * @param modificado the new modificado
	 */
	public void setModificado(boolean modificado) {
		log.info("Se modifico y llego al controlador");
		this.modificado = modificado;
	}

	/**
	 * Gets the nombre regla.
	 *
	 * @return the nombre regla
	 */
	public String getNombreRegla() {		
		if(getMuestraRequerimientoInformatico() != null){
			if (getMuestraRequerimientoInformatico().getReglaAsistente() != null 
					&& getMuestraRequerimientoInformatico().getReglaAsistente().getNombre() != null) {
				nombreRegla = getMuestraRequerimientoInformatico().getReglaAsistente().getNombre();
			} else if (getMuestraRequerimientoInformatico().getReglaPseudocodigo() != null
					&& getMuestraRequerimientoInformatico().getReglaPseudocodigo().getNombre() != null) {
				nombreRegla = getMuestraRequerimientoInformatico().getReglaPseudocodigo().getNombre();
			}
		}else if(getPasajeRequerimientoInformatico() != null){
			if (getPasajeRequerimientoInformatico().getReglaAsistente() != null 
					&& getPasajeRequerimientoInformatico().getReglaAsistente().getNombre() != null) {
				nombreRegla = getPasajeRequerimientoInformatico().getReglaAsistente().getNombre();
			} else if (getPasajeRequerimientoInformatico().getReglaPseudocodigo() != null
					&& getPasajeRequerimientoInformatico().getReglaPseudocodigo().getNombre() != null) {
				nombreRegla = getPasajeRequerimientoInformatico().getReglaPseudocodigo().getNombre();
			}
		}
		return nombreRegla;
	}
	
	/**
	 * @param nombreRegla the nombreRegla to set
	 */
	public void setNombreRegla(String nombreRegla) {
		this.nombreRegla = nombreRegla;
	}


	/**
	 * Gets the nombre regla tabla decision.
	 *
	 * @return the nombre regla tabla decision
	 */
	public String getNombreReglaTablaDecision() {
		return nombreReglaTablaDecision;
	}

	/**
	 * Sets the nombre regla tabla decision.
	 *
	 * @param nombreReglaTablaDecision the new nombre regla tabla decision
	 */
	public void setNombreReglaTablaDecision(String nombreReglaTablaDecision) {
		this.nombreReglaTablaDecision = nombreReglaTablaDecision;
	}

	/**
	 * Actualiza nombre.
	 */
	public void actualizaNombre() {
		log.info("Actualizando nombre");
	}

	/**
	 * Buscar reglas decision.
	 */
	public void buscarReglasDecision() {
		if (this.nombreReglaTablaDecision == null) {
			log.info("El nombre para la busqueda de reglas es NULL");
			return;
		}

		log.info("Buscando las reglas cuyo nombre sean o contengan a "
				+ this.nombreReglaTablaDecision);

		Query query = entityManager
				.createQuery("" + "SELECT"
								+ " tablaDecision FROM TablaDecision as tablaDecision"
								+ " WHERE upper(tablaDecision.nombre) like :patron and lastVersion = true")
				.setParameter("patron",
						"%" + this.nombreReglaTablaDecision.toUpperCase() + "%");

		this.listTablaDecision = query.getResultList();

		query = entityManager
				.createQuery("" + "SELECT"
								+ " reglaPseudocodigo FROM ReglaPseudocodigo as reglaPseudocodigo"
								+ " WHERE upper(reglaPseudocodigo.nombre) like :patron and lastVersion = true")
				.setParameter("patron",
						"%" + this.nombreReglaTablaDecision.toUpperCase() + "%");

		this.listRPS = query.getResultList();

		if (listTablaDecision.size() == 0)
			log.info("LA LISTA NO REGLAS TD TIENEN NINGUN ELEMENTO");

		if (listRPS.size() == 0)
			log.info("LA LISTA DE RPS NO TIENEN NINGUN ELEMENTO");

	}

	/**
	 * Gets the list tabla decision.
	 *
	 * @return the list tabla decision
	 */
	public List<TablaDecision> getListTablaDecision() {
		return listTablaDecision;
	}

	/**
	 * Sets the list tabla decision.
	 *
	 * @param listTablaDecision the new list tabla decision
	 */
	public void setListTablaDecision(List<TablaDecision> listTablaDecision) {
		this.listTablaDecision = listTablaDecision;
	}

	/**
	 * Gets the list rps.
	 *
	 * @return the list rps
	 */
	public List<ReglaPseudocodigo> getListRPS() {
		return listRPS;
	}

	/**
	 * Sets the list rps.
	 *
	 * @param listRPS the new list rps
	 */
	public void setListRPS(List<ReglaPseudocodigo> listRPS) {
		this.listRPS = listRPS;
	}
	
	/**
	 * Sets the regla asistente.
	 *
	 * @param reglaAsistente the new regla asistente
	 */
	public void setReglaAsistenteMuestra(TablaDecision reglaAsistente){
		
		if(reglaAsistente == null){
			log.info("tablaDecision es NULL (parametro que llega dps de seleccionar");
			return;
		}
		this.setNombreReglaSeleccionada(reglaAsistente.getNombre());
		setMuestraRequerimientoInformatico(new MuestraRequerimientoInformatico());
		getMuestraRequerimientoInformatico().setReglaAsistente(reglaAsistente);
		getMuestraRequerimientoInformatico().setReglaPseudocodigo(null);
		this.setSistemasSeleccionados(null);
		this.setListaSistemasDisponibles(null);
		
		log.info("seteamos la regla por asistente: " + getMuestraRequerimientoInformatico().getReglaAsistente()==null ? "Tabla decision es NULL" : getMuestraRequerimientoInformatico().getReglaAsistente().getNombre());
	}
	
	/**
	 * Sets the regla pseudocodigo.
	 *
	 * @param reglaPseudocodigo the new regla pseudocodigo
	 */
	public void setReglaPseudocodigoMuestra(ReglaPseudocodigo reglaPseudocodigo) {
		
		if(reglaPseudocodigo == null){
			log.info("Regla por pseudocodigo es NULL (parametro que llega dps de seleccionar");
			return;
		}
		this.setNombreReglaSeleccionada(reglaPseudocodigo.getNombre());
		setMuestraRequerimientoInformatico(new MuestraRequerimientoInformatico());		
		getMuestraRequerimientoInformatico().setReglaPseudocodigo(reglaPseudocodigo);
		getMuestraRequerimientoInformatico().setReglaAsistente(null);
		this.setSistemasSeleccionados(null);
		this.setListaSistemasDisponibles(null);
		
		log.info("seteamos la regla por pseudocodigo: " + getMuestraRequerimientoInformatico().getReglaPseudocodigo()==null ? "Regla Pseudocodigo es NULL" : getMuestraRequerimientoInformatico().getReglaPseudocodigo().getNombre());
	}
	
	/**
	 * Sets the regla asistente pasaje.
	 *
	 * @param reglaAsistente the new regla asistente pasaje
	 */
	public void setReglaAsistentePasaje(TablaDecision reglaAsistente){
		
		if(reglaAsistente == null){
			log.info("tablaDecision es NULL (parametro que llega dps de seleccionar");
			return;
		}
		setPasajeRequerimientoInformatico(new PasajeRequerimientoInformatico());
		getPasajeRequerimientoInformatico().setReglaAsistente(reglaAsistente);
		getPasajeRequerimientoInformatico().setReglaPseudocodigo(null);
		this.setSistemasSeleccionados(null);
		this.setListaSistemasDisponibles(null);
		
		log.info("seteamos la regla por asistente: " + getPasajeRequerimientoInformatico().getReglaAsistente()==null ? "Tabla decision es NULL" : getPasajeRequerimientoInformatico().getReglaAsistente().getNombre());
	}
	
	/**
	 * Sets the regla pseudocodigo pasaje.
	 *
	 * @param reglaPseudocodigo the new regla pseudocodigo pasaje
	 */
	public void setReglaPseudocodigoPasaje(ReglaPseudocodigo reglaPseudocodigo) {
		
		if(reglaPseudocodigo == null){
			log.info("Regla por pseudocodigo es NULL (parametro que llega dps de seleccionar");
			return;
		}
		setPasajeRequerimientoInformatico(new PasajeRequerimientoInformatico());		
		getPasajeRequerimientoInformatico().setReglaPseudocodigo(reglaPseudocodigo);
		getPasajeRequerimientoInformatico().setReglaAsistente(null);
		this.setSistemasSeleccionados(null);
		this.setListaSistemasDisponibles(null);
		
		log.info("seteamos la regla por pseudocodigo: " + getPasajeRequerimientoInformatico().getReglaPseudocodigo()==null ? "Regla Pseudocodigo es NULL" : getPasajeRequerimientoInformatico().getReglaPseudocodigo().getNombre());
	}
	
	/**
	 * @return the listaTipoReq
	 */
	public ArrayList<SelectItem> getListaTipoReq() {
		if(this.listaTipoReq == null){
			this.cargarComboListaTipoReq();
		}
		return listaTipoReq;
	}

	/**
	 * @param listaTipoReq the listaTipoReq to set
	 */
	public void setListaTipoReq(ArrayList<SelectItem> listaTipoReq) {
		this.listaTipoReq = listaTipoReq;
	}
	
	/**
	 * @return the tipoReq
	 */
	public TipoRequerimiento getTipoReq() {
		return tipoReq;
	}

	/**
	 * @param tipoReq the tipoReq to set
	 */
	public void setTipoReq(TipoRequerimiento tipoReq) {
		this.tipoReq = tipoReq;
	}
	
	public boolean esMuestraDeDatos(){
		return tipoReq == TipoRequerimiento.MUESTRA_DE_DATOS;
	}
	public boolean esAnclajeDeAtributos(){
		return tipoReq == TipoRequerimiento.ANCLAJE_DE_ATRIBUTOS;
	}
	public boolean esPasajeDeReglas(){
		return tipoReq == TipoRequerimiento.PASAJE_DE_REGLAS;
	}

		
	//////////////////////////////////////////////////////////
	///////////////// ANCLAJE ATRIBUTOS///////////////////////
	//////////////////////////////////////////////////////////
	
	private List<Atributo> atributos = new ArrayList<Atributo>();
	
	public List<Atributo> getAtributos() {
		return atributos;
	}

	public void setAtributos(List<Atributo> atributos) {
		this.atributos = atributos;
	}

	private Boolean allSelected = Boolean.FALSE;
	
	public Boolean getAllSelected() {
		return allSelected;
	}

	public void setAllSelected(Boolean allSelected) {
		this.allSelected = allSelected;
	}

	public void addRemoveAtributo(Atributo atributo, Integer nroFila){
		/*Si ya estaba en la lista entonces se lo va a sacar de la lista
		 * porque el usuario destildo la opcion de mover*/
		if(atributos.contains(atributo)){
			log.info("Se desmarco la fila " + (nroFila+1) + " id: " + atributo.getId());
			atributo.setSelected(Boolean.FALSE);
			atributos.remove(atributo);
			return;
		}
		log.info("Se marco la fila " + (nroFila+1) + " id: " + atributo.getId());
		atributo.setSelected(Boolean.TRUE);
		atributos.add(atributo);
	}
	
	public void addRemoveAll(List<Atributo> atrs){
		if(allSelected.equals(Boolean.FALSE)){
			allSelected = Boolean.TRUE;
		}else if(allSelected.equals(Boolean.TRUE)){
			allSelected = Boolean.FALSE;
		}
		if(allSelected.equals(Boolean.FALSE)){
			for(Atributo atributo : atrs){
				if(!esAnclado(atributo)){
					log.info("Se desmarco id: " + atributo.getId());
					atributo.setSelected(Boolean.FALSE);					
				}
			}
			atributos = new ArrayList<Atributo>();
		}else{
			//se limpia la lista
			atributos = new ArrayList<Atributo>();
			for(Atributo atributo : atrs){
				if(!esAnclado(atributo)){
					log.info("Se marco id: " + atributo.getId());
					atributo.setSelected(Boolean.TRUE);
					atributos.add(atributo);
				}
			}
		}	
	}

	public boolean esAnclado(Atributo atr){
		//por def esta anclado (logico)
		boolean anclado = true;
		if(atr.isPersistible()){
			//si es fisico, se consulta
			anclado = atr.isAnclado();
		}
		return anclado;
	}

	public void setNombreReglaSeleccionada(String nombreReglaSeleccionada) {
		this.nombreReglaSeleccionada = nombreReglaSeleccionada;
	}

	public String getNombreReglaSeleccionada() {
		return nombreReglaSeleccionada;
	}

	public void setRegistrosSitaci(ArrayList<RegistroSitaci> registrosSitaci) {
		this.registrosSitaci = registrosSitaci;
	}

	public ArrayList<RegistroSitaci> getRegistrosSitaci() {
		return registrosSitaci;
	}

	public void setRegistroSitaciEditando(RegistroSitaci registroSitaciEditando) {
		this.registroSitaciEditando = registroSitaciEditando;
	}

	public RegistroSitaci getRegistroSitaciEditando() {
		return registroSitaciEditando;
	}

	public void setRegistroSitaciParaEditar(RegistroSitaci registroSitaciParaEditar) {
		this.registroSitaciParaEditar = registroSitaciParaEditar;
	}

	public RegistroSitaci getRegistroSitaciParaEditar() {
		return registroSitaciParaEditar;
	}
}