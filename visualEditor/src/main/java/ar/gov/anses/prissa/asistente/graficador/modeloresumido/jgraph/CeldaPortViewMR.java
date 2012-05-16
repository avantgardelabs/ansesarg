package ar.gov.anses.prissa.asistente.graficador.modeloresumido.jgraph;


import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.PortView;

import ar.gov.anses.prissa.asistente.graficador.modeloresumido.CeldaMR;
import ar.gov.anses.prissa.asistente.graficador.modeloresumido.ColumnaMR;


public class CeldaPortViewMR extends PortView {

    private static final long serialVersionUID = 9176253732742047888L;
    
    private boolean localizacionAdaptativa;
    
    public CeldaPortViewMR(Object cell) {
        this(cell, false);
    }
    
    public CeldaPortViewMR(Object cell, boolean localizacionAdaptativa) {
        super(cell);
        this.localizacionAdaptativa = localizacionAdaptativa;
    }

    private double getOffset(double defaultOffset) {
        
        double offset = defaultOffset;
        
        CeldaMR celda = this.getCelda();
        
        TablaVertexViewMR cellView = TablaVertexViewMR.class.cast(this.getParentView());
        
        if ((celda != null) && (cellView != null)) {
            
            offset = cellView.getOffsetColumna(celda.getIdColumna()-1);
        }
        
        return offset;
    }
    
    private CeldaMR getCelda() {
        return CeldaMR.class.cast(DefaultPort.class.cast(this.getCell())
                                    .getUserObject());
    }
    
    @Override
    public Point2D getLocation() {
        
        TablaVertexViewMR cellView = (TablaVertexViewMR) this.getParentView();

        Rectangle2D bounds = cellView.getBounds();
        
        double x = 0;
        double y = 0;
        
        /* si la columna está vincualda es un origen */
        if (this.getCelda().tieneVinculo()) {
            
            /* parte de abajo */
            y = bounds.getMaxY();
            
        } else {
            
            /* parte de arriba */
            y = bounds.getMinY();
        }
        
        /* posicionamiento en x relativo a la columna vinculada */
        x = this.getOffset(bounds.getWidth() / 2) + bounds.getX();
        
        return new Point2D.Double(x, y);
    }
    
    @Override
    public Point2D getLocation(EdgeView edge) {

        return super.getLocation(edge);
    }
    
    @Override
    public Point2D getLocation(EdgeView edge, Point2D nearest) {
        
        TablaVertexViewMR cellView = (TablaVertexViewMR) this.getParentView();

        Rectangle2D bounds = cellView.getBounds();
        
        double x = 0;
        double y = 0;
        
        double yCentro = bounds.getCenterY();
        
        if (this.localizacionAdaptativa) {
            
            /* calcula la posición en función al punto más cercano */
    
            /* determina si está arriba o abajo */
            if (nearest.getY() < yCentro) {
                
                y = bounds.getMinY();
                
            } else {
                
                y = bounds.getMaxY();
            }
            
        } else {

            /* calcula la posición en función de si es origen o destino */
            
            /* calcula la posición en función a si es el destino o el origen */
            if (this.equals(edge.getSource())) {
                
                /* parte de abajo */
                y = bounds.getMaxY();
                
            } else {
                
                /* llegan arriba */
                y = bounds.getMinY();
            }
        }
        
        /* posicionamiento en x relativo a la columna vinculada */
        x = this.getOffset(bounds.getWidth() / 2) + bounds.getX();
        
        return new Point2D.Double(x, y);
    }

}
