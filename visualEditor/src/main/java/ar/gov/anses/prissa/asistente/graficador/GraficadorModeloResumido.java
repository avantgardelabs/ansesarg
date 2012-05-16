package ar.gov.anses.prissa.asistente.graficador;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import org.jgraph.JGraph;

import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.Edge;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

import ar.gov.anses.prissa.asistente.graficador.modeloresumido.CeldaMR;
import ar.gov.anses.prissa.asistente.graficador.modeloresumido.ColumnaMR;
import ar.gov.anses.prissa.asistente.graficador.modeloresumido.TablaMR;
import ar.gov.anses.prissa.asistente.graficador.modeloresumido.jgraph.LeyendaComponent;
import ar.gov.anses.prissa.asistente.graficador.modeloresumido.jgraph.RelacionColumnaTablaRoutingMR;
import ar.gov.anses.prissa.asistente.graficador.modeloresumido.jgraph.TablaCellViewFactoryMR;



import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.tree.JGraphTreeLayout;

public class GraficadorModeloResumido {

	private TablaMR tabla;

	private JGraph grafico;

	private JComponent componente;

	private DefaultGraphModel modelo;

	private DefaultGraphCell raiz;

	private GraphLayoutCache vista;

	private final Edge.Routing edgeRouting = new RelacionColumnaTablaRoutingMR();

	private int edgeStyle = GraphConstants.STYLE_ORTHOGONAL;

	private Map<TablaMR, DefaultGraphCell> verticesPorTabla;

	private List<GraphCell> celdas;

	/*
	 * Comparador utilizado para ordenar las celdas al momento de aplicar el
	 * layout
	 */
	private final Comparator<GraphCell> ordenadorDeCeldas = new Comparator<GraphCell>() {

		/**
		 * Utiliza el orden de inserción de las celdas para determinar su orden
		 * de procesamiento.
		 */
		public int compare(GraphCell cell1, GraphCell cell2) {

			return celdas.indexOf(cell1) - celdas.indexOf(cell2);
		}
	};

	public GraficadorModeloResumido() {

	}

	public void setTabla(TablaMR tabla) {

		this.tabla = tabla;
	}

	public JComponent getGrafico() {

		return this.componente;
	}

	/**
	 * Procesa la información de la TablaMR dada con sus relaciones y genera el
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
		this.vista = new GraphLayoutCache(this.modelo, TablaCellViewFactoryMR
				.instance());

		this.grafico = new JGraph(this.modelo, this.vista);
		this.grafico.setEnabled(true);
		this.grafico.setAutoResizeGraph(true);
		this.modelo.beginUpdate();
		this.verticesPorTabla = new HashMap<TablaMR, DefaultGraphCell>();
		this.celdas = new LinkedList<GraphCell>();
		
		
		
		this.raiz = this.procesar(tabla);
		this.modelo.endUpdate();

		/* aplica el layout */
		JGraphFacade fachada = new JGraphFacade(this.grafico,
				new Object[] { this.raiz }, true, false, true, true);

		fachada.setOrdered(true);
		fachada.setOrder(this.ordenadorDeCeldas);
		JGraphLayout layout = this.crearLayout();
		layout.run(fachada);
		Map nested = fachada.createNestedMap(true, true);
		this.grafico.getGraphLayoutCache().edit(nested);

		/* configuración de visualización */
		this.grafico.setAutoResizeGraph(true);
		//this.grafico.setAntiAliased(true);
		//this.grafico.setSize(this.grafico.getPreferredSize());

