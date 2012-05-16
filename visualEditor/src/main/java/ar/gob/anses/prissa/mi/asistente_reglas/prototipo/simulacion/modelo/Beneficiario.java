package ar.gob.anses.prissa.mi.asistente_reglas.prototipo.simulacion.modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.seam.annotations.Name;

@Name("simulador_beneficiario")
public class Beneficiario {
	
	public Beneficiario() {
		beneficio = new Beneficio();
		remuneraciones = new ArrayList<Remuneracion>();
	}
	
	private Date fechaNacimiento;
	private String nombre;
	private String apellido;
	private String cuil;
	private Date fechaInicialPago;
	private boolean autonomo;
	private boolean relacionDependencia;
	private boolean identidadAcreditada;
	private int resultadoInformeMedico;
	private boolean incompatibilidad;
	private boolean accidenteLaboral;
	private boolean sentenciaFirme;
	private boolean trabajadorActivo;
	
	
	
	public enum TipoRegularidad {
		REGULAR,
		IRREGULAR_CON_DERECHO,
		IRREGULAR_SIN_DERECHO
	}
	
	private TipoRegularidad regularidad;
	private List<Remuneracion> remuneraciones;
	private double ingresoBase;
	private Beneficio beneficio;
	
	
	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}
	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getCuil() {
		return cuil;
	}
	public void setCuil(String cuil) {
		this.cuil = cuil;
	}
	public Date getFechaInicialPago() {
		return fechaInicialPago;
	}
	public void setFechaInicialPago(Date fechaCese) {
		this.fechaInicialPago = fechaCese;
	}
	public boolean isAutonomo() {
		return autonomo;
	}
	public void setAutonomo(boolean autonomo) {
		this.autonomo = autonomo;
	}
	public boolean isRelacionDependencia() {
		return relacionDependencia;
	}
	public void setRelacionDependencia(boolean relacionDependencia) {
		this.relacionDependencia = relacionDependencia;
	}
	public TipoRegularidad getRegularidad() {
		return regularidad;
	}
	public void setRegularidad(TipoRegularidad regularidad) {
		this.regularidad = regularidad;
	}
	public List<Remuneracion> getRemuneraciones() {
		return remuneraciones;
	}
	public void setRemuneraciones(List<Remuneracion> remuneraciones) {
		this.remuneraciones = remuneraciones;
	}
	public double getIngresoBase() {
		if (ingresoBase == 0) {
			double ingreso = 0;
			for(Remuneracion remuneracion : remuneraciones) {
				ingreso += remuneracion.getMonto();
			}
			if (ingreso > 0 && remuneraciones.size() > 0) {
				ingreso = ingreso / remuneraciones.size();
			}
			ingresoBase = ingreso;
		}
		return ingresoBase;
	}
	public void setIngresoBase(double ingresoBase) {
		this.ingresoBase = ingresoBase;
	}

	public void addRemuneracion(int index, Remuneracion element) {
		remuneraciones.add(index, element);
	}
	public boolean addRemuneracion(Remuneracion o) {
		return remuneraciones.add(o);
	}
	public Remuneracion removeRemuneracion(int index) {
		return remuneraciones.remove(index);
	}
	public boolean removeRemuneracion(Object o) {
		return remuneraciones.remove(o);
	}
	public int sizeRemuneracion() {
		return remuneraciones.size();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (accidenteLaboral ? 1231 : 1237);
		result = prime * result
				+ ((apellido == null) ? 0 : apellido.hashCode());
		result = prime * result + (autonomo ? 1231 : 1237);
		result = prime * result + ((cuil == null) ? 0 : cuil.hashCode());
		result = prime
				* result
				+ ((fechaInicialPago == null) ? 0 : fechaInicialPago.hashCode());
		result = prime * result
				+ ((fechaNacimiento == null) ? 0 : fechaNacimiento.hashCode());
		result = prime * result + (identidadAcreditada ? 1231 : 1237);
		result = prime * result + (incompatibilidad ? 1231 : 1237);
		long temp;
		temp = Double.doubleToLongBits(ingresoBase);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
		result = prime * result
				+ ((regularidad == null) ? 0 : regularidad.hashCode());
		result = prime * result + (relacionDependencia ? 1231 : 1237);
		result = prime * result
				+ ((remuneraciones == null) ? 0 : remuneraciones.hashCode());
		result = prime * result + resultadoInformeMedico;
		result = prime * result + (sentenciaFirme ? 1231 : 1237);
		result = prime * result + (trabajadorActivo ? 1231 : 1237);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Beneficiario other = (Beneficiario) obj;
		if (accidenteLaboral != other.accidenteLaboral)
			return false;
		if (apellido == null) {
			if (other.apellido != null)
				return false;
		} else if (!apellido.equals(other.apellido))
			return false;
		if (autonomo != other.autonomo)
			return false;
		if (cuil == null) {
			if (other.cuil != null)
				return false;
		} else if (!cuil.equals(other.cuil))
			return false;
		if (fechaInicialPago == null) {
			if (other.fechaInicialPago != null)
				return false;
		} else if (!fechaInicialPago.equals(other.fechaInicialPago))
			return false;
		if (fechaNacimiento == null) {
			if (other.fechaNacimiento != null)
				return false;
		} else if (!fechaNacimiento.equals(other.fechaNacimiento))
			return false;
		if (identidadAcreditada != other.identidadAcreditada)
			return false;
		if (incompatibilidad != other.incompatibilidad)
			return false;
		if (Double.doubleToLongBits(ingresoBase) != Double
				.doubleToLongBits(other.ingresoBase))
			return false;
		if (nombre == null) {
			if (other.nombre != null)
				return false;
		} else if (!nombre.equals(other.nombre))
			return false;
		if (regularidad == null) {
			if (other.regularidad != null)
				return false;
		} else if (!regularidad.equals(other.regularidad))
			return false;
		if (relacionDependencia != other.relacionDependencia)
			return false;
		if (remuneraciones == null) {
			if (other.remuneraciones != null)
				return false;
		} else if (!remuneraciones.equals(other.remuneraciones))
			return false;
		if (resultadoInformeMedico != other.resultadoInformeMedico)
			return false;
		if (sentenciaFirme != other.sentenciaFirme)
			return false;
		if (trabajadorActivo != other.trabajadorActivo)
			return false;
		return true;
	}
	public Beneficio getBeneficio() {
		return beneficio;
	}
	public void setBeneficio(Beneficio beneficio) {
		this.beneficio = beneficio;
	}
	public boolean isIdentidadAcreditada() {
		return identidadAcreditada;
	}
	public void setIdentidadAcreditada(boolean identidadAcreditada) {
		this.identidadAcreditada = identidadAcreditada;
	}
	public void setResultadoInformeMedico(int resultadoInformeMedico) {
		this.resultadoInformeMedico = resultadoInformeMedico;
	}
	public int getResultadoInformeMedico() {
		return resultadoInformeMedico;
	}
	public boolean isIncompatibilidad() {
		return incompatibilidad;
	}
	public void setIncompatibilidad(boolean incompatibilidad) {
		this.incompatibilidad = incompatibilidad;
	}
	public boolean isAccidenteLaboral() {
		return accidenteLaboral;
	}
	public void setAccidenteLaboral(boolean accidenteLaboral) {
		this.accidenteLaboral = accidenteLaboral;
	}
	public boolean isSentenciaFirme() {
		return sentenciaFirme;
	}
	public void setSentenciaFirme(boolean sentenciaFirme) {
		this.sentenciaFirme = sentenciaFirme;
	}
	public boolean isTrabajadorActivo() {
		return trabajadorActivo;
	}
	public void setTrabajadorActivo(boolean trabajadorActivo) {
		this.trabajadorActivo = trabajadorActivo;
	}
	

}
