package ar.gov.anses.prissa.asistente.graficador;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.Edge;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;

import ar.gov.anses.prissa.asistente.graficador.jgraph.RelacionColumnaTablaRouting;
import ar.gov.anses.prissa.asistente.graficador.jgraph.TablaCellViewFactory;
import ar.gov.anses.prissa.asistente.graficador.modelo.Columna;
import ar.gov.anses.prissa.asistente.graficador.modelo.Tabla;
import ar.gov.anses.prissa.asistente.graficador.modeloresumido.jgraph.LeyendaComponent;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.tree.JGraphTreeLayout;

public class Graficar {

    private Tabla tabla;

    private JGraph grafico;

    private JComponent componente;
    
    private DefaultGraphModel modelo;

    private DefaultGraphCell raiz;
    
    private GraphLayoutCache vista;

    private final Edge.Routing edgeRouting =  new RelacionColumnaTablaRouting();
    
    private int edgeStyle = GraphConstants.STYLE_ORTHOGONAL; 

    private Map<Tabla, DefaultGraphCell> verticesPorTabla;
    
    private List<GraphCell> celdas;
    
    /* Comparador utilizado para ordenar las celdas al momento de aplicar el layout */
    private final Comparator<GraphCell> ordenadorDeCeldas = new Comparator<GraphCell>() {

        /**
         * Utiliza el orden de inserción de las celdas para determinar 
         * su orden de procesamiento.
         */
        public int compare(GraphCell cell1, GraphCell cell2) {

            return celdas.indexOf(cell1) - celdas.indexOf(cell2);
        }
    }; 
    
    public Graficar() {
     
    }
    
    
    public void setTabla(Tabla tabla) {

        this.tabla = tabla;
    }

    public JComponent getGrafico() {

        return this.componente;
    }

    /**
     * Procesa la información de la Tabla dada con sus relaciones y genera el
     * grafico.
     * 
     */
    @SuppressWarnings("rawtypes")
    public void procesar() {

        if (this.tabla == null) {

            throw new IllegalStateException("La tabla no puede ser nula");
        }

        /* construye el modelo y la vista */
        this.modelo = new DefaultGraphModel();
        this.vista = new GraphLayoutCache(this.modelo, TablaCellViewFactory.instance());

        this.grafico = new JGraph(this.modelo, this.vista);
        this.grafico.setEnabled(true);
        
        this.modelo.beginUpdate();
        this.verticesPorTabla = new HashMap<Tabla, DefaultGraphCell>();
        this.celdas = new LinkedList<GraphCell>();
        this.raiz = this.procesar(tabla);
        this.modelo.endUpdate();

        /* aplica el layout */
        JGraphFacade fachada = new JGraphFacade(this.grafico, 
                                                new Object[] { this.raiz }, 
                                                true, 
                                                false, 
                                                true, 
                                                true);
        
        fachada.setOrdered(true);
        fachada.setOrder(this.ordenadorDeCeldas);
        JGraphLayout layout = this.crearLayout();
        layout.run(fachada);
        Map nested = fachada.createNestedMap(true, true);
        this.grafico.getGraphLayoutCache().edit(nested);

        /* configuración de visualización */
        this.grafico.setAntiAliased(true);
        this.grafico.setSize(this.grafico.getPreferredSize());
        
        /* construye el componente usando el grafo */
        this.construirComponente();
    }
    

    private void construirComponente() {

        this.componente = new JPanel();
        this.componente.setBackground(Color.WHITE);
        GridBagLayout layout = new GridBagLayout();
        this.componente.setLayout(layout);
        this.componente.add(this.grafico, new GridBagConstraints(1,1,1,1,0,0,
                                                                 GridBagConstraints.CENTER,
                                                                 GridBagConstraints.NONE, 
                                                                 new Insets(20,20,20,20),
                                                                 0,0));
        this.componente.setSize(this.componente.getPreferredSize());
    }
    
    


    protected JGraphLayout crearLayout() {

        JGraphTreeLayout layout = new JGraphTreeLayout();
        layout.setAlignment(SwingConstants.TOP);
        layout.setLevelDistance(100);
        layout.setNodeDistance(30);
        layout.setCombineLevelNodes(true);
  
        return layout;
    }

    /**
     * Procesa la Tabla, creando el GraphCell correspondiente a la Tabla
     * y sus relaciones dentro del grafo.  
     * 
     * @param tabla
     * @return
     */
    private DefaultGraphCell procesar(Tabla tabla) {

        DefaultGraphCell verticeTabla = this.verticesPorTabla.get(tabla);
        
        if (verticeTabla == null) {
        
            verticeTabla = this.crearVertice(tabla);
            
            this.verticesPorTabla.put(tabla, verticeTabla);
            
            List<GraphCell> celdas = new LinkedList<GraphCell>();
            celdas.add(verticeTabla);
            
            for (Columna columna : tabla.getColumnas()) {
    
                if (columna.tieneVinculo()) {
    
                    DefaultGraphCell verticeTablaVinculada = this.procesar(columna
                                                        .getTablaVinculada());
                    
                    celdas.add(verticeTablaVinculada);
                      
                    DefaultEdge arista = this.crearArista(verticeTabla, 
                                                          verticeTablaVinculada,
                                                          columna);
                      
                    celdas.add(arista);
                }
            }
            
            /* luego de procesar */
            for (GraphCell celda : celdas) {
                
                this.celdas.add(celda);
                this.grafico.getGraphLayoutCache().insert(celda);
            }
        }

        return verticeTabla;
    }

    /**
     * Crea la Arista que corresponde con la relación entre una Columna de una 
     * Tabla y su Tabla vinculada.
     * 
     * @param origen
     * @param destino
     * @param columna
     * @return
     */
    private DefaultEdge crearArista(DefaultGraphCell origen, DefaultGraphCell destino,
            Columna columna) {

        DefaultEdge arista = new DefaultEdge();
        
        GraphConstants.setRouting(arista.getAttributes(), this.edgeRouting);
        GraphConstants.setLineStyle(arista.getAttributes(), this.edgeStyle);

        Object port = origen.addPort(null, columna);

        arista.setSource(port);
        arista.setTarget(destino.getFirstChild());

        return arista;
    }
    
    /**
     * Crea el Vértice que corresponde con la Tabla
     * 
     * @param tabla
     * @return
     */
    private DefaultGraphCell crearVertice(Tabla tabla) {

        DefaultGraphCell vertice = new DefaultGraphCell(tabla);
        vertice.addPort(null, tabla.getColumnaEnPosicion(tabla.getCantidadDeColumnas() - 1));
        
        return vertice;
    }

}
