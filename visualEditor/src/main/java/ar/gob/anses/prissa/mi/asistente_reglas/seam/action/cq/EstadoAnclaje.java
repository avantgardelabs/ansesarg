package ar.gob.anses.prissa.mi.asistente_reglas.seam.action.cq;

/**
 * The Enum EstadoAnclaje.
 */
public enum EstadoAnclaje {

	/**
	 * Indica que el requerimiento de anclaje fue solicitado.
	 * @deprecated
	 */
	INICIADO,

	/**
	 * Indica que el requerimiento de anclaje fue procesado.
	 * @deprecated
	 */
	FINALIZADO,

	/**
	 * Estado inicial de la solicitud de anclaje de datos al ser dada de alta
	 * automáticamente desde el Asistente de Reglas o a través de CQ mediante un
	 * usuario perteneciente al grupo Administradores_Asistente.
	 */
	Ingresado,

	/**
	 * La solicitud se encuentra siendo revisada por un Analista para determinar
	 * los sistemas impactados en el pedido.
	 */
	En_Revision_Analista,

	/**
	 * El Gerente de Sistemas se encuentra analizando la solicitud para definir
	 * si corresponde darle curso y determinar las Coordinaciones involucradas.
	 */
	En_Analisis_Gerencia,

	/**
	 * La solicitud se encuentra a la espera de ser aprobada por los
	 * Coordinadores responsables de su ejecución.
	 */
	En_Aceptacion_Coord,

	/**
	 * El equipo de Sistemas se encuentra trabajando sobre el anclaje de datos.
	 */
	En_Ejecucion,

	/**
	 * La solicitud fue enviada a los responsables del área Buss para su
	 * implementación en los ambientes correspondientes.
	 */
	En_Gestion_Buss,

	/**
	 * La solicitud está siendo desplegada en el ambiente de Testing.
	 */
	En_Despliegue_Test,

	/**
	 * Los usuarios aprobadores de Normatización se encuentran ejecutando las
	 * pruebas de aceptación en el ambiente de Testing.
	 */
	En_Verificacion_Usr_Test,

	/**
	 * La solicitud está a la espera de la aprobación del Gerente de Sistemas
	 * para su despliegue en el ambiente productivo.
	 */
	En_Aprobacion_Gerencia,

	/**
	 * La solicitud está siendo desplegada en el ambiente de Producción.
	 */
	En_Despliegue_Prod,

	/**
	 * Los usuarios aprobadores de Normatización se encuentran ejecutando las
	 * pruebas de aceptación en el ambiente de Producción.
	 */
	En_Verificacion_Usr_Prod,

	/**
	 * Se indica que la solicitud quedará
	 */
	Cancelado;

}
