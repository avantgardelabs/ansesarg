package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;



import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Accion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.AccionReglaPseudocodigo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Dominio;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Funcion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Instrumento;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.ReglaFilaPseudocodigo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.ReglaPseudocodigo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Usuario;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.custom.BaseHome;
import ar.gob.anses.prissa.mi.asistente_reglas.services.PersistenceService;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

@Scope(ScopeType.CONVERSATION)
@Name("reglaPseudocodigoHome")
@SuppressWarnings("unchecked")
public class ReglaPseudoCodigoHome extends BaseHome<ReglaPseudocodigo>{
	
	private static final long serialVersionUID = 999426568179067865L;
	
	@In
	PersistenceService persistenceService;
	
	@Logger 
	Log log;
	@In(create = true,required=false) @Out(required=false) 
	List<Atributo> atributos = new ArrayList<Atributo>();
	
	List<Atributo> atributosSeleccionados = new ArrayList<Atributo>();
	
	@In 
	FacesMessages facesMessages;
	
	private boolean filaEditada=false;
	
	//File archivo;
	
	private List<AccionReglaPseudocodigo> acciones = new ArrayList<AccionReglaPseudocodigo>();	
	public List<Atributo> getAtributosSeleccionados() {
		return atributosSeleccionados;
	}

	public void setAtributosSeleccionados(List<Atributo> atributosSeleccionados) {
		this.atributosSeleccionados = atributosSeleccionados;
	}

	private TreeNode rootNode = null; 
	private TreeNode nodoSeleccionado = null;
	
	@In(create = true,required=false) @Out(required=false) 
	String msgError;
	
	/*
	 * Variables para agregar acciones de diferentes dominios a la lista de acciones
	 */
	private Dominio dominioFiltroCondiciones;
	
	@In(required=false) @Out(required=false) 
	private ArrayList<Accion> accionesDisponibles;
	
	/*
	 **********************************************************************
	 */
	private ArrayList<Dominio> dominiosRegistrados;
	
	private TreeNode rootNodeInstrumento = null;
	private TreeNode nodoSeleccionadoInstrumento = null;
	/*private List<Entidad> entidadesDisponibles = new ArrayList<Entidad>();*/
	
	/**************************************VARIABLES NUEVAS**************************************/
	
	@In(required=false) @Out(required=false)
	private Entidad entidadActual = new Entidad();
	
	@In(required=false) @Out(required=false)
	Atributo atributoActual = new Atributo();
	
	private List<Entidad> nodos = new ArrayList<Entidad>();
		
	ReglaFilaPseudocodigo reglaFilaPseudocodigoActual = new ReglaFilaPseudocodigo();
	
	@In(required=false) @Out(required=false) 
	ArrayList<Accion> accionesSeleccionadas;
	
	List<Funcion> funcionesList;
	
	private String nombreReglaAuxManaged;

	private String nameInstrument = new String("");

	private ArrayList<Instrumento> listInstruments = new ArrayList<Instrumento>();

	private boolean reeditado=false;
		
	/**************************************GETTERS & SETTERS ************************************/
	
	
	public List<Funcion> getFuncionesList() {
		if(funcionesList == null){
			FuncionList funcionList = new FuncionList();
			this.funcionesList = funcionList.getMyResultList();
		}
		
		if(this.isManaged()){
			funcionesList.removeAll(this.instance.getFunciones());
		}
		
		return funcionesList;
	}

	public String getNameInstrument() {
		return nameInstrument;
	}

	public void setNameInstrument(String nameInstrument) {
		this.nameInstrument = nameInstrument;
	}

	public ArrayList<Instrumento> getListInstruments() {
		return listInstruments;
	}

	public void setListInstruments(ArrayList<Instrumento> listInstruments) {
		this.listInstruments = listInstruments;
	}

	public void setFuncionesList(List<Funcion> funcionesList) {
		this.funcionesList = funcionesList;
	}
	
	
	public ArrayList<Accion> getAccionesSeleccionadas() {
		Set<Accion> l = new HashSet<Accion>();
		//ver aca de usar set
		if (accionesSeleccionadas != null) {
			for (Accion a : accionesSeleccionadas) {
				if (!reglaFilaPseudocodigoActual.getAcciones().contains(a)) {
					l.add(a);
				}
			}
		}
		return new ArrayList(l);
	}
	
	public ArrayList<Accion> getAccionesSeleccionadasFila() {
		Set<Accion> l = new HashSet<Accion>();
		
		log.info("1");
		
		for (Accion a : this.accionesSeleccionadas) {
			log.info("2");
			if (!this.reglaFilaPseudocodigoActual.getAcciones().contains(a)) {
				log.info("3");
				l.add(a);
			}
		}
		return new ArrayList(l);
	}
	
	public void setAccionesSeleccionadasFila(ArrayList<Accion> ala) {}

	public void setAccionesSeleccionadas(ArrayList<Accion> accionesSeleccionadas) {
		this.accionesSeleccionadas = accionesSeleccionadas;
	}
	
	public void actualizar(){
		log.info("actualizando lista");
	}
		
	public ReglaFilaPseudocodigo getReglaFilaPseudocodigoActual() {
		return reglaFilaPseudocodigoActual;
	}

	public void setReglaFilaPseudocodigoActual(
			ReglaFilaPseudocodigo reglaFilaPseudocodigoActual) {
		this.reglaFilaPseudocodigoActual = reglaFilaPseudocodigoActual;
	}
	
