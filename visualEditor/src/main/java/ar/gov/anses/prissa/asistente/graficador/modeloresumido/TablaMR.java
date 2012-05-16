package ar.gov.anses.prissa.asistente.graficador.modeloresumido;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;

/**
 * 
 */
public class TablaMR implements Iterable<CeldaMR> {


    
    private CeldaMR createCeldaVacia(long idFila, int idColumna) {
    	return new CeldaMR(idColumna, idFila, "") {
    		 @Override
    	        public void setValor(Object valor) {
    	            throw new UnsupportedOperationException("Instancia constante");
    	        }
    		
    	};
    }
    
    private String titulo;
    private Date fecha;
    private String urlEdicion;
    private List<Number> filas;
    private List<ColumnaMR> columnas;
    private Map<IdCelda, CeldaMR> celdas;
    transient private IdCelda cursor = new IdCelda(); 

    public TablaMR(TablaDecision td) {
        super();
        this.filas = new LinkedList<Number>();
        this.columnas = new LinkedList<ColumnaMR>();
        this.celdas = new HashMap<IdCelda, CeldaMR>();
        initData(td);
    }

    private void initData(TablaDecision td) {
    	String dominio = td.getDominio().getDescripcion();
    	String version = td.getVersionRegla();
    	String nombre = td.getNombre();
    	//String descripcion = td.getDescripcion();
    	titulo = dominio+": "+nombre+" - v"+version;
    	fecha = td.getFecha();
    	urlEdicion = "http://www.google.com.ar";
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

            ColumnaMR nuevaColumna = nombre != null ? new ColumnaMR(this, idColumna, nombre) :
                                                    new ColumnaMR(this, idColumna, createCeldaVacia(0, idColumna.intValue()));
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
    
    public ColumnaMR getColumna(Number idColumna) {
        
        ColumnaMR columna = this.getColumnaImpl(idColumna);
        
        if (columna == null) {
            
            throw new NoSuchElementException("No existe la columna con id : " + idColumna);
        }
        
        return columna;
    }

    private boolean columnaExistente(Number idColumna) {
        
        return this.getColumnaImpl(idColumna) != null;
    }
    
    private ColumnaMR getColumnaImpl(Number idColumna) {
        
        boolean encontrado = false;
        Iterator<ColumnaMR> itColumnas = this.columnas.iterator();

        ColumnaMR columna = null;
        
        while (! encontrado && itColumnas.hasNext()) {
            
            columna = itColumnas.next();
            encontrado = columna.getId().equals(idColumna);
            
        }
        
        return encontrado ? columna : null;
    }

    /**
     * Agrega una CeldaMR a la Tabla para la fila y columna indicados a partir
     * de sus identificadores.
     * Si la columna y/o la fila no existen, las agrega.
     * 
     * @param idFila identificador de la fila
     * @param idColumna identificador de la columna
     * @param valor 
     */
    public void addCelda(Number idFila, Number idColumna, Object valor) {
        
        CeldaMR nuevaCelda = new CeldaMR(idColumna.intValue(), idFila.longValue(), valor);
        this.celdas.put(new IdCelda(idFila, idColumna), nuevaCelda);
        
        this.addFila(idFila);
        this.addColumna(idColumna, null);
    }

    public Iterator<CeldaMR> iterator() {
        return new IteradorTabla();
    }
    
    protected Iterator<CeldaMR> iteratorColumna(ColumnaMR columna) {
        
        return new IteradorColumna(columna);
    }
    
    public  List<ColumnaMR> getColumnas() {

        return this.columnas;
    }
    
	public List<CeldaMR> getCeldas(Number idColumna) {
		List<CeldaMR> ret = new ArrayList<CeldaMR>();
		int cf = this.getCantidadDeFilas();
		for (int i = 0; i < cf; i++) {
			CeldaMR celda = this.getCeldaEnPosicion(i, idColumna.intValue()-1);
			ret.add(celda);
		}
		return ret;
	}
    
    public int getCantidadDeColumnas() {

        return this.columnas.size();
    }

    public int getCantidadDeFilas() {

        return this.filas.size();
    }
    
    public CeldaMR getCelda(Number idFila, Number idColumna) {

        this.cursor.setIdFila(idFila);
        this.cursor.setIdColumna(idColumna);
        
        CeldaMR celda = this.celdas.get(this.cursor);

        return celda != null ? celda : createCeldaVacia(idFila.longValue(), idColumna.intValue());
    }

    public CeldaMR getCeldaEnPosicion(int nroFila, int nroColumna) {

        try {
            
            return this.getCelda(this.filas.get(nroFila), this.getColumnaEnPosicion(nroColumna).getId());
        
        } catch (IndexOutOfBoundsException e) {
            
            throw new NoExisteCeldaExceptionMR("No existe celda en la fila nro '" + 
                    nroFila + "' y columna nro '" + nroColumna + "'", e);
        }
    }
    
    public ColumnaMR getColumnaEnPosicion(int nroColumna) {

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
    int getPosicion(ColumnaMR columna) {
        
        return this.columnas.indexOf(columna);
    }
    
    /**
     * Implementación de Iterator para recorrer todas las Celdas de una Columna
     * de la Tabla.
     */
    private class IteradorColumna implements Iterator<CeldaMR> {
        
        private IdCelda idCelda;
        private Iterator<Number> itFila;
        
        public IteradorColumna(ColumnaMR columna) {
            
            this.itFila = TablaMR.this.filas.iterator();
            this.idCelda = new IdCelda();
            this.idCelda.setIdColumna(columna.getId());
        }
        
        public boolean hasNext() {

            return this.itFila.hasNext();
        }

        public CeldaMR next() {

            if (! this.hasNext()) {
                
                throw new NoExisteCeldaExceptionMR();
            }
            
            CeldaMR celda;

            this.idCelda.setIdFila(this.itFila.next());
            celda = TablaMR.this.celdas.get(this.idCelda);

            if (celda == null) {
                
                celda = createCeldaVacia(this.idCelda.idFila.longValue(), this.idCelda.idColumna.intValue());
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
    private class IteradorTabla implements Iterator<CeldaMR> {

        private IdCelda idCelda;
        
        private Iterator<Number> itFila;
        private Iterator<ColumnaMR> itColumna;
        
        public IteradorTabla() {
         
            this.itFila = TablaMR.this.filas.iterator();
            this.itColumna = TablaMR.this.columnas.iterator();
            
            if (this.itFila.hasNext()) {
                
                this.idCelda = new IdCelda();
                this.idCelda.setIdFila(this.itFila.next());
                
            }
        }
        
        public boolean hasNext() {

            return this.itFila.hasNext() || this.itColumna.hasNext();
        }

        public CeldaMR next() {

            if (! this.hasNext()) {
                
                throw new NoExisteCeldaExceptionMR();
            }
            
            CeldaMR celda;

            if (! this.itColumna.hasNext()) {
                
                this.itColumna = TablaMR.this.columnas.iterator();
                this.idCelda.setIdFila(this.itFila.next());
            }
            
            this.idCelda.setIdColumna(this.itColumna.next().getId());
            celda = TablaMR.this.celdas.get(this.idCelda);

            if (celda == null) {
                
                celda = createCeldaVacia(this.idCelda.idFila.longValue(), this.idCelda.idColumna.intValue());
            }
            
            return celda;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public String toString() {

            return TablaMR.this.celdas.values().toString();
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

	public String getTitulo() {
		return titulo;
	}

	public Date getFecha() {
		return fecha;
	}

	public String getUrlEdicion() {
		return urlEdicion;
	}


}
