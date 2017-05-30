package fi.fmi.avi.parser;

/**
 *
 * AviMessageLexer creates a {@link LexemeSequence}s out of TAC encoded Strings.
 *
 * Lexing of the TAC message only tries to identify each of the individual tokens
 * (or {@link Lexeme}s) of the input message and store their parsed parameters for
 * later use.
 *
 * Typical uses the AviMessageLexer implementations include the lexing phase
 * of the TAC message parsing before creating the actual Java POJO, and basic
 * input validation for TAC messages.
 *
 */
public interface AviMessageLexer {

    /**
     * Lexes the input String with the default parsing settings.
     *
     * @param input
     *         the TAC encoded message
     *
     * @return sequence of recongized or unrecognized {@link Lexeme}s
     *
     * @see LexemeSequence
     * s
     */
    LexemeSequence lexMessage(final String input);

    /**
     * Lexes the input String with the given parsing settings.
     *
     * @see LexemeSequence
     * @see ParsingHints
     *
     * @param input the TAC encoded message
     * @param hints parsing hints to be passed to the lexer implementation
     * @return sequence of recongized or unrecognized {@link Lexeme}s
     */
    LexemeSequence lexMessage(final String input, ParsingHints hints);

}
