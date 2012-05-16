package ar.gob.anses.prissa.mi.asistente_reglas.prototipo.simulacion.modelo;

import org.jboss.seam.annotations.Name;

@Name("simulador_remuneracion")
public class Remuneracion {
	
	private long periodo; //YYYYMM
	private double monto;
	public long getPeriodo() {
		return periodo;
	}
	public void setPeriodo(long periodo) {
		this.periodo = periodo;
	}
	public double getMonto() {
		return monto;
	}
	public void setMonto(double monto) {
		this.monto = monto;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(monto);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (int) (periodo ^ (periodo >>> 32));
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
		Remuneracion other = (Remuneracion) obj;
		if (Double.doubleToLongBits(monto) != Double
				.doubleToLongBits(other.monto))
			return false;
		if (periodo != other.periodo)
			return false;
		return true;
	}

}
