package ar.gob.anses.prissa.mi.asistente_reglas.formulas;

/********************************************************************/
/*                 Clase Analizador_Lexico                          */  
/*   Se  encarga  de  distinguir  en una  expresion aritmetica los  */
/*   distintos  elementos  atomicos (token) que  la componen. Esta  */ 
/*   clase sera llamada por el analizador sintatico, que  recibira  */
/*   los  tokens  e  ira,  con ellos verificando la sintaxis de la  */
/*   expresion aritmetica.                                          */
/*   Los tokens reconocibles estan definidos en la clase 'tokens'.  */
/********************************************************************/

class Scanner {

    private static int   posicion;     /* Indica en cada momento el ultimo
                                          caracter leido de la expr. aritmetica. */
    private static int   longitud;     /* Longitud de la expr. aritmetica.       */
    private static String expr_arit;   /* Contiene la expresion aritmetica.      */
    private static byte   token;       /* Contiene el ultimo token reconocido.   */
    private static String variable;    /* Contiene el identificador de una var.  */
    private static String constante;   /* Contiene el valor de una constante.    */




    /*  Contructor de esta clase, requiere la cadena (expr. aritmetica)   */
    
    public Scanner(String cadena) {
        expr_arit = cadena;
        posicion = 0;
        longitud = cadena.length();
    }
    
    
    
    /* Metodo principal encargado de distinguir los distintos tokens que  */
    /* forman la expresion aritmetica.                                    */
    
    public static byte leer_token () {
        String valor;
        char caracter = ' ';

        token = 0;
        if (posicion < longitud) {
           caracter = expr_arit.charAt(posicion);
           if (caracter == ' ')  caracter = quita_espacio(caracter);
        }
        if (posicion < longitud) {
            switch (caracter) {
                case '+' : token = tokens.SUMA;
                           posicion++; 
                           break; 
                case '-' : token = tokens.RESTA;
                           posicion++;                
                           break;
                case '*' : token = tokens.PRODUCTO;
                           posicion++;                
                           break; 
                case '/' : token = tokens.DIVISION;
                           posicion++;                
                           break; 
                case '^' : token = tokens.POTENCIA;
                           posicion++;                
                           break;
                case '(' : token = tokens.PARENTESIS_A;
                           posicion++;                
                           break; 
                case ')' : token = tokens.PARENTESIS_C;
                           posicion++;                
                           break; 
                default  : if ((Character.isDigit(caracter)) || (caracter == ',')) {
                                 if (caracter == '.')   valor = parte_decimal(true);
                                 else                   valor = parte_entera(caracter);
                                 if (valor == "")
                                     token = tokens.ERROR;
                                 else {
                                     token = tokens.CONSTANTE;
                                     constante = valor;
                                 }
                           } else
                                 if (((caracter >= 'a') && (caracter <= 'z')) ||
                                     ((caracter >= 'A') && (caracter <= 'Z')))  {
                                     String cadena = lee_cadena(caracter);
                                     token = var_o_funcion(cadena);
                                 } else
                                     token = tokens.ERROR;
            }
        }
        return token;
    }
    
    
    
    /*   Quita todos los espacios en blanco que pueda haber entre tokens  */
    
    private static char quita_espacio(char car) {

        while ((car == ' ') && (posicion < longitud)) {
            posicion++;
            if (posicion < longitud)
                car = expr_arit.charAt(posicion);
        }
        return car;
    }
    
    
    
    /* A continuacion  se  implementan 3  metodos  que permiten reconocer */
    /* numeros reales que utilizan la representacion IEEE 754-1985.       */
    /* Si algunos de  estos  metodos  detecta  algun  error en el numero, */
    /* devolvera una cadena vacia indicandolo.                            */
    
    /*                         PARTE_ENTERA                               */
    /* Una vez encontrado un digito numerico en la expr. aritm., se llama */
    /* a esta funcion, PARTE_ENTERA(), para coger la parte  entera, si  a */
    /* continuacion se encuentra un ',' llama a la funcion PARTE_DECIMAL, */
    /* y si se encuentra una 'e' o 'E', llamaria a PARTE_EXPONENCIAL.     */
    
