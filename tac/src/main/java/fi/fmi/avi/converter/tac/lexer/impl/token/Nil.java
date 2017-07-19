package fi.fmi.avi.converter.tac.lexer.impl.token;

import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.ISSUE_TIME;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.NIL;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.tac.lexer.Lexeme;
import fi.fmi.avi.converter.tac.lexer.impl.FactoryBasedReconstructor;
import fi.fmi.avi.converter.tac.lexer.impl.PrioritizedLexemeVisitor;

/**
 * Created by rinne on 10/02/17.
 */
public class Nil extends PrioritizedLexemeVisitor {

    public Nil(final Priority prio) {
        super(prio);
    }

    @Override
    public void visit(final Lexeme token, final ConversionHints hints) {
        if (token.getPrevious() != null && token.getPrevious().getIdentity() == ISSUE_TIME && "NIL".equalsIgnoreCase(token.getTACToken())) {
            token.identify(NIL);
        }
    }

    public static class Reconstructor extends FactoryBasedReconstructor {

        @Override
        public <T extends AviationWeatherMessage> Lexeme getAsLexeme(final T msg, Class<T> clz, final ConversionHints hints, final Object... specifier) {
            Lexeme retval = null;
            if (METAR.class.isAssignableFrom(clz)) {
                if (AviationCodeListUser.MetarStatus.MISSING == ((METAR) msg).getStatus()) {
                    retval = this.createLexeme("NIL", NIL);
                }
            } else if (TAF.class.isAssignableFrom(clz)) {
                if (AviationCodeListUser.TAFStatus.MISSING == ((TAF) msg).getStatus()) {
                    retval = this.createLexeme("NIL", NIL);
                }
            }
            return retval;
        }
    }
}
