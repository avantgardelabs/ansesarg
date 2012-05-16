package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;



import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletResponse;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Funcion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Instrumento;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Parametro;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.custom.BaseHome;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
@Scope(ScopeType.CONVERSATION)
@Name("funcionHome")
@SuppressWarnings("unchecked")

public class FuncionHome extends BaseHome<Funcion>{
	private static final long serialVersionUID = 999426568179067865L;
	@Logger Log log;
	@In FacesMessages facesMessages;
	private TreeNode rootNodeInstrumento = null;
	private TreeNode nodoSeleccionadoInstrumento = null;
	
	private Parametro parametro = new Parametro();
	private boolean validado=false;
	private List<Atributo> atributos;
	
	@In(create = true) 
	EntityManager entityManager;

	private String pseudocodigo;
	
	private boolean modificado;
	
	private boolean retornoModificado;
	
	private String tipoDatoAnterior; 
	
	private boolean parametrosModificados;
	
	private String nameInstrument = new String("");
	
	List<Instrumento>listInstruments = new ArrayList<Instrumento>();
	
	private List<TablaDecision> reglasAfectadas = new ArrayList<TablaDecision>();
	
	
	public List<TablaDecision> getReglasAfectadas() {
		return reglasAfectadas;
	}

	public void setReglasAfectadas(List<TablaDecision> reglasAfectadas) {
		this.reglasAfectadas = reglasAfectadas;
	}

	public List<Instrumento> getListInstruments() {
		return listInstruments;
	}

	public void setListInstruments(List<Instrumento> listInstruments) {
		this.listInstruments = listInstruments;
	}

	public String getNameInstrument() {
		return nameInstrument;
	}

	public void setNameInstrument(String nameInstrument) {
		this.nameInstrument = nameInstrument;
	}

	public List<Atributo> getAtributos() {
		if (this.parametro == null) return null;
		
		if(this.parametro.getEntidad() == null) {
			log.info("entidad es nulo");
			return null;
		}
		
		log.warn("Buscando los Atributos de entidad " + this.parametro.getEntidad().getNombre());
		
		if (this.parametro.getEntidad() == null) return null;
		
		Query query = entityManager.createQuery("" +
				"SELECT" +
				" atributo FROM Atributo as atributo" +
				" WHERE atributo.entidad=:ent")
				.setParameter("ent", this.parametro.getEntidad());
		
		this.atributos = query.getResultList();
		return this.atributos;
	}
	
	
	public List<TablaDecision> obtenerReglasAfectadas(){
		List<TablaDecision> reglas;
		Query query = entityManager.createQuery("" +
				"SELECT distinct " +
				" tablaDecision FROM TablaDecision as tablaDecision join tablaDecision.filas as filas join filas.acciones as accion" +
				" join accion.accionModificaHecho as accionModificaHecho join accionModificaHecho.accionModificaHechoFuncion as accionModificaHechoFuncion " +
				" join accionModificaHechoFuncion.funcion as funcion " +
				" WHERE funcion=:fun")
				.setParameter("fun", this.instance);
		reglas = query.getResultList();
		return reglas;
	}
	
		/**************************************GETTERS & SETTERS ************************************/
	public Parametro getParametro() {
		if (this.parametro == null) this.parametro = new Parametro();
		return this.parametro;
	}
	
	public String getParametronombre() {
		return this.getParametro().getNombre();
	}
	
	public void setParametronombre(String parametronombre) {
		this.getParametro().setNombre(parametronombre);
	}
	
	
	
	public void setParametro(Parametro parametro) {
		this.parametro = parametro;
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

	public void setFuncionId(Long id) {
		setId(id);
	}

	public Long getFuncionId(){
		return (Long) getId();
	}

	@Override
	protected Funcion createInstance() {
		return new Funcion();
	}

	public void wire() {
		getInstance();
		this.setTipoDatoAnterior(getInstance().getTipoDato());
	}

	public boolean isWired() {
		return true;
	}

	public Funcion getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}


	
	public String getCreatedMessage() { return "La operaci�n ha sido realizada con exito"; }
    public String getUpdatedMessage() { return "Funcion actualizada exitosamente"; }
    public String getDeletedMessage() { return "Funcion borrada exitosamente"; }

		
	public void addInstrumentoSeleccionado(Instrumento item){
		log.info(item.getDescripcion());
	}

