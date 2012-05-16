package ar.gob.anses.prissa.mi.asistente_reglas.formulas;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.log.Log;

public class Terminal {
	@Logger
	Log log;
	
	/**
    * Imprime un mensaje en el dispositivo 
    * estandar de salida sin salto de linea
    * @param mensaje mensaje que se va a imprimir
    */
   public void imprimeMensaje
                               (String mensaje) {
      log.info(mensaje);
	  System.out.print(mensaje + " ");
      System.out.flush();
   }

   /**
    * Imprime un salto de linea en el dispositivo 
    * estandar de salida
    */
   public static void imprimeMensaje() {
      System.out.println();
      System.out.flush();
   }
   
   /**
    * Lee una cadena de caracteres (string) desde 
    * el terminal(desde el dispositivo estandar 
    * de entrada).  La cadena se extiende hasta 
    * el siguiente salto de linea
    * @return la cadena leida (sin salto de linea)
    */
    
   public static String leeCadena() {
      int caracter;
      String cadena = "";
      boolean fin = false;
      while (!fin) {
         try {
            caracter = System.in.read();
            if (caracter < 0 || 
                (char)caracter == '\n')
               fin = true;
            else
               if (!Character.isISOControl((char)caracter))
                  cadena += (char)caracter; 
         } catch(java.io.IOException e) {  
               fin = true;
         }
      }
      return cadena;
   }



   /**
    * Lee una cadena desde el terminal, pero en 
    * primer lugar escribe un mensaje.
    * @param  mensaje mensaje que se escribe
    * @return la cadena leida
    */
    
   public String leeCadena
                           (String mensaje) {
      imprimeMensaje(mensaje);
      return leeCadena();
   }

   /**
    * Lee un numero entero (int) desde el terminal 
    * (dispositivo estandar de entrada). El numero 
    * debe finalizar con un salto de linea.
    * @param mensaje mensaje que se imprime 
    * @return el valor numerico leido (como int)
    * @exception NumberFormatException si se 
    * introduce un valor incorrecto
    */
    
   public int leeEntero(String mensaje) {
      while(true) {
         imprimeMensaje(mensaje);
         try {
            return Integer.valueOf(
                  leeCadena().trim()).intValue();
         } catch(NumberFormatException e) {
            System.out.println
               ("ERROR: Vuelve a intentarlo.");
         }
      }
   }

   /**
    * Lee un numero real (double) desde el terminal 
    * (dispositivo estandar de entrada). El numero 
    * debe finalizar con un salto de linea.
    * @param mensaje mensaje que se imprime 
    * @return el valor numerico leido (como double)
    * @exception NumberFormatException si se 
    * introduce un valor incorrecto
    */
    
   public double leeReal(String mensaje) {
      while(true) {
         imprimeMensaje(mensaje);
         try {
            return Double.valueOf(
                  leeCadena().trim()).doubleValue();
         } catch(NumberFormatException e) {
            System.out.println
               ("ERROR: Vuelve a intentarlo.");
         }
      }
   }

}


/******** Fin de Terminal.java ***************/
