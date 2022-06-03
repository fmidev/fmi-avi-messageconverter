package fi.fmi.avi.util;

import java.util.Locale;

public class GTSDataExchangeParseException extends GTSDataParseException {
    private static final long serialVersionUID = -7018527875651263041L;

    public GTSDataExchangeParseException(final ErrorCode errorCode, final int index, final String failedMessage) {
        super(errorCode, index, failedMessage);
    }

    public GTSDataExchangeParseException(final ErrorCode errorCode, final int index, final String failedMessage, final Throwable cause) {
        super(errorCode, index, failedMessage, cause);
    }

    @Override
    public final ErrorCode getErrorCode() {
        return (ErrorCode) super.getErrorCode();
    }

    public enum ErrorCode implements GTSDataParseException.ErrorCode {
        UNEXPECTED_END_OF_MESSAGE_LENGTH("Unexpected end of data while parsing message length"), //
        INVALID_MESSAGE_LENGTH("Invalid message length"), //
        UNEXPECTED_END_OF_FORMAT_IDENTIFIER("Unexpected end of data while parsing format identifier"), //
        INVALID_FORMAT_IDENTIFIER("Invalid format identifier");

        private final String messageTemplate;

        ErrorCode(final String message) {
            this.messageTemplate = message + " starting at index %d";
        }

        @Override
        public String message(final int index) {
            return String.format(Locale.ROOT, messageTemplate, index);
        }
    }
}
