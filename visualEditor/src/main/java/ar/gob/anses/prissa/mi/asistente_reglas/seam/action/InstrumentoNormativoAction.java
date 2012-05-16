package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.RandomUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;
import org.richfaces.model.UploadItem;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Funcion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Instrumento;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.ReglaPseudocodigo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.custom.BaseHome;
import ar.gob.anses.prissa.mi.asistente_reglas.services.PersistenceService;

@Name("instrumentoNormativoManager")
@Scope(ScopeType.CONVERSATION)
public class InstrumentoNormativoAction extends BaseHome<Instrumento> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3848991720870976506L;

	@Logger
	private Log log;
	
	private String path = "/jboss/Workspace/trunk/asistente_reglas_merge/src/main/webapp/archivosInstrumentosNormativos";
	
	@In	PersistenceService persistenceService;
	
	private TreeNode rootNode = null;
    private List<Instrumento> instrumentosSeleccionados;
        
    private TreeNode nodoSeleccionado;
    
    private String nombreNodoActual=""; 
    
    @In(create=true, required=false) @Out(required=false)
    private String nombreInstrumento;
        
    @In(create=true, required=false) @Out(required=false)
    @Temporal(TemporalType.DATE)
	private Date fechaFirma;
	
    @In(create=true, required=false) @Out(required=false)
	@Temporal(TemporalType.DATE)
	private Date fechaVigencia;
	
    @In(create=true, required=false) @Out(required=false)
	@Temporal(TemporalType.DATE)
	private Date fechaAplicacion;
	
    @In(create=true, required=false) @Out(required=false)
	private String textoExplicativo;
    
    @In FacesMessages facesMessages;
    
    @In(create=true, required=false) @Out(required=false)
    private Boolean tipoNodo = new Boolean(false);

   
	private void addNodes(Instrumento padre, TreeNode<Instrumento> node, List<Instrumento> instrumentos) {
       for(Instrumento i : instrumentos){
        	if(i.getPadre() == padre && i.getEsCarpeta() && i.getActivo() != "N") {
        		TreeNodeImpl<Instrumento> nodeImpl = new TreeNodeImpl<Instrumento>();
                nodeImpl.setData(i);
                node.addChild(i.getDescripcion(), nodeImpl);
        		addNodes(i, nodeImpl, instrumentos);
        	}
        }
    }
    
    @SuppressWarnings("unchecked")
	private void loadTree() {
        List<Instrumento> instrumentos = new InstrumentoList().getResultList();
        
        rootNode = new TreeNodeImpl<Instrumento>();
        
        if (instrumentos.isEmpty()) {
        	TreeNodeImpl<Instrumento> carpetaRaiz = new TreeNodeImpl<Instrumento>();
            
            Instrumento i = new Instrumento();
            i.setDescripcion("Instrumentos Normativos");
            i.setEsCarpeta(true);
            i.setActivo("S");
            
            persistenceService.save(i);
            
            carpetaRaiz.setData(i);
            
            rootNode.addChild("root", carpetaRaiz);
        	return;
        }
        
        addNodes(null, rootNode, instrumentos);
    }
    
    @SuppressWarnings("unchecked")
	public void processSelection(NodeSelectedEvent event) {
    	limpiarInstancia();
    	
    	HtmlTree tree = (HtmlTree) event.getComponent();
    	
    	TreeNode currentNode = tree.getModelTreeNode(tree.getRowKey());
    	
    	if(currentNode == null)
    		log.debug("El nodo que se quiso seleccionar es null");
    	
    	nodoSeleccionado = currentNode;
    	this.setNombreNodoActual(((Instrumento)nodoSeleccionado.getData()).getDescripcion());
    	log.debug("El nombre del nodo seleccionado es "+((Instrumento)nodoSeleccionado.getData()).getDescripcion());
    }
    
    @SuppressWarnings("unchecked")
	public TreeNode getTreeNode() {
        if (rootNode == null) {
            loadTree();
        }
        return rootNode;
    }

	public Collection<Instrumento> getInstrumentosSeleccionados() {
		
		return instrumentosSeleccionados;
	}

	public void setInstrumentosSeleccionados(
			List<Instrumento> instrumentosSeleccionados) {
		this.instrumentosSeleccionados = instrumentosSeleccionados;
	}
	
	public void eliminaNodo() {
		if (rootNode == null) {
			log.debug("RootNode es null");
			return;
		}
		if (nodoSeleccionado == null) { 
			log.debug("NodoSeleccionado es null");			
			return;
		}
		
		if(((Instrumento) nodoSeleccionado.getData()).getPadre() == null){
			facesMessages.add("No se puede eliminar el nodo principal: Instrumentos Normativos.");
			return;
		}
		
		log.debug("Eliminar Nodo " + ((Instrumento)nodoSeleccionado.getData()).getDescripcion());
		//elimino todos los hijos y despues el padre
		eliminarInstrumento((Instrumento)nodoSeleccionado.getData());
		
		TreeNodeImpl<Instrumento> padre = (TreeNodeImpl<Instrumento>) nodoSeleccionado.getParent();
		
		if (padre.equals(rootNode)){
			facesMessages.add("No se puede eliminar el nodo principal: Instrumentos Normativos.");
			return;
		}
		
		if (padre == null){
			log.info("El padre es NULL");
			return;
		}
		
		padre.removeChild(((TreeNodeImpl<Instrumento>)nodoSeleccionado).getData().getDescripcion());
		
		log.info("Se ha eliminado el nodo seleccionado: "+((TreeNodeImpl<Instrumento>)nodoSeleccionado).getData().getDescripcion()+" en el arbol con todos sus respectivos hijos.");
		
		listInstruments = new ArrayList<Instrumento>();
		nodoSeleccionado = null;
	}
	
	public void eliminarInstrumento(Instrumento nodo) {
		if (nodo == null) return;
		
		// me fijo si no tiene hijos
		Query query = entityManager.createQuery("" +
				"SELECT" +
				" instrumento FROM Instrumento as instrumento" +
				" WHERE instrumento.padre = :inst")
				.setParameter("inst", nodo);
		
		ArrayList<Instrumento> lstInstrumento = (ArrayList<Instrumento>)query.getResultList();
		
		if (!lstInstrumento.isEmpty()) {
			for (Instrumento entidad : lstInstrumento) {
				eliminarInstrumento(entidad);
			}
		}
		
		//borro al nodo
		nodo.setActivo("N");
		persistenceService.save(nodo);
		
		
		/*Instrumento entidad = getPersistenceContext().find(Instrumento.class, nodo.getId());
		if (entidad == null) return;
		
		entidad.setActivo("N");
		
		getPersistenceContext().flush();*/
	}
	
	public boolean addInstrumentoHijo(TreeNodeImpl<Instrumento> padre){
		log.info("Armando la instancia de instrumento con los datos ingresados...");
		
		TreeNodeImpl<Instrumento> nodeImpl = new TreeNodeImpl<Instrumento>();
        
		if(!(validarNuevoHijo(padre))){
			log.debug("La validacion del instrumento a insertar no fue exitosa.");
			return false;
		}
		
		if(padre == null){
			log.debug("El nodo padre al cual se le iba a agregar un instrumento es NULL.");
			return false;
		}

		Instrumento i = new Instrumento();
		
		if(this.isManaged())
			i.setId(this.idAux);
		
		i.setDescripcion(nombreInstrumento.trim());
		i.setFechaAplicacion(fechaAplicacion);
		i.setFechaFirma(fechaFirma);
		i.setFechaVigencia(fechaVigencia);
		i.setTextoExplicativo(textoExplicativo);
		i.setEsCarpeta(tipoNodo);
		i.setPadre(padre.getData());
		i.setActivo("S");
		i.setAttachs(this.namesAttachs);
		
		this.namesAttachs = null;
		
		limpiarInstancia();

		log.info("Se armo exitosamente la instancia con los datos del nuevo instrumento a grabar");
		grabarInstrumento(i);
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public String addInstrumento() {
		log.info("Agregando un nuevo instrumento normativo...");
		
		tipoNodo = false;
		
		if (nodoSeleccionado == null) {
        	if (rootNode == null) 
        		rootNode = new TreeNodeImpl<Instrumento>();
        	
        	nodoSeleccionado = rootNode;
        	log.debug("NodoSeleccionado fue NULL y se toma como nodo seleccionado al rootNode");
        }
		
		//log.debug("Nombre del padre del nuevo instrumento "+((Instrumento)nodoSeleccionado.getData()).getDescripcion());
		
		if (!addInstrumentoHijo((TreeNodeImpl<Instrumento>)nodoSeleccionado)) 
			return "/altaInstrumento.xhtml";
		
		//nodoSeleccionado = null;
		clearInstance();
		viewInstOfFolderSelected();
		nodoSeleccionado=null;
		log.info("Instrumento normativo agregado exitosamente.");
		return "/instrumentoEdit.xhtml";
	}
	
	public void actualizaNombre() {
		
	}
	
	private void grabarInstrumento(Instrumento i) {
		
		if (i == null){
			log.debug("La instacia a persistir llego en NULL.");
			return;
		}
		
		log.info("Persistiendo el nuevo instrumento "+i.getDescripcion()+"...");
		
		
		//try {
			if (i.getId() != null) {
				Instrumento entidad = entityManager.find(Instrumento.class, i.getId());
				entidad.setDescripcion(i.getDescripcion());
				entidad.setFechaAplicacion(i.getFechaAplicacion());
				entidad.setFechaFirma(i.getFechaFirma());
				entidad.setFechaVigencia(i.getFechaVigencia());
				entidad.setTextoExplicativo(i.getTextoExplicativo());
				entidad.setEsCarpeta(i.getEsCarpeta());
				entidad.setAttachs(i.getAttachs());
				persistenceService.save(entidad);
				//entityManager.flush();
				log.info("El nuevo instrumento normativo ha sido actulizado exitosamente: "+i.getDescripcion());
			}
			else {
				persistenceService.save(i);
				/*setInstance(i);
				persist();*/
				log.info("El nuevo instrumento normativo ha sido adherido exitosamente: "+i.getDescripcion());
			}
		/*}
		catch (Exception e) {
			
		}*/
	}
	
	@In(create=true)
	EntityManager entityManager;
	public EntityManager getEntityManager() {return this.entityManager;}
	
	@Override
	protected Instrumento createInstance() {
		return new Instrumento();
	}
	
	public boolean validarNuevoHijo(TreeNodeImpl<Instrumento> padre){
		if(padre.getData()!=null) {
			if(!(padre.getData().getEsCarpeta())){
				facesMessages.add("Debe seleccionar una carpeta");
				return false;
			}
		}
		
		boolean ret = true;
		
		if(nombreInstrumento == null) {
			facesMessages.add("El nombre de instrumento ingresado no fue valido. Ingrese un nombre distinto de vacio o valido");
			ret = false;
		}
		else {
			if (nombreInstrumento.trim().equals("")) {
				facesMessages.add("El nombre de instrumento ingresado no fue valido. Ingrese un nombre distinto de vacio o valido");
				ret = false;
			}
		}
		
		//TODO esta validacion TIENE que estar, fue comentada porq no impedia avanzar
		/*if(existeElNombre(nombreInstrumento)){
			facesMessages.add("El nombre de instrumento que desea ingresar ya esta siendo utilizado. Modifiquelo para poder guardar.");
			ret = false;
		}*/

		
		if(!(tipoNodo)){
				if(fechaVigencia==null){
					facesMessages.add("No se pudo grabar el elemento: El instrumento debe tener una fecha de vigencia");
					ret = false;
				}
				if(fechaAplicacion==null){
					facesMessages.add("No se pudo grabar el elemento: El instrumento debe tener una fecha de aplicacion");
					ret = false;
				}
				if(fechaFirma==null){
					facesMessages.add("No se pudo grabar el elemento: El instrumento debe tener una fecha de firma");
					ret = false;
				}
				if(textoExplicativo.trim().equals("") || textoExplicativo == null){
					facesMessages.add("No se pudo grabar el elemento: El instrumento debe tener un texto explicativo");
					ret = false;
				}
				
				if (fechaAplicacion != null && fechaVigencia != null) {
					if (fechaAplicacion.before(fechaVigencia)) {
						facesMessages.add("La fecha de Aplicacion debe ser mayor o igual a la fecha de vigencia");
						ret = false;
					}
				}
				
				
				if (fechaVigencia != null && fechaFirma != null) {
					if (fechaVigencia.before(fechaFirma)) {
						facesMessages.add("La fecha de Vigenca debe ser mayor o igual a la fecha de Firma");
						ret = false;
					}
				}
				
		}
		return ret;
	}
	
	public void modifyInstrumento() {
		//grabo el nodo seleccionado en la base
		if (nodoSeleccionado != null) {
			grabarInstrumento((Instrumento)nodoSeleccionado.getData());
		}
	}
		
	public void limpiarInstancia(){
		nombreInstrumento = "";
		fechaAplicacion = null;
		fechaFirma = null;
		fechaVigencia = null;
		textoExplicativo = "";
		tipoNodo=false;
	}
	
    public String getCreatedMessage() { return "Nuevo instrumento creado exitosamente"; }
    public String getUpdatedMessage() { return "Instrumento actualizado exitosamente"; }
    public String getDeletedMessage() { return "Instrumento borrado exitosamente"; }

    
    private boolean existeElNombre(String nameInst){
	    Query query = entityManager.createQuery("" +
				"SELECT" +
				" instrumento FROM Instrumento as instrumento" +
				" WHERE instrumento.descripcion = :nombreInst")
				.setParameter("nombreInst", nameInst);
    	
    	List<String> namesInstMatches = query.getResultList();
	    
    	if(this.isManaged())
    		if(nameInst.equals(this.oldNameNormativeInstrument))
    			return false;
    	
    	
    	return namesInstMatches.size() != 0;
    }
    
    public boolean getHaySeleccionado() {
    	return nodoSeleccionado != null;
    }
	
	/*====================================================NUEVOS METODOS===============================================*/
	public String goAltaInstrumento(){
		
		return "/altaInstrumento.xhtml";
	}
	
	public String cancelEdit(){
		limpiarInstancia();
		clearInstance();
		namesAttachs = new ArrayList<String>();
		return "/instrumentoEdit.xhtml";
	}
	
	
	public void myPing(){
		log.info("Llegamos al servidor");
	}
	
	public void newNormativeFolder(){
		
		if(this.newCurrentFolder == null){
			return;
		}
		
		if(this.newCurrentFolder.getDescripcion().trim().equals("")){
			facesMessages.add("No se puede crear una carpeta con nombre vacio");
			return;
		}
		
		if(nodoSeleccionado == null)
			return;
		
		
		
		if(existeElNombre(this.newCurrentFolder.getDescripcion())){
			facesMessages.add("El nombre ya existe, por favor ingrese otro nombre.");
			return;
		}
		
		this.newCurrentFolder.setPadre(((Instrumento)nodoSeleccionado.getData()));
		this.newCurrentFolder.setEsCarpeta(true);
		this.newCurrentFolder.setTextoExplicativo(null);
		this.newCurrentFolder.setFechaAplicacion(null);
		this.newCurrentFolder.setFechaFirma(null);
		this.newCurrentFolder.setFechaVigencia(null);
		this.newCurrentFolder.setActivo("S");
		
		persistenceService.save(this.newCurrentFolder);
		/*this.newCurrentFolder = entityManager.merge(this.newCurrentFolder);
		entityManager.flush();*/
		
		TreeNode<Instrumento> newNode = new TreeNodeImpl<Instrumento>();
		newNode.setData(newCurrentFolder);
		
		nodoSeleccionado.addChild(newCurrentFolder.getDescripcion(),newNode);
		
		this.newCurrentFolder = new Instrumento();
		log.info("Nueva carpeta agregada correctamente.");
	}
	
	public void exit(){
		log.info("Salimos de Instrumentos Normativos.");
	}
	
	
	public void deleteNormativeInst(Instrumento i){
		
		Query query = getPersistenceContext().createQuery("" +
				"SELECT" +
				" funcion FROM Funcion as funcion join funcion.instrumentosNormativos as instrumentos" +
				" WHERE instrumentos = :inst")
				.setParameter("inst", i);
		
		List<Funcion> listFucionesUsanInst = (List<Funcion>)query.getResultList();
		
		if(listFucionesUsanInst.size() != 0){
			log.info("No se puede eliminar el instrumento normativo debido a que esta siendo usada por una funcion");
			facesMessages.add("No se puede eliminar el instrumento normativo debido a que esta siendo usada por una funcion");
			return;
		}
		
		query = getPersistenceContext().createQuery("" +
				"SELECT" +
				" tablaDecision FROM TablaDecision as tablaDecision join tablaDecision.instrumentos as instrumentos" +
				" WHERE instrumentos = :ins")
				.setParameter("ins", i);
		
		List<TablaDecision> listReglasUsanInst = (List<TablaDecision>)query.getResultList();
		
		if(listReglasUsanInst.size() != 0){
			log.info("No se puede eliminar el instrumento normativo debido a que esta siendo usada por una regla por asistente");
			facesMessages.add("No se puede eliminar el instrumento normativo debido a que esta siendo usada por una regla por asistente");
			return;
		}
		
		
		query = getPersistenceContext().createQuery("" +
				"SELECT" +
				" reglaPseudocodigo FROM ReglaPseudocodigo as reglaPseudocodigo join reglaPseudocodigo.instrumentosNormativos as instrumentos" +
				" WHERE instrumentos = :i")
				.setParameter("i", i);
		
		List<ReglaPseudocodigo> listReglasPsUsanInst = (List<ReglaPseudocodigo>)query.getResultList();
		
		if(listReglasPsUsanInst.size() != 0){
			log.info("No se puede eliminar el instrumento normativo debido a que esta siendo usada por una regla por Pseudocodigo");
			facesMessages.add("No se puede eliminar el instrumento normativo debido a que esta siendo usada por una regla por Pseudocodigo");
			return;
		}
		
		this.eliminarInstrumento(i);
				
		listInstruments.remove(i);
	}
	
	public void viewInstOfFolderSelected(){
		
		if(nodoSeleccionado == null){
			log.info("Debe seleccionar una carpeta");
			return;
		}
		
		Query query = entityManager.createQuery("" +
				"SELECT" +
				" instrumento FROM Instrumento as instrumento" +
				" WHERE instrumento.padre = :inst and instrumento.esCarpeta = false and activo <>'N'")
				.setParameter("inst", ((Instrumento)nodoSeleccionado.getData()));
		
		this.listInstruments = (ArrayList<Instrumento>)query.getResultList();
		
		if(listInstruments.size() == 0)
			facesMessages.add("No se encontraron datos.");
	}
	
	public void searchNormativeInstrument(){
		if(nameInstrument == null){
			facesMessages.add("Para realizar la busqueda debe completar el nombre o una parte de el.");
			return;
		}
		
		if(nameInstrument.equals("")){
			facesMessages.add("Para realizar la busqueda debe completar el nombre o una parte de el.");
			return;
		}
		
		if (nameInstrument.length() < 3) {
			facesMessages.add("Debe ingresar al menos 3 caracteres para realizar la busqueda.");
			return;
		}
		
		log.info("Buscado los intrumentos normativos de la carpeta seleccionada. Los resultados de la busqueda se dejaran en una variable de instancia");
		
		Query query = entityManager.createQuery("" +
				"SELECT" +
				" instrumento FROM Instrumento as instrumento" +
				" WHERE upper(instrumento.descripcion) like :patron and instrumento.esCarpeta = false and activo <>'N'")
				.setParameter("patron", "%"+nameInstrument.toUpperCase()+"%");
		
		this.listInstruments = (ArrayList<Instrumento>) query.getResultList();
		log.info("El tamaño de la lista de la busqueda es "+listInstruments.size());
	}
	
	public void resetSearchNormativeInstrument(){
		nameInstrument = new String("");
		searchNormativeInstrument();
		log.info("Reseteando la busqueda de instrumentos normativos");
	}
	
	
	/**
	 * Metodo que maneja los instrumentos que se seleccionaron
	 * mediante un radioButton para que sean movidos a otra carpeta.
	 * La lista que maneja internamente este metodo comienza
	 * inicialmente vacia. luego si no estaba en la lista se lo agrega
	 * pero si ya estaba entonces se lo saca ya que a este metodo
	 * se lo invoca cada vez que se tilda o destilda la opcion para el 
	 * instrumento.
	 * 
	 * @param i
	 */
	public void managerNormativeInstrumentToMove(Instrumento i){
		
		/*Si ya estaba en la lista entonces se lo va a sacar de la lista
		 * porque el usuario destildo la opcion de mover*/
		if(listNormativeInstToMoveOtherFolder.contains(i)){
			listNormativeInstToMoveOtherFolder.remove(i);
			return;
		}
		
		listNormativeInstToMoveOtherFolder.add(i);
	}
	
	public String rescueCurrentNodeOnMainTree(){
		if (listNormativeInstToMoveOtherFolder.isEmpty()) {
			facesMessages.add("Debe seleccionar al menos un instrumento para mover a otra carpeta");
			return null;
		}
		for (Iterator iterator = this.listNormativeInstToMoveOtherFolder.iterator(); iterator.hasNext();) {
			Instrumento inst = (Instrumento) iterator.next();
		}
				
		return "/moverInstrumentos.xhtml";
	}
	
	public void processSelectionPanelmoves(NodeSelectedEvent event){
		HtmlTree tree = (HtmlTree) event.getComponent();
    	
    	TreeNode currentNode = tree.getModelTreeNode(tree.getRowKey());
    	
    	auxOfNodeSelected = (TreeNodeImpl<Instrumento>)currentNode;
    	
    	if(auxOfNodeSelected.getData() == null){
    		log.info("El nodo seleecionado es NULL");
    		return;
    	}
	}
	
	
	/**
	 * Metodo que se encarga de reflejar los cambios hechos en la DB
	 */
	public String moveSelectedInst(){
		listInstruments = null;
		Instrumento auxInst,auxPadre;
		
		
		if(auxOfNodeSelected == null || auxOfNodeSelected.getData() == null){
    		log.info("El nodo seleecionado es NULL!!!");
    		return null;
    	}
		
		
		log.info("Carpeta destino es: "+((Instrumento)auxOfNodeSelected.getData()).getDescripcion()+ " - id: "+((Instrumento)auxOfNodeSelected.getData()).getId());
		
		for (Iterator iterator = listNormativeInstToMoveOtherFolder.iterator(); iterator.hasNext();) {
			Instrumento i = (Instrumento) iterator.next();
			
			
			auxInst = entityManager.find(Instrumento.class, i.getId());
			auxPadre = entityManager.find(Instrumento.class, ((Instrumento)auxOfNodeSelected.getData()).getId());
			auxInst.setPadre(auxPadre);
			
			persistenceService.save(auxInst);
			//entityManager.merge(auxInst);
		}
		
		//entityManager.flush();
		
		log.info("Se actualizo con exito los instrumentos seleccionados");
		listNormativeInstToMoveOtherFolder = new ArrayList<Instrumento>();
		viewInstOfFolderSelected();
		return "/instrumentoEdit.xhtml";
	}
	
	public void listener(UploadEvent event) throws Exception {
		UploadItem uploadItem = event.getUploadItem();
		File uploadedFile = null;
		Properties props = null;
		
		if(uploadItem.isTempFile())
			uploadedFile = uploadItem.getFile();
		else{
			log.info("isTemp es false. Este caso no esta contemplado");
			myfile = uploadItem.getData();
		}
		
		URL url = getClass().getResource("Parametros.properties");

		if(url != null) {
			try {
				InputStream in = url.openStream();
				props = new Properties();
				props.load(in);
			}catch(IOException ioe) {
				log.info("NO se encuentra el archivo de propiedades Parametros.properties");
				log.info("No fue posible adjuntar el archivo para el nuevo instrumento normativo debido a errores internos.");
				facesMessages.add("No fue posible adjuntar el archivo para el nuevo instrumento normativo debido a errores internos.");
				
				//TODO deberia retornar ao relazar la exception para que se vaya a la pantalla de exception de la aplicacion???
				return;
			}
		}else{
			log.info("No fue posible adjuntar el archivo para el nuevo instrumento normativo debido a errores internos.");
			log.info("NO se encuentra el archivo de propiedades Parametros.properties");
			facesMessages.add("No fue posible adjuntar el archivo para el nuevo instrumento normativo debido a errores internos.");
			
			//TODO deberia retornar ao relazar la exception para que se vaya a la pantalla de exception de la aplicacion???
			return;
		}
		
		String path = props.getProperty("path.destino");
		String fileName = uploadItem.getFileName();
		
		//me fijo si es de windows
		if (fileName.contains("\\")) {
				String[] rutawin = fileName.split("\\\\");
				fileName = rutawin[rutawin.length-1];
		}
		
		if (path.charAt(path.length() - 1) != '/') path = path.concat("/");
		
		path = path.concat(RandomUtils.nextLong() + "/");
		
		log.info("El path para dejar los archivos es: "+path);
		
		log.info(fileName);
		log.info(uploadItem.getFile().getName());
		
		try {
			//crear directorio
			(new File(path)).mkdir();
			
			File file = new File(path,fileName);
            FileOutputStream out = new FileOutputStream(file);
            
            FileInputStream in = new FileInputStream(uploadedFile);
            
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0){
              out.write(buf, 0, len);
            }
            
            in.close();
            out.close();
         }
         catch(IOException e){ 
        	log.info("No fue posible adjuntar el archivo para el nuevo instrumento normativo debido a errores internos.");
            log.info("Problemas internos en el servidor para copiar el archivo a un directorio");
            facesMessages.add("No fue posible adjuntar el archivo para el nuevo instrumento normativo debido a errores internos.");
            //TODO se deberia lanzar una exception...
            return;
         }

        this.namesAttachs.add(path+fileName);
        log.info("Se ha cargado exitosamente el archivo "+fileName+ "En el directorio "+path);
	}

	
	public void deleteFileUploaded(String nameFileUploaded){
		
		
		try {
			File file = new File(nameFileUploaded);
			if( !file.delete() ){
				facesMessages.add("El archivo a eliminar no existe actualmente.");
				log.debug("El archivo a eliminar no existe actualmente");
			}else{
				File directorio = file.getParentFile();
				if( directorio.isDirectory() && directorio.listFiles().length == 0){
					if( !directorio.delete() )
						log.debug("La carpeta a eliminar no existe actualmente");
				}
			}
		} catch (SecurityException e) {
			facesMessages.add("No fue posible eliminar el archivo elegido por razones de seguridad.");
			log.info("No se cuentan con los permisos suficientes para eliminar el archivo");
			return;
		}
		
		namesAttachs.remove(nameFileUploaded);
	}
	
	public void abrirArchivo(String arch) {
		
		int index = arch.lastIndexOf("/");
		String nombreArch = arch.substring(index+1);
		try
		{
			FileInputStream archivo = new FileInputStream(arch);
		    log.info(arch);
		    int longitud = archivo.available();
		    byte[] datos = new byte[longitud];
		    archivo.read(datos);
		    
			HttpServletResponse response = (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
		    response.setContentType("application/force-download");
		    response.setHeader("Content-Disposition","attachment;filename=" + nombreArch);
		    response.setContentLength(datos.length);
		    
		    
		    ServletOutputStream output = response.getOutputStream();
			output.write(datos);
			output.flush();
	        output.close();
	        FacesContext.getCurrentInstance().responseComplete();
		}
		catch(Exception e)
		{ 
		    e.printStackTrace();
		}
		
	}
	
	public void setInstrumentoNormativoId(Long id) {
		setId(id);
	}

	public Long getInstrumentoNormativoId(){
		return (Long) getId();
	}

	public void wire() {
		getInstance();
		
		if(nodoSeleccionado == null)
			log.info("NodoSeleccionado es NULL");
		else
			log.info("NodoSeleccionado NO ES NULL, por eso NUEVO se evalua como false");
		
		if(this.isManaged()){
			log.info("Wire");
			
			
			this.idAux = this.instance.getId();
			this.oldNameNormativeInstrument = this.instance.getDescripcion();
						
			if(flagFechas){
				this.newCurrentFolder.setDescripcion(new String(""));
				this.fechaAplicacion = this.instance.getFechaAplicacion();
				this.fechaFirma = this.instance.getFechaFirma();
				this.fechaVigencia = this.instance.getFechaVigencia();
				this.nombreInstrumento = this.instance.getDescripcion();
				this.textoExplicativo = this.instance.getTextoExplicativo();
				flagFechas = false;
			}
			
			this.tipoNodo = this.instance.getEsCarpeta();
			
			nodoSeleccionado = new TreeNodeImpl<Instrumento>();
			nodoSeleccionado.setData(this.instance.getPadre());
			
			this.namesAttachs = this.instance.getAttachs();
		}
	}

	public boolean isWired() {
		return true;
	}

	public Instrumento getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	
	/*Solo se utiliza pero no esta contemplado el caso de que isTempFile se FALSE*/
	private byte[] myfile;
	
	List<String> namesAttachs = new ArrayList<String>();
	
	private Long idAux;
	
	/*Variable usada para el EDIT de un instrumento*/
	private String oldNameNormativeInstrument = new String("");
	
	private int cantFilesAllowed=10;
	
	private String fileName = new String("");
	
	private String nameInstrument = new String("");
	
	private String nameNormativeInstrumentFolder = new String("CARPETA PRUEBA CABLEADA");
	
	@In(required=false) @Out(required=false)
	ArrayList<Instrumento> listInstruments;
	
	private ArrayList<Instrumento> listNormativeInstToMoveOtherFolder = new ArrayList<Instrumento>();
	
	/*Variable auxiliar para el nodo selecionado en el arbol principal*/
	private TreeNodeImpl<Instrumento> auxOfNodeSelected;
	
	private Instrumento newCurrentFolder = new Instrumento();
	
	private boolean flagFechas = true;
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getNameNormativeInstrumentFolder() {
		return nameNormativeInstrumentFolder;
	}

	public void setNameNormativeInstrumentFolder(
			String nameNormativeInstrumentFolder) {
		this.nameNormativeInstrumentFolder = nameNormativeInstrumentFolder;
	}

	public String getNameInstrument() {
		return nameInstrument;
	}

	public void setNameInstrument(String nameInstrument) {
		this.nameInstrument = nameInstrument;
	}

	public Instrumento getNewCurrentFolder() {
		return newCurrentFolder;
	}

	public void setNewCurrentFolder(Instrumento newCurrentFolder) {
		this.newCurrentFolder = newCurrentFolder;
	}

	public byte[] getMyfile() {
		return myfile;
	}

	public void setFile(byte[] myfile) {
		this.myfile = myfile;
	}

	/*Siempre vamos a retornar un mas para q no se acabe nunca la cantidad de 
	 * archivos permitidos para la carga*/
	public int getCantFilesAllowed() {
		return cantFilesAllowed++;
	}

	public void setCantFilesAllowed(int cantFilesAllowed) {
		this.cantFilesAllowed = cantFilesAllowed;
	}

	public List<String> getNamesAttachs() {
		return namesAttachs;
	}

	public void setNamesAttachs(List<String> namesAttachs) {
		this.namesAttachs = namesAttachs;
	}
	
	public String soloNombre(String path) {
		File fichero=new File(path);
		return fichero.getName();
	}

	public void setNombreNodoActual(String nombreNodoActual) {
		this.nombreNodoActual = nombreNodoActual;
	}

	public String getNombreNodoActual() {
		return nombreNodoActual == null ? "" : nombreNodoActual;
	}

}
	