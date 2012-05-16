package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Ambito;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.SubRegla;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.utils.Elemento;


@Name("asistente")
@Scope(ScopeType.SESSION)
public class AsistenteAction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2946859422941315469L;

	
	/*
	@In 
	private EntityManager entityManager;
	*/
	
	private Collection<SubRegla> listaRequisitos;
	private Collection<SubRegla> listaAniosAportes;
	private Collection<SubRegla> listaRemuneracionReferencia;
	private Collection<SubRegla> listaCriteriosMovilidad;
	private Collection<SubRegla> listaConceptosLiquidacion;
	private Collection<SubRegla> listaTipoPrestacion;
	private Collection<SubRegla> listaRegimenAportes;
	
	
	private String rootMap="";
	private ArrayList pasosRealizados;
	private String paso;
	private boolean discriminaSexo;
	
	
	@RequestParameter
	private String accion;
	
 	public AsistenteAction(){
 		
		
	}
 	
 	public String llamarToolkit(String origen){
 		return "/toolkit.seam";
 	}

	public String establecerPaso(String param) {
		
		paso = param;
		
		
		if (accion!=null && accion.equals("COMENZAR"))
			pasosRealizados=new ArrayList();
		
		
		
		if (paso.equals("NOMBREREGLA")){
			pasosRealizados = new ArrayList();
			rootMap =  "Nombre de la regla";
		}
			
		
		
		if (paso.equals("TIPOPRESTACION"))
			rootMap =  "Tipo de prestación";
		
		if (paso.equals("TIPOTRABAJADOR"))
			rootMap = "Tipo de trabajador";
						
		if (paso.equals("REMUNERACIONREFERENCIA"))
			rootMap =  "Remuneración de referencia";
		
		if (paso.equals("REGIMENAPORTE"))
			rootMap = "Régimen de aportes";
		
		if (paso.equals("MOVILIDAD"))
			rootMap = "Determinación de movilidad";
		
		if (paso.equals("INSTRUMENTONORMATIVO"))
			rootMap = "Instrumentos normativos";
		
		if (paso.equals("CONCEPTOSLIQUIDACION"))
			rootMap = "Conceptos de liquidación";
		
		if (paso.equals("ANIOSAPORTADOS"))
			rootMap = "Determinación de años aportados";
		
		if (paso.equals("FINAL")){
			rootMap="";
			if (this.discriminaSexo){
				paso="INSTRUMENTONORMATIVO";
				discriminaSexo=false;
				pasosRealizados=new ArrayList();
			}
			else {
				paso="BIENVENIDA";
			}
		}
		
		Elemento elemento =new Elemento();
		elemento.setDescripcion(rootMap);
		
		if(pasosRealizados==null)
			pasosRealizados=new ArrayList();
		
		elemento.setId(pasosRealizados.size()+1);
		
		//Buscamos que no existan repetidos
		boolean repetido=false;
		Elemento temp;
		
		for (int i=0;i<pasosRealizados.size();i++){
			temp = (Elemento)pasosRealizados.get(0);
			if (temp.getDescripcion().equals("rootMap")){
				repetido=true;
				break;
			}
		}
		
		if (!repetido)
			pasosRealizados.add(elemento);
	
		
		return paso;
	}

	public String metodoPrueba() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	public Collection<SubRegla> getListaRequisitos() {
		SubRegla subregla =null;
		ArrayList<SubRegla> lista = new ArrayList();
		
		subregla= new SubRegla();
		subregla.setId(1);
		subregla.setNombre("Identificación de identidad");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(2);
		subregla.setNombre("Probatoria Laboral");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(3);
		subregla.setNombre("Regularidad");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(4);
		subregla.setNombre("Criterio de Invalidez");
		
		
		lista.add(subregla);
		this.listaRequisitos = lista;
		
		
		return listaRequisitos;
	}

	public void setListaRequisitos(Collection<SubRegla> listaRequisitos) {
		this.listaRequisitos = listaRequisitos;
	}

	public Collection<SubRegla> getListaAniosAportes() {
		SubRegla subregla =null;
		ArrayList<SubRegla> lista = new ArrayList();
		
		subregla= new SubRegla();
		subregla.setId(1);
		subregla.setNombre("Por horas de vuelo");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(2);
		subregla.setNombre("Por días de embarco");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(3);
		subregla.setNombre("Por meses trabajados");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(4);
		subregla.setNombre("Por aportes verificados");
		lista.add(subregla);
		
		this.listaAniosAportes= lista;
		return listaAniosAportes;
	}

	public void setListaAniosAportes(Collection<SubRegla> listaAniosAportes) {
		this.listaAniosAportes = listaAniosAportes;
	}

	public Collection<SubRegla> getListaRemuneracionReferencia() {
		SubRegla subregla =null;
		ArrayList<SubRegla> lista = new ArrayList();
		
		subregla= new SubRegla();
		subregla.setId(1);
		subregla.setNombre("Promedio de Últimas 60 remuneraciones");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(2);
		subregla.setNombre("Promedio de Últimas 120 remuneraciones");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(3);
		subregla.setNombre("Última Percibida");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(4);
		subregla.setNombre("Mejor de los Últimos dos años");
		lista.add(subregla);
		
		this.listaRemuneracionReferencia= lista;
		
		return listaRemuneracionReferencia;
	}

	public void setListaRemuneracionReferencia(
			Collection<SubRegla> listaRemuneracionReferencia) {
		this.listaRemuneracionReferencia = listaRemuneracionReferencia;
	}

	public Collection<SubRegla> getListaCriteriosMovilidad() {
		SubRegla subregla =null;
		ArrayList<SubRegla> lista = new ArrayList();
		
		subregla= new SubRegla();
		subregla.setId(1);
		subregla.setNombre("General 82% Movil");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(2);
		subregla.setNombre("General Personal Docente");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(3);
		subregla.setNombre("General Convenio Metalurgico");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(4);
		subregla.setNombre("N/A");
		lista.add(subregla);
		
		this.listaCriteriosMovilidad= lista;
		
		
		return listaCriteriosMovilidad;
	}

	public void setListaCriteriosMovilidad(
			Collection<SubRegla> listaCriteriosMovilidad) {
		this.listaCriteriosMovilidad = listaCriteriosMovilidad;
	}

	public Collection<SubRegla> getListaConceptosLiquidacion() {
		SubRegla subregla =null;
		ArrayList<SubRegla> lista = new ArrayList();
		
		subregla= new SubRegla();
		subregla.setId(1);
		subregla.setNombre("HABER MENSUAL");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(2);
		subregla.setNombre("P.B.U - PRESTACION BASICA UNIVERSAL");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(3);
		subregla.setNombre("P.C   - PRESTACION COMPENSATORIA");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(4);
		subregla.setNombre("P.A.P - PRESTACION ADIC.P/PERMANENC");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(5);
		subregla.setNombre("RETIRO TRANSITORIO POR INVALIDEZ");
		lista.add(subregla);			
		
		this.listaConceptosLiquidacion= lista;
		return listaConceptosLiquidacion;
	}

	public void setListaConceptosLiquidacion(
			Collection<SubRegla> listaConceptosLiquidacion) {
		this.listaConceptosLiquidacion = listaConceptosLiquidacion;
	}

	public void prueba() {
		/*
		Ambito ambito  = new Ambito();
		ambito.setDescripcion("Prueba");
		entityManager.persist(ambito);
		*/
	}

	public Collection<SubRegla> getListaTipoPrestacion() {
		SubRegla subregla =null;
		ArrayList<SubRegla> lista = new ArrayList();
		
		subregla= new SubRegla();
		subregla.setId(0);
		subregla.setNombre("Jubilación");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(1);
		subregla.setNombre("Jubilación Bancaria");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(2);
		subregla.setNombre("Jubilación Bancaria Mixta");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(3);
		subregla.setNombre("Jubilación Seguro");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(4);
		subregla.setNombre("Jubilación Seguro Mixta");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(5);
		subregla.setNombre("R.T.I");
		lista.add(subregla);			
		
		this.listaTipoPrestacion= lista;
		
		return listaTipoPrestacion;
	}

	public void setListaTipoPrestacion(Collection<SubRegla> listaTipoPrestacion) {
		this.listaTipoPrestacion = listaTipoPrestacion;
	}

	
	public String getRootMap() {
		
		return rootMap;
	}

	
	public void setRootMap(String rootMap) {
		this.rootMap = rootMap;
	}

	
	public Collection<SubRegla> getListaRegimenAportes() {
		SubRegla subregla =null;
		ArrayList<SubRegla> lista = new ArrayList();
		
		subregla= new SubRegla();
		subregla.setId(1);
		subregla.setNombre("Servicio sin Bonificación ");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(2);
		subregla.setNombre("Jornaleros por Hora");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(3);
		subregla.setNombre("Jornaleros por Día  ");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(4);
		subregla.setNombre("Zafreros, Cosecheros, Estibadores ex comercio");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(5);
		subregla.setNombre("Mucamas, Mozos, Hoteles, Empleados de Casino.");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(6);
		subregla.setNombre("Estibadores ex-navegación");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(7);
		subregla.setNombre("Aeronavegantes Dto. 4257 / 68 Inc. A");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(8);
		subregla.setNombre("Aeronavegantes Dto. 4257 / 68 Inc. B");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(9);
		subregla.setNombre("Aeronavegantes Dto. 4257 / 68 Inc. C");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(10);
		subregla.setNombre("Aeronavegantes Dto. 4257 / 68 Inc. D");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(11);
		subregla.setNombre("Aeronavegantes Dto. 4257 / 68 Inc. E");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(12);
		subregla.setNombre("Embarcados");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(13);
		subregla.setNombre("Docentes de Frontera sin Bonificación");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(14);
		subregla.setNombre("Docentes de Frontera con Bonifición");
		lista.add(subregla);
		
		subregla= new SubRegla();
		subregla.setId(15);
		subregla.setNombre("Docentes - Decreto 137/05 con Bonificación");
		lista.add(subregla);
		
		
		listaRegimenAportes=lista;
		
		return listaRegimenAportes;
	}

	public void setListaRegimenAportes(Collection<SubRegla> listaRegimenAportes) {
		this.listaRegimenAportes = listaRegimenAportes;
	}

	public ArrayList getPasosRealizados() {
		return pasosRealizados;
	}

	public void setPasosRealizados(ArrayList pasosRealizados) {
		this.pasosRealizados = pasosRealizados;
	}

	public boolean isDiscriminaSexo() {
		return discriminaSexo;
	}

	public void setDiscriminaSexo(boolean discriminaSexo) {
		this.discriminaSexo = discriminaSexo;
	}
	
	
	
	
	
	
	
	
	
	
	
}
