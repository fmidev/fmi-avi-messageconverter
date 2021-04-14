package fi.fmi.avi.converter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Result of a comple conversion operation.
 * Contains the conversion status, possible issues and the resulting message (if available).
 *
 * @author Ilkka Rinne / Spatineo Oy 2017
 */
public class ConversionResult<T> {

    private final IssueList issues;
    private T convertedMessage;
    private Status explicitStatus;

    /**
     * Default constructor wiht an empty issue list
     */
    public ConversionResult() {
        issues = new IssueList();
    }

    /**
     * Constructs a result from the given result. Does a shallow copy
     * of the issue list, the status and the result.
     *
     * @param source
     *         the result to copy
     */
    public ConversionResult(final ConversionResult<? extends T> source) {
        this.convertedMessage = source.getConvertedMessage().orElse(null);
        this.issues = new IssueList(source.getConversionIssues());
        this.explicitStatus = source.getStatus();
    }

    /**
     * Conversion success status. In the case that the explicit status is not given, returns
     * {@link Status#FAIL} if the result is null, {@link Status#WITH_WARNINGS} if there
     * are issues with {@link ConversionIssue.Severity#WARNING} but none with
     * {@link ConversionIssue.Severity#ERROR} reported,
     * {@link Status#WITH_ERRORS} if there
     * are issues with {@link ConversionIssue.Severity#ERROR} reported,
     * and {@link Status#SUCCESS} otherwise.
     *
     * @return the status of the finished conversion operation
     *
     * @see #setStatus(Status)
     */
    public Status getStatus() {
        if (this.explicitStatus != null) {
            return this.explicitStatus;
        } else {
            if (convertedMessage == null) {
                return Status.FAIL;
            } else {
                boolean warningsFound = false;
                for (final ConversionIssue issue : this.issues) {
                    if (ConversionIssue.Severity.ERROR == issue.getSeverity()) {
                        return Status.WITH_ERRORS;
                    } else if (ConversionIssue.Severity.WARNING == issue.getSeverity()) {
                        warningsFound = true;
                    }
                }
                if (warningsFound) {
                    return Status.WITH_WARNINGS;
                } else {
                    return Status.SUCCESS;
                }
            }
        }
    }

    /**
     * Explicitly sets the conversion status.
     *
     * @param status
     *         to set
     */
    public void setStatus(final Status status) {
        this.explicitStatus = status;
    }

    /**
     * Get the resulting message, if available.
     *
     * @return the result
     */
    public Optional<T> getConvertedMessage() {
        return Optional.ofNullable(this.convertedMessage);
    }

    /**
     * Sets the result of the conversion.
     *
     * @param message
     *         the conversion output
     */
    public void setConvertedMessage(final T message) {
        this.convertedMessage = message;
    }

    /**
     * Get a list of issues reported during the conversion.
     *
     * @return the issue list.
     */
    public List<ConversionIssue> getConversionIssues() {
        return Collections.unmodifiableList(this.issues);
    }

    /**
     * Adds a single issue.
     *
     * @param issue
     *         to add
     */
    public void addIssue(final ConversionIssue issue) {
        if (issue != null) {
            this.issues.add(issue);
        }
    }

    /**
     * Adds a collection of issues.
     *
     * @param issues
     *         issue collecion
     */
    public void addIssue(final Collection<ConversionIssue> issues) {
        if (issues != null && !issues.isEmpty()) {
            this.issues.addAll(issues);
        }
    }

    public enum Status {
        SUCCESS(0), FAIL(3), WITH_ERRORS(2), WITH_WARNINGS(1);
        private final int criticality;

        Status(final int criticality) {
            this.criticality = criticality;
        }

        public static boolean isMoreCritical(final Status status, final Status reference) {
            return status.criticality > reference.criticality;
        }
    }

}
