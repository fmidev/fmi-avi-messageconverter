package fi.fmi.avi.converter.tac.lexer.impl.token;

import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.RUNWAY_VISUAL_RANGE;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.MAX_VALUE;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.MIN_VALUE;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.RELATIONAL_OPERATOR;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.RELATIONAL_OPERATOR2;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.RUNWAY;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.TENDENCY_OPERATOR;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.UNIT;

import java.util.regex.Matcher;

import fi.fmi.avi.model.AviationCodeListUser.RelationalOperator;
import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.RunwayDirection;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.tac.lexer.SerializingException;
import fi.fmi.avi.converter.tac.lexer.Lexeme;
import fi.fmi.avi.converter.tac.lexer.Lexeme.Identity;
import fi.fmi.avi.converter.tac.lexer.impl.FactoryBasedReconstructor;
import fi.fmi.avi.converter.tac.lexer.impl.RecognizingAviMessageTokenLexer;
import fi.fmi.avi.converter.tac.lexer.impl.RegexMatchingLexemeVisitor;

/**
 * Created by rinne on 10/02/17.
 */
public class RunwayVisualRange extends RegexMatchingLexemeVisitor {

    public RunwayVisualRange(final Priority prio) {
        super("^R([0-9]{2}[LRC]?)/([MP])?([0-9]{4})(V([MP])?([0-9]{4}))?([UDN])?(FT)?$", prio);
    }

    @Override
	public void visitIfMatched(final Lexeme token, final Matcher match, final ConversionHints hints) {
		String runway = match.group(1);
		RecognizingAviMessageTokenLexer.RelationalOperator belowAboveIndicator = RecognizingAviMessageTokenLexer.RelationalOperator.forCode(match.group(2));
		int visibility = Integer.parseInt(match.group(3));
		token.identify(RUNWAY_VISUAL_RANGE);
		token.setParsedValue(RUNWAY, runway);
		token.setParsedValue(MIN_VALUE, visibility);
		if (belowAboveIndicator != null) {
			token.setParsedValue(RELATIONAL_OPERATOR, belowAboveIndicator);
		}
		String variablePart = match.group(4);
		if (variablePart != null) {
			belowAboveIndicator = RecognizingAviMessageTokenLexer.RelationalOperator.forCode(match.group(5));
			if (belowAboveIndicator != null) {
				token.setParsedValue(RELATIONAL_OPERATOR2, belowAboveIndicator);
			}
			int variableVis = Integer.parseInt(match.group(6));
			token.setParsedValue(MAX_VALUE, variableVis);
		}
		RecognizingAviMessageTokenLexer.TendencyOperator tendencyIndicator = RecognizingAviMessageTokenLexer.TendencyOperator.forCode(match.group(7));
		if (tendencyIndicator != null) {
			token.setParsedValue(TENDENCY_OPERATOR, tendencyIndicator);
		}
		String unit = match.group(8);
		if (unit != null) {
			token.setParsedValue(UNIT, "ft");
		} else {
			token.setParsedValue(UNIT, "m");
		}

	}

	public static class Reconstructor extends FactoryBasedReconstructor {

		@Override
		public <T extends AviationWeatherMessage> Lexeme getAsLexeme(T msg, Class<T> clz, ConversionHints hints, Object... specifier)
				throws SerializingException {
			Lexeme retval = null;

			fi.fmi.avi.model.metar.RunwayVisualRange rvr = getAs(specifier, fi.fmi.avi.model.metar.RunwayVisualRange.class);
			if (rvr != null) {
				StringBuilder builder = new StringBuilder();
				RunwayDirection rwd = rvr.getRunwayDirection();
				if (rwd == null) {
					throw new SerializingException("Runway direction cannot be null for RunwayVisualRange");
				}
				builder.append("R");
				builder.append(rwd.getDesignator());
				builder.append("/");

				NumericMeasure meanRvr = rvr.getMeanRVR();
				String appendUnit = null;
				if (meanRvr != null) {
					RelationalOperator operator = rvr.getMeanRVROperator();

					appendUnit = appendVisibility(meanRvr, operator, builder);

				} else {
					NumericMeasure min = rvr.getVaryingRVRMinimum();
					NumericMeasure max = rvr.getVaryingRVRMaximum();

					if (max == null || max.getValue() == null) {
						throw new SerializingException("Cannot tokenize RunwayVisualRange with varying RVR, but missing max RVR");
					}
					if (min == null || min.getValue() == null) {
						throw new SerializingException("Cannot tokenize RunwayVisualRange with varying RVR, but missing min RVR");
					}

					if (!min.getUom().equals(max.getUom())) {
						throw new SerializingException(
								"Cannot tokenize RunwayVisualRange with inconsistent unit of measure for varying RVR: '" + min.getUom() + "' for min, '"
										+ max.getUom() + "' for max");
					}

					appendUnit = appendVisibility(min, rvr.getVaryingRVRMinimumOperator(), builder);
					builder.append("V");
					appendUnit = appendVisibility(max, rvr.getVaryingRVRMaximumOperator(), builder);
				}

				if (rvr.getPastTendency() != null) {
					switch (rvr.getPastTendency()) {
						case DOWNWARD:
							builder.append("D");
							break;

						case UPWARD:
							builder.append("U");
							break;

						case NO_CHANGE:
							builder.append("N");
							break;
					}
				}

				if (appendUnit != null) {
					builder.append(appendUnit);
				}

				retval = this.createLexeme(builder.toString(), Identity.RUNWAY_VISUAL_RANGE);
			}

			return retval;
		}

		private String appendVisibility(NumericMeasure meanRvr, RelationalOperator operator, StringBuilder builder) throws SerializingException {
			String appendUnit = null;
			if (operator != null) {
				switch (operator) {
					case ABOVE:
						builder.append("P");
						break;
					case BELOW:
						builder.append("M");
						break;
				}
			}

			Double value = meanRvr.getValue();
			if (value == null) {
				throw new SerializingException("Missing value for RunwayVisualRange.meanRVR");
			}

			builder.append(String.format("%04d", value.intValue()));
			if ("ft".equals(meanRvr.getUom())) {
				appendUnit = "FT";
			} else if (!"m".equals(meanRvr.getUom())) {
				throw new SerializingException("Unknown unit of measure '" + meanRvr.getUom() + "' for RunwayVisualRange, allowed are 'm' and 'ft'");
			}
			return appendUnit;
		}
	}
}
