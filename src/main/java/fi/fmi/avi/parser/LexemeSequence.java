package fi.fmi.avi.parser;

import java.util.List;

/**
 *
 */
public interface LexemeSequence {

    String getTAC();

    Lexeme getFirstLexeme();

    Lexeme getLastLexeme();

    List<Lexeme> getLexemes();

    List<LexemeSequence> splitBy(Lexeme.Identity...ids);

}
