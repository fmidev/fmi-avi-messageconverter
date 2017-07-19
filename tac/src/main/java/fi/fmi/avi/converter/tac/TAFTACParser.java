package fi.fmi.avi.converter.tac;

import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity;

import java.util.ArrayList;
import java.util.List;

import fi.fmi.avi.converter.ConversionIssue;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.converter.tac.lexer.AviMessageLexer;
import fi.fmi.avi.converter.tac.lexer.Lexeme;
import fi.fmi.avi.converter.tac.lexer.LexemeSequence;
import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.impl.CloudForecastImpl;
import fi.fmi.avi.model.impl.NumericMeasureImpl;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFAirTemperatureForecast;
import fi.fmi.avi.model.taf.TAFBaseForecast;
import fi.fmi.avi.model.taf.TAFChangeForecast;
import fi.fmi.avi.model.taf.TAFForecast;
import fi.fmi.avi.model.taf.TAFSurfaceWind;
import fi.fmi.avi.model.taf.impl.TAFAirTemperatureForecastImpl;
import fi.fmi.avi.model.taf.impl.TAFBaseForecastImpl;
import fi.fmi.avi.model.taf.impl.TAFChangeForecastImpl;
import fi.fmi.avi.model.taf.impl.TAFImpl;
import fi.fmi.avi.model.taf.impl.TAFSurfaceWindImpl;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.tac.lexer.impl.RecognizingAviMessageTokenLexer;
import fi.fmi.avi.converter.tac.lexer.impl.token.CloudLayer;
import fi.fmi.avi.converter.tac.lexer.impl.token.ForecastChangeIndicator;
import fi.fmi.avi.converter.tac.lexer.impl.token.MetricHorizontalVisibility;
import fi.fmi.avi.converter.tac.lexer.impl.token.SurfaceWind;
import fi.fmi.avi.converter.tac.lexer.impl.token.CloudLayer.CloudCover;

/**
 *
 * @author Ilkka Rinne / Spatineo Oy 2017
 */
public class TAFTACParser extends AbstractTACParser<TAF> {

    private static Identity[] zeroOrOneAllowed = { Identity.AERODROME_DESIGNATOR, Identity.ISSUE_TIME, Identity.VALID_TIME, Identity.CORRECTION, Identity.AMENDMENT, Identity.CANCELLATION, Identity.NIL, Identity.MIN_TEMPERATURE,
            Identity.MAX_TEMPERATURE, Identity.REMARKS_START };

    private AviMessageLexer lexer;

    public void setTACLexer(final AviMessageLexer lexer) {
        this.lexer = lexer;
    }

