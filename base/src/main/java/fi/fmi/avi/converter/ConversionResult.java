package fi.fmi.avi.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Ilkka Rinne / Spatineo Oy 2017
 */
public class ConversionResult<T> {

    public enum Status { SUCCESS, FAIL, WITH_ERRORS }

    private T convertedMessage;
    private List<ConversionIssue> issues;

    public ConversionResult() {
        issues = new ArrayList<>();
    }

    public Status getStatus() {
        if (convertedMessage == null) {
            return Status.FAIL;
        } else if (this.issues.size() == 0) {
            return Status.SUCCESS;
        } else {
            return Status.WITH_ERRORS;
        }
    }

    public T getConvertedMessage() {
        return this.convertedMessage;
    }

    public List<ConversionIssue> getConversionIssues() {
        return this.issues;
    }


    public void setConvertedMessage(T message) {
        this.convertedMessage = message;
    }

    public void addIssue(ConversionIssue issue) {
        if (issue != null) {
            this.issues.add(issue);
        }
    }

    public void addIssue(Collection<ConversionIssue> issues) {
        if (issues != null && !issues.isEmpty()) {
            this.issues.addAll(issues);
        }
    }

}
