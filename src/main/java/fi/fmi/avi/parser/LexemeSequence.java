package fi.fmi.avi.parser;

import java.util.List;

import fi.fmi.avi.data.AviationWeatherMessage;

/**
 * A sequence of {@link Lexeme}s corresponding to a message or part of it.
 * Typically produced as the result of the {@link AviMessageLexer#lexMessage(String)}
 * or {@link AviMessageTACTokenizer#tokenizeMessage(AviationWeatherMessage)}.
 *
 * To create a new instance from a String,
 * use {@link LexingFactory#createLexemeSequence(String, ParsingHints)}
 * or build a sequence dynamically using {@link LexemeSequenceBuilder}
 * available from {@link LexingFactory#createLexemeSequenceBuilder()}.
 *
 * @author Ilkka Rinne / Spatineo 2017
 *
 */
public interface LexemeSequence {

    /**
     * Returns a TAC encoded version of the whole sequence.
     * Implementations should return the exactly same TAC message
     * used for constructing the LexemeSequence is possible.
     *
     * @return the TAC representation
     */
    String getTAC();

    /**
     * Convenience methos for accessing the first {@link Lexeme} in the sequence.
     *
     * @return the first {@link Lexeme}
     */
    Lexeme getFirstLexeme();

    /**
     * Convenience methos for accessing the last {@link Lexeme} in the sequence.
     *
     * @return the first {@link Lexeme}
     */
    Lexeme getLastLexeme();

    /**
     * List of all {@link Lexeme}s in the sequence from the first to the last.
     *
     * Note that Java 8 users may filter the returned list conveniently using
     * the Stream API:
     * <pre>
     *     List<Lexeme> recognizedLexemes = lexed.getLexemes().stream()
     *       .filter((lexeme) -> Lexeme.Status.UNRECOGNIZED != lexeme.getStatus())
     *       .collect(Collectors.toList());
     * </pre>
     *
     * @return contained Lexemes as a list
     */
    List<Lexeme> getLexemes();

    /**
     * Returns a list of sub-sequences cut from the sequence split by given {@link Lexeme.Identity} set.
     * A new sub-sequence starts at each found {@link Lexeme} identified as any of the given
     * <code>ids</code>. Zero-length sub-sequences will be not returned, so if the first
     * {@link Lexeme} matches, the first returned {@link LexemeSequence} starts at the
     * first {@link Lexeme}. If the last {@link Lexeme} matches, the last returned
     * {@link LexemeSequence} contains only the last Lexeme.
     *
     * If not matches are found, the original {@link LexemeSequence} is returned as the
     * only list item.
     *
     * @param ids
     * @return
     */
    List<LexemeSequence> splitBy(Lexeme.Identity...ids);

}
