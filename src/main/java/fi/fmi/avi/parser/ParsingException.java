package fi.fmi.avi.parser;

/**
 * Created by rinne on 20/12/16.
 */
public class ParsingException extends Exception {

    public enum Type {
        SYNTAX_ERROR, LOGICAL_ERROR, MISSING_DATA, OTHER
    }

    private Type type;

    public ParsingException(final Type type) {
        this.type = type;
    }

    public ParsingException(final Type type, final Throwable cause) {
        super(cause);
        this.type = type;
    }

    public ParsingException(final Type type, final String message, final Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    public ParsingException(final Type type, final String message) {
        super(message);
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

}
