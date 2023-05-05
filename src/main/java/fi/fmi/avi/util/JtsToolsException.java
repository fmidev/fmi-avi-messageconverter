package fi.fmi.avi.util;

public class JtsToolsException extends Exception{
    public JtsToolsException(final String msg, final Throwable err) {
        super(msg, err);
    }
    public JtsToolsException(final String msg) {
        super(msg);
    }
}
