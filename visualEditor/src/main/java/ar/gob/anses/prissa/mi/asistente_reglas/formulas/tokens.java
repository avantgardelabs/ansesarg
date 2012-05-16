package ar.gob.anses.prissa.mi.asistente_reglas.formulas;

class tokens {
    
//        Tokens que pueden aparecer en una expresion aritmetica
    
    final static byte FIN = 0; 
    final static byte ERROR = 1; 
    final static byte CONSTANTE = 2; 
    final static byte VARIABLE = 3; //Puede ser otra Formula
    final static byte SUMA = 4; 
    final static byte RESTA = 5; 
    final static byte PRODUCTO = 6; 
    final static byte DIVISION = 7; 
    final static byte POTENCIA = 8; 
    final static byte MENOS_UNARIO = 9;   //Este token solo es para la traduccion.
    final static byte PARENTESIS_A = 10; 
    final static byte PARENTESIS_C = 11; 
    final static byte REDONDEAR = 12;
    final static byte RAIZ = 13;
    final static byte MAX = 14;
    final static byte MIN = 15;
    final static byte SUMATORIA = 16;
    final static byte CONTAR=17;
    final static byte PROMEDIO=18;


//        Palabras reservadas. Son funciones matematicas que se pueden usar
//        en una expresion aritmetica.

    final static int NUM_PAL_RES = 7;
    final static String palabras_reser[] = {"redondear",
    										"contar",
                                            "raiz",
                                            "max",
                                            "min",
                                            "promedio",
                                            "sumatoria"};
    

//        Posibles errores que puede reconocer el parser (quizas, no todos)

    final static String ERROR1 = "Se ha encontrado un operando despues de un ')'"+
                                 " u otro operando.";
    final static String ERROR2 = "Se ha encontrado un '(' cuando esperaba un operador";
    final static String ERROR3 = " en expresi\u00F3n aritm\u00E9tica";
    
}
