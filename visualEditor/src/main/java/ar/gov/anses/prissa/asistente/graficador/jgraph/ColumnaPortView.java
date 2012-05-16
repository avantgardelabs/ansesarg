package ar.gov.anses.prissa.asistente.graficador.jgraph;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jgraph.graph.DefaultPort;

import org.jgraph.graph.EdgeView;
import org.jgraph.graph.PortView;

import ar.gov.anses.prissa.asistente.graficador.modelo.Columna;

public class ColumnaPortView extends PortView {

    private static final long serialVersionUID = 9176253732742047888L;
    
    private boolean localizacionAdaptativa;
    
    public ColumnaPortView(Object cell) {
        this(cell, false);
    }
    
    public ColumnaPortView(Object cell, boolean localizacionAdaptativa) {
        super(cell);
        this.localizacionAdaptativa = localizacionAdaptativa;
    }

    private double getOffset(double defaultOffset) {
        
        double offset = defaultOffset;
        
        Columna columna = this.getColumna();
        
        TablaVertexView cellView = TablaVertexView.class.cast(this.getParentView());
        
        if ((columna != null) && (cellView != null)) {
            
            offset = cellView.getOffsetColumna(columna.getPosicion());
        }
        
        return offset;
    }
    
    private Columna getColumna() {
        return Columna.class.cast(DefaultPort.class.cast(this.getCell())
                                    .getUserObject());
    }
    
    @Override
    public Point2D getLocation() {
        
        TablaVertexView cellView = (TablaVertexView) this.getParentView();

        Rectangle2D bounds = cellView.getBounds();
        
        double x = 0;
        double y = 0;
        
        /* si la columna está vincualda es un origen */
        if (this.getColumna().tieneVinculo()) {
            
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
        
        TablaVertexView cellView = (TablaVertexView) this.getParentView();

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
