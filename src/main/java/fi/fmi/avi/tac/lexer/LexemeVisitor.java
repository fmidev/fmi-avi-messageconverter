package fi.fmi.avi.tac.lexer;

import fi.fmi.avi.converter.ConversionHints;

/**
 * Part of the Visitor Design Pattern for conditionally modifying the visited {@link Lexeme}s.
 * Mainly used by the lexer implementations in order to identify and parse the contents
 * of individual {@link Lexeme}s by trial-and-error.
 *
 * Especially useful if there are more than one different implementations of {@link Lexeme},
 * because the testing and modifying logic can be separated from the {@link Lexeme}
 * implementation logic.
 *
 * A typical implementation tests an unrecognized {@link Lexeme} against a pattern or relative
 * position in the {@link LexemeSequence}, in order to identify it as a particular type of
 * {@link Lexeme}. If the recognized, the {@link LexemeVisitor} modifies the visited
 * {@link Lexeme} by calling the {@link Lexeme#identify(Lexeme.Identity)} and possibly
 * {@link Lexeme#setParsedValue(Lexeme.ParsedValueName, Object)} to store pre-parsed
 * content to be used later.
 *
 * The other complementary part the Visitor implementation is the
 * {@link Lexeme#accept(LexemeVisitor, ConversionHints)},
 * which the {@link LexemeVisitor} implementations call as part of the
 * {@link #visit(Lexeme, ConversionHints)} method code:
 *
 * <pre>
 *   public void visit(final Lexeme token, final ParsingHints hints) {
 *     if (!token.isRecognized() &amp;&amp; visitors != null) {
 *       for (LexemeVisitor v : visitors) {
 *         token.accept(v, hints);
 *         if (token.isRecognized()) {
 *           break;
 *         }
 *       }
 *     }
 *   }
 * </pre>
 *
 * @see Lexeme#accept(LexemeVisitor, ConversionHints)
 *
 * @author Ilkka Rinne / Spatineo 2017
 *
 */
public interface LexemeVisitor {

    /**
     * Defines a way to visit a {@link Lexeme}.
     *
     * @param token to visit
     * @param hints hints to be passed to the visitor fro guiding the possible modifications
     */
    void visit(Lexeme token, ConversionHints hints);
}
