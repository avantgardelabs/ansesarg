package ar.gov.anses.prissa.asistente.graficador.modeloresumido;


import java.util.NoSuchElementException;

import javax.ejb.ApplicationException;

@ApplicationException
public class TablaVaciaExceptionMR extends NoSuchElementException {

    private static final long serialVersionUID = -4106826738777253394L;

    public TablaVaciaExceptionMR() {
    }

    public TablaVaciaExceptionMR(String s) {
        super(s);
    }
    
    public TablaVaciaExceptionMR(String s, Throwable cause) {
        this(s);
        this.initCause(cause);
    }

    public TablaVaciaExceptionMR(Throwable cause) {
        this();
        this.initCause(cause);
    }

}
