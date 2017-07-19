package fi.fmi.avi.converter.tac.lexer.impl.token;

import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.DAY1;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.DAY2;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.HOUR1;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.HOUR2;

import java.util.regex.Matcher;

import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.AviationCodeListUser.TAFChangeIndicator;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFChangeForecast;
import fi.fmi.avi.converter.tac.lexer.Lexeme;
import fi.fmi.avi.converter.tac.lexer.SerializingException;
import fi.fmi.avi.converter.tac.lexer.Lexeme.Identity;
import fi.fmi.avi.converter.tac.lexer.impl.FactoryBasedReconstructor;

/**
 * Created by rinne on 10/02/17.
 */
public class ChangeForecastTimeGroup extends TimeHandlingRegex {
	
    public ChangeForecastTimeGroup(final Priority prio) {
        super("^(([0-9]{2})([0-9]{2}))|(([0-9]{2})([0-9]{2})/([0-9]{2})([0-9]{2}))$", prio);
    }

    @Override
	public void visitIfMatched(final Lexeme token, final Matcher match, final ConversionHints hints) {
		if (token.hasPrevious() && token.getPrevious().getIdentity() == Identity.FORECAST_CHANGE_INDICATOR) {
            if (match.group(1) != null) {
                //old 24h TAF: HHHH
            	double certainty = 0.5; //could also be horizontal visibility
            	Lexeme l = token.getNext();
            	if (l != null && (Identity.SURFACE_WIND == l.getIdentity() || Identity.HORIZONTAL_VISIBILITY == l.getIdentity())) {
            		certainty = 1.0;
            	}
                int fromHour = Integer.parseInt(match.group(2));
                int toHour = Integer.parseInt(match.group(3));
                if (timeOkHour(fromHour) && timeOkHour(toHour)) {
                	token.identify(Identity.CHANGE_FORECAST_TIME_GROUP, certainty);
                    token.setParsedValue(HOUR1, fromHour);
                    token.setParsedValue(HOUR2, toHour);
                } else {
                    token.identify(Identity.CHANGE_FORECAST_TIME_GROUP, Lexeme.Status.SYNTAX_ERROR, "Invalid time(s)", 0.3);
                }

            } else {
                //30h TAF
                int fromDay = Integer.parseInt(match.group(5));
                int fromHour = Integer.parseInt(match.group(6));
                int toDay = Integer.parseInt(match.group(7));
                int toHour = Integer.parseInt(match.group(8));
                if (timeOkDayHour(fromDay, fromHour) && timeOkDayHour(toDay, toHour)) {
                	token.identify(Identity.CHANGE_FORECAST_TIME_GROUP);
                	token.setParsedValue(DAY1, fromDay);
                    token.setParsedValue(DAY2, toDay);
                    token.setParsedValue(HOUR1, fromHour);
                    token.setParsedValue(HOUR2, toHour);
                } else {
                    token.identify(Identity.CHANGE_FORECAST_TIME_GROUP, Lexeme.Status.SYNTAX_ERROR, "Invalid date and/or time");
                }
            }
        }
    }

    public static class Reconstructor extends FactoryBasedReconstructor {

		@Override
		public <T extends AviationWeatherMessage> Lexeme getAsLexeme(T msg, Class<T> clz, ConversionHints hints, Object... specifier)
				throws SerializingException {
			Lexeme retval = null;
			if (TAF.class.isAssignableFrom(clz)) {
				TAFChangeForecast forecast = getAs(specifier, TAFChangeForecast.class);
				if (forecast != null && forecast.getChangeIndicator() != TAFChangeIndicator.FROM) {
                    retval = this.createLexeme(forecast.getPartialValidityTimePeriod(), Identity.CHANGE_FORECAST_TIME_GROUP);
                }
			}
			return retval;
		}
    	
    }
   
}
