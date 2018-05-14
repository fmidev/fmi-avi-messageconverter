package fi.fmi.avi.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Ilkka Rinne / Spatineo Oy 2017
 */
public class ConversionResult<T> {

    public enum Status { SUCCESS, FAIL, WITH_ERRORS }

    private T convertedMessage;
    private List<ConversionIssue> issues;
    private Status explicitStatus;

    public ConversionResult() {
        issues = new ArrayList<>();
    }

    public ConversionResult(final ConversionResult<? extends T> source) {
        source.getConvertedMessage().ifPresent(this::setConvertedMessage);
        this.issues = new ArrayList<>(source.getConversionIssues());
        this.explicitStatus = source.getStatus();
    }

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
    
    public void setStatus(final Status status) {
      this.explicitStatus = status;
    }

    public Optional<T> getConvertedMessage() {
        return Optional.ofNullable(this.convertedMessage);
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
