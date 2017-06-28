package fi.fmi.avi.parser;

import fi.fmi.avi.data.AviationWeatherMessage;

/**
 * AviMessageParser parses a message into a Java POJO representing the aviation weather message
 * of the requested type.
 *
 * Example:
 * <pre>
 *  ParsingResult&lt;TAF&gt; result = parser.parseMessage("TAF EFAB 190815Z 1909/1915 14008G15MPS 9999 BKN010 BKN015=",ParserSpecification.TAC_TO_TAF_POJO);
 *  if (ParsingResult.ParsingStatus.SUCCESS = result.getStatus()) {
 *      TAF pojo = result.getParsedMessage();
 *  }
 * </pre>
 *
 *  @author Ilkka Rinne / Spatineo Oy 2017
 */
public interface AviMessageParser {

    /**
     * Parses the given message into a Java POJO of the type <code>type</code>.
     *
     * The returned {@link ParsingResult} includes the status, the parsed POJO and the possible
     * {@link ParsingIssue}s.
     *
     * @see ParsingResult
     *
     * @param input the input message
     * @param spec {@link ConversionSpecification} to use
     * @param <S> the type of the input message
     * @param <T> the type of the POJO to return
     * @return the result of the parsing
     * 
     */
    <S, T extends AviationWeatherMessage> ParsingResult<T> parseMessage(S input, ConversionSpecification<S, T> spec);

    /**
     * Parses the given message into a Java POJO of the type <code>type</code> using
     * the provided parsing hints.
     *
     * The returned {@link ParsingResult} includes the status, the parsed POJO and the possible
     * {@link ParsingIssue}s.
     *
     * @param input the input message
     * @param spec {@link ConversionSpecification} to use
     * @param <S> the type of the input message
     * @param <T> the type of the POJO to return
     * @param hints the parsing hints to guide the parsing implementation
     *
     * @return the result of the parsing
     */
    <S, T extends AviationWeatherMessage> ParsingResult<T> parseMessage(S input, ConversionSpecification<S, T> spec, ConversionHints hints);
}
