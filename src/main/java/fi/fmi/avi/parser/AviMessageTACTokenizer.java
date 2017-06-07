package fi.fmi.avi.parser;

import fi.fmi.avi.data.AviationWeatherMessage;

/**
 * AviMessageTACTokenizer creates a {@link LexemeSequence} of TAC encoded tokens from the
 * given aviation weather message POJO. This is typically used for serializing the POJO
 * into a TAC encoded String:
 *
 * <pre>
 *     AviMessageParser parser;
 *     AviMessageLexer lexer;
 *     AviMessageTACTokenizer tokenizer;
 *     ...
 *
 *     String original = "TAF EFAB 190815Z 1909/1915 14008G15MPS 9999 BKN010 BKN015=";
 *     ParsingResult<TAF> result = parser.parseMessage(lexer.lexMessage(original));
 *      if (ParsingResult.ParsingStatus.SUCCESS = result.getStatus()) {
 *          TAF pojo = result.getParsedMessage();
 *          LexemeSequence seq = tokenizer.tokenizeMessage(pojo);
 *          if (original.equals(seq.getTAC()) {
 *              //loopback succeeded
 *          }
 *      }
 *
 * </pre>
 *
 */
public interface AviMessageTACTokenizer {

    /**
     * Returns a {@link LexemeSequence} containing the TAC {@link Lexeme}s for the given {@link AviationWeatherMessage}.
     * Uses the default tokenizing rules.
     *
     * @param msg
     *         the input message
     *
     * @return sequence of TAC tokens
     *
     * @throws SerializingException
     *         if tokenizing cannot be carried out due to missing or incorrect input message data content.
     */
    LexemeSequence tokenizeMessage(AviationWeatherMessage msg) throws SerializingException;

    /**
     * Returns a {@link LexemeSequence} containing the TAC {@link Lexeme}s for the given {@link AviationWeatherMessage}.
     * Uses the provided tokenizing hints.
     *
     * @param msg the input message
     * @param hints the hints for fine-tuning the tokenizer functionality
     * @return sequence of TAC tokens
     * @throws SerializingException if tokenizing cannot be carried out due to missing or incorrect input message data content.
     */
    LexemeSequence tokenizeMessage(AviationWeatherMessage msg, ConversionHints hints) throws SerializingException;
}
