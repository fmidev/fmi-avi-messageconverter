package fi.fmi.avi.converter;

/**
 * An issue to be reported within the {@link ConversionResult}.
 *
 * This way of error/warning reporting is preferred over "fail-fast" conversions
 * exceptions to enable returning partially populated message objects from the
 * {@link AviMessageConverter#convertMessage(Object, ConversionSpecification)}.
 *
 * @author Ilkka Rinne / Spatineo 2017
 */
public class ConversionIssue {

    /**
     * Severity for issues.
     * <ul>
     * <li>{@link Severity#ERROR} should be used for issues which are likely to cause the converted message to either be unavailable or
     * seriously incomplete (not usable for issue without corrections.</li>
     * <li>{@link Severity#WARNING} should be used for issues which may affect the correctness or information content of the converted message.</li>
     * </ul>
     */
    public enum Severity {
        ERROR, WARNING, INFO
    }

    /**
     * Issue type
     */
    public enum Type {
        SYNTAX, LOGICAL, MISSING_DATA, OTHER
    }

    private final Type type;
    private final String message;
    private final Severity severity;
    private final Throwable ex;

    /**
     * Creates an issue with severity and a message of {@link Type#SYNTAX}.
     *
     * @param severity severity of the issue
     * @param message error message
     */
    public ConversionIssue(final Severity severity, final String message) {
        this(severity, Type.SYNTAX, message, null);
    }

    /**
     * Creates an issue with severity, type and a message.
     *
     * @param severity
     *         severity of the issue
     * @param type
     *         issue kind
     * @param message
     *         error message
     */
    public ConversionIssue(final Severity severity, final Type type, final String message) {
        this(severity, type, message, null);
    }

    /**
     * Creates an issue with type and a message of {@link Severity#ERROR}.
     *
     * @param type issue kind
     * @param message error message
     */
    public ConversionIssue(final Type type, final String message) {
        this(Severity.ERROR, type, message, null);
    }

    /**
     * Creates an issue with severity, type, message and a cause.
     *
     * @param type
     *         issue kind
     * @param severity
     *         severity of the issue
     * @param message
     *         message to report
     * @param cause
     *         the reason for the issue
     */
    public ConversionIssue(final Severity severity, final Type type, final String message, final Throwable cause) {
        this.type = type;
        this.severity = severity;
        this.message = message;
        this.ex = cause;
    }

    /**
     * Creates an issue with type, message and a cause of {@link Severity#ERROR}.
     *
     * @param type
     *         issue kind
     * @param message
     *         message to report
     * @param cause the reason for the issue
     */
    public ConversionIssue(final Type type, final String message, final Throwable cause) {
        this.type = type;
        this.severity = Severity.ERROR;
        this.message = message;
        this.ex = cause;
    }

    /**
     * Access the issue severity.
     *
     * @return the severity
     */
    public Severity getSeverity() {
        return this.severity;
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
     * Possible root cause of the issue.
     *
     * @return the cause or null
     */
    public Throwable getCause() {
        return ex;
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
