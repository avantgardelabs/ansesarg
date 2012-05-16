package ar.gob.anses.prissa.mi.asistente_reglas.formulas;

//    Esta clase implementa una estructura de tipo PILA para alamcenar en 
//    ella los operandos y los resultados intermedios fruto de la evaluacion
//    de la expresion artimetica. Se implementa los metodos tipicos para el
//    manejo de una pila. 

class pila_evaluacion {

    private static pila_evaluacion cima;
    
    private double operando;
    private pila_evaluacion siguiente;
    
    
    public pila_evaluacion() {
    
        cima = null;
    }
    
    public pila_evaluacion (double valor) {
    
        operando = valor;
        if (cima == null) {
            cima = this;
            siguiente = null;
        } else {
            siguiente = cima;
            cima = this;
        }

    }
    
    
    public static boolean vacia() {
        boolean estado = false;
        
        if (cima == null)
            estado = true;
            
        return estado;
    }
    
        
    public static void apila(double elemento) {
    
        new pila_evaluacion(elemento);
    }
    
    
    public static double lee_cima() {
    
        if (cima == null)
            return -1;
        else
            return cima.operando;
    }
 
       
    public static double desapila() {
    
        double elemento = 0;
        
        if (cima != null) {
            elemento = cima.operando;
            cima = cima.siguiente;
        }
        return elemento;
    }
    
    
    public static void quita_cima() {
    
        cima = cima.siguiente;
    }
}
    
