package fi.fmi.avi.converter;

/**
 * A converter capable only processing a specific type of input message to a particular
 * output.
 *
 *
 * @param <S>
 *           input message type
 * @param <T>
 *           output message type
 *
 * @author Ilkka Rinne / Spatineo Oy 2017
 */
public interface AviMessageSpecificConverter<S, T> {

    /**
     * Converts a single message.
     *
     * @param input
     *         input message
     * @param hints
     *         parsing hints
     *
     * @return the {@link ConversionResult} with the converter message and the possible conversion issues
     */
    ConversionResult<T> convertMessage(S input, ConversionHints hints);
}
