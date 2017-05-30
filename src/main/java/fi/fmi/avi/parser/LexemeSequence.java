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

    /*
    Iterator<Lexeme> getLexemes();

    Iterator<Lexeme> getRecognizedLexemes();

    Iterator<Lexeme> getUnrecognizedLexemes();

    int getTotalLexemeCount();
    */

}
