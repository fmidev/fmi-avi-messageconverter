package fi.fmi.avi.converter.tac.lexer.impl.token;

import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.NO_SIGNIFICANT_WEATHER;

import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.metar.TrendForecast;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFChangeForecast;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.tac.lexer.SerializingException;
import fi.fmi.avi.converter.tac.lexer.Lexeme;
import fi.fmi.avi.converter.tac.lexer.impl.FactoryBasedReconstructor;
import fi.fmi.avi.converter.tac.lexer.impl.PrioritizedLexemeVisitor;

/**
 * Created by rinne on 10/02/17.
 */
public class NoSignificantWeather extends PrioritizedLexemeVisitor {
    public NoSignificantWeather(final Priority prio) {
        super(prio);
    }

    @Override
    public void visit(final Lexeme token, final ConversionHints hints) {
        if ("NSW".equalsIgnoreCase(token.getTACToken())) {
			token.identify(NO_SIGNIFICANT_WEATHER);
        }
    }
    
    
    public static class Reconstructor extends FactoryBasedReconstructor {
    	@Override
        public <T extends AviationWeatherMessage> Lexeme getAsLexeme(T msg, Class<T> clz, ConversionHints hints, Object... specifier)
                throws SerializingException {
            Lexeme retval = null;
			
			if (TAF.class.isAssignableFrom(clz)) {
				TAFChangeForecast forecast = getAs(specifier, TAFChangeForecast.class);
				
				if (forecast != null) {
					if (forecast.isNoSignificantWeather()) {
						retval = this.createLexeme("NSW", NO_SIGNIFICANT_WEATHER);
					}
				}
			}
			
			if (METAR.class.isAssignableFrom(clz)) {
				TrendForecast trend = getAs(specifier, TrendForecast.class);
				if (trend != null && trend.isNoSignificantWeather()) {
					retval = this.createLexeme("NSW", NO_SIGNIFICANT_WEATHER);
				}
			}
			
			
			return retval;
    	}
    }
}
