package ar.gov.anses.prissa.asistente.graficador.jgraph;

import java.awt.Dimension;

import java.awt.geom.Rectangle2D;

import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.VertexView;

import ar.gov.anses.prissa.asistente.graficador.modelo.Tabla;

public class TablaVertexView extends VertexView {

    private static final long serialVersionUID = 1360122068679946304L;

    private TablaVertexRenderer renderer;

    public TablaVertexView(DefaultGraphCell cell) {

        super(cell);
    }

    @Override
    public TablaVertexRenderer getRenderer() {

        if (this.renderer == null) {
            this.renderer = new TablaVertexRenderer(getTabla());
        }
        
        return this.renderer;
    }

    @Override
    public DefaultGraphCell getCell() {

        return (DefaultGraphCell) super.getCell();
    }

    public Tabla getTabla() {

        return (Tabla) this.getCell().getUserObject();
    }

    @Override
    public void update(GraphLayoutCache cache) {

        bounds = GraphConstants.getBounds(allAttributes);
        
        if (bounds == null) {
            
            bounds = this.calculateBounds();
            GraphConstants.setBounds(allAttributes, bounds);
        }

        super.update(cache);
    }
    
    private Rectangle2D calculateBounds() {
        
        Rectangle2D bounds = null;
        
        Dimension size = this.getRenderer().getMainPanel().getPreferredSize();
        bounds = allAttributes.createRect(0,0,size.getWidth(), size.getHeight());
        
        return bounds;
    }
    
    double getOffsetColumna(int nroColumna) {
        
        return this.getRenderer().getOffsetColumna(nroColumna);
    }
}
