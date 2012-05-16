package ar.gov.anses.prissa.asistente.graficador.modeloresumido;


import java.util.NoSuchElementException;

public class NoExisteCeldaExceptionMR extends NoSuchElementException {

    private static final long serialVersionUID = 3978903259212885498L;

    public NoExisteCeldaExceptionMR() {
        super();
    }

    public NoExisteCeldaExceptionMR(String s) {
        super(s);
    }

    public NoExisteCeldaExceptionMR(String s, Throwable cause) {
        this(s);
        this.initCause(cause);
    }

    public NoExisteCeldaExceptionMR(Throwable cause) {
        this();
        this.initCause(cause);
    }
}