	public Entidad getEntidadActual() {
		return entidadActual;
	}

	public void setEntidadActual(Entidad entidadActual) {
		this.entidadActual = entidadActual;
	}

	public Atributo getAtributoActual() {
		return atributoActual;
	}

	public void setAtributoActual(Atributo atributoActual) {
		
		if(atributoActual != null){
			log.info("atributo elegido: "+atributoActual.getNombre()+" "+
					atributoActual.getDescripcion()+" "+
					atributoActual.getTipoDato()+" "+
					atributoActual.getEntidad()==null?"Tiene la entidad vacia":atributoActual.getEntidad().getNombre()+" "+
					atributoActual.getTablaDecision());
		}
		this.atributoActual = atributoActual;
	}

	public void setNodoSeleccionadoInstrumento(
			TreeNode nodoSeleccionadoInstrumento) {
		this.nodoSeleccionadoInstrumento = nodoSeleccionadoInstrumento;
	}

	public TreeNode getNodoSeleccionadoInstrumento() {
		return nodoSeleccionadoInstrumento;
	}

	public void setRootNodeInstrumento(TreeNode rootNodeInstrumento) {
		this.rootNodeInstrumento = rootNodeInstrumento;
	}

	public TreeNode getRootNodeInstrumento() {
		return rootNodeInstrumento;
	}

	public void setReglaPseudocodigoId(Long id) {
		setId(id);
	}

	public Long getReglaPseudocodigoId(){
		return (Long) getId();
	}

	@Override
	protected ReglaPseudocodigo createInstance() {
		ReglaPseudocodigo reglaPseudocodigo = new ReglaPseudocodigo();
		/*archivo = new File("reglasPorPseudo.txt");
		try {
			  // A partir del objeto File creamos el fichero físicamente
			  if (archivo.createNewFile())
			    log.info("El fichero se ha creado correctamente");
			  else
			    log.info("No ha podido ser creado el fichero");
			} catch (IOException ioe) {
			  ioe.printStackTrace();
			}*/
		return reglaPseudocodigo;
	}

	public void wire() {
		getInstance();
		if(this.instance != null && this.instance.getId() != null) {
			this.accionesSeleccionadas = new ArrayList<Accion>();
			this.accionesSeleccionadas.addAll(this.instance.getAcciones());
			this.nombreReglaAuxManaged = this.instance.getNombre();
		}
	}

	public boolean isWired() {
		return true;
	}

	public ReglaPseudocodigo getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public void setAcciones(List<AccionReglaPseudocodigo> lista){
		this.acciones = lista;
	}

	public List<AccionReglaPseudocodigo> getAcciones() {
		return acciones;
	}

	public String getCreatedMessage() { return "Regla guardada en modo borrador"; }
    public String getUpdatedMessage() { return "Regla actualizada exitosamente"; }
    public String getDeletedMessage() { return "Regla borrada exitosamente"; }

	public void getAtributosCondicionDeEntidad() {
		/*
		if(nodoSeleccionado == null){
			log.info("nodoSeleccionado es NULL");
			return;
		}
		
		if(nodoSeleccionado.getData().getClass().getSimpleName().equals("Atributo")){
			log.info("El nodo seleccionado para agregar es del tipo Atributo");
			return;
		}
		*/
		
		if (entidadActual == null) return;
		
		Query query;
		query = persistenceService.createQuery("" +
				"SELECT" +
				" atributo FROM Atributo as atributo" +
				" WHERE atributo.entidad = :ent")
				.setParameter("ent", entidadActual);
			log.warn("Buscando los Atributos de entidad " + entidadActual.getNombre());
			log.warn(query.getResultList().size());
			this.atributos = query.getResultList();
			return;
	}
	
	
    public List<Atributo> getAtributos(){
    	return this.atributos;
    }

    public void setAtributos(List<Atributo> atributos) {
		this.atributos = atributos;
	}

    /*
	public void setEntidadesDisponibles(List<Entidad> entidadesDisponibles) {
		this.entidadesDisponibles = entidadesDisponibles;
	}

	public List<Entidad> getEntidadesDisponibles() {
		return entidadesDisponibles;
	}*/
	
	/**********************************************METODOS AGREGAR - ADD ****************************************/
	
    public String init() {
    	this.atributos=new ArrayList<Atributo>();
    	if (this.acciones != null) this.acciones.clear();
    	if (this.accionesSeleccionadas != null) this.accionesSeleccionadas.clear();
    	this.atributoActual = new Atributo();
    	if (this.dominiosRegistrados != null) this.dominiosRegistrados.clear();
    	this.dominioFiltroCondiciones = new Dominio();
    	this.entidadActual=new Entidad();
    	if (this.atributosSeleccionados != null) this.atributosSeleccionados.clear();
    	if (this.listInstruments!= null) this.listInstruments.clear();
    	return "/reglaPseudocodigoEdit.xhtml";
    }
    	
    	
    
	public void addEntidad(){
		
		if (entidadActual == null) {
			msgError = "Debe seleccionar una entidad";
			return;
		}
		
		if(entidadActual.getNombre() == null){
			msgError = "Debe seleccionar una entidad";
		}
		
		if(rootNode.getChild(entidadActual.getNombre()) != null)
			return;
		
		//TODO se usa nodos y entidadesDisponibles????
		this.nodos.add(this.entidadActual);
		//this.entidadesDisponibles.add(this.entidadActual);
		
		TreeNode t = new TreeNodeImpl();
		t.setData(this.entidadActual);
		rootNode.addChild(this.entidadActual.getNombre(),t);
	}
	
