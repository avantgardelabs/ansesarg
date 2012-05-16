package ar.gob.anses.prissa.mi.asistente_reglas.formulas;

//    Esta clase implementa una estructura de tipo PILA para alamcenar en 
//    ella los operandos y parentesis que van apareciendo en la expresion
//    aritmetica en notacion infija (la clasica). Durante el proceso de 
//    traduccion se iran cogiendo segun la prioridad de cada uno de ellos.
//    Se implementa los metodos tipicos para el manejo de una pila. 

class pila_traduccion {

    private static pila_traduccion cima;
    
    private byte operador;
    private pila_traduccion siguiente;
    
    
    public pila_traduccion() {
    
        cima = null;
    }
    
    public pila_traduccion (byte elemento) {
    
        operador = elemento;
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
    
        
    public static void apila(byte elemento) {
    
        new pila_traduccion(elemento);
    }
    
    
    public static byte lee_cima() {
    
        if (cima == null)
            return -1;
        else
            return cima.operador;
    }
 
       
    public static byte desapila() {
    
        byte elemento = -1;
        
        if (cima != null) {
            elemento = cima.operador;
            cima = cima.siguiente;
        }
        return elemento;
    }
    
    
    public static void quita_cima() {
    
        cima = cima.siguiente;
    }
}
    
