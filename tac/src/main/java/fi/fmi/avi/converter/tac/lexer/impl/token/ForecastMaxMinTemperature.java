package fi.fmi.avi.converter.tac.lexer.impl.token;

import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.MAX_TEMPERATURE;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.MIN_TEMPERATURE;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.DAY1;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.HOUR1;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.VALUE;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFAirTemperatureForecast;
import fi.fmi.avi.model.taf.TAFBaseForecast;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.tac.lexer.SerializingException;
import fi.fmi.avi.converter.tac.lexer.Lexeme;
import fi.fmi.avi.converter.tac.lexer.impl.FactoryBasedReconstructor;

/**
 * Created by rinne on 10/02/17.
 */
public class ForecastMaxMinTemperature extends TimeHandlingRegex {

    public enum TemperatureForecastType {
        MINIMUM("TN"), MAXIMUM("TX");

        private final String code;

        TemperatureForecastType(final String code) {
            this.code = code;
        }

        public static TemperatureForecastType forCode(final String code) {
            for (TemperatureForecastType w : values()) {
                if (w.code.equals(code)) {
                    return w;
                }
            }
            return null;
        }

    }

    public ForecastMaxMinTemperature(final Priority prio) {
        super("^(TX|TN)(M)?([0-9]{2})/([0-9]{2})?([0-9]{2})(Z)?$", prio);
    }

    @Override
    public void visitIfMatched(final Lexeme token, final Matcher match, final ConversionHints hints) {
        TemperatureForecastType kind = TemperatureForecastType.forCode(match.group(1));
        boolean isNegative = match.group(2) != null;
        int value = Integer.parseInt(match.group(3));
        if (isNegative) {
            value = value * -1;
        }
        int day = -1;
        if (match.group(3) != null) {
            day = Integer.parseInt(match.group(4));
        }
        int hour = Integer.parseInt(match.group(5));

        Lexeme.Identity kindLexemeIdentity;
        if (TemperatureForecastType.MAXIMUM == kind) {
            kindLexemeIdentity = MAX_TEMPERATURE;
        } else {
            kindLexemeIdentity = MIN_TEMPERATURE;
        }
        
        if (timeOkDayHour(day, hour)) {
            if (ConversionHints.VALUE_TIMEZONE_ID_POLICY_STRICT == hints.get(ConversionHints.KEY_TIMEZONE_ID_POLICY)) {
                if (match.group(6) == null) {
                	token.identify(kindLexemeIdentity,Lexeme.Status.WARNING,"Missing time zone ID 'Z'");
                } else {
                	token.identify(kindLexemeIdentity);
                }
            } else {
            	token.identify(kindLexemeIdentity);
            }
        	
            if (day > -1) {
                token.setParsedValue(DAY1, day);
            }
            token.setParsedValue(HOUR1, hour);
            token.setParsedValue(VALUE, value);
        } else {
            token.identify(kindLexemeIdentity,Lexeme.Status.SYNTAX_ERROR,"Invalid day/hour values");
        }
        
    }

    public static class Reconstructor extends FactoryBasedReconstructor {
    	@Override
        public <T extends AviationWeatherMessage> List<Lexeme> getAsLexemes(T msg, Class<T> clz, ConversionHints hints, Object... specifier)
                throws SerializingException {
            List<Lexeme> retval = new ArrayList<>();
    		
    		if (TAF.class.isAssignableFrom(clz)) {
    			
    			TAFBaseForecast forecast = getAs(specifier, TAFBaseForecast.class);
    			
    			if (forecast.getTemperatures() != null) {
    				for (TAFAirTemperatureForecast temp : forecast.getTemperatures()) {

    					if (temp.getMaxTemperature() != null) {
    						if (!"degC".equals(temp.getMaxTemperature().getUom())) {
                                throw new SerializingException(
                                        "Unsupported unit of measurement for maximum temperature: '" + temp.getMaxTemperature().getUom() + "'");
                            }
    						
    						String s = formatTemp("TX", 
    								temp.getMaxTemperature().getValue(), 
    								temp.getMaxTemperatureDayOfMonth(),
    								temp.getMaxTemperatureHour());

                            retval.add(this.createLexeme(s, MAX_TEMPERATURE));
                        }
    					
    					if (temp.getMinTemperature() != null) {
    						if (!"degC".equals(temp.getMinTemperature().getUom())) {
                                throw new SerializingException(
                                        "Unsupported unit of measurement for minimum temperature: '" + temp.getMaxTemperature().getUom() + "'");
                            }
    						
    						String s = formatTemp("TN", 
    								temp.getMinTemperature().getValue(), 
    								temp.getMinTemperatureDayOfMonth(),
    								temp.getMinTemperatureHour());

                            retval.add(this.createLexeme(s, MIN_TEMPERATURE));
                        }
    					
    				}
    			}
    		}
    		
    		return retval;
    	}
    	
    	public String formatTemp(String prefix, Double temp, int day, int hour) {
    		String s = String.format("%s%02d/%02d%02dZ",
					temp < 0.0 ? prefix + "M" : prefix,
					Math.abs(temp.intValue()),
					day,
					hour);
    		
    		return s;
    	}
    }
}
