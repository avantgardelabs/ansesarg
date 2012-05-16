package ar.gob.anses.prissa.mi.asistente_reglas.formulas;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Formula;

@Name("analizaExistenciaFormula")

public class AnalizaExistenciaFormula {

	@In
	private EntityManager entityManager;
	
 	public boolean existeFormula(String nombre){
		Formula formula = new Formula();
		Query query = entityManager.createQuery("SELECT f FROM Formula f where f.nombre = :nom").setParameter("nom", nombre);
		formula = (Formula) query.getSingleResult();
		if(formula!=null)
			return true;
		else
			return false;
	}
}
