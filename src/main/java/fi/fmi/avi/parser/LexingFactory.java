package fi.fmi.avi.parser;

/**
 * Created by rinne on 17/02/17.
 */
public interface LexingFactory {

    LexemeSequence createLexemeSequence(final String input, final ParsingHints hints);

    LexemeSequenceBuilder createLexemeSequenceBuilder();

    Lexeme createLexeme(final String token);

    Lexeme createLexeme(final String token, final Lexeme.Identity identity);

    Lexeme createLexeme(final String token, final Lexeme.Identity identity, final Lexeme.Status status);

}
