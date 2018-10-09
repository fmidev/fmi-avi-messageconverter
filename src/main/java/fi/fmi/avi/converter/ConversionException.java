package fi.fmi.avi.converter;

/**
 * An exception occurring during message conversions.
 */
public class ConversionException extends Exception {

    private static final long serialVersionUID = -7814522773938439916L;

    public ConversionException(String message) {
        super(message);
    }

    public ConversionException(Throwable cause) {
        super(cause);
    }

    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConversionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
