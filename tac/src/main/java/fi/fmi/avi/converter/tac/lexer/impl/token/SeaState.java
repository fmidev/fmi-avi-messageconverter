package fi.fmi.avi.converter.tac.lexer.impl.token;

import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.SEA_STATE;

import java.util.regex.Matcher;

import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.tac.lexer.SerializingException;
import fi.fmi.avi.converter.tac.lexer.Lexeme;
import fi.fmi.avi.converter.tac.lexer.impl.FactoryBasedReconstructor;
import fi.fmi.avi.converter.tac.lexer.impl.RegexMatchingLexemeVisitor;

/**
 * Created by rinne on 10/02/17.
 */
public class SeaState extends RegexMatchingLexemeVisitor {

    public enum SeaSurfaceState {
        CALM_GLASSY('0'),
        CALM_RIPPLED('1'),
        SMOOTH_WAVELETS('2'),
        SLIGHT('3'),
        MODERATE('4'),
        ROUGH('5'),
        VERY_ROUGH('6'),
        HIGH('7'),
        VERY_HIGH('8'),
        PHENOMENAL('9'),
        MISSING('/');

        private char code;

        SeaSurfaceState(final char code) {
            this.code = code;
        }

        public char getCode() {
        	return code;
        }
        
        public static SeaSurfaceState forCode(final char code) {
            for (SeaSurfaceState w : values()) {
                if (w.code == code) {
                    return w;
                }
            }
            return null;
        }
    }

    public SeaState(final Priority prio) {
        super("^W(M?)([0-9]{2}|//)/(S([0-9]|/)|H([0-9]{1,3}))$", prio);
    }

    @Override
    public void visitIfMatched(final Lexeme token, final Matcher match, final ConversionHints hints) {
        Integer seaSurfaceTemperature = null;
        if (!"//".equals(match.group(2))) {
            seaSurfaceTemperature = Integer.valueOf(match.group(2));
        }
        if (seaSurfaceTemperature != null && match.group(1) != null) {
            seaSurfaceTemperature *= -1;
        }
        SeaSurfaceState state = null;
        if (match.group(4) != null) {
            state = SeaSurfaceState.forCode(match.group(4).charAt(0));
        }
        Integer waveHeight = null;
        if (match.group(5) != null) {
            waveHeight = Integer.valueOf(match.group(5));
        }
        token.identify(SEA_STATE);
        Object[] values = new Object[3];
        if (seaSurfaceTemperature != null) {
            values[0] = seaSurfaceTemperature;
            token.setParsedValue(Lexeme.ParsedValueName.UNIT, "degC");
        }
        if (state != null) {
            values[1] = state;
        }
        if (waveHeight != null) {
            values[2] = (double)waveHeight.intValue() * 0.1;
            token.setParsedValue(Lexeme.ParsedValueName.UNIT2, "m");
        }
        token.setParsedValue(Lexeme.ParsedValueName.VALUE, values);     
    }
    
    public static class Reconstructor extends FactoryBasedReconstructor {

        @Override
        public <T extends AviationWeatherMessage> Lexeme getAsLexeme(final T msg, Class<T> clz, final ConversionHints hints, final Object... specifier) throws SerializingException {
            Lexeme retval = null;

            if (clz.isAssignableFrom(METAR.class)) {
            	METAR metar = (METAR)msg;
            	
            	fi.fmi.avi.model.metar.SeaState state = metar.getSeaState();
            	
            	if (state != null) {
            		StringBuilder builder = new StringBuilder("W");
            		
            		NumericMeasure temp = state.getSeaSurfaceTemperature();
            		if (temp == null) {
            			builder.append("//");
            		} else {
            			if ("degC".equals(temp.getUom())) {
            				int value = temp.getValue().intValue();
            				if (value < 0) {
            					builder.append("M");
            					value *= -1;
            				}
            				builder.append(String.format("%02d/", value));
            				
            			} else {
            				throw new SerializingException("Sea state temperature must be in degC, cannot serialize");
            			}
            		}
            		
            		NumericMeasure waveHeight = state.getSignificantWaveHeight();
            		
            		if (state.getSeaSurfaceState() != null && waveHeight != null) {
            			throw new SerializingException("Sea state can only contain either surface state or wave height, not both");
            		}
            		
            		if (state.getSeaSurfaceState() == null && waveHeight == null) {
            			throw new SerializingException("Sea state has to contain either surface state or wave height");
            		}
            		
            		if (state.getSeaSurfaceState() != null) {
            			// Sea surface state
            			//builder.append(String.format("S%c", state.getSeaSurfaceState().getCode()));
            			builder.append("S"+state.getSeaSurfaceState().getCode());
            			
            		} else {
            			// Significant wave height
            			if (!"m".equals(waveHeight.getUom())) {
            				throw new SerializingException("Sea state wave height must be in meters");
            			}
            			
            			int height = (int)Math.round(waveHeight.getValue() / 0.1);
            			
            			if (height < 0 || height > 999) {
            				throw new SerializingException("Sea state wave height must be between 0 and 100 meters, it was "+waveHeight.getValue());
            			}
            			
            			builder.append(String.format("H%d", height));
            		}
            		
            		
            		retval = createLexeme(builder.toString(), SEA_STATE);
            	}
            }
            
            return retval;
        }
    }
}
