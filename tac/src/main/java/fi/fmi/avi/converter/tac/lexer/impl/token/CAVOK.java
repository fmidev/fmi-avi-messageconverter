package fi.fmi.avi.converter.tac.lexer.impl.token;

import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.CAVOK;

import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.metar.TrendForecast;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFBaseForecast;
import fi.fmi.avi.model.taf.TAFChangeForecast;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.tac.lexer.Lexeme;
import fi.fmi.avi.converter.tac.lexer.impl.FactoryBasedReconstructor;
import fi.fmi.avi.converter.tac.lexer.impl.PrioritizedLexemeVisitor;

/**
 * Created by rinne on 10/02/17.
 */
public class CAVOK extends PrioritizedLexemeVisitor {
    public CAVOK(final Priority prio) {
        super(prio);
    }

    @Override
    public void visit(final Lexeme token, final ConversionHints hints) {
        if ("CAVOK".equalsIgnoreCase(token.getTACToken())) {
            token.identify(CAVOK);
        }
    }

    public static class Reconstructor extends FactoryBasedReconstructor {

        @Override
        public <T extends AviationWeatherMessage> Lexeme getAsLexeme(final T msg, Class<T> clz, final ConversionHints hints, final Object... specifier) {
            Lexeme retval = null;
            if (METAR.class.isAssignableFrom(clz)) {
                METAR m = (METAR) msg;
                
                if (specifier == null || specifier.length == 0) {
                	if (m.isCeilingAndVisibilityOk()) {
                		retval = this.createLexeme("CAVOK", CAVOK);
                	}
                } else if (specifier != null) {
                	TrendForecast trendForecast = getAs(specifier, TrendForecast.class);
                	if (trendForecast != null && trendForecast.isCeilingAndVisibilityOk()) {
                		retval = this.createLexeme("CAVOK", CAVOK);
                	}
                }
            
            } else if (TAF.class.isAssignableFrom(clz)) {
                TAF t = (TAF) msg;
                if (specifier != null && specifier[0] instanceof TAFBaseForecast) {
                    TAFBaseForecast b = (TAFBaseForecast) specifier[0];
                    if (b.isCeilingAndVisibilityOk()) {
                        retval = this.createLexeme("CAVOK", CAVOK);
                    }
                } else if (specifier != null && specifier[0] instanceof TAFChangeForecast) {
                    TAFChangeForecast c = (TAFChangeForecast) specifier[0];
                    if (c.isCeilingAndVisibilityOk()) {
                        retval = this.createLexeme("CAVOK", CAVOK);
                    }
                }
            }
            return retval;
        }
    }
}
