package ar.gov.anses.prissa.asistente.modelosemantico;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.jboss.seam.annotations.Name;

@Entity
@Name("persona")
public class Persona {

	@Id
	private String cuil;
	
	private int tipodocumento;
	
	private String sexo;
	
	@Temporal(TemporalType.DATE)
	private Date fecha_nacimiento;
	
	@Transient
	private boolean tipo_doc_valido;
	
	@Transient
	private boolean datos_filiatorios_validos;
	
	@Transient
	private Boolean sexo_valido;
	
	private int estado_acreditado;
	
	private int dependencia_acredito;
	
	private int codigo_postal;
	
	private String localidad;
	
	private int provincia;
	
	@Temporal(TemporalType.DATE)
	private Date fecha_fallecimiento;
	
	@Transient  private Boolean persona_valida;
	@Transient  private Boolean dependencia_valida;
	@Transient  private Boolean esta_acreditado;
	@Transient  private Boolean es_argentino;
	@Transient  private Boolean reside_extranjero;
	@Transient  private Boolean edad_valida_RTI;
	@Transient  private Boolean existe_DPREV;
	@Transient  private Boolean datos_filiatorios_acreditados;
	
	@Transient  private Boolean existe_Domicilio;

	@Transient
	private String marca_legajo_domicilio;

	private String marca_de_residente;
	private String tipodomicilio;
	private String nacionalidad;
	private String udai_modifica_datos;
	private Boolean cp_provincia_localidad;


	public String getCuil() {
		return cuil;
	}

	public void setCuil(String cuil) {
		this.cuil = cuil;
	}


	public Date getFechaNacimiento() {
		return fecha_nacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fecha_nacimiento = fechaNacimiento;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	
		public Boolean getSexo_valido() {
		return sexo_valido;
	}

	public void setSexo_valido(Boolean sexo_valido) {
		this.sexo_valido = sexo_valido;
	}

	public boolean isDatos_filiatorios_validos() {
		return datos_filiatorios_validos;
	}

	public void setDatos_filiatorios_validos(boolean datos_filiatorios_validos) {
		this.datos_filiatorios_validos = datos_filiatorios_validos;
	}

	public void setTipo_doc_valido(boolean tipo_doc_valido) {
		this.tipo_doc_valido = tipo_doc_valido;
	}

	public boolean isTipo_doc_valido() {
		return tipo_doc_valido;
	}

	public Date getFecha_nacimiento() {
		return fecha_nacimiento;
	}

	public void setFecha_nacimiento(Date fecha_nacimiento) {
		this.fecha_nacimiento = fecha_nacimiento;
	}

	public int getEstado_acreditado() {
		return estado_acreditado;
	}

	public void setEstado_acreditado(int estado_acreditado) {
		this.estado_acreditado = estado_acreditado;
	}

	public int getDependencia_acredito() {
		return dependencia_acredito;
	}

	public void setDependencia_acredito(int dependencia_acredito) {
		this.dependencia_acredito = dependencia_acredito;
	}

	public int getCodigo_postal() {
		return codigo_postal;
	}

	public void setCodigo_postal(int codigo_postal) {
		this.codigo_postal = codigo_postal;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public int getProvincia() {
		return provincia;
	}

	public void setProvincia(int provincia) {
		this.provincia = provincia;
	}

	public Date getFecha_fallecimiento() {
		return fecha_fallecimiento;
	}

	public void setFecha_fallecimiento(Date fecha_fallecimiento) {
		this.fecha_fallecimiento = fecha_fallecimiento;
	}

	public Boolean getPersona_valida() {
		return persona_valida;
	}

	public void setPersona_valida(Boolean persona_valida) {
		this.persona_valida = persona_valida;
	}

	public Boolean getDependencia_valida() {
		return dependencia_valida;
	}

	public void setDependencia_valida(Boolean dependencia_valida) {
		this.dependencia_valida = dependencia_valida;
	}

	public Boolean getEsta_acreditado() {
		return esta_acreditado;
	}

	public void setEsta_acreditado(Boolean esta_acreditado) {
		this.esta_acreditado = esta_acreditado;
	}

	public Boolean getEs_argentino() {
		return es_argentino;
	}

	public void setEs_argentino(Boolean es_argentino) {
		this.es_argentino = es_argentino;
	}

	public Boolean getReside_extranjero() {
		return reside_extranjero;
	}

	public void setReside_extranjero(Boolean reside_extranjero) {
		this.reside_extranjero = reside_extranjero;
	}

	public Boolean getEdad_valida_RTI() {
		return edad_valida_RTI;
	}

	public void setEdad_valida_RTI(Boolean edad_valida_RTI) {
		this.edad_valida_RTI = edad_valida_RTI;
	}

	public Boolean getExiste_DPREV() {
		return existe_DPREV;
	}

	public void setExiste_DPREV(Boolean existe_DPREV) {
		this.existe_DPREV = existe_DPREV;
	}

	public Boolean getDatos_filiatorios_acreditados() {
		return datos_filiatorios_acreditados;
	}

	public void setDatos_filiatorios_acreditados(
			Boolean datos_filiatorios_acreditados) {
		this.datos_filiatorios_acreditados = datos_filiatorios_acreditados;
	}

	public String getMarca_legajo_domicilio() {
		return marca_legajo_domicilio;
	}

	public void setMarca_legajo_domicilio(String marca_legajo_domicilio) {
		this.marca_legajo_domicilio = marca_legajo_domicilio;
	}

	public String getMarca_de_residente() {
		return marca_de_residente;
	}

	public void setMarca_de_residente(String marca_de_residente) {
		this.marca_de_residente = marca_de_residente;
	}

	public String getTipodomicilio() {
		return tipodomicilio;
	}

	public void setTipodomicilio(String tipodomicilio) {
		this.tipodomicilio = tipodomicilio;
	}

	public String getNacionalidad() {
		return nacionalidad;
	}

	public void setNacionalidad(String nacionalidad) {
		this.nacionalidad = nacionalidad;
	}

	public String getUdai_modifica_datos() {
		return udai_modifica_datos;
	}

	public void setUdai_modifica_datos(String udai_modifica_datos) {
		this.udai_modifica_datos = udai_modifica_datos;
	}

	public Boolean getCp_provincia_localidad() {
		return cp_provincia_localidad;
	}

	public void setCp_provincia_localidad(Boolean cp_provincia_localidad) {
		this.cp_provincia_localidad = cp_provincia_localidad;
	}

	public int getTipodocumento() {
		return tipodocumento;
	}

	public void setTipodocumento(int tipodocumento) {
		this.tipodocumento = tipodocumento;
	}

	public void setExiste_Domicilio(Boolean existe_Domicilio) {
		this.existe_Domicilio = existe_Domicilio;
	}

	public Boolean getExiste_Domicilio() {
		return existe_Domicilio;
	}
	
}
