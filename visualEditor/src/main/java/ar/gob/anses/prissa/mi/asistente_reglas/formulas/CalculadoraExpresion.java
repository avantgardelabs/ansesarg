package ar.gob.anses.prissa.mi.asistente_reglas.formulas;

import java.util.List;

import javax.faces.application.FacesMessage;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Incognita;


@Name("calcularExpresion")
public class CalculadoraExpresion {
	
	
	@In
	FacesMessages facesMessages;
	private static List<Incognita> incognitas;
	
	
	public static boolean calcularExpresion(String cadena) {
		boolean valida = false;
		byte token;
		Traductor_polaca expr = null;
       
                      //   Peticion de expr. aritmetica
                    
            if (cadena != "") {
                new Parser(cadena);          // <-- Inicializa el parser. 
                                             //     (Analizador Sintatico)
                Parser.analiza();            // <-- Se inicia el analisis
                token = Parser.resultado();  // <-- Devuelve el resultado del analisis.  
            
                if ( token == tokens.FIN) {          // <-- Analisis correcto.
                     expr = Parser.expr_polaca();    // <-- Obtencion de expr. postfija
                     escribir_notacion_polaca(expr);
//                     new Evaluacion_polaca(expr.next());    // <-- Evaluacion de eprex.
//                     resultado = Evaluacion_polaca.valor_final();
//                     System.out.println();                  //     aritmetica.  
//                     System.out.println("---> Resultado : " +
//                                         Evaluacion_polaca.valor_final());
                     FacesMessages.instance().add(
             				new FacesMessage("La Sintaxis de la f\u00F3rmula es correcta"));
                     valida = true;
                } else                               // <-- Analisis incorrecto.
                    Gestion_Error(token);
            }
            return valida;
    }


    
//

    private static void Gestion_Error(byte token) {
        String ERROR = "";
    
        if ((token == 2) || (token ==3))
            ERROR = tokens.ERROR1;
        else
            if (token == 10)
                ERROR = tokens.ERROR2;
            else
                ERROR = tokens.ERROR3;
        FacesMessages.instance().add(
//        		new FacesMessage("Error: " + ERROR + " en posicion " + Parser.Posicion()));
				new FacesMessage("Error: " + ERROR));
        System.out.println("*** ERROR : " + ERROR + "\n***         " + 
                           "en posicion " + Parser.Posicion());
    }


    
//

    private static void escribir_notacion_polaca(Traductor_polaca expr) {
        Traductor_polaca item = expr.next();
        byte token;
        
      
        do {
                token = item.elemento();
                if ((token ==2) || (token == 3)) 
                    System.out.print(item.ident_con() + " ");
                else
                	System.out.print(a_caracter(token) + " ");
                item = item.next();
        } while (item != expr.next());
        
        System.out.println();
        System.out.println();
    }
    
    public static List<Incognita> getIncognitas() {
		return incognitas;
	}



	public static void setIncognitas(List<Incognita> incognitas) {
		CalculadoraExpresion.incognitas = incognitas;
	}



	private static String a_caracter (byte elemento) {
            
            String caracter = "";
            
            switch(elemento) {
                case tokens.REDONDEAR :
                    caracter = "redondear";
                    break;
                case tokens.RAIZ :
                    caracter = "raiz";
                    break;
                case tokens.MAX:
                    caracter = "max";
                    break;
                case tokens.MIN:
                    caracter = "min";
                    break;
                case tokens.SUMATORIA:
                    caracter = "sumatoria";
                    break;
                case tokens.CONTAR:
                    caracter = "contar";
                    break;
                case tokens.PROMEDIO:
                    caracter = "promedio";
                    break;
                case tokens.MENOS_UNARIO:
                    caracter = "_";
                    break;
                case tokens.POTENCIA : 
                    caracter = "^"; 
                    break; 
                case tokens.PRODUCTO : 
                    caracter = "*";
                    break;
                case tokens.DIVISION : 
                    caracter = "/"; 
                    break; 
                case tokens.SUMA :
                    caracter = "+";
                    break;
                case tokens.RESTA : 
                    caracter = "-";
                    break;
            }
            
            return caracter;
    }
   
}