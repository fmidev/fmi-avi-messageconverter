package fi.fmi.avi.parser;

/**
 * Created by rinne on 20/12/16.
 */
public class ParsingIssue {

    public enum Type {
        SYNTAX_ERROR, LOGICAL_ERROR, MISSING_DATA, OTHER
    }

    private Type type;
    private String message;

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