	public void addEntidadAtributo(){
		msgError = "";
		if (entidadActual == null) {
			msgError = "Debe seleccionar una entidad";
			return;
		}
		
		if(entidadActual.getNombre() == null){
			msgError = "Debe seleccionar una entidad";
		}
		
		if (atributoActual == null) {
			msgError = "Debe seleccionar un atributo";
			return;
		}
		
		if(atributoActual.getNombre() == null){
			msgError = "Debe seleccionar un atributo";
			return;
		}
		
		//me fijo si existe el atributo
		if (this.instance.getAtributos().contains(atributoActual)) {
			msgError = "El atributo ya esta agregado a la lista";
			return;
		}
		else {
			//lo agrego ahora... y ya fue
			this.instance.getAtributos().add(atributoActual);
		}
		
		TreeNode t = null;
		
		
		if(rootNode != null && (t=rootNode.getChild(entidadActual.getNombre())) != null ){
			log.info("Existe en el arbol la entiudad");
		}else if(rootNode != null && t == null){
			t = new TreeNodeImpl<Entidad>();
			t.setData(this.entidadActual);
			rootNode.addChild(this.entidadActual.getNombre(),t);
		}else
			log.info("No esta disponible el arbol de entidades y atributos");
		
		
		// le agrego el atributo al nodo
		TreeNode nodoAtributo = new TreeNodeImpl<Atributo>();
		nodoAtributo.setData(atributoActual);
		t.addChild(atributoActual.getNombre(), nodoAtributo);
	}
	
		
	public void addInstrumentoSeleccionado(Instrumento item){
		log.info(item.getDescripcion());
	}
	
	public void addAtributo(){
		
		if (nodoSeleccionado == null) {
        	if (rootNode == null)
        		rootNode = new TreeNodeImpl();
        		
        	nodoSeleccionado = rootNode;
        }
		
		addAtributoHijo((TreeNodeImpl)nodoSeleccionado);
	}
	
	
	public void addAtributoHijo(TreeNodeImpl padre){
		TreeNodeImpl nodeImpl = new TreeNodeImpl();
        
		if(this.atributoActual != null && this.atributoActual.getNombre() != null){
			nodeImpl.setData(this.atributoActual);
			padre.addChild(this.atributoActual.getNombre(), nodeImpl);
			
			
			if(this.instance.getAtributos() == null){
				log.info("La lista de atributos para la entidad es NULL");
				return;
			}
			
			this.instance.getAtributos().add(this.atributoActual);
			log.info("Agregue atributo al arbol y reglaPseudocodigoHome.instance"+this.atributoActual.getEntidad().getNombre()+"."+this.atributoActual.getNombre());
						
			this.atributoActual = new Atributo();
		}else
			log.info("AtributoActual es NULL o el nombre del atributo es NULL");
		
	}

	/*************************************************METODOS DE ELIMINAR - REMOVE *********************/
	
	/**
	 * Metodo invocado desde reglaPseudocodigoEditCondiciones.xhtml
	 * Elimina un nodo del arbol Ent-Attr
	 */
	public void eliminarNodo() {
		
		if (nodoSeleccionado == null) 
			return;
		
		Object aux = nodoSeleccionado.getData();
		
		if( aux.getClass().getSimpleName().equals("Entidad")){
			log.info("Entramos a eliminar el nodo Entidad");
			
			Iterator<Map.Entry<Object, TreeNode<Atributo>>> it = nodoSeleccionado.getChildren();
			
			while (it.hasNext()) {
				this.instance.getAtributos().remove(((TreeNodeImpl<Atributo>)it.next().getValue()).getData());
			}
			
			rootNode.removeChild(((Entidad)aux).getNombre());
			nodos.remove((Entidad)aux);
		}
		else{
			TreeNode<Entidad> nodeTreeEntidad = rootNode.getChild(((Atributo)aux).getEntidad().getNombre());
			nodeTreeEntidad.removeChild(((Atributo)aux).getNombre());
			this.instance.getAtributos().remove(aux);
		}
		
		nodoSeleccionado = null;
	}
	
	/**
	 * Este metodo elmina una ReglaFilaPseudocodigo
	 * @param unaReglaFila
	 */
	public void eliminarFila(ReglaFilaPseudocodigo unaReglaFila){
		if(unaReglaFila == null){
			log.info("La fila a eliminar el NULL");
			return;
		}
		
		if(this.instance.getReglasFilas() == null){
			log.info("La lista que contiene las \"ReglasFilas\" es NULL");
			return;
		}
		
		if(this.instance.getReglasFilas().contains(unaReglaFila)){
			this.instance.getReglasFilas().remove(unaReglaFila);
			log.info("Borramos "+unaReglaFila.toString());
		}
	}
	
	@Begin(join=true)
	public String editarFila(ReglaFilaPseudocodigo unafila) {
		if (unafila == null)
			return null;
		if(this.instance.getReglasFilas()==null)
			return null;
		if (this.instance.getReglasFilas().contains(unafila)){
			int indice=this.instance.getReglasFilas().indexOf(unafila);
			this.reglaFilaPseudocodigoActual=this.instance.getReglasFilas().get(indice);
			filaEditada = true;
			return "/reglaPorPseudoCodigoAgregarCondiciones.xhtml";
		}
		else
			return null;
	}
	
