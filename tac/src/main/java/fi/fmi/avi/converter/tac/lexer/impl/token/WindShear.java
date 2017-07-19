package fi.fmi.avi.converter.tac.lexer.impl.token;

import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.WIND_SHEAR;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.RUNWAY;

import java.util.regex.Matcher;

import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.RunwayDirection;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.tac.lexer.Lexeme;
import fi.fmi.avi.converter.tac.lexer.impl.FactoryBasedReconstructor;
import fi.fmi.avi.converter.tac.lexer.impl.RegexMatchingLexemeVisitor;

/**
 * Created by rinne on 10/02/17.
 */
public class WindShear extends RegexMatchingLexemeVisitor {

    public WindShear(final Priority prio) {
        super("^WS\\s(ALL\\s)?(?:RWY|R(?:WY)?([0-9]{2}[LRC]?))$", prio);
    }

    @Override
    public void visitIfMatched(final Lexeme token, final Matcher match, final ConversionHints hints) {
        if (match.group(1) != null) {
        	token.identify(WIND_SHEAR);
            token.setParsedValue(RUNWAY, "ALL");
        } else if (match.group(2) != null) {
        	token.identify(WIND_SHEAR);
            token.setParsedValue(RUNWAY, match.group(2));
        } else {
            token.identify(WIND_SHEAR, Lexeme.Status.SYNTAX_ERROR, "Could not understand runway code");
        }
    }
    
    public static class Reconstructor extends FactoryBasedReconstructor {

        @Override
        public <T extends AviationWeatherMessage> Lexeme getAsLexeme(final T msg, Class<T> clz, final ConversionHints hints, final Object... specifier) {
            Lexeme retval = null;
            fi.fmi.avi.model.metar.WindShear windShear = null;
            
            if (clz.isAssignableFrom(METAR.class)) {
            	METAR metar = (METAR)msg;
            	
            	windShear = metar.getWindShear();
            }
            
            if (windShear != null) {
            	StringBuilder str = new StringBuilder("WS");
            	if (windShear.isAllRunways()) {
            		str.append(" ALL RWY");
            	} else {
                    boolean annex3_16th = hints.containsValue(ConversionHints.VALUE_SERIALIZATION_POLICY_ANNEX3_16TH);
                    for (RunwayDirection rwd :  windShear.getRunwayDirections()) {
            			if (annex3_16th) {
            				str.append(" RWY");
            			} else {
            				str.append(" R");
            			}
            			str.append(rwd.getDesignator());
            		}
            	}
            	
            	retval = createLexeme(str.toString(), WIND_SHEAR);
            }
            
            
        	return retval;
        }

    }
}
