package ar.gov.anses.prissa.asistente.graficador.modelo;


import java.util.Iterator;

/**
 * Una Columna agrupa Celdas bajo un encabezado común.
 *
 */
public class Columna implements Iterable<Celda>{

    private Number id;
    
    private Tabla tabla;
    
    private Celda encabezado;
    
    private Tabla tablaVinculada;

    private Columna(Tabla tabla, Number id) {
        super();
        this.tabla = tabla;
        this.id = id;
    }
    
    public Columna(Tabla tabla, Number id, Celda encabezado) {
        this(tabla, id);
        this.encabezado = encabezado;
    }
    
    public Columna(Tabla tabla, Number id, String valor) {
        this(tabla, id);
        this.encabezado = new Celda(valor);
    }

    public void vincular(Tabla tabla) {

        this.tablaVinculada = tabla;
    }

    public boolean tieneVinculo() {

        return this.tablaVinculada != null;
    }

    public Tabla getTablaVinculada() {

        return this.tablaVinculada;
    }

    public Celda getEncabezado() {

        return this.encabezado;
    }

    public Number getId() {
        
        return this.id;
    }

    public Iterator<Celda> iterator() {
        
        return this.tabla.iteratorColumna(this);
    }
    
    public Tabla getTabla() {
        
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