    @Override
    public ConversionResult<TAF> convertMessage(final String input, final ConversionHints hints) {
        ConversionResult<TAF> retval = new ConversionResult<>();
        LexemeSequence lexed = null;
        if (this.lexer == null) {
            throw new IllegalStateException("TAC lexer not set");
        }
        lexed = this.lexer.lexMessage(input, hints);
        if (Identity.TAF_START != lexed.getFirstLexeme().getIdentityIfAcceptable()) {
            retval.addIssue(new ConversionIssue(ConversionIssue.Type.SYNTAX_ERROR, "The input message is not recognized as TAF"));
            return retval;
        }

        if (endsInEndToken(lexed, hints)) {
            retval.addIssue(checkZeroOrOne(lexed, zeroOrOneAllowed));
            TAF taf = new TAFImpl();

            retval.setConvertedMessage(taf);
            Identity[] stopAt = { Identity.AERODROME_DESIGNATOR, Identity.ISSUE_TIME, Identity.NIL, Identity.VALID_TIME, Identity.CANCELLATION, Identity.SURFACE_WIND, Identity.HORIZONTAL_VISIBILITY, Identity.WEATHER, Identity.CLOUD, Identity.CAVOK,
                    Identity.MIN_TEMPERATURE, Identity.MAX_TEMPERATURE, Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };

            findNext(Identity.CORRECTION, lexed.getFirstLexeme(), stopAt, (match) -> taf.setStatus(AviationCodeListUser.TAFStatus.CORRECTION));

            findNext(Identity.AMENDMENT, lexed.getFirstLexeme(), stopAt, (match) -> {
                TAF.TAFStatus status = taf.getStatus();
                if (status != null) {
                    retval.addIssue(new ConversionIssue(ConversionIssue.Type.SYNTAX_ERROR,
                            "TAF cannot be both " + TAF.TAFStatus.AMENDMENT + " and " + status + " at " + "the same time"));
                } else {
                    taf.setStatus(AviationCodeListUser.TAFStatus.AMENDMENT);
                }
            });

            if (taf.getStatus() == null) {
                taf.setStatus(AviationCodeListUser.TAFStatus.NORMAL);
            }

            stopAt = new Identity[] { Identity.ISSUE_TIME, Identity.NIL, Identity.VALID_TIME, Identity.CANCELLATION, Identity.SURFACE_WIND, Identity.HORIZONTAL_VISIBILITY, Identity.WEATHER, Identity.CLOUD, Identity.CAVOK, Identity.MIN_TEMPERATURE,
                    Identity.MAX_TEMPERATURE, Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };

            findNext(Identity.AERODROME_DESIGNATOR, lexed.getFirstLexeme(), stopAt,
                    (match) -> {
            			Aerodrome ad = new Aerodrome(match.getParsedValue(Lexeme.ParsedValueName.VALUE, String.class));
                    	taf.setAerodrome(ad);
                    }, 
                    () -> {
                        retval.addIssue(new ConversionIssue(ConversionIssue.Type.SYNTAX_ERROR, "Aerodrome designator not given in " + input));
                    });

            retval.addIssue(updateTAFIssueTime(taf, lexed, hints));
            stopAt = new Identity[] { Identity.VALID_TIME, Identity.CANCELLATION, Identity.SURFACE_WIND, Identity.HORIZONTAL_VISIBILITY, Identity.WEATHER, Identity.CLOUD, Identity.CAVOK, Identity.MIN_TEMPERATURE, Identity.MAX_TEMPERATURE,
                    Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };

            findNext(Identity.NIL, lexed.getFirstLexeme(), stopAt, (match) -> {
                taf.setStatus(AviationCodeListUser.TAFStatus.MISSING);
                if (match.getNext() != null) {
                    Identity nextTokenId = match.getNext().getIdentityIfAcceptable();
                    if (Identity.END_TOKEN != nextTokenId && Identity.REMARKS_START != nextTokenId) {
                        retval.addIssue(new ConversionIssue(ConversionIssue.Type.LOGICAL_ERROR, "Missing TAF message contains extra tokens after NIL: " + input));
                    }
                }
            });

            if (AviationCodeListUser.TAFStatus.MISSING == taf.getStatus()) {
                return retval;
            }

            retval.addIssue(updateTAFValidTime(taf, lexed, hints));

            stopAt = new Identity[] { Identity.SURFACE_WIND, Identity.HORIZONTAL_VISIBILITY, Identity.WEATHER, Identity.CLOUD, Identity.CAVOK, Identity.MIN_TEMPERATURE, Identity.MAX_TEMPERATURE, Identity.FORECAST_CHANGE_INDICATOR,
                    Identity.REMARKS_START };
            findNext(Identity.CANCELLATION, lexed.getFirstLexeme(), stopAt, (match) -> {
                taf.setStatus(AviationCodeListUser.TAFStatus.CANCELLATION);
                if (match.getNext() != null) {
                    Identity nextTokenId = match.getNext().getIdentityIfAcceptable();
                    if (Identity.END_TOKEN != nextTokenId && Identity.REMARKS_START != nextTokenId) {
                        retval.addIssue(new ConversionIssue(ConversionIssue.Type.LOGICAL_ERROR, "Cancelled TAF message contains extra tokens after CNL: " + input));
                    }
                }
            });
            
            updateRemarks(retval, lexed, hints);

            if (AviationCodeListUser.TAFStatus.CANCELLATION == taf.getStatus()) {
                return retval;
            }

            retval.addIssue(updateBaseForecast(taf, lexed, hints));
            retval.addIssue(updateChangeForecasts(taf, lexed, hints));

        } else {
            retval.addIssue(new ConversionIssue(ConversionIssue.Type.SYNTAX_ERROR, "Message does not end in end token"));
        }
        return retval;
    }

