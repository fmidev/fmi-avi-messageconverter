package fi.fmi.avi.converter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Result of a comple conversion operation.
 * Contains the conversion status, possible issues and the resulting message (if available).
 *
 * @author Ilkka Rinne / Spatineo Oy 2017
 */
public class ConversionResult<T> {

    public enum Status { SUCCESS, FAIL, WITH_ERRORS }

    private T convertedMessage;
    private IssueList issues;
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
     * @param source the result to copy
     */
    public ConversionResult(final ConversionResult<? extends T> source) {
        source.getConvertedMessage().ifPresent(this::setConvertedMessage);
        this.issues = new IssueList(source.getConversionIssues());
        this.explicitStatus = source.getStatus();
    }

    /**
     * Conversion success status. In the case that the explicit status is not given, returns
     * {@link Status#FAIL} if the result is null, {@link Status#SUCCESS} if there
     * are no issues reported, and {@link Status#WITH_ERRORS} otherwise.
     *
     * @return the status of the finished conversion operation
     * @see #setStatus(Status)
     */
    public Status getStatus() {
        if (this.explicitStatus != null) {
            return this.explicitStatus;
        } else {
            if (convertedMessage == null) {
                return Status.FAIL;
            } else if (this.issues.size() == 0) {
                return Status.SUCCESS;
            } else {
                return Status.WITH_ERRORS;
            }
        }
    }

    /**
     * Explicitly sets the conversion status.
     *
     * @param status to set
     */
    public void setStatus(final Status status) {
        this.explicitStatus = status;
    }

    /**
     * Get the resulting message, if available.
     * @return the result
     */
    public Optional<T> getConvertedMessage() {
        return Optional.ofNullable(this.convertedMessage);
    }

    /**
     * Get a list of issues reported during the conversion.
     * @return the issue list.
     */
    public List<ConversionIssue> getConversionIssues() {
        return this.issues;
    }

    /**
     * Sets the result of the conversion.
     * @param message the conversion output
     */
    public void setConvertedMessage(T message) {
        this.convertedMessage = message;
    }

    /**
     * Adds a single issue.
     * @param issue to add
     */
    public void addIssue(ConversionIssue issue) {
        if (issue != null) {
            this.issues.add(issue);
        }
    }

    /**
     * Adds a collection of issues.
     * @param issues issue collecion
     */
    public void addIssue(Collection<ConversionIssue> issues) {
        if (issues != null && !issues.isEmpty()) {
            this.issues.addAll(issues);
        }
    }

}
