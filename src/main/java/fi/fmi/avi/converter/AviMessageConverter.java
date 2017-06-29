package fi.fmi.avi.converter;

import java.util.Set;

/**
 * AviMessageConverter converts an aviation weather message from one type to another.
 *
 * Example:
 * <pre>
 *  ConversionResult&lt;TAF&gt; result = converter.convertMessage("TAF EFAB 190815Z 1909/1915 14008G15MPS 9999 BKN010 BKN015=",ConversionSpecification.TAC_TO_TAF_POJO);
 *  if (ConversionResult.Status.SUCCESS = result.getStatus()) {
 *      TAF pojo = result.getParsedMessage();
 *  }
 * </pre>
 *
 *  @author Ilkka Rinne / Spatineo Oy 2017
 */
public interface AviMessageConverter {

    /**
     * Parses the given message into a Java POJO of the type <code>type</code>.
     *
     * The returned {@link ConversionResult} includes the status, the parsed POJO and the possible
     * {@link ConversionIssue}s.
     *
     * @see ConversionResult
     *
     * @param input the input message
     * @param spec {@link ConversionSpecification} to use
     * @param <S> the type of the input message
     * @param <T> the type of the POJO to return
     * @return the result of the conversion
     * 
     */
    <S, T> ConversionResult<T> convertMessage(S input, ConversionSpecification<S, T> spec);

    /**
     * Parses the given message into a Java POJO of the type <code>type</code> using
     * the provided parsing hints.
     *
     * The returned {@link ConversionResult} includes the status, the parsed POJO and the possible
     * {@link ConversionIssue}s.
     *
     * @param input the input message
     * @param spec {@link ConversionSpecification} to use
     * @param <S> the type of the input message
     * @param <T> the type of the POJO to return
     * @param hints to guide the conversion process
     *
     * @return the result of the conversion
     */
    <S, T> ConversionResult<T> convertMessage(S input, ConversionSpecification<S, T> spec, ConversionHints hints);
    
    /**
     * 
     * @return
     */
    Set<ConversionSpecification<?,?>> getSupportedSpecifications();
}
