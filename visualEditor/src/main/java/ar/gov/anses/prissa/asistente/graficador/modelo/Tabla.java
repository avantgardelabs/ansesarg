package ar.gov.anses.prissa.asistente.graficador.modelo;


import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gov.anses.prissa.asistente.graficador.modeloresumido.CeldaMR;
import ar.gov.anses.prissa.asistente.graficador.modeloresumido.ColumnaMR;


/**
 * 
 */
public class Tabla implements Iterable<Celda> {

    public static final Celda CELDA_VACIA = new Celda("") {
        
        @Override
        public void setValor(String valor) {
            throw new UnsupportedOperationException("Instancia constante");
        }
    };
    
    private List<Number> filas;
    private List<Columna> columnas;
    private Map<IdCelda, Celda> celdas;
    transient private IdCelda cursor = new IdCelda(); 
    private String titulo;
    private Date fecha;

    public Tabla() {
        super();
        this.filas = new LinkedList<Number>();
        this.columnas = new LinkedList<Columna>();
        this.celdas = new HashMap<IdCelda, Celda>();
        
    }

    public Tabla(TablaDecision td) {
    	 super();
         this.filas = new LinkedList<Number>();
         this.columnas = new LinkedList<Columna>();
         this.celdas = new HashMap<IdCelda, Celda>();
         initData(td);
    }
    
    private void initData(TablaDecision td) {
    	String dominio = td.getDominio().getDescripcion();
    	String version = td.getVersionRegla();
    	String nombre = td.getNombre();
    	//String descripcion = td.getDescripcion();
    	setTitulo(dominio+": "+nombre+" - v"+version);
    	setFecha(td.getFecha());    	
    }
    /**
     * Agrega una nueva columna a la Tabla a partir de su identificador y
     * su nombre. Incorpora una celda entre los encabezados para esta nueva
     * columna utilizando el nombre como valor de la misma. 
     * 
     * @param idColumna identificador de la columna
     * @param nombre 
     */
    public void addColumna(Number idColumna, String nombre) {

        /* solo agrega la columna si no existe previamente */
        if (! this.columnaExistente(idColumna)) {

            Columna nuevaColumna = nombre != null ? new Columna(this, idColumna, nombre) :
                                                    new Columna(this, idColumna, CELDA_VACIA);
            this.columnas.add(nuevaColumna);
        }
    }
    
    /**
     * Agrega una nueva fila a la Tabla a partir de su identificador.
     * 
     * @param idFila
     */
    private void addFila(Number idFila) {
        
        /* solo agrega la columna si no existe previamente */
        if (! this.filas.contains(idFila)) {
            
            this.filas.add(idFila);
        }
    }
    
    public Columna getColumna(Number idColumna) {
        
        Columna columna = this.getColumnaImpl(idColumna);
        
        if (columna == null) {
            
            throw new NoSuchElementException("No existe la columna con id : " + idColumna);
        }
        
        return columna;
    }

    private boolean columnaExistente(Number idColumna) {
        
        return this.getColumnaImpl(idColumna) != null;
    }
    
    private Columna getColumnaImpl(Number idColumna) {
        
        boolean encontrado = false;
        Iterator<Columna> itColumnas = this.columnas.iterator();

        Columna columna = null;
        
        while (! encontrado && itColumnas.hasNext()) {
            
            columna = itColumnas.next();
            encontrado = columna.getId().equals(idColumna);
            
        }
        
        return encontrado ? columna : null;
    }

    /**
     * Agrega una Celda a la Tabla para la fila y columna indicados a partir
     * de sus identificadores.
     * Si la columna y/o la fila no existen, las agrega.
     * 
     * @param idFila identificador de la fila
     * @param idColumna identificador de la columna
     * @param valor 
     */
    public void addCelda(Number idFila, Number idColumna, String valor) {
        
        Celda nuevaCelda = new Celda(valor);
        this.celdas.put(new IdCelda(idFila, idColumna), nuevaCelda);
        
        this.addFila(idFila);
        this.addColumna(idColumna, null);
    }

    public Iterator<Celda> iterator() {
        return new IteradorTabla();
    }
    
    protected Iterator<Celda> iteratorColumna(Columna columna) {
        
        return new IteradorColumna(columna);
    }
    
    public  List<Columna> getColumnas() {

        return this.columnas;
    }
    
    public int getCantidadDeColumnas() {

        return this.columnas.size();
    }

    public int getCantidadDeFilas() {

        return this.filas.size();
    }
    
