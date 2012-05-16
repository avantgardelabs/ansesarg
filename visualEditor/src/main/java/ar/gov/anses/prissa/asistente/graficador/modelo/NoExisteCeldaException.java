package ar.gov.anses.prissa.asistente.graficador.modelo;


import java.util.NoSuchElementException;

public class NoExisteCeldaException extends NoSuchElementException {

    private static final long serialVersionUID = 3978903259212885498L;

    public NoExisteCeldaException() {
        super();
    }

    public NoExisteCeldaException(String s) {
        super(s);
    }

    public NoExisteCeldaException(String s, Throwable cause) {
        this(s);
        this.initCause(cause);
    }

    public NoExisteCeldaException(Throwable cause) {
        this();
        this.initCause(cause);
    }
}
