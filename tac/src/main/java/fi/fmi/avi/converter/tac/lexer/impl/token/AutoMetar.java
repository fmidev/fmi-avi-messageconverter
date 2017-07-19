package fi.fmi.avi.converter.tac.lexer.impl.token;

import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.AUTOMATED;

import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.tac.lexer.Lexeme;
import fi.fmi.avi.converter.tac.lexer.impl.FactoryBasedReconstructor;
import fi.fmi.avi.converter.tac.lexer.impl.PrioritizedLexemeVisitor;

/**
 * Created by rinne on 10/02/17.
 */
public class AutoMetar extends PrioritizedLexemeVisitor {
    public AutoMetar(final Priority prio) {
        super(prio);
    }

    @Override
    public void visit(final Lexeme token, final ConversionHints hints) {
        if ("AUTO".equalsIgnoreCase(token.getTACToken())) {
            token.identify(AUTOMATED);
        }
    }

    public static class Reconstructor extends FactoryBasedReconstructor {

        @Override
        public <T extends AviationWeatherMessage> Lexeme getAsLexeme(final T msg, Class<T> clz, final ConversionHints hints, final Object... specifier) {
            if (METAR.class.isAssignableFrom(clz)) {
                METAR m = (METAR) msg;
                if (m.isAutomatedStation()) {
                    return this.createLexeme("AUTO", Lexeme.Identity.AUTOMATED);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }
}
