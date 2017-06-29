package fi.fmi.avi.converter;

import java.util.Collection;
import java.util.List;

/**
 * Container for conversion result returned by {@link AviMessageConverter#convertMessage(Object, ConversionSpecification)}.
 *
 * @param <T> the type of the contained result message.
 *
 * @author Ilkka Rinne / Spatineo 2017
 */
public interface ConversionResult<T> {

    /**
     * General status of the conversion operation.
     */
    enum Status {SUCCESS, WITH_ERRORS, FAIL}

    /**
     * Access to the conversion status.
     *
     * @return the conversion status
     */
    Status getStatus();

    /**
     * Access to the converted message. Can be null after the parsing operation, if a reasonably complete
     * result object could not be created. Users should always check the {@link Status} after the operation
     * to make sure there were no problems:
     *
     * <pre>
     *     ConversionResult&lt;TAF&gt; result = converter.convertMessage("TAF EFAB 190815Z 1909/1915 14008G15MPS 9999 BKN010 BKN015=", ConversionSpecification.TAC_TO_TAF_POJO);
     *     if (ConversionResult.Status.SUCCESS == result.getStatus()) {
     *         //OK to continue
     *         TAF pojo = result.getConvertedMessage();
     *         ...
     *     }
     * </pre>
     *
     * @return the converted message object, or null if not available
     */
    T getConvertedMessage();

    /**
     * List of issues detected during the conversion operation.
     *
     * @return parsing issues
     */
    List<ConversionIssue> getConversionIssues();

    /**
     * Sets the wrapped message POJO.
     *
     * @param message
     *         the parsed message POJO
     */
    void setConvertedMessage(T message);

    /**
     * Adds one {@link ConversionIssue} to the result.
     *
     * @param issue to add
     */
    void addIssue(ConversionIssue issue);

    /**
     * Adds a collection of {@link ConversionIssue}s to the result.
     *
     * @param issues to add
     */
    void addIssue(Collection<ConversionIssue> issues);

}
