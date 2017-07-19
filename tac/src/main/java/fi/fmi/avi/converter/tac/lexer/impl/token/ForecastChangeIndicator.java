package fi.fmi.avi.converter.tac.lexer.impl.token;

import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.FORECAST_CHANGE_INDICATOR;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.DAY1;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.HOUR1;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.MINUTE1;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.TYPE;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.metar.TrendForecast;
import fi.fmi.avi.model.metar.TrendTimeGroups;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFChangeForecast;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.tac.lexer.SerializingException;
import fi.fmi.avi.converter.tac.lexer.Lexeme;
import fi.fmi.avi.converter.tac.lexer.impl.FactoryBasedReconstructor;

/**
 * Created by rinne on 10/02/17.
 */
public class ForecastChangeIndicator extends TimeHandlingRegex {

	public enum ForecastChangeIndicatorType {
        TEMPORARY_FLUCTUATIONS("TEMPO"),
        BECOMING("BECMG"),
        WITH_40_PCT_PROBABILITY("PROB40"),
        WITH_30_PCT_PROBABILITY("PROB30"),
        TEMPO_WITH_40_PCT_PROBABILITY("PROB40 TEMPO"),
        TEMPO_WITH_30_PCT_PROBABILITY("PROB30 TEMPO"),
        AT("AT"),
        FROM("FM"), UNTIL("TL"), NO_SIGNIFICANT_CHANGES("NOSIG");

        private String code;

        ForecastChangeIndicatorType(final String code) {
            this.code = code;
        }

        public static ForecastChangeIndicatorType forCode(final String code) {
            for (ForecastChangeIndicatorType w : values()) {
                if (w.code.equals(code)) {
                    return w;
                }
            }
            return null;
        }

    }

    public ForecastChangeIndicator(final Priority prio) {
        super("^(NOSIG|TEMPO|BECMG|PROB40|PROB30|PROB30 TEMPO|PROB40 TEMPO)|((AT|FM|TL)([0-9]{2})?([0-9]{2})([0-9]{2}))$",
                prio);
    }

    @Override
    public void visitIfMatched(final Lexeme token, final Matcher match, final ConversionHints hints) {
        ForecastChangeIndicatorType indicator;
        if (match.group(1) != null) {
        	token.identify(FORECAST_CHANGE_INDICATOR);
            indicator = ForecastChangeIndicatorType.forCode(match.group(1));
            token.setParsedValue(TYPE, indicator);
        } else {
            indicator = ForecastChangeIndicatorType.forCode(match.group(3));
            int day = -1;
            if (match.group(4) != null) {
                day = Integer.parseInt(match.group(4));
            }
            int hour = Integer.parseInt(match.group(5));
            int minute = Integer.parseInt(match.group(6));
            if (timeOkDayHourMinute(day, hour, minute)) {
            	token.identify(FORECAST_CHANGE_INDICATOR);
                if (day > -1) {
                    token.setParsedValue(DAY1, day);
                }
                token.setParsedValue(HOUR1, hour);
                token.setParsedValue(MINUTE1, minute);
                token.setParsedValue(TYPE, indicator);
            } else {
                token.identify(FORECAST_CHANGE_INDICATOR, Lexeme.Status.SYNTAX_ERROR, "Invalid time");
            }
        }
    }
    

    public static class Reconstructor extends FactoryBasedReconstructor {

