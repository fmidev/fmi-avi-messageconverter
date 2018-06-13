package fi.fmi.avi.converter;

import java.util.ArrayList;

/**
 * Created by rinne on 12/06/2018.
 */
public class IssueList extends ArrayList<ConversionIssue> {

    public void add(final ConversionIssue.Severity severity, final String message) {
        this.add(new ConversionIssue(severity, message));
    }

    public void add(final ConversionIssue.Severity severity, final ConversionIssue.Type type, final String message) {
        this.add(new ConversionIssue(severity, type, message));
    }

    public void add(final ConversionIssue.Severity severity, final ConversionIssue.Type type, final String message, final Throwable cause) {
        this.add(new ConversionIssue(severity, type, message, cause));
    }

}
