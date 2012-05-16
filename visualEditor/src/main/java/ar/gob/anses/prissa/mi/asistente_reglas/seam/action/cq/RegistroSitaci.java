package ar.gob.anses.prissa.mi.asistente_reglas.seam.action.cq;

import java.io.Serializable;

import org.jboss.seam.ScopeType;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.CONVERSATION)
@Name("registroSitaci")
public class RegistroSitaci implements Serializable{
	
	private static final long serialVersionUID = 3663443092857139274L;
	private String nroSitaci;
	private String dnsOrigen;
	private long longitud;
	private long cantReg;
	private String descripcion;
		
	public String getNroSitaci() {
		return nroSitaci;
	}
	public void setNroSitaci(String nroSitaci) {
		this.nroSitaci = nroSitaci;
	}
	public String getDnsOrigen() {
		return dnsOrigen;
	}
	public void setDnsOrigen(String dnsOrigen) {
		this.dnsOrigen = dnsOrigen;
	}
	public long getLongitud() {
		return longitud;
	}
	public void setLongitud(long longitud) {
		this.longitud = longitud;
	}
	public long getCantReg() {
		return cantReg;
	}
	public void setCantReg(long cantReg) {
		this.cantReg = cantReg;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	

}
