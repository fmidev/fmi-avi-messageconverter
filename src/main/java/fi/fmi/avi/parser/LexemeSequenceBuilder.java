package fi.fmi.avi.parser;

/**
 * Created by rinne on 01/03/17.
 */
public interface LexemeSequenceBuilder {
    LexemeSequenceBuilder append(Lexeme lexeme);

    LexemeSequence build();
}
