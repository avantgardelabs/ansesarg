package ar.gov.anses.prissa.asistente.graficador.jgraph;

import javax.swing.event.TableModelListener;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import ar.gov.anses.prissa.asistente.graficador.modelo.Tabla;

public class TablaToTableModelAdapter implements TableModel  {

    private Tabla tabla;
    
    public TablaToTableModelAdapter(Tabla tabla) {
     
        this.tabla = tabla;
    }
    
    public int getRowCount() {

        return this.tabla.getCantidadDeFilas();
    }

    public int getColumnCount() {
        
        return this.tabla.getCantidadDeColumnas();
    }

    public String getColumnName(int columnIndex) {
        
        return this.tabla.getColumnaEnPosicion(columnIndex).getEncabezado().getValor();
    }

    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return this.tabla.getCeldaEnPosicion(rowIndex, columnIndex).getValor();
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        throw new UnsupportedOperationException();
    }

    public void addTableModelListener(TableModelListener l) {
    }

    public void removeTableModelListener(TableModelListener l) {
    }

}
