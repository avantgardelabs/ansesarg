package ar.gov.anses.prissa.asistente.graficador.modeloresumido.jgraph;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.VertexView;

import ar.gov.anses.prissa.asistente.graficador.modeloresumido.TablaMR;


public class TablaVertexViewMR extends VertexView {

    private static final long serialVersionUID = 1360122068679946304L;

    private TablaVertexRendererMR renderer;

    public TablaVertexViewMR(DefaultGraphCell cell) {

        super(cell);
        renderer = new TablaVertexRendererMR(getTabla());
    }

    @Override
    public TablaVertexRendererMR getRenderer() {
        return this.renderer;
    }


    public TablaMR getTabla() {

        return (TablaMR) ((DefaultGraphCell)this.getCell()).getUserObject();
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

