package ar.gov.anses.prissa.asistente.graficador.modelo;


import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Accion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Condicion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.CondicionAtributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.CondicionFormula;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Descisor;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.FilaTablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Literal;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;

@Scope(ScopeType.EVENT)
@Name("fabricaDeTabla")
@AutoCreate
public class FabricaDeTabla {
    
    @Logger
    private Log log;
    
    /**
     * Mapa que relaciona una tabla de decisión con su representación
     * "graficable". Utilizado para no procesar dos veces la misma tabla.
     */
    private Map<TablaDecision, Tabla> tablasCargadas = new HashMap<TablaDecision, Tabla>();

    
    /**
     * Toma una tablaDecision y la convierte en una tabla apta para ser
     * graficada por el componente de graficación.
     * 
     * @param tablaDecision
     * @return
     * @throws TablaVaciaException Si la tabla no tiene filas, columnas o acciones.
     */
    public Tabla convertirTabla(TablaDecision tablaDecision) {

        log.info("#0 - Convirtiendo la tabla #0", tablaDecision.getNombre());
        Tabla tablaDecisionGraficador = null;

        if (tablasCargadas.containsKey(tablaDecision)) {

            log.debug("#0 - La tabla #0 ya está cargada", tablaDecision.getNombre());
            tablaDecisionGraficador = tablasCargadas.get(tablaDecision);

        } else {

            if (tablaDecision.getFilas() == null || tablaDecision.getFilas().size() <= 0) {
                throw new TablaVaciaException("La tabla no tiene filas");
            }

            log.debug("#0 - La tabla tiene #1 filas", tablaDecision.getNombre(),
                    tablaDecision.getFilas().size());
            
            tablaDecisionGraficador = new Tabla(tablaDecision);
            this.tablasCargadas.put(tablaDecision, tablaDecisionGraficador);
            
            /*
             * Cargamos las columnas en base a la primera fila
             */
            cargarHeadersColumnas(tablaDecision, tablaDecisionGraficador);

            /*
             * Ahora cargamos las filas
             */
            cargarFilas(tablaDecision, tablaDecisionGraficador);

        }

        return tablaDecisionGraficador;
    }

    /**
     * Dada una tabla de decision obtiene los nombres de todas sus columnas y los carga 
     * como encabezados de la tabla graficable.
     * 
     * @param tablaDecision
     * @param tablaDecisionGraficador
     * @throws TablaVaciaException Si la tabla no tiene columnas o acciones.
     */
    private void cargarHeadersColumnas(TablaDecision tablaDecision,
            Tabla tablaDecisionGraficador) {
        FilaTablaDecision fila = tablaDecision.getFilas().get(0);

        if (fila.getValores() == null || fila.getValores().size() <= 0) {
            throw new TablaVaciaException("La tabla no tiene columnas");
        }

        for (Descisor decisor : fila.getValores()) {

            Condicion condicion = decisor.getCondicion();
            
            Long idColumna = condicion.getId();
            
            log.debug("#0 - Agregando columna #1, #2",
                      tablaDecision.getNombre(), idColumna, condicion.getNombre());
            
            tablaDecisionGraficador.addColumna(idColumna, condicion.getNombre());

            TablaDecision reglaAsociada = condicion.getRegla();

            /*
             * True si la columna está asociada a otra regla.
             */
            Boolean columnaTieneReglaAsociada = (reglaAsociada != null);

            log.debug("#0 - La columna tiene regla asociada? #1",
                      tablaDecision.getNombre(), columnaTieneReglaAsociada);

            /*
             * Cargamos recursivamente la tabla asociada.
             */
            if (columnaTieneReglaAsociada) {
                Tabla tablaReglaAsociada = this.convertirTabla(reglaAsociada);

                log.debug("#0 - Regla asociada obtenida #1",
                          tablaDecision.getNombre(), tablaReglaAsociada);

                tablaDecisionGraficador.getColumna(idColumna).vincular(tablaReglaAsociada);
            }

        }
        
        if (fila.getAcciones() == null || fila.getAcciones().size() <= 0) {
            throw new TablaVaciaException("La tabla no tiene columnas");
        }
        //Agregamos la columna acciones a la tabla
        tablaDecisionGraficador.addColumna(0, "Acci�n");
    }

