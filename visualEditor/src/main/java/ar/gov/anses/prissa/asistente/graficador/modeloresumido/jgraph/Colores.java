package ar.gov.anses.prissa.asistente.graficador.modeloresumido.jgraph;



import java.awt.Color;
import java.nio.charset.Charset;

public enum Colores {

	ROJO(new Color(255,0,0),		"Condiciones que son sobre atributos lógicos y nos son alimentados de ninguna manera,\n entiéndase la acción de otra regla."),
	NARANJA(new Color(241, 150, 70),"El atributo que se evalúa en la condición no está anclado y no tiene pedido de anclaje."),
	AMARILLO(new Color(255, 255, 0),"El atributo que se evalúa en la condición tiene un pedido de anclaje pero todavía\n no está hecho el anclaje"),
	CELESTE(new Color(0, 176, 240),	"El atributo que se evalúa en la condición, si es lógico, tiene la acción de una regla\n que lo nutre; si es físico, tiene el anclaje pedido y finalizado."),
	VIOLETA(new Color(112, 48, 160),"Condiciones que son sobre atributos lógicos y nos son alimentados de ninguna manera,\n entiéndase por una función."),
	VERDE(new Color(146, 208, 80),	"El atributo que se evalúa en la condición tiene un pedido de anclaje y ya se realizo\n el anclaje en desarrollo.");
	
	public final Color color;
	public final String descripcion;
	
	private Colores(Color c, String d) {
		color = c;
		descripcion = new String(d.getBytes(), Charset.forName("UTF8"));
	}
}
