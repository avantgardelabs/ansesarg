package ar.gob.anses.prissa.mi.asistente_reglas.formulas;


//    A partir de su constructor, esta clase recibe la expresion aritmetica
//    (expr_polaca)que va a evaluar, en notacion polaca o postfija, y 
//    a continuacion pasa a evaluarla (evalua()).
//    Para evaluar una expresion, esta clase se apoya en las siguientes clases:
//
//        - lista_variables :  en ella se almacenan los valores que van tomando
//          las distintas variables  que va apareciendo en la expresion arit.
//
//        - pila_evaluacion : esta clase  implementa  una  pila donde  se van 
//          almacenando los operandos  sobre los  que se le  van  aplicar los 
//          operadores que formen la expresion aritm. El resultado de aplicar
//          un operador (normalmente binario) sobre los dos ultimos operandos
//          apilados, se almacenara en la misma pila.
//
//    El resultado de evaluar la expresion aritmetica se alamcena en 'resultado'.


class Evaluacion_polaca {

    private static Traductor_polaca expr_polaca;
    private static double resultado = 0;
    
    
    public Evaluacion_polaca(Traductor_polaca expresion) {
        
        new pila_evaluacion();
        expr_polaca = expresion;
        evalua();
    }

    

    public static double valor_final() {
    
        return resultado;
    }
    
    

    private void evalua () {
        lista_variables temporal=null;
        Traductor_polaca tope = expr_polaca;
        double valor = 0.0;
        String nombre_var="";
        
        new lista_variables();
        do {
            switch (expr_polaca.elemento()) {
                case tokens.CONSTANTE:
                    pila_evaluacion.apila
                            (Double.valueOf(expr_polaca.ident_con()).doubleValue());
                    break;
                case tokens.VARIABLE:
                    nombre_var = expr_polaca.ident_con();
                    temporal = lista_variables.busca_variable(nombre_var);
                    if (temporal != null)
                        pila_evaluacion.apila(temporal.Valor());
                    else {
//                        valor = Terminal.leeReal("--> Valor para "+nombre_var + " : "); aca va variable
                        new lista_variables(nombre_var, valor);
                        pila_evaluacion.apila(valor);
                    }
                    break;
                default:
                    aplica_operador(expr_polaca.elemento());
            }
            expr_polaca = expr_polaca.next();
        } while (expr_polaca != tope);
        
        resultado = pila_evaluacion.desapila();
    }



    
    private void aplica_operador(byte operador) {
    
        double primer_op=0, seg_op=0;

        seg_op = pila_evaluacion.desapila();
            
        switch(operador) {
            case tokens.REDONDEAR:
                pila_evaluacion.apila(-1*seg_op);
                break;
            case tokens.RAIZ:
            	pila_evaluacion.apila(-1*seg_op);
                break;
            case tokens.MAX:
            	pila_evaluacion.apila(-1*seg_op);
                break;
            case tokens.MIN:
            	pila_evaluacion.apila(-1*seg_op);
                break;
            case tokens.SUMATORIA:
            	pila_evaluacion.apila(-1*seg_op);
                break;
            case tokens.MENOS_UNARIO :
                pila_evaluacion.apila(-1*seg_op);
                break;
            case tokens.POTENCIA :
                primer_op = pila_evaluacion.desapila();
                pila_evaluacion.apila(Math.pow(primer_op,seg_op)); 
                break; 
            case tokens.PRODUCTO :
                primer_op = pila_evaluacion.desapila(); 
                pila_evaluacion.apila(primer_op*seg_op);
                break; 
            case tokens.DIVISION :
                primer_op = pila_evaluacion.desapila();
                pila_evaluacion.apila(primer_op/seg_op); 
                break; 
            case tokens.SUMA :
                primer_op = pila_evaluacion.desapila();
                pila_evaluacion.apila(primer_op+seg_op); 
                break; 
            case tokens.RESTA : 
                primer_op = pila_evaluacion.desapila();
                pila_evaluacion.apila(primer_op-seg_op);
                break; 
        }
    }

}        //*****************    Evaluacion_polaca.java    *******************
