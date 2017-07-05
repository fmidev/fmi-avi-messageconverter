package fi.fmi.avi.converter;

import java.util.Set;

/**
 * AviMessageConverter converts an aviation weather message from one type to another.
 * Not that not all conversions are lossless.
 *
 * Example:
 * <pre>
 *  ConversionResult&lt;TAF&gt; result = converter.convertMessage("TAF EFAB 190815Z 1909/1915 14008G15MPS 9999 BKN010 BKN015=",ConversionSpecification.TAC_TO_TAF_POJO);
 *  if (ConversionResult.Status.SUCCESS = result.getStatus()) {
 *      TAF pojo = result.getConvertedMessage();
 *  }
 * </pre>
 *
 *  @author Ilkka Rinne / Spatineo Oy 2017
 */
public interface AviMessageConverter {

    /**
     * Converts the given message according to the <code>spec</code>.
     *
     * The returned {@link ConversionResult} includes the status, the converted message and the possible
     * {@link ConversionIssue}s.
     *
     * @see ConversionResult
     *
     * @param input the input message
     * @param spec {@link ConversionSpecification} to use
     * @param <S> the type of the input message
     * @param <T> the type of the output message
     * 
     * @return the result of the conversion
     * 
     */
    <S, T> ConversionResult<T> convertMessage(S input, ConversionSpecification<S, T> spec);

    /**
     * Converts the given message according to the <code>spec</code> using
     * the provided conversion hints.
     *
     * The returned {@link ConversionResult} includes the status, the converted message and the possible
     * {@link ConversionIssue}s.
     *
     * @param input the input message
     * @param spec {@link ConversionSpecification} to use
     * @param <S> the type of the input message
     * @param <T> the type of the output message
     * @param hints to guide the conversion process
     *
     * @return the result of the conversion
     */
    <S, T> ConversionResult<T> convertMessage(S input, ConversionSpecification<S, T> spec, ConversionHints hints);
    
    /**
     * 
     * @param spec
     * @param converter
     */
    public <S, T> void setMessageSpecificConverter(ConversionSpecification<S, T> spec, AviMessageSpecificConverter<S, T> converter);
    	
 
    /**
     * Returns all the {@link ConversionSpecification}s supported by this 
     * AviMessageConverter implementation.
     * 
     * @return set of supported specifications
     */
    Set<ConversionSpecification<?,?>> getSupportedSpecifications();
}
