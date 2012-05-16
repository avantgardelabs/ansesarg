package ar.gov.anses.prissa.asistente.graficador.modeloresumido.jgraph;


import java.awt.Color;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import ar.gov.anses.prissa.asistente.graficador.modeloresumido.TablaMR;


public class TablaToTableModelAdapterMR implements TableModel {

    private TablaMR tabla;
    
    public TablaToTableModelAdapterMR(TablaMR tabla) {
     
        this.tabla = tabla;
    }
    
    public int getRowCount() {

        return this.tabla.getCantidadDeFilas();
    }

    public int getColumnCount() {
        
        return this.tabla.getCantidadDeColumnas();
    }

    public String getColumnName(int columnIndex) {
        
        return (String)this.tabla.getColumnaEnPosicion(columnIndex).getEncabezado().getValor();
    }

    public Class<?> getColumnClass(int columnIndex) {
    	return getValueAt(0, columnIndex).getClass();
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
    		//return this.tabla.getCeldaEnPosicion(rowIndex, columnIndex).getValor();
    	if (columnIndex==0) {
    		Object ret = this.tabla.getCeldaEnPosicion(rowIndex, columnIndex).getValor();
    		if (ret instanceof Color) {
				return ret;
			} else {
				return Color.WHITE;
			}
    	}else {
    		Object ret = this.tabla.getCeldaEnPosicion(rowIndex, columnIndex).getValor();
    		return ret;
    	}
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        throw new UnsupportedOperationException();
    }

    public void addTableModelListener(TableModelListener l) {
    }

    public void removeTableModelListener(TableModelListener l) {
    }

}
