package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;
import org.richfaces.component.UITree;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;
	


@Name("toolkit")
@Scope(ScopeType.SESSION)
public class ToolkitManager implements Serializable {
	
	@Logger
	private Log log;
	
	public ToolkitManager()  {
		
	}
	
	private TreeNode rootNode = null;
    private List<String> selectedNodeChildren = new ArrayList<String>();    
    
    private String nodeTitle;
    private static final String DATA_PATH = "/tmp/entidades.properties";
    
    private void addNodes(String path, TreeNode node, Properties properties) {
        boolean end = false;
        int counter = 1;
        
        while (!end) {
            String key = path != null ? path + '.' + counter : String.valueOf(counter);

            String value = properties.getProperty(key);
            if (value != null) {
                TreeNodeImpl nodeImpl = new TreeNodeImpl();
                nodeImpl.setData(value);
                node.addChild(new Integer(counter), nodeImpl);
                addNodes(key, nodeImpl, properties);
                counter++;
            } else {
                end = true;
            }
        }
    }
    
    private void loadTree() {
    	
    	
    	
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();        
        InputStream dataStream = this.getClass().getResourceAsStream("entidades.properties");
         
        
        try {
        	/*
        	File f = new File(DATA_PATH);
            dataStream =  new FileInputStream(f);
            */
        	
            Properties properties = new Properties();
        
            if (dataStream==null){            	
            	log.fatal("No se pudo encontrar el archivo de entidades.");
            }
            properties.load(dataStream);
            
            rootNode = new TreeNodeImpl();
            addNodes(null, rootNode, properties);
            
        } catch (IOException e) {
            throw new FacesException(e.getMessage(), e);
        } finally {
            if (dataStream != null) {
                try {
                    dataStream.close();
                } catch (IOException e) {
                    externalContext.log(e.getMessage(), e);
                }
            }
        }
    }
    
    public void processSelection(NodeSelectedEvent event) {
        HtmlTree tree = (HtmlTree) event.getComponent();
        nodeTitle = nodeTitle + " " + (String) tree.getRowData();
        selectedNodeChildren.clear();
        TreeNode currentNode = tree.getModelTreeNode(tree.getRowKey());
        if (currentNode.isLeaf()){
            selectedNodeChildren.add((String)currentNode.getData());
        }else
        {
            Iterator<Map.Entry<Object, TreeNode>> it = currentNode.getChildren();
            while (it!=null &&it.hasNext()) {
                Map.Entry<Object, TreeNode> entry = it.next();
                selectedNodeChildren.add(entry.getValue().getData().toString()); 
            }
        }
    }
    
    public TreeNode getTreeNode() {
    	
    	
    	
        if (rootNode == null) {
            loadTree();
        }
        
        return rootNode;
    }


    
    public String getNodeTitle() {
        return nodeTitle;
    }

    public void setNodeTitle(String nodeTitle) {
    	
    	
    	
        this.nodeTitle =  nodeTitle;
        
        
    }

}