	/***************************************METODOS PARA ARBOL DE CONDICIONES*********************************/
	
	public void processSelection(NodeSelectedEvent event) {
    	try {
			HtmlTree tree = (HtmlTree) event.getComponent();
	    	
	    	TreeNode currentNode = tree.getModelTreeNode(tree.getRowKey());
	    	
	    	nodoSeleccionado = currentNode;
    	}
    	catch(Exception e) {
    		log.error(e.getMessage());
    	}
    }
	
	public TreeNode getTreeNode() {
		if (rootNode == null) {
			log.info("rootNode es NULL");
            loadTree();
		}
		log.info("rootNode ya esta cargado");
        return rootNode;
   
    }
    
    private void loadTree() {
    	rootNode = new TreeNodeImpl();
        
    	//TODO cuando pasan estas cosas de inconsistencia, como por ejemplo que la lista de atributos sea null cuando nunca deberia ser NULL. deberiamos tirar una exception con un facesMessage????
        if (this.instance.getAtributos() == null) {
        	log.info("NO hay atributos para reglaPseducodigoHome.");
        	return;
        }
        
        if(this.instance.getAtributos().size() == 0){
        	facesMessages.add("La lista de atributos para este regla esta vacia.");
        	return;
        }
        
        addNodes();
    }
    
    private void addNodes() {
    	TreeNode parent = null;
    	
        log.info("adhiriendo nodos al arbol");
        
        if(this.instance.getAtributos() == null){
        	log.info("La lista de atributos esta en NULL");
        	return;
        }
        
        for(Atributo a: this.instance.getAtributos()){
        	
        	if((parent=rootNode.getChild(a.getEntidad().getNombre())) == null){
        	
	        	TreeNode nodoEnt  = new TreeNodeImpl<Entidad>();
	        	nodoEnt.setData(a.getEntidad());
	        	rootNode.addChild(a.getEntidad().getNombre(), nodoEnt);
	        	
	        	TreeNode nodoAttr = new TreeNodeImpl<Atributo>();
	        	nodoAttr.setData(a);
	        	nodoEnt.addChild(a.getNombre(),nodoAttr);
        	}else{
        		TreeNode attr = new TreeNodeImpl<Atributo>();
        		attr.setData(a);
        		parent.addChild(a.getNombre(), attr);
        	}
        }
        
     }
    
    public TreeNode getTreeNodeInstrumento() {
		if (rootNodeInstrumento == null) {
            loadTreeInstrumento();
        }
        return rootNodeInstrumento;
    }
     
    private void loadTreeInstrumento() {
    	List<Instrumento> instrumentos = new InstrumentoList().getResultList(); 
        rootNodeInstrumento = new TreeNodeImpl<Instrumento>();
        
        if (instrumentos.isEmpty()) {
        	TreeNodeImpl<Instrumento> carpetaRaiz = new TreeNodeImpl<Instrumento>();
            
            Instrumento i = new Instrumento();
            i.setDescripcion("Instrumentos Normativos");
            i.setEsCarpeta(true);
            
            carpetaRaiz.setData(i);
            
            rootNodeInstrumento.addChild("root", carpetaRaiz);
        	return;
        }
        
        addNodesInstrumento(null, rootNodeInstrumento, instrumentos);
    }
    
    public void processSelectionInstrumento(NodeSelectedEvent event) {
    	HtmlTree tree = (HtmlTree) event.getComponent();
    	
    	TreeNode currentNode = tree.getModelTreeNode(tree.getRowKey());
 
    	nodoSeleccionadoInstrumento = currentNode;	
    }
    
    private void addNodesInstrumento(Instrumento padre, TreeNode<Instrumento> node, List<Instrumento> instrumentos) {
        for(Instrumento i : instrumentos) {
         	if(i.getPadre() == padre && i.getEsCarpeta() && i.getActivo() != "N") {
         		TreeNodeImpl<Instrumento> nodeImpl = new TreeNodeImpl<Instrumento>();
                nodeImpl.setData(i);
                node.addChild(i.getDescripcion(), nodeImpl);
         		addNodesInstrumento(i, nodeImpl, instrumentos);
         	}
         }
     }
    
    public void eliminarInstrumentoSeleccionado(Instrumento instrumento) {
		this.instance.getInstrumentosNormativos().remove(instrumento);
	}
    
    public void addInstrumentoSeleccionado(){
		if (nodoSeleccionadoInstrumento == null) return;
		if (nodoSeleccionadoInstrumento.getData() == null) return;
		addInstrumento((TreeNodeImpl<Instrumento>)nodoSeleccionadoInstrumento);
	}
    
    private void addInstrumento(TreeNodeImpl<Instrumento> nodo) {	
		if (nodo.isLeaf()) {
			//agrego el instrumento porque es una hoja
			
			if (!this.instance.getInstrumentosNormativos().contains(nodo.getData()))
				this.instance.getInstrumentosNormativos().add(nodo.getData());
		}
		else {
			Iterator<Map.Entry<Object, TreeNode<Instrumento>>> it =	nodo.getChildren();
			while(it.hasNext()){
				
				addInstrumento((TreeNodeImpl<Instrumento>)it.next().getValue());
			}
	    }
	    
	}
    
    
    /***************************************METODOS GENERICOS ***********************************************/
    public void addInst(Instrumento i){
		
		if(this.instance.getInstrumentosNormativos().contains(i))
			return;
		
		this.instance.getInstrumentosNormativos().add(i);
	}
	
