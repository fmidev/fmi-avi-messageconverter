package fi.fmi.avi.parser;

import fi.fmi.avi.data.AviationWeatherMessage;

/**
 * AviMessageParser parses a {@link LexemeSequence} into a Java POJO representing the aviation weather message
 * of the requested type. Methods of this class are typically used together with {@link AviMessageLexer} to parse
 * TAC encoded aviation weather messages:
 * <pre>
 *  ParsingResult<TAF> result = parser.parseMessage(lexer.lexMessage("TAF EFAB 190815Z 1909/1915 14008G15MPS 9999 BKN010 BKN015="));
 *  if (ParsingResult.ParsingStatus.SUCCESS = result.getStatus()) {
 *      TAF pojo = result.getParsedMessage();
 *  }
 * </pre>
 */
public interface AviMessageParser {

    /**
     *
     * @param lexed
     * @param type
     * @param <T>
     * @return
     */
    <T extends AviationWeatherMessage> ParsingResult<T> parseMessage(LexemeSequence lexed, Class<T> type);

    <T extends AviationWeatherMessage> ParsingResult<T> parseMessage(LexemeSequence lexed, Class<T> type, ParsingHints hints);
}
