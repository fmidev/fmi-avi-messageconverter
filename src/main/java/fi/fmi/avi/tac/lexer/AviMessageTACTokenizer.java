package fi.fmi.avi.tac.lexer;

import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.data.AviationWeatherMessage;

/**
 * AviMessageTACTokenizer creates a {@link LexemeSequence} of TAC encoded tokens from the
 * given aviation weather message POJO.
 * 
 * @author Ilkka Rinne / Spatineo Oy 2017
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
