package ar.gov.anses.prissa.asistente.graficador.modeloresumido;


public class CeldaMR {

    private Object valor;
    
    private int idColumna;
    
    private long idFila;

    public CeldaMR(int idColumna, long idFila, Object valor) {
        super();
        this.valor = valor;
        this.idColumna=idColumna;
        this.idFila=idFila;
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }
    
    @Override
    public String toString() {
     
        return "Celda[ valor: " + this.valor + ", tablaVinculada: "+this.getTablaVinculada()+"]";
    }
    
    private TablaMR tablaVinculada;
    
    public void vincular(TablaMR tabla) {

        this.tablaVinculada = tabla;
    }

    public boolean tieneVinculo() {

        return this.tablaVinculada != null;
    }

    public TablaMR getTablaVinculada() {

        return this.tablaVinculada;
    }

	public int getIdColumna() {
		return idColumna;
	}

	
	public long getIdFila() {
		return idFila;
	}

}
