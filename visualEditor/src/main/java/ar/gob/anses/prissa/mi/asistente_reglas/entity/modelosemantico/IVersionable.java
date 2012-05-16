package ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico;

public interface IVersionable extends IEntity{
	
	Long getVersionNumber();
	void setVersionNumber(Long versionNumber);
	
	boolean isRemoved();
	void setRemoved(boolean val);
	
	Long getIdOriginal();
	void setIdOriginal(Long val);
	
	Long getPreviousVersionNumber();
	void setPreviousVersionNumber(Long val);
	
	boolean isLastVersion();
	void setLastVersion(boolean val);
}
