package fi.fmi.avi.converter;

/**
 * An exception occurring during message conversions.
 */
public class ConversionException extends Exception {

    private static final long serialVersionUID = -7814522773938439916L;

    public ConversionException(final String message) {
        super(message);
    }

    public ConversionException(final Throwable cause) {
        super(cause);
    }

    public ConversionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ConversionException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
