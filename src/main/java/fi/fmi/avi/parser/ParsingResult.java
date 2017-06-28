package fi.fmi.avi.parser;

import java.util.Collection;
import java.util.List;

import fi.fmi.avi.data.AviationWeatherMessage;

/**
 * Container for parsing result returned by {@link AviMessageParser#parseMessage(Object, ConversionSpecification)}.
 *
 * @param <T> the type of the parsed message.
 *
 * @author Ilkka Rinne / Spatineo 2017
 */
public interface ParsingResult<T extends AviationWeatherMessage> {

    /**
     * General status of the parsing operation.
     */
    enum ParsingStatus {SUCCESS, WITH_ERRORS, FAIL}

    /**
     * Access to the parsing status.
     *
     * @return the parsing status
     */
    ParsingStatus getStatus();

    /**
     * Access to the parsed message. Can be null after the parsing operation, if a reasonably complete
     * POJO could not be created. Users should always check the {@link ParsingStatus} after the operation
     * to make sure there were no problems:
     *
     * <pre>
     *     ParsingResult&lt;TAF&gt; result = parser.parseMessage(tacEncoded, TAF.class);
     *     if (ParsingResult.ParsingStatus.SUCCESS == result.getStatus()) {
     *         //OK to continue
     *         TAF pojo = result.getParsedMessage();
     *         ...
     *     }
     * </pre>
     *
     * @return the parsed message POJO, or null if not available
     */
    T getParsedMessage();

    /**
     * List of parsing issues detected during the parsing operation.
     *
     * @return parsing issues
     */
    List<ParsingIssue> getParsingIssues();

    /**
     * Sets the wrapped message POJO.
     *
     * @param message
     *         the parsed message POJO
     */
    void setParsedMessage(T message);

    /**
     * Adds one {@link ParsingIssue} to the result.
     *
     * @param issue to add
     */
    void addIssue(ParsingIssue issue);

    /**
     * Adds a collection of {@link ParsingIssue}s to the result.
     *
     * @param issues to add
     */
    void addIssue(Collection<ParsingIssue> issues);

}