    /**
     * Procesa las filas de la tabla de decisión y las carga en la tabla graficable
     * 
     * @param tablaDecision
     * @param tablaDecisionGraficador
     */
    private void cargarFilas(TablaDecision tablaDecision,
            Tabla tablaDecisionGraficador) {
       
        for (FilaTablaDecision fila : tablaDecision.getFilas()) {

            Long idFila = fila.getId();
            for (Descisor decisor : fila.getValores()) {
            	//En caso de que sea por literal
            	if(decisor.getValor()!=null){
	                Literal valorDecisor = decisor.getValor();
	                Long idColumna = decisor.getCondicion().getId();
	                StringBuilder textoCondicion = new StringBuilder();
	
	                /*
	                 * El valor de la celda se compone del operador lógico concatenado con
	                 * la descripción del campo teniendo en cuenta que la celda
	                 * puede estar vacía.
	                 * 
	                 * Tomado de fichaReglaAsistente.xhtml
	                 * 
	                 */
	                if (valorDecisor != null) {
	                    textoCondicion.append(valorDecisor.getOperadorLogico());
	                    textoCondicion.append(" ");
	                    textoCondicion.append(valorDecisor.getDescripcion());
	                }
	
	                log.debug("Añadiendo celda (#0, #1) con valor #2", idFila, idColumna, textoCondicion); 
	                tablaDecisionGraficador.addCelda(idFila, idColumna, textoCondicion.toString());
	           }
            
            //En caso de que sea atributo de otra entidad
        	if(decisor.getValorCondicionAtributo()!=null){
                CondicionAtributo condicionAtributo = decisor.getValorCondicionAtributo();
                Long idColumna = decisor.getCondicion().getId();
                StringBuilder textoCondicion = new StringBuilder();

                /*
                 * El valor de la celda se compone del operador lógico concatenado con
                 * la descripción del campo teniendo en cuenta que la celda
                 * puede estar vacía.
                 * 
                 * Tomado de fichaReglaAsistente.xhtml
                 * 
                 */
                if (condicionAtributo != null) {
                    textoCondicion.append(condicionAtributo.getOperadorLogico());
                    textoCondicion.append(" ");
                    textoCondicion.append(condicionAtributo.getAtributo2().getDescripcion());
                }

                log.debug("Añadiendo celda (#0, #1) con valor #2", idFila, idColumna, textoCondicion); 
                tablaDecisionGraficador.addCelda(idFila, idColumna, textoCondicion.toString());
            }
        	
            //En caso de que sea una Formula
        	if(decisor.getValorCondicionFormula()!=null){
                CondicionFormula condicionFormula = decisor.getValorCondicionFormula();
                Long idColumna = decisor.getCondicion().getId();
                StringBuilder textoCondicion = new StringBuilder();

                /*
                 * El valor de la celda se compone del operador lógico concatenado con
                 * la descripción del campo teniendo en cuenta que la celda
                 * puede estar vacía.
                 * 
                 * Tomado de fichaReglaAsistente.xhtml
                 * 
                 */
                if (condicionFormula != null) {
                    textoCondicion.append(condicionFormula.getOperadorLogico());
                    textoCondicion.append(" ");
                    textoCondicion.append(condicionFormula.getFormula().getCuerpo());
                }

                log.debug("Añadiendo celda (#0, #1) con valor #2", idFila, idColumna, textoCondicion); 
                tablaDecisionGraficador.addCelda(idFila, idColumna, textoCondicion.toString());
            }
        	
        }
            
            /*
             * Agregamos una columna más con todas las acciones a tomar
             */
            StringBuilder acciones = new StringBuilder();
            for(Accion accion : fila.getAcciones()){
                acciones.append(accion.getNombre())
                        .append(" ");
            }
            
            tablaDecisionGraficador.addCelda(idFila, 0, acciones.toString());
        }
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public Log getLog() {
        return log;
    }
}