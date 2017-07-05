package fi.fmi.avi.converter;

/**
 * A converter capable only processing a specific type of input message to a particular
 * output.
 *
 *
 * @param <S>
 *           input message type
 * @param <T>
 *          parsed output message type
 *
 * @author Ilkka Rinne / Spatineo Oy 2017
 */
public interface AviMessageSpecificConverter<S, T> {

    /**
     * Parses a single message.
     *
     * @param input
     *         input message
     * @param hints
     *         parsing hints
     *
     * @return the {@link ConversionResult} with the POJO and the possible parsing issues
     */
    ConversionResult<T> convertMessage(S input, ConversionHints hints);
}
