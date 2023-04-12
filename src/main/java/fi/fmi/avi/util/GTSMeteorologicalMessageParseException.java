package fi.fmi.avi.util;

import java.util.Locale;

public class GTSMeteorologicalMessageParseException extends GTSDataParseException {
    private static final long serialVersionUID = 5204811503440506474L;

    public GTSMeteorologicalMessageParseException(final ErrorCode errorCode, final int index, final String failedMessage) {
        super(errorCode, index, failedMessage);
    }

    public GTSMeteorologicalMessageParseException(final ErrorCode errorCode, final int index, final String failedMessage, final Throwable cause) {
        super(errorCode, index, failedMessage, cause);
    }

    @Override
    public final ErrorCode getErrorCode() {
        return (ErrorCode) super.getErrorCode();
    }

    public enum ErrorCode implements GTSDataParseException.ErrorCode {
        MISSING_STARTING_LINE_PREFIX("Missing starting line prefix"), //
        UNEXPECTED_STARTING_LINE_PREFIX("Unexpected starting line prefix"), //
        UNEXPECTED_END_OF_TRANSMISSION_SEQUENCE_NUMBER("Unexpected end of data while parsing transmission sequence number"), //
        UNEXPECTED_LINE_BREAKS_IN_TRANSMISSION_SEQUENCE_NUMBER("Unexpected line breaks in transmission sequence number"), //
        MISSING_HEADING_PREFIX("Missing heading prefix"), //
        UNEXPECTED_HEADING_PREFIX("Unexpected heading prefix"), //
        MISSING_END_OF_MESSAGE_SIGNALS("Missing end of message signals"), //
        NO_SEPARATION_OF_HEADING_AND_TEXT("Heading and text separator not found"), //
        UNEXPECTED_LINE_BREAKS_IN_HEADING("Unexpected line breaks in heading"), //
        UNEXPECTED_END_OF_TEXT("Unexpected end of data while parsing text");

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
