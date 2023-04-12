package fi.fmi.avi.util;

public class GTSDataParseException extends RuntimeException {
    private static final long serialVersionUID = 4246342002125774900L;

    private final ErrorCode errorCode;
    private final int index;
    private final String failedMessage;

    public GTSDataParseException(final ErrorCode errorCode, final int index, final String failedMessage) {
        super(errorCode.message(index));
        this.errorCode = errorCode;
        this.index = index;
        this.failedMessage = failedMessage;
    }

    public GTSDataParseException(final ErrorCode errorCode, final int index, final String failedMessage, final Throwable cause) {
        this(errorCode, index, failedMessage);
        initCause(cause);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public final int getIndex() {
        return index;
    }

    public final String getFailedMessage() {
        return failedMessage;
    }

    public interface ErrorCode {
        String name();

        String message(final int index);
    }
}
