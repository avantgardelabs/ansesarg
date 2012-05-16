package ar.gov.anses.prissa.asistente.graficador.modelo;


import java.util.NoSuchElementException;

import javax.ejb.ApplicationException;

@ApplicationException
public class TablaVaciaException extends NoSuchElementException {

    private static final long serialVersionUID = -4106826738777253394L;

    public TablaVaciaException() {
    }

    public TablaVaciaException(String s) {
        super(s);
    }
    
    public TablaVaciaException(String s, Throwable cause) {
        this(s);
        this.initCause(cause);
    }

    public TablaVaciaException(Throwable cause) {
        this();
        this.initCause(cause);
    }

}