		/* construye el componente usando el grafo */
		this.construirComponente();
	}

	private void construirComponente() {

		this.componente = new JPanel();
		this.componente.setBackground(Color.WHITE);
		GridBagLayout layout = new GridBagLayout();
		this.componente.setLayout(layout);
		this.componente.add(this.grafico, new GridBagConstraints(1, 1, 1, 1, 0,
				0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(20, 20, 20, 20), 0, 0));
		this.componente.add(new LeyendaComponent(),new GridBagConstraints(2, 2, 1, 1, 0,
				0, GridBagConstraints.LAST_LINE_END, GridBagConstraints.NONE,
				new Insets(20, 20, 20, 20), 0, 0));
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
	 * Procesa la TablaMR, creando el GraphCell correspondiente a la TablaMR y sus
	 * relaciones dentro del grafo.
	 * 
	 * @param tabla
	 * @return
	 */
	private DefaultGraphCell procesar(TablaMR tabla) {

		DefaultGraphCell verticeTabla = this.verticesPorTabla.get(tabla);

		if (verticeTabla == null) {

			verticeTabla = this.crearVertice(tabla);

			this.verticesPorTabla.put(tabla, verticeTabla);

			List<GraphCell> celdas = new LinkedList<GraphCell>();
			celdas.add(verticeTabla);

			//ColumnaMR columna = tabla.getColumna(2);
			List<CeldaMR> celdasCondicion = tabla.getCeldas(2);
			
			for (CeldaMR celda : celdasCondicion) {

				if (celda.tieneVinculo()) {

					DefaultGraphCell verticeTablaVinculada = this
							.procesar(celda.getTablaVinculada());

					celdas.add(verticeTablaVinculada);

					DefaultEdge arista = this.crearArista(verticeTabla,
							verticeTablaVinculada, celda);

					celdas.add(arista);
				}
			}

			/* luego de procesar */
			for (GraphCell celda : celdas) {
				GraphConstants.setAutoSize(celda.getAttributes(), true);
				this.celdas.add(celda);
				this.grafico.getGraphLayoutCache().insert(celda);
			}
		}

		return verticeTabla;
	}

	/**
	 * Crea la Arista que corresponde con la relación entre una ColumnaMR de una
	 * TablaMR y su TablaMR vinculada.
	 * 
	 * @param origen
	 * @param destino
	 * @param columna
	 * @return
	 */
	private DefaultEdge crearArista(DefaultGraphCell origen,
			DefaultGraphCell destino, CeldaMR celda) {

		DefaultEdge arista = new DefaultEdge();

		GraphConstants.setRouting(arista.getAttributes(), this.edgeRouting);
		GraphConstants.setLineStyle(arista.getAttributes(), this.edgeStyle);

		Object port = origen.addPort(null, celda);

		arista.setSource(port);
		arista.setTarget(destino.getFirstChild());

		return arista;
	}

	/**
	 * Crea el Vértice que corresponde con la TablaMR
	 * 
	 * @param tabla
	 * @return
	 */
	private DefaultGraphCell crearVertice(TablaMR tabla) {

		DefaultGraphCell vertice = new DefaultGraphCell(tabla);
		ColumnaMR col = tabla.getColumnaEnPosicion(tabla
				.getCantidadDeColumnas() - 1);
		vertice.addPort(null, col);

		return vertice;
	}

	public static void main(String[] args) {
		GraphModel model = new DefaultGraphModel();
		GraphLayoutCache view = new GraphLayoutCache(model,
				new DefaultCellViewFactory());
		JGraph graph = new JGraph(model, view);
		DefaultGraphCell[] cells = new DefaultGraphCell[3];
		cells[0] = new DefaultGraphCell(new String("Hello"));
		GraphConstants.setBounds(cells[0].getAttributes(),
				new Rectangle2D.Double(20, 20, 40, 20));
		GraphConstants.setGradientColor(cells[0].getAttributes(), Color.orange);
		GraphConstants.setOpaque(cells[0].getAttributes(), true);
		DefaultPort port0 = new DefaultPort();
		cells[0].add(port0);
		cells[1] = new DefaultGraphCell(new String("World"));
		GraphConstants.setBounds(cells[1].getAttributes(),
				new Rectangle2D.Double(140, 140, 40, 20));

		GraphConstants.setGradientColor(cells[1].getAttributes(), Color.red);
		GraphConstants.setOpaque(cells[1].getAttributes(), true);
		DefaultPort port1 = new DefaultPort();
		cells[1].add(port1);
		DefaultEdge edge = new DefaultEdge();
		edge.setSource(cells[0].getChildAt(0));
		edge.setTarget(cells[1].getChildAt(0));
		cells[2] = edge;
		int arrow = GraphConstants.ARROW_CLASSIC;
		GraphConstants.setLineEnd(edge.getAttributes(), arrow);
		GraphConstants.setEndFill(edge.getAttributes(), true);
		graph.getGraphLayoutCache().insert(cells);

		JFrame frame = new JFrame();
		frame.getContentPane().add(new JScrollPane(graph));
		frame.pack();
		frame.setVisible(true);

	}

}