		@Override
        public <T extends AviationWeatherMessage> List<Lexeme> getAsLexemes(T msg, Class<T> clz, ConversionHints hints, Object... specifier)
                throws SerializingException {
            List<Lexeme> retval = new ArrayList<>();

            if (msg instanceof TAF) {
                TAFChangeForecast changeForecast = getAs(specifier, TAFChangeForecast.class);

                if (changeForecast != null) {
                    switch (changeForecast.getChangeIndicator()) {
                        case BECOMING:
                            retval.add(this.createLexeme("BECMG", FORECAST_CHANGE_INDICATOR));
                            break;
                        case TEMPORARY_FLUCTUATIONS:
                            retval.add(this.createLexeme("TEMPO", FORECAST_CHANGE_INDICATOR));
                            break;
                        case PROBABILITY_30:
                            retval.add(this.createLexeme("PROB30", FORECAST_CHANGE_INDICATOR));
                            break;
                        case PROBABILITY_40:
                            retval.add(this.createLexeme("PROB40", FORECAST_CHANGE_INDICATOR));
                            break;
                        case PROBABILITY_30_TEMPORARY_FLUCTUATIONS:
                            retval.add(this.createLexeme("PROB30", FORECAST_CHANGE_INDICATOR));
                            retval.add(this.createLexeme("TEMPO", FORECAST_CHANGE_INDICATOR));
                            break;
                        case PROBABILITY_40_TEMPORARY_FLUCTUATIONS:
                            retval.add(this.createLexeme("PROB40", FORECAST_CHANGE_INDICATOR));
                            retval.add(this.createLexeme("TEMPO", FORECAST_CHANGE_INDICATOR));
                            break;
                        case FROM:
                            retval.add(createLexeme_From(changeForecast));
                            break;
                    }
                }
            } else if (msg instanceof METAR) {
                TrendForecast trend = getAs(specifier, TrendForecast.class);
                if (trend != null) {
                    switch (trend.getChangeIndicator()) {
                        case BECOMING: {
                            retval.add(this.createLexeme("BECMG", FORECAST_CHANGE_INDICATOR));
                            List<Lexeme> periodOfChange = createTrendTimeChangePeriods(trend.getTimeGroups());
                            if (periodOfChange.isEmpty()) {
                                throw new SerializingException("No period of time for the trend of type BECOMING");
                            }
                            retval.addAll(periodOfChange);
                            break;
                        }
                        case TEMPORARY_FLUCTUATIONS: {
                            retval.add(this.createLexeme("TEMPO", FORECAST_CHANGE_INDICATOR));
                            List<Lexeme> periodOfChange = createTrendTimeChangePeriods(trend.getTimeGroups());
                            if (!periodOfChange.isEmpty()) {
                                retval.addAll(periodOfChange);
                            }
                            break;
                        }
                        case NO_SIGNIFICANT_CHANGES:
                            retval.add(this.createLexeme("NOSIG", FORECAST_CHANGE_INDICATOR));
                            break;
                    }
                }
            }

            return retval;
		}

		private Lexeme createLexeme_From(TAFChangeForecast changeForecast) {
			StringBuilder ret = new StringBuilder("FM");
            if (changeForecast.getValidityStartDayOfMonth() > -1) {
                ret.append(String.format("%02d%02d%02d", changeForecast.getValidityStartDayOfMonth(), changeForecast.getValidityStartHour(),
                        changeForecast.getValidityStartMinute()));
            } else {
                ret.append(String.format("%02d%02d", changeForecast.getValidityStartHour(), changeForecast.getValidityStartMinute()));
            }

            return this.createLexeme(ret.toString(), FORECAST_CHANGE_INDICATOR);
        }

        private List<Lexeme> createTrendTimeChangePeriods(final TrendTimeGroups timeGroups) {
            List<Lexeme> retval = new ArrayList<>();
            if (timeGroups != null) {
                if (timeGroups.isSingleInstance()) {
                    if (timeGroups.getPartialStartTime() != null) {
                        StringBuilder ret = new StringBuilder("AT");
                        ret.append(timeGroups.getPartialStartTime());
                        retval.add(this.createLexeme(ret.toString(), FORECAST_CHANGE_INDICATOR));
                    }
                } else {
                    if (timeGroups.getPartialStartTime() != null) {
                        StringBuilder ret = new StringBuilder("FM");
                        ret.append(timeGroups.getPartialStartTime());
                        retval.add(this.createLexeme(ret.toString(), FORECAST_CHANGE_INDICATOR));
                    }
                    if (timeGroups.getPartialEndTime() != null) {
                        StringBuilder ret = new StringBuilder("TL");
                        ret.append(timeGroups.getPartialEndTime());
                        retval.add(this.createLexeme(ret.toString(), FORECAST_CHANGE_INDICATOR));
                    }
                }
            }
            return retval;
        }

    }

}
