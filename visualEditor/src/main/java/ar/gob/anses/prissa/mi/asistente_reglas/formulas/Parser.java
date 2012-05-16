package ar.gob.anses.prissa.mi.asistente_reglas.formulas;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Formula;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Incognita;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;

//    Esta clase se encarga de realizar el reconocimento sintatico de una
//    expresion aritmetica expresada de  forma infija  (la clasica). Para
//    ello, llama constantemente a la  clase 'Scanner' hasta alcanzar  el
//    final de la expresion. La clase 'Scanner' le ira pasando al 'Parser'
//    los distintos tokens que distinga en la expresion aritmetica  e ira
//    comprobando  si  estan  colocados  en  una secuencia sintaticamente 
//    correcta. Paralelamente, el 'Parser' tambien  invocara  a la  clase 
//    'Traductor_polaca' pasandole los tokens verificados para que, esta,
//    vaya traduciendo la expresion aritm. a su equivalente a polaca.


class Parser {
	
    private static byte token = 0;

    
//    El constructor de esta clase se encarga de inicializar las clases en  la
//    que se apoya:
//      - Scanner : le dara la expresion aritm. para que, a partir de esta, le
//                  de el siguiente token, cada vez que  Parser llame  a  esta 
//                  clase.
//      - Traductor_polaca : conforme Parser vaya verificando los tokens que le
//                  pase Scanner, a su vez se los ira pasando a esta clase para
//                  que al final del reconocimiento sintactico, tenga la expre.
//                  aritmetica expresada en notacion polaca.

    public Parser(String cadena) {
        
        new Scanner(cadena);
        new Traductor_polaca();
    }

    
            
//    La clase que invoque a Parser, cuando esta  finalize, podra  consultar el
//    resultado final del reconocimiento sintatico a traves de este metodo.
//        Si token = tokens.FIN entonces 
//                el reconocimiento ha terminado con exito
//        sino
//                Ha habido algun error

    public static byte resultado() {
    
        return token;
    }
    


//    Metodo principal de esta clase. En el analisis sintatico de una expresion
//    aritmetica, se rige por el diagrama sintactico mostrado en la documentacion
//    adjunta a esta aplicacion.

    public static void analiza() {
    
        token = Scanner.leer_token();
        
        if ((token == tokens.SUMA) || (token == tokens.RESTA))  {
            if (token == tokens.RESTA) 
                new Traductor_polaca(tokens.MENOS_UNARIO);
            token = Scanner.leer_token();
        }
        
        termino();
        
        while ((token == tokens.SUMA) || (token == tokens.RESTA)) {
            new Traductor_polaca(token);
            token = Scanner.leer_token();
            termino();
        }
        
        if ((token != tokens.VARIABLE) && (token != tokens.CONSTANTE))
            new Traductor_polaca(token);
    }

    
    private static void termino () {
    
        factor();
        
        while ((token == tokens.PRODUCTO) || (token == tokens.DIVISION)
               || (token == tokens.POTENCIA)) {
            new Traductor_polaca(token);   
            token = Scanner.leer_token();
            factor();
        }
    }
    
    
    private static void factor () {
        
        switch (token) {
            case tokens.CONSTANTE : 
                new Traductor_polaca(token,Scanner.valor_constante());
                token = Scanner.leer_token();
                break;
                     
            case tokens.VARIABLE  : 
                new Traductor_polaca(token, Scanner.identificador_variable());
                 if((existeFormula(Scanner.identificador_variable()))||(existeVariable(Scanner.identificador_variable())))
                	token = Scanner.leer_token();
                else
                	token = tokens.ERROR;
                break;
                
            case tokens.REDONDEAR :                // Funciones matematicas.
            case tokens.RAIZ :
            case tokens.MAX:
            case tokens.MIN:
            case tokens.CONTAR:
            case tokens.PROMEDIO:
            case tokens.SUMATORIA:
                new Traductor_polaca(token);
                token = Scanner.leer_token();
                if (token == tokens.PARENTESIS_A) {
                    new Traductor_polaca(token);
                    analiza();
                    if (token == tokens.PARENTESIS_C)  
                        token = Scanner.leer_token();
                    else
                        token = tokens.ERROR;
                } else 
                    token = tokens.ERROR;
                break;
                
            case tokens.PARENTESIS_A :
                new Traductor_polaca(token);
                analiza();
                if (token == tokens.PARENTESIS_C)  
                     token = Scanner.leer_token();
                else
                     token = tokens.ERROR;             
                break;
                
            default : 
                token = tokens.ERROR;
        }
    }
    


//    Metodo que devuelve la expresion aritmetica traducida a su notacion
//    postfija. Este metodo, a  su vez,  invoca a  un metodo de la clase
//    'Traductor_polaca' quien es la que realmente contiene la expresion.

    public static Traductor_polaca expr_polaca() {
    
        return Traductor_polaca.expresion();
    }

    
            
//    Se utiliza para conocer en que posicion de la cadena estaba cuando se 
//    ha producido el error (mas o menos).

    public static int  Posicion() {
    
        return Scanner.Posicion();
    }
    
//	@In(create = true)
//	private static EntityManager entityManager;
//	
 	public static boolean existeFormula(String nombre){
 		
 		EntityManager em = (EntityManager) Component.getInstance("entityManager");
		Formula formula = null;
 		Query query = em.createQuery("SELECT f FROM Formula f where f.nombre = :nom").setParameter("nom", nombre);
		
		try {
			formula = (Formula) query.getSingleResult();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(formula!=null)
			return true;
		else
			return false;
	}
 	
 public static boolean existeVariable(String nombre){
	 List<Incognita> incognitas = CalculadoraExpresion.getIncognitas();
 
	 if(nombre.length()>1)
		 return false;
	 else{
		 char cod = nombre.charAt(0);
	 for (Iterator iterator = incognitas.iterator(); iterator.hasNext(); ) {
		    Incognita inc = (Incognita) iterator.next();
		    if(inc.getCodigo()==cod)
		    	return true;
	 	}
	 }
	 return false;
 }	
 	
//public static boolean existeVariable(String nombre){
// 		
// 		EntityManager em = (EntityManager) Component.getInstance("entityManager");
//		Atributo atributo = null;
//
//		String[] cad = nombre.split("\\.");
//		Query query = null;
//		if(cad.length>1){
//		query = em.createQuery("SELECT a FROM Atributo a where ((a.nombre = :nomAtr) AND (a.entidad.nombre = :nomEnt))").setParameter("nomAtr", cad[1]).setParameter("nomEnt", cad[0]);
//		}
//		try {
//			atributo = (Atributo) query.getSingleResult();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		if(atributo!=null)
//			return true;
//		else{
////			if(existeEntidad(cad[0])==true)
////				return true;
//			return false;
//			}
//		
//	}

//	public static boolean existeEntidad(String nombre){
//			
//		EntityManager em = (EntityManager) Component.getInstance("entityManager");
//		Entidad entidad = null;
//		Query query = em.createQuery("SELECT e FROM Entidad e where e.nombre = :nom").setParameter("nom", nombre);
//		
//		try {
//			entidad = (Entidad) query.getSingleResult();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		if(entidad!=null)
//			return true;
//		else
//			return false;
//	}

}  // ******************        FIN DE LA CLASE        *******************
