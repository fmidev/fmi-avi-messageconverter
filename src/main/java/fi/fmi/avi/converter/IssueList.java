package fi.fmi.avi.converter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A convenience class for handling a list of {@link ConversionIssue}s.
 */
public class IssueList extends ArrayList<ConversionIssue> {

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public IssueList() {
    }

    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param c
     *         the collection whose elements are to be placed into this list
     *
     * @throws NullPointerException
     *         if the specified collection is null
     */
    public IssueList(final Collection<? extends ConversionIssue> c) {
        super(c);
    }

    /**
     * Creates and adds one {@link ConversionIssue} with the given severity
     * and of type {@link ConversionIssue.Type#SYNTAX}.
     *
     * @param severity
     *         severity of the issue
     * @param message
     *         the issue description
     */
    public void add(final ConversionIssue.Severity severity, final String message) {
        this.add(new ConversionIssue(severity, ConversionIssue.Type.SYNTAX, message));
    }

    /**
     * Creates and adds one {@link ConversionIssue} with the given severity and type.
     *
     * @param severity severity of the issue
     * @param type type of the issue
     * @param message the issue description
     */
    public void add(final ConversionIssue.Severity severity, final ConversionIssue.Type type, final String message) {
        this.add(new ConversionIssue(severity, type, message));
    }

    /**
     * Creates and adds one {@link ConversionIssue} with the given severity, type and cause.
     *
     * @param severity severity of the issue
     * @param type type of the issue
     * @param message the issue description
     * @param cause originating cause to report
     */
    public void add(final ConversionIssue.Severity severity, final ConversionIssue.Type type, final String message, final Throwable cause) {
        this.add(new ConversionIssue(severity, type, message, cause));
    }

}
