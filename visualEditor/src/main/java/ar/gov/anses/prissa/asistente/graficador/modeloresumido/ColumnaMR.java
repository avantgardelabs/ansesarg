package ar.gov.anses.prissa.asistente.graficador.modeloresumido;


import java.util.Iterator;

/**
 * Una Columna agrupa Celdas bajo un encabezado común.
 *
 */
public class ColumnaMR implements Iterable<CeldaMR>{

    private Number id;
    
    private TablaMR tabla;
    
    private CeldaMR encabezado;
    


    private ColumnaMR(TablaMR tabla, Number id) {
        super();
        this.tabla = tabla;
        this.id = id;
    }
    
    public ColumnaMR(TablaMR tabla, Number id, CeldaMR encabezado) {
        this(tabla, id);
        this.encabezado = encabezado;
    }
    
    public ColumnaMR(TablaMR tabla, Number id, String valor) {
        this(tabla, id);
        this.encabezado = new CeldaMR(id.intValue(), 0, valor);
    }


    public CeldaMR getEncabezado() {

        return this.encabezado;
    }

    public Number getId() {
        
        return this.id;
    }

    public Iterator<CeldaMR> iterator() {
        
        return this.tabla.iteratorColumna(this);
    }
    
    public TablaMR getTabla() {
        
        return this.tabla;
    }
    
    public int getPosicion() {
        
        return this.getTabla().getPosicion(this);
    }
    
    @Override
    public String toString() {
     
        return this.getEncabezado().toString();
    }
}