	public void viewInstOfFolderSelected(){
		
		if(nodoSeleccionadoInstrumento == null){
			log.info("Debe seleccionar una carpeta");
			return;
		}
		
		Query query = persistenceService.createQuery("" +
				"SELECT" +
				" instrumento FROM Instrumento as instrumento" +
				" WHERE instrumento.padre = :inst and instrumento.esCarpeta = false and activo <>'N'")
				.setParameter("inst", ((Instrumento)nodoSeleccionadoInstrumento.getData()));
		
		this.listInstruments = (ArrayList<Instrumento>)query.getResultList();
		
		if(listInstruments.size() == 0)
			facesMessages.add("No se encontraron datos.");
	}
    
    
    
	public void searchNormativeInstrument(){
		if(nameInstrument == null){
			facesMessages.add("Para realizar la busqueda debe completar el nombre o una parte de el.");
			return;
		}
		
		log.info("Buscado los intrumentos normativos de la carpeta seleccionada. Los resultados de la busqueda se dejaran en una variable de instancia");
		
		Query query =	persistenceService.createQuery("" +
				"SELECT" +
				" instrumento FROM Instrumento as instrumento" +
				" WHERE instrumento.descripcion like :patron and instrumento.esCarpeta = false and activo <>'N'")
				.setParameter("patron", "%"+nameInstrument+"%");
		
		this.listInstruments = (ArrayList<Instrumento>) query.getResultList();
		log.info("El tamaño de la lista de la busqueda es "+listInstruments.size());
	}
    
    
    
	public boolean getHayListaInstrumentos() {
		return !this.instance.getInstrumentosNormativos().isEmpty();
	}
	
	public boolean getHayListaCondiciones() {
		//TODO este metodo se sigue usando???
		if (nodos == null) return false;
		return !nodos.isEmpty(); 
	}
	
