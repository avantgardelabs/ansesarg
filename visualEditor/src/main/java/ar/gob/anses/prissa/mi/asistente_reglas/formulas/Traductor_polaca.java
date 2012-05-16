package ar.gob.anses.prissa.mi.asistente_reglas.formulas;

//    Esta clase se encarga de traducir un expresion artimetica en notacion
//    infija  a  notacion  postfija  (polaca). Es  invocada desde la clase 
//    'parser', la cual le pasa  los distintos  elementos (tokens)  que  va 
//    reconociendo y que esta utilizara para ir formando la expresion aritm.
//    en notacion polaca. Para ello se  apoya en la clase 'pila_traduccion'. 
//
//    La clase 'Traductor_polaca' ira formando una lista circular (parecida
//    a la que forma la clase 'lista_variables') donde cada elemento  tiene 
//    dos campos (esta lista contendra la expr. aritm. polaca):
//        - token : un operador, una variable o una constante.
//        - ident_o_con : si token es una variable o una constante, en este 
//          campo se almacenara el identificador o el valor, respectivamente.
//
//    Conforme le vaya llegando tokens hara lo siguiente:
//    .si token = operador lo apila, pero si el operador que hay en la cima de
//     la pila es de mayor prioridad que el que voy a apilar, este se quita y 
//     se anade a la lista circular.
//    .si token = variable o constante, se anade directamente a la lista.
//    .si token = '(' se apila directamente.
//    .si token = ')' se  deapila  todos  los  operadores  que  hay  en 
//     'pila_tarduccion' y se van anadiendo a la lista, hasta que se encuentre
//     el '(' en dicha pila.
//
//    Las  prioridades  de los  operadores se  pueden  comprobar en el metodo 
//    'prioridad_token'.


class Traductor_polaca {
        
        private static Traductor_polaca fin; 
        
        private byte token;
        private String ident_o_con;
        private Traductor_polaca siguiente;
        
        
        public Traductor_polaca() {
            new pila_traduccion();
            fin = null;
        }
        
        public Traductor_polaca(byte elemento) {
            
            switch(elemento) {
                case tokens.FIN : 
                    if (!pila_traduccion.vacia()) {
                        token = pila_traduccion.desapila();
                        ident_o_con = "";
                        anade_lista(this);
                        new Traductor_polaca(elemento);
                    }
                    break;
                case tokens.PARENTESIS_A :
                    pila_traduccion.apila(elemento);
                    break;
                case tokens.PARENTESIS_C :
                    if (!pila_traduccion.vacia()) {
                        if (pila_traduccion.lee_cima() != tokens.PARENTESIS_A) {
                            token = pila_traduccion.desapila();
                            ident_o_con = "";
                            anade_lista(this);
                            new Traductor_polaca(elemento);
                        } else 
                            pila_traduccion.quita_cima();
                    }
                    break;
                case tokens.ERROR :
                    break;
                default:
                    if (prioridad_token(elemento) > 
                        prioridad_token(pila_traduccion.lee_cima()))
                        pila_traduccion.apila(elemento);
                    else {
                        token = pila_traduccion.desapila();
                        ident_o_con = "";
                        anade_lista(this);
                        new Traductor_polaca(elemento);
                    }
            }
        }
        
        
        public Traductor_polaca(byte elemento, String especifico) {
            
            anade_lista(this); 
            token = elemento;
            ident_o_con = especifico;
        }
        
        
        private void anade_lista(Traductor_polaca elemento) {
            
            if (fin == null) {
                fin = elemento; 
                elemento.siguiente = fin;
            } else {
                elemento.siguiente = fin.siguiente;
                fin.siguiente = elemento;
                fin = elemento;
            }
        }
        
        
        private byte prioridad_token (byte elemento) {
            
            byte prioridad;
            
            switch(elemento) {
                case tokens.REDONDEAR :
                case tokens.RAIZ :
                case tokens.MAX:
                case tokens.CONTAR:
                case tokens.PROMEDIO:
                case tokens.MIN:
                case tokens.SUMATORIA:
                    prioridad = 6;
                    break;
                case tokens.MENOS_UNARIO:
                    prioridad = 5;
                    break;
                case tokens.POTENCIA : 
                    prioridad = 4; 
                    break; 
                case tokens.PRODUCTO : 
                case tokens.DIVISION : 
                    prioridad = 3; 
                    break; 
                case tokens.SUMA :
                case tokens.RESTA : 
                    prioridad = 2;
                    break;
                case tokens.PARENTESIS_A: 
                    prioridad = 1;
                    break; 
                case tokens.PARENTESIS_C: 
                    prioridad = 0;
                    break; 
                default :
                    prioridad = -1;
            }
            
            return prioridad;
        }
        
        
        public static Traductor_polaca expresion() {
            return fin;
        }
        
        
        public Traductor_polaca next() {
            
            return siguiente;
        }
        
        
        public byte elemento() {
            
            return token;
        }
        
        
        public String ident_con() {
            
            return ident_o_con;
        }
        
}
