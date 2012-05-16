package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("usuario")
@Scope(ScopeType.SESSION)
@AutoCreate
public class Usuario {

	private String username;
	private String metodoAutenticacion;
	private String nombre;
	private String email;
	private String oficina;
	private String oficinaDesc;
	private String ip;
	private String rol;
	private String token;
	private String sign;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getMetodoAutenticacion() {
		return metodoAutenticacion;
	}
	public void setMetodoAutenticacion(String metodoAutenticacion) {
		this.metodoAutenticacion = metodoAutenticacion;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getOficina() {
		return oficina;
	}
	public void setOficina(String oficina) {
		this.oficina = oficina;
	}
	public String getOficinaDesc() {
		return oficinaDesc;
	}
	public void setOficinaDesc(String oficinaDesc) {
		this.oficinaDesc = oficinaDesc;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public void setRol(String rol) {
		this.rol = rol;
	}
	public String getRol() {
		return rol;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
	
}