	private void setearFechaYAutor() {
		//fecha
		this.getInstance().setFecha(new Date(System.currentTimeMillis()));
		
		//autor
		Usuario user = (Usuario)Contexts.getSessionContext().get("user");
		if(user.getNombre() != null && !user.getNombre().trim().equals("")){
			this.getInstance().setAutor(user.getNombre());
		}else{
			this.getInstance().setAutor(user.getUsername());
		}
	}

	
	/**
	 * Metodo que se invoca cuando se va desde reglaPseudocodigoEdit.xhtml a 
	 * reglaPseudocodigoEditCondiciones.xhtml
	 */
	public String irAPaso2() {
		
		boolean existInstrumentos = false;
		
		if (!getHayListaInstrumentos()) {
    		existInstrumentos = true;
    	}
		
		
		if(!this.isManaged()){
			ReglaPseudocodigoList allRules = new ReglaPseudocodigoList();
			
			List<ReglaPseudocodigo> listOfRules = allRules.getMyResultList();
			
			for(ReglaPseudocodigo r : listOfRules){ 
				if(r.getNombre().equals(this.instance.getNombre()) && (!reeditado)){
					
					if(existInstrumentos){
	    				facesMessages.add("Debe seleccionar al menos un instrumento normativo");
					}
					
					facesMessages.add("El nombre de la regla ya existe, por favor modifique el nombre de la regla");
	    		return null;
	    		}
			}
		}
		
		setearFechaYAutor();
		
		persistenceService.save(this.getInstance());
		facesMessages.add("La regla ha sido autoguardada en modo borrador");
		/*if(existInstrumentos){
			facesMessages.add("Debe seleccionar al menos un instrumento normativo");
			return null;
		}*/
		
    	return "";
    }
	
	
	/**
	 * 
	 * Metodo que se invoca cuando se va desde reglaPseudocodigoEditCondiciones.xhtml a
	 * reglaPseudocodigoEditAcciones.xhtml
	 */
	public String irAPaso3() {
		boolean flagErrAcciones = false;

		/*
		 * NO INVOCAR A limpiarCondicion y limpiarEntidad
		 */
		nodoSeleccionado = null;
		log.info("Los atributos finalmente seleccionados son:");
		for (Atributo unAtrr : this.instance.getAtributos()) {
			log.info("ATTR: "+unAtrr.getEntidad().getNombre()+"."+unAtrr.getNombre());
		}
		
		if (this.accionesSeleccionadas.size() == 0) {
			flagErrAcciones = true;
			facesMessages.add("Ud. Debe elegir al menos una accion");
		}
		
		if(flagErrAcciones){
			log.info("No se elegio ninguna accion y al menos una tiene que haber seleccionada");
			return null;
		}
		
		setearFechaYAutor();
		
		persistenceService.save(this.instance);
		facesMessages.add("La regla ha sido autoguardada en modo borrador");
    	return "";
    }
	
	
	/**
	 * Este metodo se invoca cuando vamos de reglaPseudocodigoEditAcciones.xhtml
	 * a reglaPorPseudoCodigoAgregarCondiciones.xhtml
	 */
	public String irAPaso4(){
		
		//this.reglaFilaPseudocodigoActual.setCondicionPseudocodigo(this.reglaFilaPseudocodigoActual.getCondicionPseudocodigo());
		log.info("Nos redirigimos al paso 4 para dar de alta una 'condicion'");
		this.reglaFilaPseudocodigoActual = new ReglaFilaPseudocodigo();
		this.filaEditada = false;
		return "";
	}
	
	
	/**
	 * Este metodo se invoca cuando se va desde reglaPorPseudoCodigoAgregarCondiciones.xhtml
	 * a reglaPorPseudoCodigoNuevaFila.xhtml
	 */
	public String irAPaso5(){
		
		if(this.reglaFilaPseudocodigoActual.getCondicionPseudocodigo() == null || 
				this.reglaFilaPseudocodigoActual.getCondicionPseudocodigo().isEmpty()){
			facesMessages.add("La codicion no debe estar vacía.");
			return null;
		}
		
		/*
		this.reglaFilaPseudocodigoActual.setMensajeOperadorUdai(new String());
		this.reglaFilaPseudocodigoActual.setMensajeUsuarioWEB(new String());
		this.reglaFilaPseudocodigoActual.setObservacion(new String());*/
		
		
		if(this.reglaFilaPseudocodigoActual.getMensajeOperadorUdai() == null)
			this.reglaFilaPseudocodigoActual.setMensajeOperadorUdai(new String());
		
		if(this.reglaFilaPseudocodigoActual.getMensajeUsuarioWEB() == null)
			this.reglaFilaPseudocodigoActual.setMensajeUsuarioWEB(new String());
		
		if(this.reglaFilaPseudocodigoActual.getObservacion() == null)
			this.reglaFilaPseudocodigoActual.setObservacion(new String());
		
		/*falta meter la logica de acciones: sacar de las acciones disponibles las que ya USE en OTRAS FILAS*/
		
		log.info("Nos dirigimos al paso 5, donde se cargan las acciones [MensajeOperadorUdai - MensajeUsuarioWWEB - observacion]");
		
		return "/reglaPorPseudoCodigoNuevaFila.xhtml";
	}
	
	
	/**
	 * Este metodo se invoca cuando se va desde reglaPorPseudoCodigoNuevaFila.xhtml a 
	 * reglaPseudocodigoEditAcciones, una vez completado el ciclo del alta de una nueva
	 * fila para la tabla que se encuentra en la vista reglaPseudocodigoEditAcciones.
	 * */
	public String irAPasoFinal(){

		if(this.reglaFilaPseudocodigoActual.getAcciones().size() == 0) {
			facesMessages.add("Debe agregar una accion a la regla");
			return null;
		}
		
		log.info("UDAI = "+this.reglaFilaPseudocodigoActual.getMensajeOperadorUdai());
		
		/*if((this.reglaFilaPseudocodigoActual.getMensajeOperadorUdai() == null || this.reglaFilaPseudocodigoActual.getMensajeOperadorUdai().isEmpty()) &&
				(this.reglaFilaPseudocodigoActual.getMensajeUsuarioWEB() == null || this.reglaFilaPseudocodigoActual.getMensajeUsuarioWEB().isEmpty()) &&
				(this.reglaFilaPseudocodigoActual.getObservacion() == null || this.reglaFilaPseudocodigoActual.getObservacion().isEmpty())){
			facesMessages.add("Verifique alguno de los siguientes puntos:");
			facesMessages.add("Mensaje para operador de UDAI");
			facesMessages.add("Mensaje para ciudadano");
			facesMessages.add("Observacion general");
			return null;
		}*/
		
		setearFechaYAutor();
		
		if (this.isManaged()){
			if (!filaEditada) {
				this.instance.getReglasFilas().add(this.reglaFilaPseudocodigoActual);
				//se comenta esta linea porque repite acciones
				//this.accionesSeleccionadas.addAll(this.reglaFilaPseudocodigoActual.getAcciones());
			}
			filaEditada=true;
			persistenceService.update(this.instance);
			this.reglaFilaPseudocodigoActual = new ReglaFilaPseudocodigo();
		}
		else {
			if (!filaEditada) {
				this.instance.getReglasFilas().add(this.reglaFilaPseudocodigoActual);
				//se comenta esta linea porque repite acciones
				//this.accionesSeleccionadas.addAll(this.reglaFilaPseudocodigoActual.getAcciones());
			}
			filaEditada=true;
			this.reglaFilaPseudocodigoActual = new ReglaFilaPseudocodigo();
			persistenceService.save(this.instance);
	     	facesMessages.add("La regla ha sido autoguardada en formato borrador");
			log.info("Volvemos al paso final, donde se puede volver a agregar una fila o terminar el alta de la regla por pseudocodigo");
			
		}
		return "/reglaPseudocodigoEditAcciones";
	}
	
    public void actualizarNombre() {
    	//TODO este metodo se sigue usando???}
    }
    	
    public void exit() {
    	log.info("Estamos terminando de la conversacion");
    	this.setId(null);
    }
        
    public String cancelPaso1(){
    	log.info("Cancelando el paso 1");
    	return "/reglaPseudocodigoList.xhtml";
    }
    
    public void atras() {
    	this.msgError = "";
    	reeditado=true;
    	nodoSeleccionado=null;

    }
    
