package fi.fmi.avi.tac.lexer;

/**
 * Exception created during the message serialization.
 *
 * @author Ilkka Rinne / Spatineo Oy 2017
 */
public class SerializingException extends Exception {

	private static final long serialVersionUID = -8528248357845259224L;

	/**
	 * The default constructor with empty message and cause.
	 */
	public SerializingException() {
	}

	/**
	 * Exception with a message only.
	 *
	 * @param message the message
	 */
	public SerializingException(final String message) {
		super(message);
	}

	/**
	 * Exception with the cause only.
	 *
	 * @param cause reason behind the scenes
	 */
	public SerializingException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Exception with both a message and the cause.
	 *
	 * @param message the message
	 * @param cause the reason behind the scenes
	 */
	public SerializingException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