    private static String parte_entera(char car) {
        String numero = "", numero2;
        
        do {
            numero += car;
            posicion++;
            if (posicion < longitud)
                car = expr_arit.charAt(posicion);
        } while ((Character.isDigit(car)) && (posicion < longitud));
        
        if (posicion < longitud)
            switch (car) {
                case '.': numero2 = parte_decimal(false);
                          if (numero2 == "")  numero = "";
                          else                numero += numero2;
                          break;
                case 'e':
                case 'E': numero2 = parte_exponencial();
                          if (numero2 == "")  numero = "";
                          else                numero += numero2;
                          break;  
                }
            
        return numero;
    }
    
    
    
    /*                             PARTE_DECIMAL                          */
    /*  Este metodo tiene dos comportamientos diferentes  en funcion  del */
    /*  parametro 'modo', de  manera  que si vale true, despues del punto */
    /*  decimal debe aparecer, al  menos, un digito (esto se exige cuando */
    /*  el numero no tiene parte entera), si 'modo' vale false (el numero */
    /*  tiene parte entera) da igual. A continuacion  se buscara si tiene */
    /*  parte exponencial.                                                */
    
    private static String parte_decimal(boolean modo) {
        String numero = ','+ "", numero2;
        char car;
        
        posicion++;
        if (posicion < longitud) {
            car = expr_arit.charAt(posicion);
            if (Character.isDigit(car)) {
                do {
                    numero += car;
                    posicion++;
                    if (posicion < longitud)
                        car = expr_arit.charAt(posicion);
                } while ((Character.isDigit(car)) && (posicion < longitud));
                
                if ((posicion < longitud) && ((car == 'e') || (car == 'E'))) {
                    numero2 = parte_exponencial();
                    if (numero2 == "") numero = "";
                    else               numero += numero2;
                }
            } else
                if (modo)   numero = "";
                else
                    if ((car == 'e') || (car == 'E')) {
                        numero2 = parte_exponencial();
                        if (numero2 == "") numero = "";
                        else               numero += numero2;
                    }
        }
        return numero;
    }    
    
    
        
    /*                         PARTE_EXPONENCIAL                          */
    
    private static String parte_exponencial() {
        String numero= 'E' + "";
        char car;
        
        posicion++;
        if (posicion < longitud) {
            car = expr_arit.charAt(posicion);
            if ((car == '+') || (car == '-')) {
                numero += car;
                posicion++;
                if (posicion < longitud)
                    car = expr_arit.charAt(posicion);
            }
            if (Character.isDigit(car)) 
                do {
                    numero += car;
                    posicion++;
                    if (posicion < longitud)
                        car = expr_arit.charAt(posicion);
                } while ((Character.isDigit(car)) && (posicion < longitud));
            else 
                numero = "";
        } else 
            numero = "";
        
        return numero;
    }

        
    
    /*                           IDENT_VARIABLE                           */
    /* Reconoce identificadores de variables. Estos  estaran  compuestos, */
    /* primero, por  un  caracter  alfabetico, y  despues  por uno  o mas */
    /* caracteres alfanumericos.                                          */
    
    private static String lee_cadena(char car) {
        String identi = "";
                
        do {
            identi += car;
            posicion++;
            if (posicion < longitud)
                car = expr_arit.charAt(posicion);
        } while ((((car >= 'a') && (car <= 'z')) || ((car >= 'A') && (car <= 'Z')) 
                    || (Character.isDigit(car))) && (posicion < longitud)||(car == '.'));
        
        return identi;
    }


    private static byte var_o_funcion(String cadena) {
        byte elemento = 3;
        byte i = 0;
        
        while (( i < tokens.NUM_PAL_RES) && 
                !(cadena.equalsIgnoreCase(tokens.palabras_reser[i])))
            i++;
            
        if (i != tokens.NUM_PAL_RES)     // Esta suma dara el valor del token, segun 
            elemento = (byte)(i + 12);   // lo definido en tokens.java     
        else
            variable = cadena; 
                                           
        return  elemento;
    }


    /*     Metodos que permiten acceder a campos privados de la clase     */ 

    public static String identificador_variable() {
        return variable;
    }
    
    public static String valor_constante() {
        return constante;
    }
    
    public static int Posicion() {
        return posicion;
    }
    
}
    
