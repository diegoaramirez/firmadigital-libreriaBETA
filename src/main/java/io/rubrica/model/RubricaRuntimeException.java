package io.rubrica.model;

/**
 * Representa un error en la aplicación.
 */
public class RubricaRuntimeException extends RuntimeException 	{

	private static final long serialVersionUID = 1342960380916921427L;

	public RubricaRuntimeException() {
        super();
    }

    public RubricaRuntimeException(String message) {
        super(message);
    }

    public RubricaRuntimeException(Throwable cause) {
        super(cause);
    }

    public RubricaRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
