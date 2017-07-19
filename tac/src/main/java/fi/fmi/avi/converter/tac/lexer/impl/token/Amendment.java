package fi.fmi.avi.converter.tac.lexer.impl.token;

import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.AMENDMENT;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.tac.lexer.Lexeme;
import fi.fmi.avi.converter.tac.lexer.impl.FactoryBasedReconstructor;
import fi.fmi.avi.converter.tac.lexer.impl.PrioritizedLexemeVisitor;

/**
 * Created by rinne on 10/02/17.
 */
public class Amendment extends PrioritizedLexemeVisitor {

    public Amendment(final Priority prio) {
        super(prio);
    }

    @Override
    public void visit(final Lexeme token, final ConversionHints hints) {
        if (token.getPrevious() == token.getFirst() && "AMD".equalsIgnoreCase(token.getTACToken())) {
            token.identify(AMENDMENT);
        }
    }

    public static class Reconstructor extends FactoryBasedReconstructor {

        @Override
        public <T extends AviationWeatherMessage> Lexeme getAsLexeme(final T msg, Class<T> clz, final ConversionHints hints, final Object... specifier) {
            Lexeme retval = null;
            if (TAF.class.isAssignableFrom(clz)) {
                // Note: cancellation messages are also amendments
                if (AviationCodeListUser.TAFStatus.AMENDMENT == ((TAF) msg).getStatus() ||
                    AviationCodeListUser.TAFStatus.CANCELLATION == ((TAF) msg).getStatus()) {
                    retval = this.createLexeme("AMD", AMENDMENT);
                }

            }
            return retval;
        }
    }
}