    public void exportar() {
    	//String pseudocodigo = this.instance.getPseudocodigo();
    	
    	try {
    		this.editarReglasLiterales();
    		
	   	   	Document documento = new Document();
	    	FileOutputStream ficheroPdf = new FileOutputStream("fichero.pdf");
	    	PdfWriter.getInstance(documento, ficheroPdf);
	    	documento.open();
	    	
	    	
	    	
	    	for (ReglaFilaPseudocodigo lite : this.instance.getReglasFilas()) {
	    		documento.add(new Paragraph(lite.getReglaLiteral()));
	    	}
	    	documento.close();
	    	
	    	InputStream in = new FileInputStream("fichero.pdf");
	    	byte[] data = new byte[in.available()];
	    	in.read(data);
	
	    	
	    	HttpServletResponse response = (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
	    	response.setContentType("application/*");
			response.addHeader("Content-Disposition","attachment;filename=" + 
					this.getInstance().getNombre().replace(" ", "%20") + ".pdf" );
			response.setContentLength(data.length);
	    	
			
			OutputStream output = response.getOutputStream();
			output.write(data);
			output.flush();
	        output.close();
	        FacesContext.getCurrentInstance().responseComplete();
	        
	        
    	}
    	catch (Exception e) {
    		facesMessages.add("Debe agregar al menos una fila antes de poder exportar la regla a PDF");
    	}
    }

    
    /**
     * 
     * Pregunta si un nodo del arbol para ENTIDADES-ATRIBUTOS es un atributo!!!
     */
    public boolean esAtributo(){
    	
    	if(nodoSeleccionado==null){ 
    		return true;
    	}
    	
    	return nodoSeleccionado.getData().getClass().getSimpleName().equals("Atributo"); 
    }
    
    @Override
    public String persist(){
    	
    	//FIXME: BORRAR agregado por German Leotta para probar edicion de reglas
    	//este codigo deberia estar en otro metodo
    	
    	/* Seteo la lista de acciones de la instancia con la lista nueva actualizada para ser guardada */
    	/*if (super.isManaged()) {
    		this.instance.setAcciones(accionesSeleccionadas);
    		this.editarReglasLiterales();
    		return super.update();
    	}*/
    	//END FIXME
    	
    		/*
	    	if(this.accionesSeleccionadas == null){
	    		log.info("Las acciones seleccionadas esta en NULL");
	    		return null;
	    	}
	    	
	    	if(this.accionesSeleccionadas.size() == 0){
	    		log.info("El tamaño de la lista de accionesSeleccionadas es 0");
	    		return null;
	    	}
	    	
	    	if(this.instance.getAtributos() == null){
	    		log.info("La lista de atributos esta NULL");
	    		return null;
	    	}
	    	
	    	if(this.instance.getAtributos().size() == 0){
	    		log.info("El tamaño de la lista de atributos esta en NULL");
	    		return null;
	    	}*/
	    	
	    	if(this.instance.getReglasFilas() == null){
	    		log.info("La lista de reglasFilas en el momento de persistir una ReglaPorPseudocodigo es NULL");
	    		return null;
	    	}
	    	
	    	/*if( this.instance.getReglasFilas().size() == 0 ){
	    		facesMessages.add("La regla fue guardada sin ninguna");
	    	}*/
    	
    	
    	this.instance.setAcciones(this.accionesSeleccionadas);

    	ReglaPseudocodigoList allRules = new ReglaPseudocodigoList();
		
		List<ReglaPseudocodigo> listOfRules = allRules.getMyResultList();
		
		
		/*for(ReglaPseudocodigo r : listOfRules){
			if(this.isManaged() && r.getNombre().equals(this.nombreReglaAuxManaged))
				continue;
			
			if(r.getNombre().equals(this.instance.getNombre())){
				facesMessages.add("El nombre de la regla ya existe. Por favor vuelva al paso 1 e ingrese otro nombre para la regla.");
				return "/reglaPseudocodigoEdit.xhtml";
			}
		}*/
		
		String cadena = this.editarReglasLiterales();
		if(cadena.length() > 5000){
			facesMessages.add("El texto de la regla literal excede el maximo permitido (5000 caracteres)");
			return "/reglaPseudocodigoEditAcciones.xhtml";
		}
		
		/*try {
			this.generarArchivoDeTexto();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
    	
    	return super.persist();
    }
   
    private String editarReglasLiterales() {
		
    	int cont=0;
    	String msjSalida = "";
    	
    	String cadena = "\npackage ar.org.anses.prissa.reglas;\n";
	    cadena = cadena + "\n\nimport ar.gov.anses.prissa.asistente.modelosemantico.*; \n" +
	    		"import ar.gob.anses.prissa.mi.asistente_reglas.simulador.MensajeSistema;" +
	    		"\n\n\n";
	
    	for (ReglaFilaPseudocodigo regla: this.instance.getReglasFilas()) {
        	cadena = cadena + "rule "+ this.instance.getNombre() + "_" + (++cont) + "\n" ;
        	cadena = cadena + "\tno-loop true \n";
        	cadena = cadena + "\twhen \n" + "\t\t$var:Tipo" + "(" + regla.getCondicionPseudocodigo() + ");" + "\n";
        	cadena = cadena + "\tthen \n"; 
           	for (Accion ac: regla.getAcciones()) {
        		if (ac.getTipoAccion().equals("MH")){
					if (ac.getAccionModificaHecho().getAtributo().getTipoDato().equals("TEXTO")){
						cadena = cadena + "\t\t$" + ac.getAccionModificaHecho().getEntidad().getNombre() + ".set" + ac.getAccionModificaHecho().getAtributo().getNombre() + "(\"" + ac.getAccionModificaHecho().getAccionModificaHechoLiteral().getLiteral() + "\");";
						}
					else
						cadena= cadena + "\t\t$" + ac.getAccionModificaHecho().getEntidad().getNombre() + ".set" + ac.getAccionModificaHecho().getAtributo().getNombre() + "(" + ac.getAccionModificaHecho().getAccionModificaHechoLiteral().getLiteral() + ");";
				msjSalida =  "Para la Entidad: " + ac.getAccionModificaHecho().getEntidad().getNombre() 
        					+ " en el  Atributo: " + ac.getAccionModificaHecho().getAtributo().getNombre()  + " se establece el  Valor: " + ac.getAccionModificaHecho().getAccionModificaHechoLiteral().getLiteral();
        		cadena = cadena + "\n\t\t$var.unMetodo(\"" + msjSalida + "\");\n";
        		}
        	}
 
			cadena = cadena + "\n\t\tSystem.out.println(\"Se activo regla\");\n";
        	cadena = cadena + "end \n\n";
        	regla.setReglaLiteral(cadena);
        	log.info("Regla fila literal : " + cadena);
        }
    	return cadena;	
	}

	/*
    private void generarArchivoDeTexto() throws IOException {
		
    	try {
    		int contador=0;
    		if (this.isManaged()) {
    			
    		}
    		PrintWriter fileOut = new PrintWriter (new FileWriter(archivo,true));
    		for (ReglaFilaPseudocodigo regla: this.instance.getReglasFilas()) {
    			fileOut.println("rule " + this.instance.getNombre().trim() + "_" + ++contador);
        		fileOut.println("	when");
        		fileOut.println("		" + regla.getCondicionPseudocodigo());
        		fileOut.println("	then");
        		for (Accion accion: regla.getAcciones()) {
        			fileOut.println("		" + accion.getNombre());
        		}
        		fileOut.println("end");
        		fileOut.println();
    		}
    		fileOut.close();
    		
    	}
    	catch (IOException e) {
			log.info(" excepcion lanzada por archivo no creado/abierto");
		}
	}
*/
	public boolean isNodoSeleccionado() {
    	return nodoSeleccionado != null;
    }

	public void setMsgError(String msgError) {
		this.msgError = msgError;
	}

	public String getMsgError() {
		return msgError;
	}
	
	public void actualizaPseudoCodigoAux(){ 
		//TODO este metodo es invocado por alguna vista??? }
	}
	    
	public void cancelEdit() {
		reeditado=false;
		//TODO este metodo se sigue usando???
		//super.clearInstance();
	}
	
	public List<Accion> getAccionesReglaFila(ReglaFilaPseudocodigo unaReglaFila){
		if(unaReglaFila == null){
			log.info("La regla por la cual se buscaran las acciones correspondientes a esa fila es NULL ");
			return null;
		}
		
		if(this.instance.getReglasFilas() == null){
			log.info("Las reglas filas son NULL");
			return null;
		}
		
		if(this.instance.getReglasFilas().size() == 0){
			log.info("La longitud de la lista es 0");
			return null;
		}
		
		List<ReglaFilaPseudocodigo> reglasFilas = this.instance.getReglasFilas();
		
		ReglaFilaPseudocodigo reglaFila = reglasFilas.get(this.instance.getReglasFilas().indexOf(unaReglaFila));
		
		return reglaFila.getAcciones();
	}
	
	public void irALista() {}

	public void setDominiosRegistrados(ArrayList<Dominio> dominiosRegistrados) {
		this.dominiosRegistrados = dominiosRegistrados;
	}

	public ArrayList<Dominio> getDominiosRegistrados() {
		dominiosRegistrados  = (ArrayList<Dominio>) getPersistenceContext().createQuery("" +
		" select dominio from Dominio as Dominio order by Dominio.descripcion").getResultList();
		return dominiosRegistrados;
	}

	public void setDominioFiltroCondiciones(Dominio dominioFiltroCondiciones) {
		this.dominioFiltroCondiciones = dominioFiltroCondiciones;
	}

	public Dominio getDominioFiltroCondiciones() {
		return dominioFiltroCondiciones;
	}

	public String filtrarAccion() {
		
		log.info("filtrarAccion");
		Query query = getPersistenceContext().createQuery("" +
				"SELECT" +
				" Accion FROM Accion as Accion" +
				" WHERE Accion.removed = false and Accion.dominio=:dom")
				.setParameter("dom", dominioFiltroCondiciones);
		
		this.accionesDisponibles = (ArrayList<Accion>)query.getResultList();
		if(this.isManaged() && this.accionesSeleccionadas != null) {
			this.accionesDisponibles.removeAll(this.accionesSeleccionadas);
		}
		return null;		
	}

	
	public ArrayList<Accion> getAccionesDisponibles() {
		ArrayList<Accion> al = new ArrayList<Accion>();
		if (accionesDisponibles != null && accionesSeleccionadas != null) {
			for (Accion a : accionesDisponibles) {
				if (!accionesSeleccionadas.contains(a)) {
					al.add(a);
				}
			}
			return al;
		}
		return accionesDisponibles;
	}

	
	public void setAccionesDisponibles(ArrayList<Accion> accionesDisponibles) {
		this.accionesDisponibles = accionesDisponibles;
	}
	
	public void actualizarAccionesDisponibles(){
		ArrayList<Accion> accionesABorrar = new ArrayList<Accion>();
		for(Accion accion : accionesDisponibles){
			if(!accion.getDominio().equals(dominioFiltroCondiciones)){
				accionesABorrar.add(accion);
			}
		}
		accionesDisponibles.removeAll(accionesABorrar);
		log.info("Se actualizo la lista de acciones");
		
	}

}