	/*************************************************METODOS DE ELIMINAR - REMOVE *********************/
	
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
    	//getInstrumentosSeleccionados().remove(instrumento);
	}
    
    public void addInstrumentoSeleccionado(){
    	//si no hay nada seleccionado me voy
		if (nodoSeleccionadoInstrumento == null) return;
		//si no tiene datos en el nodo seleccionado me voy
		if (nodoSeleccionadoInstrumento.getData() == null) return;
		//si es el nodo raiz y no tiene hijos me voy
		log.info(nodoSeleccionadoInstrumento.getParent().equals(this.rootNodeInstrumento));
		
		if (nodoSeleccionadoInstrumento.getParent().equals(this.rootNodeInstrumento)) {
			log.info("padre del nodo es nulo");
		}
		if (!nodoSeleccionadoInstrumento.getChildren().hasNext()) {
			log.info("no tiene hijos");
		}
		if (nodoSeleccionadoInstrumento.getParent() == null && !nodoSeleccionadoInstrumento.getChildren().hasNext()) {
			log.info("Entre");
			return;
		}
		
		addInstrumento((TreeNodeImpl<Instrumento>)nodoSeleccionadoInstrumento);
	}
    
    private void addInstrumento(TreeNodeImpl<Instrumento> nodo) {	
		if (nodo.isLeaf() && !nodo.getParent().equals(this.rootNodeInstrumento) && !nodo.getData().getEsCarpeta()) {
			//agrego el instrumento porque es una hoja
			
			if (!this.instance.getInstrumentosNormativos().contains(nodo.getData()))
				this.instance.getInstrumentosNormativos().add(nodo.getData());
		}
		else {
			Iterator<Map.Entry<Object, TreeNode<Instrumento>>> it =	nodo.getChildren();
			while(it.hasNext()){
				//en este punto solo agrego las hojas
				TreeNodeImpl<Instrumento> nodoHijo = (TreeNodeImpl<Instrumento>)it.next().getValue();
				if (nodoHijo.isLeaf()) addInstrumento(nodoHijo);
			}
	    }
	}
    
    public void eliminarParametro (Parametro p) {
    	this.instance.getParametros().remove(p);
    	this.setParametrosModificados(true);
    }
    /***************************************METODOS GENERICOS ***********************************************/
    
	public boolean getHayListaInstrumentos() {
		return !this.instance.getInstrumentosNormativos().isEmpty();
	}
	

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
		
		Query query = entityManager.createQuery("" +
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
		
		if(nameInstrument.length() < 3){
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
		
		if (this.listInstruments.isEmpty()) {
			facesMessages.add("No se han encontrado resultados.");
		}
		
		log.info("El tamaño de la lista de la busqueda es "+listInstruments.size());
	}
	
    public void actualizarNombre() {}
    	
    public void exit() {
    	log.info("Estamos saliendo de la conversacion");
    }
    
    public void exportar() throws IOException, DocumentException{
    	
    	HttpServletResponse response = (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();

    	response.setContentType("application/octet-stream");
		response.addHeader("Content-Disposition","attachment;filename=" + 
				this.getInstance().getNombre().replace(" ", "%20") + ".pdf" ) ;
		
		Document documento = new Document();
    	FileOutputStream ficheroPdf = new FileOutputStream("ficheroFunciones.pdf");
    	PdfWriter.getInstance(documento, ficheroPdf);
    	documento.open();
    	documento.add(new Paragraph(pseudocodigo));
    	documento.close();
    	
    	InputStream in = new FileInputStream("ficheroFunciones.pdf");
    	byte[] data = new byte[in.available()];
    	in.read(data);

		response.setContentLength(data.length);
		OutputStream output = response.getOutputStream();
		output.write(data) ;
		output.flush();
        output.close () ;
        FacesContext.getCurrentInstance().responseComplete();
    }

    
    @Override
    public String persist(){
		List<Funcion> l = new FuncionList().getMyResultList();
		
		
		if(this.instance.getNombre() == null){
			log.info("El nombre de la funcion es NULL");
			return null;
		}
		
		log.info(this.instance.getNombre());
		log.info("longitud de la lista "+l.size());
		
		for(Funcion f : l) {
			if (f.getId() == this.instance.getId()) continue;
			
			if(f.getNombre().equals(this.instance.getNombre())){
				facesMessages.add("El nombre de la funcion ya existe. Por favor vuelva al paso 1 e ingrese otro nombre.");
				return null;
			}
		}
		
		String retorno = super.persist();
		
		if(this.isRetornoModificado() || this.isParametrosModificados()){
			log.info("La funcion fue modificada. Informar Reglas afectadas");
			this.reglasAfectadas = obtenerReglasAfectadas();
			if(reglasAfectadas.size() > 0){
				retorno = "reglas";
				log.info("REGLAS AFECTADAS");
				for(TablaDecision regla : reglasAfectadas){
					log.info("ID: " + regla.getId() + " NOMBRE: " + regla.getNombre());
				}
			}else{
				log.info("La funcion no afecta a ninguna regla.");
			}
		}
		//try {
			return retorno;
		/*}
		catch (IllegalStateException ise) {
			for (Parametro p : this.instance.getParametros()) {
				if (p.getAtributo() == null) {
					log.info("EL ATRIBUTO ES ULOOOOO!!!!!");
					break;
				}
				entityManager.merge(p);
				//this.getPersistenceContext(). .save(p);
			}
			return super.persist();
		}*/
    }
    
    public void continuar(){
    	log.info("Reglas afectadas informadas");
    }
    
	
	public void actualizaPseudoCodigoAux(){}
	    
	public void cancelEdit() {
		//super.clearInstance();
	}
	
	public void agregarParametro() {
		boolean valido = true;
		
		if (parametro == null) {
			facesMessages.add("El parametro es nulo.");
			return;
		}
		
		if (parametro.getNombre() == null) {
			facesMessages.add("El nombre del parametro es nulo.");
			return;
		}
		
		if (!this.parametro.getNombre().matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
			facesMessages.add("El nombre del parametro no es valido.");
			valido = false;
		}
		
		for (Parametro p : this.instance.getParametros()) {
			if (p.getNombre().equals(this.parametro.getNombre())) {
				facesMessages.add("El nombre del parametro ya existe");
				valido = false;
				break;
			}
		}
		
		if (parametro.getNombre().equals("")) {
			facesMessages.add("El nombre del parametro no puede ser vacio.");
			valido = false;
		}
		
		if (parametro.getDescripcion().equals("")) {
			facesMessages.add("La descripcion no puede ser vacio.");
			valido = false;
		}
		
		if (parametro.getEntidad() == null) {
			facesMessages.add("La entidad no puede ser vacio.");
			valido = false;
		}
		
		if (parametro.getAtributo() == null) {
			facesMessages.add("El atributo no puede ser vacio.");
			valido = false;
		}
		
		if (valido) {
			Parametro p = new Parametro();
			p.setAtributo(parametro.getAtributo());
			p.setDescripcion(parametro.getDescripcion());
			p.setEntidad(parametro.getEntidad());
			p.setNombre(parametro.getNombre());
			this.instance.getParametros().add(p);
			this.setParametro(null);
			
			this.setParametrosModificados(true);
		}
	}

	public void atras() {}
	
	public String irAPaso2() {
		boolean hayerror = false;
		if (!this.instance.getNombre().matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
			facesMessages.add("El nombre de la funcion no es valido");
			hayerror = true;
		}
		if (!getHayListaInstrumentos()) {
			log.info("No hay instrumentos");
    		facesMessages.add("Debe seleccionar al menos un instrumento normativo");
    		hayerror = true;
    	}
		
		if(!getInstance().getTipoDato().equals(this.getTipoDatoAnterior())){
			this.setRetornoModificado(true);
		}
		
		return hayerror ? null : "";
    }

	public String irAPaso4() {
		pseudocodigo = "package ar.gob.anses.prissa.mi.asistente_reglas.funciones;\n\n";
		String retType = "";
		
		if (this.instance.getTipoDato().equals("NUMERO")) {
			retType = " long ";
		}
		else if (this.instance.getTipoDato().equals("TEXTO")) {
			retType = " java.lang.String ";
		}
		else if (this.instance.getTipoDato().equals("FECHA")) {
			retType = " java.util.Date ";
		}
		else if (this.instance.getTipoDato().equals("BOOLEANO")) {
			retType = " boolean ";
		}
		
		pseudocodigo += "function" + retType + this.instance.getNombre() + "(";
		
		
		boolean pongocoma = false;
		
		for (Parametro p : this.instance.getParametros()) {
			if (pongocoma) {
				pseudocodigo += ",";
			}
			pongocoma = true;
			
			if (p.getAtributo().getTipoDato().equals("NUMERO")) {
				retType = " long ";
			}
			else if (p.getAtributo().getTipoDato().equals("TEXTO")) {
				retType = " java.lang.String ";
			}
			else if (p.getAtributo().getTipoDato().equals("FECHA")) {
				retType = " java.util.Date ";
			}
			else if (p.getAtributo().getTipoDato().equals("BOOLEANO")) {
				retType = " boolean ";
			}
			
			pseudocodigo += retType + p.getNombre();
		}
		
		pseudocodigo += ") {\n";
		pseudocodigo += "/*\n" + this.instance.getDescripcion() + "\n*/";
		pseudocodigo += "\n" + this.instance.getCuerpo() + "\n}";
		
		return "";
	}
	
	public String irAPaso3() {
		return "";
	}

	public void setPseudocodigo(String pseudocodigo) {
		this.pseudocodigo = pseudocodigo;
	}

	public String getPseudocodigo() {
		return pseudocodigo;
	}

	public void setModificado(boolean modificado) {
		this.modificado = modificado;
	}

	public boolean getModificado() {
		return modificado;
	}

	public boolean isRetornoModificado() {
		return retornoModificado;
	}

	public void setRetornoModificado(boolean retornoModificado) {
		this.retornoModificado = retornoModificado;
	}

	public boolean isParametrosModificados() {
		return parametrosModificados;
	}

	public void setParametrosModificados(boolean parametrosModificados) {
		this.parametrosModificados = parametrosModificados;
	}

	public String getTipoDatoAnterior() {
		return tipoDatoAnterior;
	}

	public void setTipoDatoAnterior(String tipoDatoAnterior) {
		this.tipoDatoAnterior = tipoDatoAnterior;
	}

	public void setValidado(boolean validado) {
		this.validado = validado;
	}

	public boolean isValidado() {
		return validado;
	}
	
	public void validate() {
		validado = true;
		try {
			KnowledgeBase kbase = readKnowledgeBase();
		}
		catch (Exception e) {
			facesMessages.add("La funcion no es valida");
			facesMessages.add(e.getMessage());
			validado = false;
		}		
	}
	
	private KnowledgeBase readKnowledgeBase() throws Exception {
		/*
		log.info("1");
		File file = new File("resource.drl");
		log.info("2");
		if(!file.exists()){
			log.info("2.1");
			file.createNewFile();
			log.info("2.2");
		}
		log.info("3");
		Writer output = new BufferedWriter(new FileWriter(file));
		log.info("4");
	    output.write(this.pseudocodigo);
	    log.info("5");
	    output.close();
	    log.info("6");
		*/
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newByteArrayResource(this.pseudocodigo.getBytes()), ResourceType.DRL);
		KnowledgeBuilderErrors errors = kbuilder.getErrors();
		if (errors.size() > 0) {
			for (KnowledgeBuilderError error: errors) {
				facesMessages.add(error.getMessage());
			}
			throw new IllegalArgumentException("Errores al parsear la funcion.");
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		return kbase;
	}
}