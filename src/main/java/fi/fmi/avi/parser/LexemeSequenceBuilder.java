package fi.fmi.avi.parser;

import java.util.List;

/**
 * Created by rinne on 01/03/17.
 */
public interface LexemeSequenceBuilder {
    LexemeSequenceBuilder append(Lexeme lexeme);
    
    LexemeSequenceBuilder appendAll(List<Lexeme> lexemes);

    LexemeSequence build();
}
