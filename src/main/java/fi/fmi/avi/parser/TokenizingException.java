package fi.fmi.avi.parser;

public class TokenizingException extends Exception {

	public TokenizingException() {
	}

	public TokenizingException(String message) {
		super(message);
	}

	public TokenizingException(Throwable cause) {
		super(cause);
	}

	public TokenizingException(String message, Throwable cause) {
		super(message, cause);
	}

}
