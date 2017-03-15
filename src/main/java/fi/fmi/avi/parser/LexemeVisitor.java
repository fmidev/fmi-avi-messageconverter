package fi.fmi.avi.parser;

/**
 * Created by rinne on 21/12/16.
 */
public interface LexemeVisitor {

    void visit(Lexeme token, ParsingHints hints);
}
