package fi.fmi.avi.parser;

import java.util.Iterator;

/**
 * Created by rinne on 16/12/16.
 */
public interface LexemeSequence {

    String getTAC();

    Lexeme getFirstLexeme();

    Lexeme getLastLexeme();

    Iterator<Lexeme> getLexemes();

    Iterator<Lexeme> getRecognizedLexemes();

    Iterator<Lexeme> getUnrecognizedLexemes();

    int getTotalLexemeCount();

}
