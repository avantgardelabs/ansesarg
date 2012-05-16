package ar.gov.anses.prissa.asistente.graficador.modelo;

public class Celda {

    private String valor;

    public Celda(String valor) {
        super();
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
        
    }
    
    @Override
    public String toString() {
     
        return "Celda[ valor: " + this.valor + "]";
    }
}
