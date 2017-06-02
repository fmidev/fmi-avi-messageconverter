package fi.fmi.avi.parser;

/**
 * A parsing issue to be reported within the {@link ParsingResult}.
 *
 * This way of error/warning reporting is preferred over "fail-fast" parsing
 * exceptions to enable returning partially populated message POJOs from the
 * {@link AviMessageParser#parseMessage(LexemeSequence, Class)}.
 *
 */
public class ParsingIssue {

    public enum Type {
        SYNTAX_ERROR, LOGICAL_ERROR, MISSING_DATA, OTHER
    }

    private final Type type;
    private final String message;

    public ParsingIssue(final Type type, final String message) {
        this.type = type;
        this.message = message;
    }

    public Type getType() {
        return this.type;
    }

    public String getMessage() {
        return this.message;
    }

    public String toString() {
        return this.type + ":" + this.message;
    }

}
