package fi.fmi.avi.parser;

public class TokenizingException extends Exception {

	private static final long serialVersionUID = -8528248357845259224L;

	public TokenizingException() {
	}

	public TokenizingException(final String message) {
		super(message);
	}

	public TokenizingException(final Throwable cause) {
		super(cause);
	}

	public TokenizingException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
