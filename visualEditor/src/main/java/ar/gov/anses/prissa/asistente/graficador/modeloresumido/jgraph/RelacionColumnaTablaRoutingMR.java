package ar.gov.anses.prissa.asistente.graficador.modeloresumido.jgraph;


import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import org.jgraph.graph.DefaultEdge.LoopRouting;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.PortView;

public class RelacionColumnaTablaRoutingMR extends LoopRouting {

    private static final long serialVersionUID = 3508957711460323360L;

    
    @Override
    public List<Object> routeEdge(GraphLayoutCache cache, EdgeView edge) {

        List<Object> points = null;
        
        PortView source = (PortView) edge.getSource();
        PortView target = (PortView) edge.getTarget();
        
        if ((source != null) && (target != null)) {
            
            points = new LinkedList<Object>();
            
            Point2D from = source.getLocation(edge, target.getLocation());
            Point2D to = target.getLocation(edge, source.getLocation());
            
            points.add(source);
            
            double x;
            double y;
            
            x = from.getX();
            y = from.getY() + (10 * Math.signum(to.getY() - from.getY()));
            
            points.add(new Point2D.Double(x,y));
            
            x = to.getX();
            y = to.getY() + (10 * Math.signum(from.getY() - to.getY()));

            points.add(new Point2D.Double(x,y));
                        
            points.add(target);
        }
        
        return points; 
    }
}
