package fi.fmi.avi.parser;

/**
 * A parsing issue to be reported within the {@link ParsingResult}.
 *
 * This way of error/warning reporting is preferred over "fail-fast" parsing
 * exceptions to enable returning partially populated message POJOs from the
 * {@link AviMessageParser#parseMessage(Object, ConversionSpecification)}.
 *
 * @author Ilkka Rinne / Spatineo 2017
 */
public class ParsingIssue {

    /**
     * Issue type
     */
    public enum Type {
        SYNTAX_ERROR, LOGICAL_ERROR, MISSING_DATA, OTHER
    }

    private final Type type;
    private final String message;

    /**
     * Default constructor.
     *
     * @param type
     *         issue kind
     * @param message
     *         message to report
     */
    public ParsingIssue(final Type type, final String message) {
        this.type = type;
        this.message = message;
    }

    /**
     * Access to the issue type.
     *
     * @return issue type
     */
    public Type getType() {
        return this.type;
    }

    /**
     *
     * Access to the issue message.
     *
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Overridden to return a readable data.
     *
     * @return type and message concatenated
     */
    public String toString() {
        return this.type + ":" + this.message;
    }

}