    private List<ConversionIssue> updateTAFIssueTime(final TAF fct, final LexemeSequence lexed, final ConversionHints hints) {
        List<ConversionIssue> retval = new ArrayList<>();
        Identity[] before = new Identity[] { Identity.NIL, Identity.VALID_TIME, Identity.CANCELLATION, Identity.SURFACE_WIND, Identity.HORIZONTAL_VISIBILITY, Identity.WEATHER, Identity.CLOUD, Identity.CAVOK, Identity.MIN_TEMPERATURE,
                Identity.MAX_TEMPERATURE, Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
        retval.addAll(updateIssueTime(fct, lexed, before, hints));
        return retval;
    }

    private List<ConversionIssue> updateTAFValidTime(final TAF fct, final LexemeSequence lexed, final ConversionHints hints) {
        List<ConversionIssue> retval = new ArrayList<>();
        Identity[] before = new Identity[] { Identity.CANCELLATION, Identity.SURFACE_WIND, Identity.HORIZONTAL_VISIBILITY, Identity.WEATHER, Identity.CLOUD, Identity.CAVOK, Identity.MIN_TEMPERATURE, Identity.MAX_TEMPERATURE,
                Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
        findNext(Identity.VALID_TIME, lexed.getFirstLexeme(), before, (match) -> {
            Integer startDay = match.getParsedValue(Lexeme.ParsedValueName.DAY1, Integer.class);
            Integer endDay = match.getParsedValue(Lexeme.ParsedValueName.DAY2, Integer.class);
            Integer startHour = match.getParsedValue(Lexeme.ParsedValueName.HOUR1, Integer.class);
            Integer endHour = match.getParsedValue(Lexeme.ParsedValueName.HOUR2, Integer.class);
            if (startDay != null && startHour != null && endHour != null) {
            	if (endDay != null) {
            		fct.setPartialValidityTimePeriod(startDay, endDay, startHour, endHour);
            	} else {
            		fct.setPartialValidityTimePeriod(startDay, startHour, endHour);
            	}
            } else {
                retval.add(new ConversionIssue(ConversionIssue.Type.MISSING_DATA, "Must have at least startDay, startHour and endHour of validity " + match.getTACToken()));
            }
        }, () -> {
            retval.add(new ConversionIssue(ConversionIssue.Type.MISSING_DATA, "Missing validity"));
        });
        return retval;
    }

    private List<ConversionIssue> updateBaseForecast(final TAF fct, final LexemeSequence lexed, final ConversionHints hints) {
        List<ConversionIssue> retval = new ArrayList<>();
        TAFBaseForecast baseFct = new TAFBaseForecastImpl();

        Identity[] before = { Identity.CAVOK, Identity.HORIZONTAL_VISIBILITY, Identity.WEATHER, Identity.CLOUD, Identity.MIN_TEMPERATURE, Identity.MAX_TEMPERATURE, Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
        retval.addAll(updateForecastSurfaceWind(baseFct, lexed.getFirstLexeme(), before, hints));

        before = new Identity[] { Identity.HORIZONTAL_VISIBILITY, Identity.WEATHER, Identity.CLOUD, Identity.MIN_TEMPERATURE, Identity.MAX_TEMPERATURE, Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
        findNext(Identity.CAVOK, lexed.getFirstLexeme(), before, (match) -> baseFct.setCeilingAndVisibilityOk(true));

        before = new Identity[] { Identity.WEATHER, Identity.CLOUD, Identity.MIN_TEMPERATURE, Identity.MAX_TEMPERATURE, Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
        retval.addAll(updateVisibility(baseFct, lexed.getFirstLexeme(), before, hints));

        before = new Identity[] { Identity.CLOUD, Identity.MIN_TEMPERATURE, Identity.MAX_TEMPERATURE, Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
        retval.addAll(updateWeather(baseFct, lexed.getFirstLexeme(), before, hints));

        before = new Identity[] { Identity.MIN_TEMPERATURE, Identity.MAX_TEMPERATURE, Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
        retval.addAll(updateClouds(baseFct, lexed.getFirstLexeme(), before, hints));

        retval.addAll(updateTemperatures(baseFct, lexed.getFirstLexeme(), hints));

        fct.setBaseForecast(baseFct);
        return retval;
    }

    private List<ConversionIssue> updateTemperatures(final TAFBaseForecast baseFct, final Lexeme from, final ConversionHints hints) {
        List<ConversionIssue> retval = new ArrayList<>();
        List<TAFAirTemperatureForecast> temps = new ArrayList<>();
        TAFAirTemperatureForecast airTemperatureForecast;
        Identity[] stopAt = { Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
        Lexeme maxTempToken = findNext(Identity.MAX_TEMPERATURE, from, stopAt);

        if (maxTempToken != null) {
        	Lexeme minBeforeFirstMax = findNext(Identity.MIN_TEMPERATURE, from, new Identity[] { maxTempToken.getIdentity() });
        	if (minBeforeFirstMax != null) {
        		retval.add(new ConversionIssue(ConversionIssue.Type.SYNTAX_ERROR,
        				"Minimum temperature given before maximum temperature: " + minBeforeFirstMax.getTACToken()));
        	}
        }
        
        Lexeme minTempToken;
        while (maxTempToken != null) {
            minTempToken = findNext(Identity.MIN_TEMPERATURE, maxTempToken, stopAt);
            if (minTempToken != null) {
                airTemperatureForecast = new TAFAirTemperatureForecastImpl();
                Integer day = minTempToken.getParsedValue(Lexeme.ParsedValueName.DAY1, Integer.class);
                Integer hour = minTempToken.getParsedValue(Lexeme.ParsedValueName.HOUR1, Integer.class);
                Integer value = minTempToken.getParsedValue(Lexeme.ParsedValueName.VALUE, Integer.class);

                if (day != null && hour != null) {
                    airTemperatureForecast.setPartialMinTemperatureTime(day, hour);
                } else {
                    retval.add(new ConversionIssue(ConversionIssue.Type.MISSING_DATA,
                            "Missing day of month and/or hour of day for min forecast temperature: " + minTempToken.getTACToken()));
                }

                if (value != null) {
                    airTemperatureForecast.setMinTemperature(new NumericMeasureImpl(value, "degC"));
                } else {
                    retval.add(new ConversionIssue(ConversionIssue.Type.MISSING_DATA, "Missing value for min forecast temperature: " + minTempToken.getTACToken()));
                }

                day = maxTempToken.getParsedValue(Lexeme.ParsedValueName.DAY1, Integer.class);
                hour = maxTempToken.getParsedValue(Lexeme.ParsedValueName.HOUR1, Integer.class);
                value = maxTempToken.getParsedValue(Lexeme.ParsedValueName.VALUE, Integer.class);

                if (day != null && hour != null) {
                    airTemperatureForecast.setPartialMaxTemperatureTime(day, hour);
                } else {
                    retval.add(new ConversionIssue(ConversionIssue.Type.MISSING_DATA,
                            "Missing day of month and/or hour of day for max forecast temperature: " + maxTempToken.getTACToken()));
                }

                if (value != null) {
                    airTemperatureForecast.setMaxTemperature(new NumericMeasureImpl(value, "degC"));
                } else {
                    retval.add(new ConversionIssue(ConversionIssue.Type.MISSING_DATA, "Missing value for max forecast temperature: " + maxTempToken.getTACToken()));
                }
                temps.add(airTemperatureForecast);
                maxTempToken = findNext(Identity.MAX_TEMPERATURE, minTempToken, stopAt);
            } else {
            	maxTempToken = null;
            }
        }
        if (!temps.isEmpty()) {
            baseFct.setTemperatures(temps);
        }
        return retval;
    }

    private List<ConversionIssue> updateChangeForecasts(final TAF fct, final LexemeSequence lexed, final ConversionHints hints) {
        List<ConversionIssue> retval = new ArrayList<>();
        Identity[] stopAt = { Identity.REMARKS_START };
        findNext(Identity.FORECAST_CHANGE_INDICATOR, lexed.getFirstLexeme(), stopAt, (match) -> {
            List<TAFChangeForecast> changeForecasts = new ArrayList<>();
            while (match != null) {

                //PROB30 [TEMPO] or PROB40 [TEMPO] or BECMG or TEMPO or FM
                ForecastChangeIndicator.ForecastChangeIndicatorType type = match.getParsedValue(Lexeme.ParsedValueName.TYPE,
                        ForecastChangeIndicator.ForecastChangeIndicatorType.class);
                if (match.hasNext()) {
                    Lexeme next = match.getNext();
                    if (Identity.REMARKS_START != next.getIdentityIfAcceptable() && Identity.END_TOKEN != next.getIdentityIfAcceptable()) {
                        TAFChangeForecast changeFct = new TAFChangeForecastImpl();
                        switch (type) {
                            case TEMPORARY_FLUCTUATIONS:
                                changeFct.setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.TEMPORARY_FLUCTUATIONS);
                                updateChangeForecastContents(changeFct, type, match, hints);
                                break;
                            case BECOMING:
                                changeFct.setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.BECOMING);
                                updateChangeForecastContents(changeFct, type, match, hints);
                                break;
                            case FROM:
                                changeFct.setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.FROM);
                                Integer day = match.getParsedValue(Lexeme.ParsedValueName.DAY1, Integer.class);
                                Integer hour = match.getParsedValue(Lexeme.ParsedValueName.HOUR1, Integer.class);
                                Integer minute = match.getParsedValue(Lexeme.ParsedValueName.MINUTE1, Integer.class);
                                if (hour != null && minute != null) {
                                	if (day != null) {
                                		changeFct.setPartialValidityStartTime(day, hour, minute);
                                	} else {
                                		changeFct.setPartialValidityStartTime(-1, hour, minute);
                                	}
                                } else {
                                    retval.add(new ConversionIssue(ConversionIssue.Type.MISSING_DATA,
                                            "Missing validity start hour or minute in " + match.getTACToken()));
                                }
                                updateChangeForecastContents(changeFct, type, match, hints);
                                break;
                            case WITH_40_PCT_PROBABILITY:
                                changeFct.setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.PROBABILITY_40);
                                updateChangeForecastContents(changeFct, type, match, hints);
                                break;
                            case WITH_30_PCT_PROBABILITY:
                                changeFct.setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.PROBABILITY_30);
                                updateChangeForecastContents(changeFct, type, match, hints);
                                break;
                            case TEMPO_WITH_30_PCT_PROBABILITY:
                                changeFct.setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.PROBABILITY_30_TEMPORARY_FLUCTUATIONS);
                                updateChangeForecastContents(changeFct, type, match, hints);
                                break;
                            case TEMPO_WITH_40_PCT_PROBABILITY:
                                changeFct.setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.PROBABILITY_40_TEMPORARY_FLUCTUATIONS);
                                updateChangeForecastContents(changeFct, type, match, hints);
                                break;
                            case AT:
                            case UNTIL:
                                retval.add(new ConversionIssue(ConversionIssue.Type.SYNTAX_ERROR, "Change group " + type + " is not allowed in TAF"));
                                break;
                            default:
                                retval.add(new ConversionIssue(ConversionIssue.Type.SYNTAX_ERROR, "Unknown change group " + type));
                                break;
                        }
                        changeForecasts.add(changeFct);
                    } else {
                        retval.add(new ConversionIssue(ConversionIssue.Type.MISSING_DATA, "Missing change group content"));
                    }
                } else {
                    retval.add(new ConversionIssue(ConversionIssue.Type.MISSING_DATA, "Missing change group content"));
                }
                match = findNext(Identity.FORECAST_CHANGE_INDICATOR, match, stopAt);
            }
            if (!changeForecasts.isEmpty()) {
                fct.setChangeForecasts(changeForecasts);
            }
        });
        return retval;
    }

    private List<ConversionIssue> updateChangeForecastContents(final TAFChangeForecast fct, final ForecastChangeIndicator.ForecastChangeIndicatorType type,
            final Lexeme from, final ConversionHints hints) {
        List<ConversionIssue> retval = new ArrayList<>();
        Identity[] before = { Identity.CAVOK, Identity.HORIZONTAL_VISIBILITY, Identity.WEATHER, Identity.CLOUD, Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
        if (ForecastChangeIndicator.ForecastChangeIndicatorType.FROM != type) {
            Lexeme timeGroup = findNext(Identity.CHANGE_FORECAST_TIME_GROUP, from, before);
            if (timeGroup != null) {
                Integer startDay = timeGroup.getParsedValue(Lexeme.ParsedValueName.DAY1, Integer.class);
                Integer endDay = timeGroup.getParsedValue(Lexeme.ParsedValueName.DAY2, Integer.class);
                Integer startHour = timeGroup.getParsedValue(Lexeme.ParsedValueName.HOUR1, Integer.class);
                Integer endHour = timeGroup.getParsedValue(Lexeme.ParsedValueName.HOUR2, Integer.class);
                if (startHour != null && endHour != null) {
                	if (startDay != null && endDay != null) {
                		fct.setPartialValidityTimePeriod(startDay, endDay, startHour, endHour);
                	} else {
                		fct.setPartialValidityTimePeriod(startHour, endHour);
                	}
                } else {
                    retval.add(new ConversionIssue(ConversionIssue.Type.MISSING_DATA,
                            "Missing validity day, hour or minute for change group in " + timeGroup.getTACToken()));
                }
            } else {
                retval.add(new ConversionIssue(ConversionIssue.Type.MISSING_DATA, "Missing validity time for change group after " + from.getTACToken()));
            }
        }

        before = new Identity[] { Identity.CAVOK, Identity.HORIZONTAL_VISIBILITY, Identity.WEATHER, Identity.CLOUD, Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
        retval.addAll(updateForecastSurfaceWind(fct, from, before, hints));

        before = new Identity[] { Identity.HORIZONTAL_VISIBILITY, Identity.WEATHER, Identity.CLOUD, Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
        findNext(Identity.CAVOK, from, before, (match) -> fct.setCeilingAndVisibilityOk(true));

        before = new Identity[] { Identity.WEATHER, Identity.CLOUD, Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
        retval.addAll(updateVisibility(fct, from, before, hints));

        before = new Identity[] { Identity.CLOUD, Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
        retval.addAll(updateWeather(fct, from, before, hints));

        findNext(Identity.NO_SIGNIFICANT_WEATHER, from, before, (match) -> {
            if (fct.getForecastWeather() != null && !fct.getForecastWeather().isEmpty()) {
                retval.add(new ConversionIssue(ConversionIssue.Type.LOGICAL_ERROR, "Cannot have both NSW and weather in the same change forecast"));
            } else {
                fct.setNoSignificantWeather(true);
            }
        });

        before = new Identity[] { Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
        retval.addAll(updateClouds(fct, from, before, hints));

        return retval;
    }

    private List<ConversionIssue> updateForecastSurfaceWind(final TAFForecast fct, final Lexeme from, final Identity[] before, final ConversionHints hints) {
        List<ConversionIssue> retval = new ArrayList<>();
        findNext(Identity.SURFACE_WIND, from, before, (match) -> {
            TAFSurfaceWind wind = new TAFSurfaceWindImpl();
            Object direction = match.getParsedValue(Lexeme.ParsedValueName.DIRECTION, Object.class);
            Integer meanSpeed = match.getParsedValue(Lexeme.ParsedValueName.MEAN_VALUE, Integer.class);
            Integer gustSpeed = match.getParsedValue(Lexeme.ParsedValueName.MAX_VALUE, Integer.class);
            String unit = match.getParsedValue(Lexeme.ParsedValueName.UNIT, String.class);

            if (direction == SurfaceWind.WindDirection.VARIABLE) {
                wind.setVariableDirection(true);
            } else if (direction instanceof Integer) {
                wind.setMeanWindDirection(new NumericMeasureImpl((Integer) direction, "deg"));
            } else {
                retval.add(new ConversionIssue(ConversionIssue.Type.MISSING_DATA, "Surface wind direction is missing: " + match.getTACToken()));
            }

            if (meanSpeed != null) {
                wind.setMeanWindSpeed(new NumericMeasureImpl(meanSpeed, unit));
            } else {
                retval.add(new ConversionIssue(ConversionIssue.Type.MISSING_DATA, "Surface wind mean speed is missing: " + match.getTACToken()));
            }

            if (gustSpeed != null) {
                wind.setWindGust(new NumericMeasureImpl(gustSpeed, unit));
            }
            fct.setSurfaceWind(wind);
        }, () -> {
            if (fct instanceof TAFBaseForecast) {
                retval.add(new ConversionIssue(ConversionIssue.Type.MISSING_DATA, "Surface wind is missing from TAF base forecast"));
            }
        });
        return retval;
    }

    private List<ConversionIssue> updateVisibility(final TAFForecast fct, final Lexeme from, final Identity[] before, final ConversionHints hints) {
        List<ConversionIssue> retval = new ArrayList<>();
        findNext(Identity.HORIZONTAL_VISIBILITY, from, before, (match) -> {
            Double distance = match.getParsedValue(Lexeme.ParsedValueName.VALUE, Double.class);
            String unit = match.getParsedValue(Lexeme.ParsedValueName.UNIT, String.class);
            RecognizingAviMessageTokenLexer.RelationalOperator distanceOperator = match.getParsedValue(Lexeme.ParsedValueName.RELATIONAL_OPERATOR,
                    RecognizingAviMessageTokenLexer.RelationalOperator.class);
            MetricHorizontalVisibility.DirectionValue direction = match.getParsedValue(Lexeme.ParsedValueName.DIRECTION,
                    MetricHorizontalVisibility.DirectionValue.class);
            if (direction != null) {
                retval.add(new ConversionIssue(ConversionIssue.Type.SYNTAX_ERROR, "Directional horizontal visibility not allowed in TAF: " + match.getTACToken()));
            }
            if (distance != null && unit != null) {
                fct.setPrevailingVisibility(new NumericMeasureImpl(distance, unit));
            } else {
                retval.add(new ConversionIssue(ConversionIssue.Type.MISSING_DATA, "Missing visibility value or unit: " + match.getTACToken()));
            }
            if (distanceOperator != null) {
                if (RecognizingAviMessageTokenLexer.RelationalOperator.LESS_THAN == distanceOperator) {
                    fct.setPrevailingVisibilityOperator(AviationCodeListUser.RelationalOperator.BELOW);
                } else {
                    fct.setPrevailingVisibilityOperator(AviationCodeListUser.RelationalOperator.ABOVE);
                }
            }
        }, () -> {
            if (fct instanceof TAFBaseForecast) {
                if (!fct.isCeilingAndVisibilityOk()) {
                    retval.add(new ConversionIssue(ConversionIssue.Type.MISSING_DATA, "Visibility or CAVOK is missing from TAF base forecast"));
                }
            }
        });
        return retval;
    }

    private List<ConversionIssue> updateWeather(final TAFForecast fct, final Lexeme from, final Identity[] before, final ConversionHints hints) {
        List<ConversionIssue> retval = new ArrayList<>();
        findNext(Identity.WEATHER, from, before, (match) -> {
            if (match != null) {
                List<fi.fmi.avi.model.Weather> weather = new ArrayList<>();
                retval.addAll(appendWeatherCodes(match, weather, before, hints));
                if (!weather.isEmpty()) {
                    fct.setForecastWeather(weather);
                }
            }
        });
        return retval;
    }

    private List<ConversionIssue> updateClouds(final TAFForecast fct, final Lexeme from, final Identity[] before, final ConversionHints hints) {
        List<ConversionIssue> retval = new ArrayList<>();
        findNext(Identity.CLOUD, from, before, (match) -> {
            CloudForecast cloud = new CloudForecastImpl();
            List<fi.fmi.avi.model.CloudLayer> layers = new ArrayList<>();
            while (match != null) {
                CloudLayer.CloudCover cover = match.getParsedValue(Lexeme.ParsedValueName.COVER, CloudLayer.CloudCover.class);
                Object value = match.getParsedValue(Lexeme.ParsedValueName.VALUE, Object.class);
                String unit = match.getParsedValue(Lexeme.ParsedValueName.UNIT, String.class);
                if (CloudLayer.CloudCover.SKY_OBSCURED == cover) {
                    Integer height;
                    if (value instanceof Integer) {
                        height = (Integer) value;
                        if ("hft".equals(unit)) {
                            height = height * 100;
                            unit = "ft";
                        }
                    } else {
                        retval.add(new ConversionIssue(ConversionIssue.Type.SYNTAX_ERROR, "Cloud layer height is not an integer in " + match.getTACToken()));
                        height = null;
                    }
                    cloud.setVerticalVisibility(new NumericMeasureImpl(height, unit));
                } else if (CloudCover.NO_SIG_CLOUDS == cover) {
                	cloud.setNoSignificantCloud(true);
                } else {
                    fi.fmi.avi.model.CloudLayer layer = getCloudLayer(match);
                    if (layer != null) {
                        layers.add(layer);
                    } else {
                        retval.add(new ConversionIssue(ConversionIssue.Type.SYNTAX_ERROR, "Could not parse token " + match.getTACToken() + " as cloud " + "layer"));
                    }
                }

                match = findNext(Identity.CLOUD, match, before);
            }
            if (!layers.isEmpty()) {
                cloud.setLayers(layers);
            }
            fct.setCloud(cloud);
        }, () -> {
            if (fct instanceof TAFBaseForecast) {
                if (!fct.isCeilingAndVisibilityOk()) {
                    retval.add(new ConversionIssue(ConversionIssue.Type.MISSING_DATA, "Cloud or CAVOK is missing from TAF base forecast"));
                }
            }
        });
        return retval;
    }

}
