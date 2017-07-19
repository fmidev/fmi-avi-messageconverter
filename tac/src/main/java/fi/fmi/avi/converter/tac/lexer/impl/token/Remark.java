package fi.fmi.avi.converter.tac.lexer.impl.token;

import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.REMARK;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.REMARKS_START;

import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.tac.lexer.Lexeme;
import fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName;
import fi.fmi.avi.converter.tac.lexer.impl.PrioritizedLexemeVisitor;

/**
 * Created by rinne on 10/02/17.
 */
public class Remark extends PrioritizedLexemeVisitor {
    public Remark(final Priority prio) {
        super(prio);
    }

    @Override
    public void visit(final Lexeme token, final ConversionHints hints) {
        if (token.getPrevious() != null) {
            Lexeme prev = token.getPrevious();
            if ((REMARK == prev.getIdentityIfAcceptable() || REMARKS_START == prev.getIdentityIfAcceptable()) && !"=".equals(token.getTACToken())) {
                token.identify(REMARK);
                token.setParsedValue(ParsedValueName.VALUE, token.getTACToken());
            }
        }
    }
}
