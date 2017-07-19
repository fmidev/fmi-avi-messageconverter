package fi.fmi.avi.converter.tac.lexer.impl.token;

import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.VARIABLE_WIND_DIRECTION;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.MAX_DIRECTION;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.MIN_DIRECTION;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.UNIT;

import java.util.regex.Matcher;

import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.metar.ObservedSurfaceWind;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.tac.lexer.SerializingException;
import fi.fmi.avi.converter.tac.lexer.Lexeme;
import fi.fmi.avi.converter.tac.lexer.impl.FactoryBasedReconstructor;
import fi.fmi.avi.converter.tac.lexer.impl.RegexMatchingLexemeVisitor;

/**
 * Created by rinne on 10/02/17.
 */
public class VariableSurfaceWind extends RegexMatchingLexemeVisitor {

    public VariableSurfaceWind(final Priority prio) {
        super("^([0-9]{3})V([0-9]{3})$", prio);
    }

    @Override
    public void visitIfMatched(final Lexeme token, final Matcher match, final ConversionHints hints) {
        boolean formatOk = true;
        int minDirection, maxDirection;
        minDirection = Integer.parseInt(match.group(1));
        maxDirection = Integer.parseInt(match.group(2));
        if (minDirection < 0 || minDirection >= 360 || maxDirection < 0 || maxDirection >= 360) {
            formatOk = false;
        }
        if (formatOk) {
            token.identify(VARIABLE_WIND_DIRECTION);
            token.setParsedValue(MIN_DIRECTION, minDirection);
            token.setParsedValue(MAX_DIRECTION, maxDirection);
            token.setParsedValue(UNIT, "deg");
        } else {
            token.identify(VARIABLE_WIND_DIRECTION, Lexeme.Status.SYNTAX_ERROR, "Wind directions invalid");
        }
    }
    
    
    public static class Reconstructor extends FactoryBasedReconstructor {
    	@Override
    	public <T extends AviationWeatherMessage> Lexeme getAsLexeme(T msg, Class<T> clz, ConversionHints hints,
    			Object... specifier) throws SerializingException {
    		Lexeme retval = null;
    		
    		NumericMeasure clockwise = null, counter = null;
    		
            if (METAR.class.isAssignableFrom(clz)) {
                METAR m = (METAR) msg;
                ObservedSurfaceWind wind = m.getSurfaceWind();
                
                if (wind != null) {
                	clockwise = wind.getExtremeClockwiseWindDirection();
                	counter = wind.getExtremeCounterClockwiseWindDirection();
                }
            }
        	
            if (clockwise != null || counter != null) {
        		String str = createString(clockwise, counter);
        	    retval = this.createLexeme(str, VARIABLE_WIND_DIRECTION);
        	}

            return retval;
    	}

		private String createString(NumericMeasure clockwise, NumericMeasure counter) throws SerializingException
		{
			// Both must be set
			if (clockwise == null || counter == null) {
        		throw new SerializingException("Only either extreme clockwise or counter-clocwise wind direction given. Unable to serialize token");
        	}
			
			if (!"deg".equals(counter.getUom())) {
				throw new SerializingException("Counter-clockwise extreme wind direction is not in degress (but in '"+counter.getUom()+"'), unable to serialize");
			}

			if (!"deg".equals(clockwise.getUom())) {
				throw new SerializingException("Clockwise extreme wind direction is not in degress (but in '"+clockwise.getUom()+"'), unable to serialize");
			}

			if (counter.getValue() < 0.0 || counter.getValue() >= 360.0) {
				throw new SerializingException("Illegal counter-clockwise extreme wind direction "+counter.getValue()+" "+counter.getUom());
			}

			if (clockwise.getValue() < 0.0 || clockwise.getValue() >= 360.0) {
				throw new SerializingException("Illegal clockwise extreme wind direction "+clockwise.getValue()+" "+clockwise.getUom());
			}

			
			String ret = String.format("%03dV%03d", counter.getValue().intValue(), clockwise.getValue().intValue());
			
			return ret;
		}
    }
}
