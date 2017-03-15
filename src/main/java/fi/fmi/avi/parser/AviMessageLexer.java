package fi.fmi.avi.parser;

/**
 * Created by rinne on 19/12/16.
 */
public interface AviMessageLexer {

    LexemeSequence lexMessage(final String input);

    LexemeSequence lexMessage(final String input, ParsingHints hints);

}
