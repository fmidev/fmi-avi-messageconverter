package fi.fmi.avi.converter.tac.lexer.impl.token;

import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.SURFACE_WIND;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.DIRECTION;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.MAX_VALUE;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.MEAN_VALUE;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.UNIT;

import java.util.regex.Matcher;

import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.metar.ObservedSurfaceWind;
import fi.fmi.avi.model.metar.TrendForecast;
import fi.fmi.avi.model.metar.TrendForecastSurfaceWind;
import fi.fmi.avi.model.taf.TAFForecast;
import fi.fmi.avi.model.taf.TAFSurfaceWind;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.tac.lexer.SerializingException;
import fi.fmi.avi.converter.tac.lexer.Lexeme;
import fi.fmi.avi.converter.tac.lexer.impl.FactoryBasedReconstructor;
import fi.fmi.avi.converter.tac.lexer.impl.RegexMatchingLexemeVisitor;

/**
 * Created by rinne on 10/02/17.
 */
public class SurfaceWind extends RegexMatchingLexemeVisitor {

    public enum WindDirection {
        VARIABLE("VRB");

        private String code;

        WindDirection(final String code) {
            this.code = code;
        }

        public static WindDirection forCode(final String code) {
            for (WindDirection w : values()) {
                if (w.code.equals(code)) {
                    return w;
                }
            }
            return null;
        }
    }

    public SurfaceWind(final Priority prio) {
        super("^(VRB|[0-9]{3})([0-9]{2})(G[0-9]{2})?(KT|MPS|KMH)$", prio);
    }

    @Override
    public void visitIfMatched(final Lexeme token, final Matcher match, final ConversionHints hints) {
        boolean formatOk = true;
        int direction = -1, mean, gustValue = -1;
        String unit;
        if (!"VRB".equals(match.group(1))) {
            direction = Integer.parseInt(match.group(1));
        }
        mean = Integer.parseInt(match.group(2));
        String gust = match.group(3);
        if (gust != null && 'G' == gust.charAt(0)) {
            try {
                gustValue = Integer.parseInt(gust.substring(1));
                if (gustValue < 0) {
                    formatOk = false;
                }
            } catch (NumberFormatException nfe) {
                formatOk = false;
            }
        }
        unit = match.group(4);
        if (direction >= 360 || mean < 0 || unit == null) {
            formatOk = false;
        }

        if (formatOk) {
        	token.identify(SURFACE_WIND);
            if (direction == -1) {
                token.setParsedValue(DIRECTION, WindDirection.VARIABLE);
            } else {
                token.setParsedValue(DIRECTION, Integer.valueOf(direction));
            }
            token.setParsedValue(MEAN_VALUE, Integer.valueOf(mean));
            if (gustValue > -1) {
                token.setParsedValue(MAX_VALUE, Integer.valueOf(gustValue));
            }
            token.setParsedValue(UNIT, unit.toLowerCase());
        } else {
            token.identify(SURFACE_WIND, Lexeme.Status.SYNTAX_ERROR, "Wind direction or speed values invalid");
        }
    }

	public static class Reconstructor extends FactoryBasedReconstructor {

		@Override
        public <T extends AviationWeatherMessage> Lexeme getAsLexeme(T msg, Class<T> clz, final ConversionHints hints, Object... specifier)
                throws SerializingException {
            Lexeme retval = null;

            TAFForecast fct = getAs(specifier, TAFForecast.class);
            if (fct != null) {
                TAFSurfaceWind wind = fct.getSurfaceWind();
                if (wind != null) {
                	StringBuilder builder = new StringBuilder();
                	
                	
                	if (wind.isVariableDirection()) {
                        builder.append("VRB");
                    } else if (!wind.getMeanWindDirection().getUom().equals("deg")) {
                        throw new SerializingException("Mean wind direction unit is not 'deg': " + wind.getMeanWindDirection().getUom());
                    } else {
                    	builder.append(String.format("%03d", wind.getMeanWindDirection().getValue().intValue()));
                    }

                    this.appendCommonWindParameters(builder, wind.getMeanWindSpeed(), wind.getMeanWindDirection(), wind.getWindGust());
                    retval = this.createLexeme(builder.toString(), Lexeme.Identity.SURFACE_WIND);

                }
            } else if (msg instanceof METAR) {
                TrendForecast trend = getAs(specifier, TrendForecast.class);
                
                String tokenStr = null;
                if (trend != null) {
                    TrendForecastSurfaceWind wind = trend.getSurfaceWind();
                    if (wind != null) {
                        StringBuilder builder = new StringBuilder();
                        builder.append(String.format("%03d", wind.getMeanWindDirection().getValue().intValue()));
                        this.appendCommonWindParameters(builder, wind.getMeanWindSpeed(), wind.getMeanWindDirection(), wind.getWindGust());
                        tokenStr = builder.toString();
                    }
                } else {
                    METAR m = (METAR) msg;
                    ObservedSurfaceWind wind = m.getSurfaceWind();
                    StringBuilder builder = new StringBuilder();
                    if (wind.isVariableDirection()) {
                        builder.append("VRB");
                    } else if (!wind.getMeanWindDirection().getUom().equals("deg")) {
                        throw new SerializingException("Mean wind direction unit is not 'deg': " + wind.getMeanWindDirection().getUom());
                    } else {
                        builder.append(String.format("%03d", wind.getMeanWindDirection().getValue().intValue()));
                    }
                    this.appendCommonWindParameters(builder, wind.getMeanWindSpeed(), wind.getMeanWindDirection(), wind.getWindGust());
                    tokenStr = builder.toString();
                }
                
                if (tokenStr != null) {
                	retval = this.createLexeme(tokenStr, Lexeme.Identity.SURFACE_WIND);
                }
            }

			return retval;
		}

        private void appendCommonWindParameters(StringBuilder builder, NumericMeasure meanSpeed, NumericMeasure meanDirection, NumericMeasure gustSpeed)
                throws SerializingException {
            int speed = meanSpeed.getValue().intValue();
            appendSpeed(builder, speed);

            if (gustSpeed != null) {
                if (!gustSpeed.getUom().equals(gustSpeed.getUom())) {
                    throw new SerializingException(
                            "Wind gust speed unit '" + gustSpeed.getUom() + "' is not the same as mean wind speed unit '" + meanSpeed.getUom() + "'");
                }
                builder.append("G");
                appendSpeed(builder, gustSpeed.getValue().intValue());
            }

            builder.append(meanSpeed.getUom().toUpperCase());
        }

        private void appendSpeed(StringBuilder builder, int speed) throws SerializingException {
            if (speed < 0 || speed >= 1000) {
                throw new SerializingException("Wind speed value " + speed + " is not withing acceptable range [0,1000]");
            }
			builder.append(String.format("%02d", speed));
		}
	}
}
