package io.rubrica.model;

/**
 * Representa un error en la aplicación.
 */
public class RubricaException extends RuntimeException 	{

	private static final long serialVersionUID = 1342960380916921427L;

	public RubricaException() {
        super();
    }

    public RubricaException(String message) {
        super(message);
    }

    public RubricaException(Throwable cause) {
        super(cause);
    }

    public RubricaException(String message, Throwable cause) {
        super(message, cause);
    }
}
