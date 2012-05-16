package ar.gov.anses.prissa.asistente.modelosemantico;

import java.util.ArrayList;

public class ControlRegla {
	
	private  ArrayList reglas;

	
	public  void agregarRegla(String regla){
		if (!existeRegla(regla)){
			reglas.add(regla);
			System.out.println("Se agrega la regla: " + regla + " al vector ");
		}
			
	}
	
	public  boolean existeRegla(String regla){
		
		if (reglas == null){
			reglas = new ArrayList();
			return false;
		}
		else {
			for (int i=0;i<reglas.size();i++){
				if (regla.equals((String)reglas.get(i))){
					System.out.println("Se encontro la regla: " + regla + " en la posicion: " + i);
					return true;
				}
			}
			return false;
		}
			
	}
	

}