    public Celda getCelda(Number idFila, Number idColumna) {

        this.cursor.setIdFila(idFila);
        this.cursor.setIdColumna(idColumna);
        
        Celda celda = this.celdas.get(this.cursor);

        return celda != null ? celda : CELDA_VACIA;
    }

    public Celda getCeldaEnPosicion(int nroFila, int nroColumna) {

        try {
            
            return this.getCelda(this.filas.get(nroFila), this.getColumnaEnPosicion(nroColumna).getId());
        
        } catch (IndexOutOfBoundsException e) {
            
            throw new NoExisteCeldaException("No existe celda en la fila nro '" + 
                    nroFila + "' y columna nro '" + nroColumna + "'", e);
        }
    }
    
    public Columna getColumnaEnPosicion(int nroColumna) {

        try {
            
            return this.columnas.get(nroColumna);
            
        } catch(IndexOutOfBoundsException e) {
            
            throw new IllegalArgumentException("No existe la columna nro '" + nroColumna, e);
        }
    }
    
    /**
     * @param columna
     * @return posicion en la que se encuentra la columna respecto de la tabla.
     */
    int getPosicion(Columna columna) {
        
        return this.columnas.indexOf(columna);
    }
    
    public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Date getFecha() {
		return fecha;
	}

	/**
     * Implementación de Iterator para recorrer todas las Celdas de una Columna
     * de la Tabla.
     */
    private class IteradorColumna implements Iterator<Celda> {
        
        private IdCelda idCelda;
        private Iterator<Number> itFila;
        
        public IteradorColumna(Columna columna) {
            
            this.itFila = Tabla.this.filas.iterator();
            this.idCelda = new IdCelda();
            this.idCelda.setIdColumna(columna.getId());
        }
        
        public boolean hasNext() {

            return this.itFila.hasNext();
        }

        public Celda next() {

            if (! this.hasNext()) {
                
                throw new NoExisteCeldaException();
            }
            
            Celda celda;

            this.idCelda.setIdFila(this.itFila.next());
            celda = Tabla.this.celdas.get(this.idCelda);

            if (celda == null) {
                
                celda = CELDA_VACIA;
            }
            
            return celda;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    /**
     * Implementación de Iterator para recorrer todas las Celdas de la Tabla.
     */
    private class IteradorTabla implements Iterator<Celda> {

        private IdCelda idCelda;
        
        private Iterator<Number> itFila;
        private Iterator<Columna> itColumna;
        
        public IteradorTabla() {
         
            this.itFila = Tabla.this.filas.iterator();
            this.itColumna = Tabla.this.columnas.iterator();
            
            if (this.itFila.hasNext()) {
                
                this.idCelda = new IdCelda();
                this.idCelda.setIdFila(this.itFila.next());
                
            }
        }
        
        public boolean hasNext() {

            return this.itFila.hasNext() || this.itColumna.hasNext();
        }

        public Celda next() {

            if (! this.hasNext()) {
                
                throw new NoExisteCeldaException();
            }
            
            Celda celda;

            if (! this.itColumna.hasNext()) {
                
                this.itColumna = Tabla.this.columnas.iterator();
                this.idCelda.setIdFila(this.itFila.next());
            }
            
            this.idCelda.setIdColumna(this.itColumna.next().getId());
            celda = Tabla.this.celdas.get(this.idCelda);

            if (celda == null) {
                
                celda = CELDA_VACIA;
            }
            
            return celda;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public String toString() {

            return Tabla.this.celdas.values().toString();
        }
        
    }
    
    private static class IdCelda {
        
        protected Number idFila;
        
        protected Number idColumna;

        public IdCelda(Number idFila, Number idColumna) {
            super();
            this.idFila = idFila;
            this.idColumna = idColumna;
        }
        
        public IdCelda() {
        }

        private void setIdFila(Number idFila) {
            this.idFila = idFila;
        }

        private void setIdColumna(Number idColumna) {
            this.idColumna = idColumna;
        }
        
        @Override
        public boolean equals(Object other) {
         
            boolean equals = (this == other);
            
            if (! equals && other != null && other instanceof IdCelda) {
                
                IdCelda castOther = IdCelda.class.cast(other);
                
                equals = this.idFila.equals(castOther.idFila) && 
                         this.idColumna.equals(castOther.idColumna);
            }
            
            return equals;
        }
        
        @Override
        public int hashCode() {
         
            return this.idFila.hashCode() ^ this.idColumna.hashCode();
        }
    }

}
