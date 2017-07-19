package fi.fmi.avi.converter.tac.lexer.impl.token;

import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.CORRECTION;

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
public class Correction extends PrioritizedLexemeVisitor {

    public Correction(final Priority prio) {
        super(prio);
    }

    @Override
    public void visit(final Lexeme token, final ConversionHints hints) {
        if (token.getPrevious() == token.getFirst() && "COR".equalsIgnoreCase(token.getTACToken())) {
            token.identify(CORRECTION);
        }
    }

    public static class Reconstructor extends FactoryBasedReconstructor {

        @Override
        public <T extends AviationWeatherMessage> Lexeme getAsLexeme(final T msg, Class<T> clz, final ConversionHints hints, final Object... specifier) {
            Lexeme retval = null;
            if (METAR.class.isAssignableFrom(clz)) {
                if (AviationCodeListUser.MetarStatus.CORRECTION == ((METAR) msg).getStatus()) {
                    retval = this.createLexeme("COR", CORRECTION);
                }
            } else if (TAF.class.isAssignableFrom(clz)) {
                if (AviationCodeListUser.TAFStatus.CORRECTION == ((TAF) msg).getStatus()) {
                    retval = this.createLexeme("COR", CORRECTION);
                }
            }
            return retval;
        }
    }
}
