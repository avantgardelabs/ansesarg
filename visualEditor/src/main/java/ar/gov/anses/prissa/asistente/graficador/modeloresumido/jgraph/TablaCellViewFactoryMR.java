package ar.gov.anses.prissa.asistente.graficador.modeloresumido.jgraph;


import org.jgraph.graph.CellViewFactory;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.PortView;
import org.jgraph.graph.VertexView;

import ar.gov.anses.prissa.asistente.graficador.modeloresumido.CeldaMR;
import ar.gov.anses.prissa.asistente.graficador.modeloresumido.ColumnaMR;
import ar.gov.anses.prissa.asistente.graficador.modeloresumido.TablaMR;



public class TablaCellViewFactoryMR extends DefaultCellViewFactory {

    private static final long serialVersionUID = 9103356736692602458L;

    private static final CellViewFactory instance = new TablaCellViewFactoryMR();
    
    private TablaCellViewFactoryMR() {
        
    }
    
    public static CellViewFactory instance() {

        return instance;
    }

    @Override
    protected VertexView createVertexView(Object cellObject) {
        
        DefaultGraphCell cell = DefaultGraphCell.class.cast(cellObject);
        
        VertexView view;
        
        Object userObject = cell.getUserObject();
        
        if (userObject == null) {
            
            view = super.createVertexView(cell); 
        
        } else if (userObject instanceof TablaMR) {
            
            view = new TablaVertexViewMR(cell);
            
        } else {
            
            view = super.createVertexView(cell);
        }
        
        return view;
    }

    @Override
    protected PortView createPortView(Object cellObject) {
     
        DefaultGraphCell cell = DefaultGraphCell.class.cast(cellObject);
        
        PortView view;
        
        Object userObject = cell.getUserObject();
        
        if (userObject == null) {
            
            view = super.createPortView(cell);
            
        } else if (userObject instanceof ColumnaMR) {
            
            view = new ColumnaPortViewMR(cell, true);
            
        } else if (userObject instanceof CeldaMR) {
            
            view = new CeldaPortViewMR(cell, true);
            
        } else {
            
            view = super.createPortView(cell);
        }
        
        return view;
    }
}
