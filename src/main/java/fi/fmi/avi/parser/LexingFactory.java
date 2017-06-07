package fi.fmi.avi.parser;

/**
 * A Factory for all lexing related things.
 *
 * @author Ilkka Rinne / Spatineo 2017
 */
public interface LexingFactory {

    /**
     * Creates a structurally unmodifiable {@link LexemeSequence} from the String <code>input</code> containing white-space separated,
     * TAC encoded aviation weather message. The contained {@link Lexeme}s all have initial status of
     * {@link Lexeme.Status#UNRECOGNIZED}.
     *
     * The <code>hints</code> is used for providing guidance for fine-tuning the
     * Factory implementation. The implementations should consider the given hints
     * but are free to ignore them.
     *
     * The character start and end positions for each Lexeme
     * in the original input String (taking into account extra white-space between token) is
     * available using {@link Lexeme#getStartIndex()} and {@link Lexeme#getEndIndex()}.
     *
     * @param input the TAC encoded message
     * @param hints guiding instructions for the implementation
     * @return a "raw", unprocessed Lexeme sequence
     */
    LexemeSequence createLexemeSequence(final String input, final ConversionHints hints);

    /**
     * Creates a new {@link LexemeSequenceBuilder} for constructing a new {@link LexemeSequence}
     * one or more String token at a time.
     *
     * @return the builder
     */
    LexemeSequenceBuilder createLexemeSequenceBuilder();

    /**
     * Creates a single {@link Lexeme} containing the <code>token</code> as it's
     * {@link Lexeme#getTACToken()} with {@link Lexeme#getIdentity()} <code>null</code>
     * and {@link Lexeme#getStatus()} {@link Lexeme.Status#UNRECOGNIZED}.
     *
     * @param token the TAC token
     * @return a raw Lexeme
     */
    Lexeme createLexeme(final String token);

    /**
     * Creates a single {@link Lexeme} containing the <code>token</code> as it's
     * {@link Lexeme#getTACToken()} with {@link Lexeme#getIdentity()} <code>identity</code>
     * and {@link Lexeme#getStatus()} {@link Lexeme.Status#OK}.
     *
     * @param token the TAC token
     * @param identity the forced identity
     * @return a recognized Lexeme
     */
    Lexeme createLexeme(final String token, final Lexeme.Identity identity);

    /**
     * Creates a single {@link Lexeme} containing the <code>token</code> as it's
     * {@link Lexeme#getTACToken()} with {@link Lexeme#getIdentity()} <code>identity</code>
     * and {@link Lexeme#getStatus()} <code>status</code>.
     *
     * @param token
     * @param identity
     * @param status
     * @return a recognized Lexeme with the given status
     */
    Lexeme createLexeme(final String token, final Lexeme.Identity identity, final Lexeme.Status status);

}
