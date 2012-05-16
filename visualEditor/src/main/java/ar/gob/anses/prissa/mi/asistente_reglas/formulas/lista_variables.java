package ar.gob.anses.prissa.mi.asistente_reglas.formulas;

//    Esta clase implementa una lista circular, donde el primer elemento de
//    la lista esta senalado por el puntero del ultimo elemento de esta y este 
//    ultimo elemento por el puntero 'fin'.
//    En esta lista se almacenan las variables que aparecen en la expresion 
//    aritmetica asi como el valor asociado a cada una de ella.


class lista_variables {

    private static lista_variables fin ;
    
    private String         nombre;
    private double         valor;
    private lista_variables siguiente;
    
    
    public lista_variables(){
        fin = null;
    }
    
    public lista_variables(String name, double value) {
    
        anade_lista(this); 
        nombre = name;
        valor = value;
    }
        
        
    private void anade_lista(lista_variables elemento) {
    
        if (fin == null) {
            fin = elemento; 
            elemento.siguiente = fin;
        } else {
            elemento.siguiente = fin.siguiente;
            fin.siguiente = elemento;
            fin = elemento;
        }
    }

    
    
        
//    Este metodo se encarga de buscar una variable dada 'name' en la lista
//    de variables. Si la encuentra, devuelve la direccion de la variable para
//    poder acceder al valor asociado a ella. En caso contario devuelve null.
    
    public static lista_variables busca_variable(String name) {
        lista_variables variable = null;
        
        if (fin != null) {
            lista_variables elemento = fin.siguiente;
            boolean salida = true;
            do {
                if (elemento.nombre.equalsIgnoreCase(name)) {
                    salida = false;
                    variable = elemento;
                } else
                    elemento = elemento.siguiente;
            } while ((elemento != fin.siguiente) && (salida));
        }
        return variable;
    }
        
    
    
//    Metodos que permiten el acceso a las distintas variables 
//    que conforman a la clase.

    public double Valor() {
    
        return valor;
    }

//

    public static lista_variables FIN () {
    
        return fin;
    }

//
    
    public lista_variables Siguiente () {
    
        return siguiente;
    }
     
    
//
            
    public String Nombre () {
    
        return nombre;
    }

}    //******************    lista_variables.java    ******************
