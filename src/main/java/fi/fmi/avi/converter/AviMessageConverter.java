package fi.fmi.avi.converter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * AviMessageConverter converts an aviation weather message from one type to another.
 * Not that not all conversions are lossless.
 *
 * Example:
 * <pre>
 *  ConversionResult&lt;TAF&gt; result = converter.convertMessage("TAF EFAB 190815Z 1909/1915 14008G15MPS 9999 BKN010 BKN015=",TACConverter.TAC_TO_TAF_POJO);
 *  if (ConversionResult.Status.SUCCESS = result.getStatus()) {
 *      TAF pojo = result.getConvertedMessage();
 *  }
 * </pre>
 *
 * @author Ilkka Rinne / Spatineo Oy 2017
 */
public class AviMessageConverter {

    private final Map<ConversionSpecification<?, ?>, AviMessageSpecificConverter<?, ?>> converters = new HashMap<>();

    /**
     * Converts the given message according to the <code>spec</code>.
     *
     * The returned {@link ConversionResult} includes the status, the converted message and the possible
     * {@link ConversionIssue}s.
     *
     * @param input
     *         the input message
     * @param spec
     *         {@link ConversionSpecification} to use
     * @param <S>
     *         the type of the input message
     * @param <T>
     *         the type of the output message
     *
     * @return the result of the conversion
     *
     * @see ConversionResult
     */
    public <S, T> ConversionResult<T> convertMessage(final S input, final ConversionSpecification<S, T> spec) {
        return convertMessage(input, spec, null);
    }

    /**
     * Converts the given message according to the <code>spec</code> using
     * the provided conversion hints.
     *
     * The returned {@link ConversionResult} includes the status, the converted message and the possible
     * {@link ConversionIssue}s.
     *
     * @param input
     *         the input message
     * @param spec
     *         {@link ConversionSpecification} to use
     * @param <S>
     *         the type of the input message
     * @param <T>
     *         the type of the output message
     * @param hints
     *         to guide the conversion process
     *
     * @return the result of the conversion
     */
    @SuppressWarnings("unchecked")
    public <S, T> ConversionResult<T> convertMessage(final S input, final ConversionSpecification<S, T> spec, final ConversionHints hints) {
        for (final ConversionSpecification<?, ?> toMatch : converters.keySet()) {
            if (toMatch.equals(spec)) {
                return ((AviMessageSpecificConverter<S, T>) converters.get(spec)).convertMessage(input, hints);
            }
        }
        throw new IllegalArgumentException("No converter for conversion specification " + spec + ", check configuration");
    }

    /**
     * Sets the message specific converter.
     *
     * @param spec
     *         the provided specification
     * @param converter
     *         the converter implementation
     * @param <S>
     *         source object class
     * @param <T>
     *         target object class
     */
    public <S, T> void setMessageSpecificConverter(final ConversionSpecification<S, T> spec, final AviMessageSpecificConverter<S, T> converter) {
        this.converters.put(spec, converter);
    }

    /**
     * Return true is the particular conversion is supported by this converter.
     *
     * @param spec
     *         to query
     *
     * @return true if supported
     */
    public boolean isSpecificationSupported(final ConversionSpecification<?, ?> spec) {
        return this.converters.containsKey(spec);
    }

    public <U, Z> AviMessageSpecificConverter<U, Z> getConverter(final ConversionSpecification<U, Z> spec) {
        return (AviMessageSpecificConverter<U, Z>) this.converters.get(spec);
    }

    /**
     * Returns all the {@link ConversionSpecification}s supported by this
     * AviMessageConverter implementation.
     *
     * @return set of supported specifications
     */
    public Set<ConversionSpecification<?, ?>> getSupportedSpecifications() {
        return this.converters.keySet();
    }

}
