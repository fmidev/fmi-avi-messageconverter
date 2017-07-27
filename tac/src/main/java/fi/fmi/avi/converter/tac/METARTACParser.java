package fi.fmi.avi.converter.tac;

import static fi.fmi.avi.model.AviationCodeListUser.TrendForecastChangeIndicator.BECOMING;
import static fi.fmi.avi.model.AviationCodeListUser.TrendForecastChangeIndicator.TEMPORARY_FLUCTUATIONS;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.fmi.avi.converter.ConversionIssue;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.converter.ConversionIssue.Type;
import fi.fmi.avi.converter.tac.lexer.AviMessageLexer;
import fi.fmi.avi.converter.tac.lexer.Lexeme;
import fi.fmi.avi.converter.tac.lexer.LexemeSequence;
import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.AviationCodeListUser.BreakingAction;
import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.RunwayDirection;
import fi.fmi.avi.model.impl.CloudForecastImpl;
import fi.fmi.avi.model.impl.NumericMeasureImpl;
import fi.fmi.avi.model.impl.WeatherImpl;
import fi.fmi.avi.model.metar.HorizontalVisibility;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.metar.ObservedClouds;
import fi.fmi.avi.model.metar.ObservedSurfaceWind;
import fi.fmi.avi.model.metar.RunwayState;
import fi.fmi.avi.model.metar.RunwayVisualRange;
import fi.fmi.avi.model.metar.SeaState;
import fi.fmi.avi.model.metar.TrendForecast;
import fi.fmi.avi.model.metar.TrendForecastSurfaceWind;
import fi.fmi.avi.model.metar.TrendTimeGroups;
import fi.fmi.avi.model.metar.WindShear;
import fi.fmi.avi.model.metar.impl.HorizontalVisibilityImpl;
import fi.fmi.avi.model.metar.impl.METARImpl;
import fi.fmi.avi.model.metar.impl.ObservedCloudsImpl;
import fi.fmi.avi.model.metar.impl.ObservedSurfaceWindImpl;
import fi.fmi.avi.model.metar.impl.RunwayStateImpl;
import fi.fmi.avi.model.metar.impl.RunwayVisualRangeImpl;
import fi.fmi.avi.model.metar.impl.SeaStateImpl;
import fi.fmi.avi.model.metar.impl.TrendForecastImpl;
import fi.fmi.avi.model.metar.impl.TrendForecastSurfaceWindImpl;
import fi.fmi.avi.model.metar.impl.TrendTimeGroupsImpl;
import fi.fmi.avi.model.metar.impl.WindShearImpl;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName;
import fi.fmi.avi.converter.tac.lexer.impl.RecognizingAviMessageTokenLexer;
import fi.fmi.avi.converter.tac.lexer.impl.token.AtmosphericPressureQNH;
import fi.fmi.avi.converter.tac.lexer.impl.token.CloudLayer;
import fi.fmi.avi.converter.tac.lexer.impl.token.ColorCode;
import fi.fmi.avi.converter.tac.lexer.impl.token.ForecastChangeIndicator;
import fi.fmi.avi.converter.tac.lexer.impl.token.MetricHorizontalVisibility;
import fi.fmi.avi.converter.tac.lexer.impl.token.SurfaceWind;
import fi.fmi.avi.converter.tac.lexer.impl.token.Weather;
import fi.fmi.avi.converter.tac.lexer.impl.token.CloudLayer.CloudCover;
import fi.fmi.avi.converter.tac.lexer.impl.token.RunwayState.RunwayStateContamination;
import fi.fmi.avi.converter.tac.lexer.impl.token.RunwayState.RunwayStateDeposit;
import fi.fmi.avi.converter.tac.lexer.impl.token.RunwayState.RunwayStateReportSpecialValue;
import fi.fmi.avi.converter.tac.lexer.impl.token.RunwayState.RunwayStateReportType;

/**
 *
 * @author Ilkka Rinne / Spatineo Oy 2017
 */
public class METARTACParser extends AbstractTACParser<METAR> {

    private static final Logger LOG = LoggerFactory.getLogger(METARTACParser.class);

    private static Lexeme.Identity[] zeroOrOneAllowed = { Lexeme.Identity.AERODROME_DESIGNATOR, Identity.ISSUE_TIME, Identity.AIR_DEWPOINT_TEMPERATURE, Identity.AIR_PRESSURE_QNH, Identity.WIND_SHEAR, Identity.SEA_STATE,
            Identity.REMARKS_START };

    private AviMessageLexer lexer;

    public void setTACLexer(final AviMessageLexer lexer) {
        this.lexer = lexer;
    }

    public ConversionResult<METAR> convertMessage(final String input, final ConversionHints hints) {
        ConversionResult<METAR> result = new ConversionResult<>();
        LexemeSequence lexed = null;
        if (this.lexer == null) {
            throw new IllegalStateException("TAC lexer not set");
        }

        lexed = this.lexer.lexMessage(input, hints);
        if (Identity.METAR_START != lexed.getFirstLexeme().getIdentityIfAcceptable()) {
            result.addIssue(new ConversionIssue(Type.SYNTAX_ERROR, "Input message is not recognized as METAR"));
            return result;
        }


        if (endsInEndToken(lexed, hints)) {
            List<ConversionIssue> issues = checkZeroOrOne(lexed, zeroOrOneAllowed);
            if (!issues.isEmpty()) {
                result.addIssue(issues);
            }
            result.setConvertedMessage(new METARImpl());
            if (lexed.getTAC() != null) {
                result.getConvertedMessage().setTranslatedTAC(lexed.getTAC());
                result.getConvertedMessage().setTranslationTime(ZonedDateTime.now());
            }
            Identity[] stopAt = { Identity.AERODROME_DESIGNATOR, Identity.ISSUE_TIME, Identity.SURFACE_WIND, Identity.CAVOK, Identity.HORIZONTAL_VISIBILITY, Identity.CLOUD, Identity.AIR_DEWPOINT_TEMPERATURE,
                    Identity.AIR_PRESSURE_QNH, Identity.RECENT_WEATHER, Identity.WIND_SHEAR, Identity.SEA_STATE, Identity.RUNWAY_STATE, Identity.COLOR_CODE, Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
            findNext(Identity.CORRECTION, lexed.getFirstLexeme(), stopAt, (match) -> result.getConvertedMessage().setStatus(AviationCodeListUser.MetarStatus.CORRECTION),
                    () -> result.getConvertedMessage().setStatus(AviationCodeListUser.MetarStatus.NORMAL));

            stopAt = new Identity[] { Identity.SURFACE_WIND, Identity.CAVOK, Identity.HORIZONTAL_VISIBILITY, Identity.CLOUD, Identity.AIR_DEWPOINT_TEMPERATURE, Identity.AIR_PRESSURE_QNH, Identity.RECENT_WEATHER, Identity.WIND_SHEAR,
                    Identity.SEA_STATE, Identity.RUNWAY_STATE, Identity.COLOR_CODE, Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
            findNext(Identity.AUTOMATED, lexed.getFirstLexeme(), stopAt, (match) -> result.getConvertedMessage().setAutomatedStation(true));
            
            stopAt = new Identity[] { Identity.ISSUE_TIME, Identity.SURFACE_WIND, Identity.CAVOK, Identity.HORIZONTAL_VISIBILITY, Identity.CLOUD, Identity.AIR_DEWPOINT_TEMPERATURE, Identity.AIR_PRESSURE_QNH, Identity.RECENT_WEATHER,
                    Identity.WIND_SHEAR, Identity.SEA_STATE, Identity.RUNWAY_STATE, Identity.COLOR_CODE, Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
            findNext(Identity.AERODROME_DESIGNATOR, lexed.getFirstLexeme(), stopAt,
                    (match) -> {
                    	Aerodrome ad = new Aerodrome(match.getParsedValue(Lexeme.ParsedValueName.VALUE, String.class));
                    	result.getConvertedMessage().setAerodrome(ad);
                    }, 
                    () -> {
                        result.addIssue(new ConversionIssue(ConversionIssue.Type.SYNTAX_ERROR, "Aerodrome designator not given in " + input));
                    });

            updateMetarIssueTime(result, lexed, hints);
            updateObservedSurfaceWind(result, lexed, hints);

            stopAt = new Identity[] { Identity.HORIZONTAL_VISIBILITY, Identity.RUNWAY_VISUAL_RANGE, Identity.CLOUD, Identity.AIR_DEWPOINT_TEMPERATURE, Identity.AIR_PRESSURE_QNH, Identity.RECENT_WEATHER, Identity.WIND_SHEAR,
                    Identity.SEA_STATE, Identity.RUNWAY_STATE, Identity.COLOR_CODE, Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
            findNext(Identity.CAVOK, lexed.getFirstLexeme(), stopAt, (match) -> result.getConvertedMessage().setCeilingAndVisibilityOk(true));

            updateHorizontalVisibility(result, lexed, hints);
            updateRVR(result, lexed, hints);
            updatePresentWeather(result, lexed, hints);
            updateClouds(result, lexed, hints);
            updateTemperatures(result, lexed, hints);
            updateQNH(result, lexed, hints);
            updateRecentWeather(result, lexed, hints);
            updateWindShear(result, lexed, hints);
            updateSeaState(result, lexed, hints);
            updateRunwayStates(result, lexed, hints);
            updateColorState(result, lexed, hints);
            updateTrends(result, lexed, hints);
            updateRemarks(result, lexed, hints);
        } else {
            result.addIssue(new ConversionIssue(Type.SYNTAX_ERROR, "Message does not end in end token"));
        }
        return result;
    }

    private static void updateMetarIssueTime(final ConversionResult<METAR> result, final LexemeSequence lexed, final ConversionHints hints) {
        final METAR msg = result.getConvertedMessage();
        Identity[] before = { Identity.SURFACE_WIND, Identity.CAVOK, Identity.HORIZONTAL_VISIBILITY, Identity.RUNWAY_VISUAL_RANGE, Identity.CLOUD, Identity.AIR_DEWPOINT_TEMPERATURE, Identity.AIR_PRESSURE_QNH,
                Identity.RECENT_WEATHER, Identity.WIND_SHEAR, Identity.SEA_STATE, Identity.RUNWAY_STATE, Identity.COLOR_CODE, Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
        result.addIssue(updateIssueTime(msg, lexed, before, hints));
    }

    private static void updateObservedSurfaceWind(final ConversionResult<METAR> result, final LexemeSequence lexed, final ConversionHints hints) {
        final METAR msg = result.getConvertedMessage();
        Identity[] before = { Identity.CAVOK, Identity.HORIZONTAL_VISIBILITY, Identity.RUNWAY_VISUAL_RANGE, Identity.CLOUD, Identity.AIR_DEWPOINT_TEMPERATURE, Identity.AIR_PRESSURE_QNH, Identity.RECENT_WEATHER, Identity.WIND_SHEAR,
                Identity.SEA_STATE, Identity.RUNWAY_STATE, Identity.COLOR_CODE, Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
        findNext(Identity.SURFACE_WIND, lexed.getFirstLexeme(), before, (match) -> {
            Object direction = match.getParsedValue(Lexeme.ParsedValueName.DIRECTION, Object.class);
            Integer meanSpeed = match.getParsedValue(Lexeme.ParsedValueName.MEAN_VALUE, Integer.class);
            Integer gust = match.getParsedValue(Lexeme.ParsedValueName.MAX_VALUE, Integer.class);
            String unit = match.getParsedValue(Lexeme.ParsedValueName.UNIT, String.class);

            final ObservedSurfaceWind wind = new ObservedSurfaceWindImpl();

            if (direction == SurfaceWind.WindDirection.VARIABLE) {
                wind.setVariableDirection(true);
            } else if (direction != null && direction instanceof Integer) {
                wind.setMeanWindDirection(new NumericMeasureImpl((Integer) direction, "deg"));
            } else {
                result.addIssue(new ConversionIssue(ConversionIssue.Type.MISSING_DATA, "Direction missing for surface wind:" + match.getTACToken()));
            }

            if (meanSpeed != null) {
                wind.setMeanWindSpeed(new NumericMeasureImpl(meanSpeed, unit));
            } else {
                result.addIssue(new ConversionIssue(ConversionIssue.Type.MISSING_DATA, "Mean speed missing for surface wind:" + match.getTACToken()));
            }

            if (gust != null) {
                wind.setWindGust(new NumericMeasureImpl(gust, unit));
            }

            findNext(Identity.VARIABLE_WIND_DIRECTION, match, before, (match2) -> {
                Integer maxDirection = match2.getParsedValue(Lexeme.ParsedValueName.MAX_DIRECTION, Integer.class);
                Integer minDirection = match2.getParsedValue(Lexeme.ParsedValueName.MIN_DIRECTION, Integer.class);

                if (minDirection != null) {
                    wind.setExtremeCounterClockwiseWindDirection(new NumericMeasureImpl(minDirection, "deg"));
                }
                if (maxDirection != null) {
                    wind.setExtremeClockwiseWindDirection(new NumericMeasureImpl(maxDirection, "deg"));
                }
            });
            msg.setSurfaceWind(wind);
        }, () -> {
            //TODO: cases where it's ok to be missing the surface wind
            result.addIssue(new ConversionIssue(ConversionIssue.Type.SYNTAX_ERROR, "Missing surface wind information in " + lexed.getTAC()));
        });
    }

    private static void updateHorizontalVisibility(final ConversionResult<METAR> result, final LexemeSequence lexed, final ConversionHints hints) {
        final METAR msg = result.getConvertedMessage();
        Identity[] before = { Identity.RUNWAY_VISUAL_RANGE, Identity.CLOUD, Identity.AIR_DEWPOINT_TEMPERATURE, Identity.AIR_PRESSURE_QNH, Identity.RECENT_WEATHER, Identity.WIND_SHEAR, Identity.SEA_STATE, Identity.RUNWAY_STATE,
                Identity.COLOR_CODE, Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };

        findNext(Identity.HORIZONTAL_VISIBILITY, lexed.getFirstLexeme(), before, (match) -> {
            HorizontalVisibility vis = new HorizontalVisibilityImpl();
            while (match != null) {
                MetricHorizontalVisibility.DirectionValue direction = match.getParsedValue(Lexeme.ParsedValueName.DIRECTION,
                        MetricHorizontalVisibility.DirectionValue.class);
                String unit = match.getParsedValue(Lexeme.ParsedValueName.UNIT, String.class);
                Double value = match.getParsedValue(Lexeme.ParsedValueName.VALUE, Double.class);
                RecognizingAviMessageTokenLexer.RelationalOperator operator = match.getParsedValue(Lexeme.ParsedValueName.RELATIONAL_OPERATOR,
                        RecognizingAviMessageTokenLexer.RelationalOperator.class);
                if (direction != null) {
                    if (vis.getMinimumVisibility() != null) {
                        result.addIssue(new ConversionIssue(Type.LOGICAL_ERROR, "More than one directional horizontal visibility given: " + match.getTACToken()));
                    } else {
                        vis.setMinimumVisibility(new NumericMeasureImpl(value, unit));
                        vis.setMinimumVisibilityDirection(new NumericMeasureImpl(direction.inDegrees(), "deg"));
                    }
                } else {
                    if (vis.getPrevailingVisibility() != null) {
                        result.addIssue(new ConversionIssue(Type.LOGICAL_ERROR, "More than one prevailing horizontal visibility given: " + match.getTACToken()));
                    } else {
                        vis.setPrevailingVisibility(new NumericMeasureImpl(value, unit));
                        if (RecognizingAviMessageTokenLexer.RelationalOperator.LESS_THAN == operator) {
                            vis.setPrevailingVisibilityOperator(AviationCodeListUser.RelationalOperator.BELOW);
                        } else if (RecognizingAviMessageTokenLexer.RelationalOperator.MORE_THAN == operator) {
                            vis.setPrevailingVisibilityOperator(AviationCodeListUser.RelationalOperator.ABOVE);
                        }
                    }
                }
                match = findNext(Identity.HORIZONTAL_VISIBILITY, match, before);
            }
            msg.setVisibility(vis);
        }, () -> {
            // If no horizontal visibility and no CAVOK
            if (!result.getConvertedMessage().isCeilingAndVisibilityOk()) {
                result.addIssue(new ConversionIssue(ConversionIssue.Type.SYNTAX_ERROR, "Missing horizontal visibility / cavok in " + lexed.getTAC()));
            }
        });
    }

    private static void updateRVR(final ConversionResult<METAR> result, final LexemeSequence lexed, final ConversionHints hints) {
        final METAR msg = result.getConvertedMessage();
        Identity[] before = { Identity.CLOUD, Identity.AIR_DEWPOINT_TEMPERATURE, Identity.AIR_PRESSURE_QNH, Identity.RECENT_WEATHER, Identity.WIND_SHEAR, Identity.SEA_STATE, Identity.RUNWAY_STATE, Identity.COLOR_CODE,
                Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
        findNext(Identity.RUNWAY_VISUAL_RANGE, lexed.getFirstLexeme(), before, (match) -> {
            List<RunwayVisualRange> rvrs = new ArrayList<>();
            while (match != null) {
            	String rwCode = match.getParsedValue(Lexeme.ParsedValueName.RUNWAY, String.class);
            	 if (rwCode == null) {
                     result.addIssue(new ConversionIssue(ConversionIssue.Type.SYNTAX_ERROR, "Missing runway code for RVR in " + match.getTACToken()));
                 } else {
                	 RunwayDirection runway = new RunwayDirection(rwCode);
                	 runway.setAssociatedAirportHeliport(msg.getAerodrome());

	                Integer minValue = match.getParsedValue(Lexeme.ParsedValueName.MIN_VALUE, Integer.class);
	                RecognizingAviMessageTokenLexer.RelationalOperator minValueOperator = match.getParsedValue(Lexeme.ParsedValueName.RELATIONAL_OPERATOR,
	                        RecognizingAviMessageTokenLexer.RelationalOperator.class);
	                Integer maxValue = match.getParsedValue(Lexeme.ParsedValueName.MAX_VALUE, Integer.class);
	                RecognizingAviMessageTokenLexer.RelationalOperator maxValueOperator = match.getParsedValue(Lexeme.ParsedValueName.RELATIONAL_OPERATOR2,
	                        RecognizingAviMessageTokenLexer.RelationalOperator.class);
	                RecognizingAviMessageTokenLexer.TendencyOperator tendencyIndicator = match.getParsedValue(Lexeme.ParsedValueName.TENDENCY_OPERATOR,
	                        RecognizingAviMessageTokenLexer.TendencyOperator.class);
	                String unit = match.getParsedValue(Lexeme.ParsedValueName.UNIT, String.class);

	                if (minValue == null) {
	                    result.addIssue(new ConversionIssue(ConversionIssue.Type.SYNTAX_ERROR, "Missing visibility value for RVR in " + match.getTACToken()));
	                }
	                RunwayVisualRange rvr = new RunwayVisualRangeImpl();
	                rvr.setRunwayDirection(runway);
	                if (maxValue != null && minValue != null) {
	                    rvr.setVaryingRVRMinimum(new NumericMeasureImpl(minValue, unit));
	                    if (RecognizingAviMessageTokenLexer.RelationalOperator.LESS_THAN == minValueOperator) {
	                        rvr.setVaryingRVRMinimumOperator(AviationCodeListUser.RelationalOperator.BELOW);
	                    } else if (RecognizingAviMessageTokenLexer.RelationalOperator.MORE_THAN == minValueOperator) {
	                        rvr.setVaryingRVRMinimumOperator(AviationCodeListUser.RelationalOperator.ABOVE);
	                    }
	                    
	                    rvr.setVaryingRVRMaximum(new NumericMeasureImpl(maxValue, unit));
	                    if (RecognizingAviMessageTokenLexer.RelationalOperator.LESS_THAN == maxValueOperator) {
	                        rvr.setVaryingRVRMaximumOperator(AviationCodeListUser.RelationalOperator.BELOW);
	                    } else if (RecognizingAviMessageTokenLexer.RelationalOperator.MORE_THAN == maxValueOperator) {
	                        rvr.setVaryingRVRMaximumOperator(AviationCodeListUser.RelationalOperator.ABOVE);
	                    }
	                } else if (minValue != null) {
	                    rvr.setMeanRVR(new NumericMeasureImpl(minValue, unit));
	                    if (RecognizingAviMessageTokenLexer.RelationalOperator.LESS_THAN == minValueOperator) {
	                        rvr.setMeanRVROperator(AviationCodeListUser.RelationalOperator.BELOW);
	                    } else if (RecognizingAviMessageTokenLexer.RelationalOperator.MORE_THAN == minValueOperator) {
	                        rvr.setMeanRVROperator(AviationCodeListUser.RelationalOperator.ABOVE);
	                    }
	                }
	                if (RecognizingAviMessageTokenLexer.TendencyOperator.DOWNWARD == tendencyIndicator) {
	                    rvr.setPastTendency(AviationCodeListUser.VisualRangeTendency.DOWNWARD);
	                } else if (RecognizingAviMessageTokenLexer.TendencyOperator.UPWARD == tendencyIndicator) {
	                    rvr.setPastTendency(AviationCodeListUser.VisualRangeTendency.UPWARD);
	                } else if (RecognizingAviMessageTokenLexer.TendencyOperator.NO_CHANGE == tendencyIndicator) {
	                    rvr.setPastTendency(AviationCodeListUser.VisualRangeTendency.NO_CHANGE);
	                }
	                rvrs.add(rvr);
                 }
                 match = findNext(Identity.RUNWAY_VISUAL_RANGE, match, before);
            }
            msg.setRunwayVisualRanges(rvrs);
        });
    }

    private static void updatePresentWeather(final ConversionResult<METAR> result, final LexemeSequence lexed, final ConversionHints hints) {
        final METAR msg = result.getConvertedMessage();
        Identity[] before = { Identity.CLOUD, Identity.AIR_DEWPOINT_TEMPERATURE, Identity.AIR_PRESSURE_QNH, Identity.RECENT_WEATHER, Identity.WIND_SHEAR, Identity.SEA_STATE, Identity.RUNWAY_STATE, Identity.COLOR_CODE,
                Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
        findNext(Identity.WEATHER, lexed.getFirstLexeme(), before, (match) -> {
            List<fi.fmi.avi.model.Weather> weather = new ArrayList<>();
            result.addIssue(appendWeatherCodes(match, weather, before, hints));
            if (!weather.isEmpty()) {
                msg.setPresentWeather(weather);
            }
        });
    }

    private static void updateClouds(final ConversionResult<METAR> result, final LexemeSequence lexed, final ConversionHints hints) {
        final METAR msg = result.getConvertedMessage();
        Identity[] before = { Identity.AIR_DEWPOINT_TEMPERATURE, Identity.AIR_PRESSURE_QNH, Identity.RECENT_WEATHER, Identity.WIND_SHEAR, Identity.SEA_STATE, Identity.RUNWAY_STATE, Identity.COLOR_CODE,
                Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
        findNext(Identity.CLOUD, lexed.getFirstLexeme(), before, (match) -> {
            ObservedClouds clouds = new ObservedCloudsImpl();
            List<fi.fmi.avi.model.CloudLayer> layers = new ArrayList<>();

            while (match != null) {
                CloudLayer.CloudCover cover = match.getParsedValue(Lexeme.ParsedValueName.COVER, CloudLayer.CloudCover.class);
                Object value = match.getParsedValue(Lexeme.ParsedValueName.VALUE, Object.class);
                String unit = match.getParsedValue(Lexeme.ParsedValueName.UNIT, String.class);

                if (CloudLayer.SpecialValue.AMOUNT_AND_HEIGHT_UNOBSERVABLE_BY_AUTO_SYSTEM == value) {
                    clouds.setAmountAndHeightUnobservableByAutoSystem(true);
                } else if (CloudLayer.CloudCover.NO_SIG_CLOUDS == cover) {
                	clouds.setNoSignificantCloud(true);
                } else if (value instanceof Integer) {
                    if (CloudLayer.CloudCover.SKY_OBSCURED == cover) {
                        int height = ((Integer) value).intValue();
                        if ("hft".equals(unit)) {
                            height = height * 100;
                            unit = "[ft_i]";
                        }
                        clouds.setVerticalVisibility(new NumericMeasureImpl(height, unit));
                    } else {
                        fi.fmi.avi.model.CloudLayer layer = getCloudLayer(match);
                        if (layer != null) {
                            layers.add(layer);
                        } else {
                            result.addIssue(new ConversionIssue(Type.SYNTAX_ERROR, "Could not parse token " + match.getTACToken() + " as cloud layer"));
                        }
                    }
                } else {
                    result.addIssue(new ConversionIssue(ConversionIssue.Type.SYNTAX_ERROR, "Cloud layer height is not an integer in " + match.getTACToken()));
                }

                match = findNext(Identity.CLOUD, match, before);
            }
            if (!layers.isEmpty()) {
                clouds.setLayers(layers);
            }
            msg.setClouds(clouds);
        });

    }

    private static void updateTemperatures(final ConversionResult<METAR> result, final LexemeSequence lexed, final ConversionHints hints) {
        final METAR msg = result.getConvertedMessage();
        Identity[] before = { Identity.AIR_PRESSURE_QNH, Identity.RECENT_WEATHER, Identity.WIND_SHEAR, Identity.SEA_STATE, Identity.RUNWAY_STATE, Identity.COLOR_CODE, Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
        findNext(Identity.AIR_DEWPOINT_TEMPERATURE, lexed.getFirstLexeme(), before, (match) -> {
            String unit = match.getParsedValue(Lexeme.ParsedValueName.UNIT, String.class);
            Integer[] values = match.getParsedValue(Lexeme.ParsedValueName.VALUE, Integer[].class);
            if (values == null) {
                result.addIssue(new ConversionIssue(Type.MISSING_DATA, "Missing air temperature and dewpoint temperature values in " + match.getTACToken()));
            } else {
                if (values[0] != null) {
                    msg.setAirTemperature(new NumericMeasureImpl(values[0], unit));
                } else {
                    result.addIssue(new ConversionIssue(Type.SYNTAX_ERROR, "Missing air temperature value in " + match.getTACToken()));
                }
                if (values[1] != null) {
                    msg.setDewpointTemperature(new NumericMeasureImpl(values[1], unit));
                } else {
                    result.addIssue(new ConversionIssue(Type.SYNTAX_ERROR, "Missing dewpoint temperature value in " + match.getTACToken()));
                }
            }
        }, () -> {
            result.addIssue(new ConversionIssue(Type.MISSING_DATA, "Missing air temperature and dewpoint temperature values in " + lexed.getTAC()));
        });

    }

    private static void updateQNH(final ConversionResult<METAR> result, final LexemeSequence lexed, final ConversionHints hints) {
        final METAR msg = result.getConvertedMessage();
        Identity[] before = { Identity.RECENT_WEATHER, Identity.WIND_SHEAR, Identity.SEA_STATE, Identity.RUNWAY_STATE, Identity.COLOR_CODE, Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
        findNext(Identity.AIR_PRESSURE_QNH, lexed.getFirstLexeme(), before, (match) -> {
            AtmosphericPressureQNH.PressureMeasurementUnit unit = match.getParsedValue(Lexeme.ParsedValueName.UNIT,
                    AtmosphericPressureQNH.PressureMeasurementUnit.class);
            Integer value = match.getParsedValue(Lexeme.ParsedValueName.VALUE, Integer.class);
            if (value != null) {
                String unitStr = "";
                if (unit == AtmosphericPressureQNH.PressureMeasurementUnit.HECTOPASCAL) {
                    unitStr = "hPa";
                } else if (unit == AtmosphericPressureQNH.PressureMeasurementUnit.INCHES_OF_MERCURY) {
                    unitStr = "in Hg";
                } else {
                    result.addIssue(
                            new ConversionIssue(ConversionIssue.Type.SYNTAX_ERROR, "Unknown unit for air pressure: " + unitStr + " in " + match.getTACToken()));
                }
                msg.setAltimeterSettingQNH(new NumericMeasureImpl(value, unitStr));
            } else {
                result.addIssue(new ConversionIssue(ConversionIssue.Type.MISSING_DATA, "Missing air pressure value: " + match.getTACToken()));
            }
        }, () -> {
            result.addIssue(new ConversionIssue(ConversionIssue.Type.SYNTAX_ERROR, "QNH missing in " + lexed.getTAC()));
        });
    }

    private static void updateRecentWeather(final ConversionResult<METAR> result, final LexemeSequence lexed, final ConversionHints hints) {
        final METAR msg = result.getConvertedMessage();
        Identity[] before = { Identity.WIND_SHEAR, Identity.SEA_STATE, Identity.RUNWAY_STATE, Identity.COLOR_CODE, Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
        findNext(Identity.RECENT_WEATHER, lexed.getFirstLexeme(), before, (match) -> {
            List<fi.fmi.avi.model.Weather> weather = new ArrayList<>();
            result.addIssue(appendWeatherCodes(match, weather, before, hints));
            if (!weather.isEmpty()) {
                msg.setRecentWeather(weather);
            }
        });
    }

    private static void updateWindShear(final ConversionResult<METAR> result, final LexemeSequence lexed, final ConversionHints hints) {
        final METAR msg = result.getConvertedMessage();
        Identity[] before = { Identity.SEA_STATE, Identity.RUNWAY_STATE, Identity.COLOR_CODE, Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
        findNext(Identity.WIND_SHEAR, lexed.getFirstLexeme(), before, (match) -> {
            final WindShear ws = new WindShearImpl();
            List<RunwayDirection> runways = new ArrayList<>();
            while (match != null) {
                String rw = match.getParsedValue(Lexeme.ParsedValueName.RUNWAY, String.class);
                if ("ALL".equals(rw)) {
                	if (!runways.isEmpty()) {
                        result.addIssue(new ConversionIssue(Type.LOGICAL_ERROR,
                                "Wind shear reported both to all runways and at least one specific runway: " + match.getTACToken()));
                    } else {
                        ws.setAllRunways(true);
                    }
                } else if (rw != null) {
                	if (ws.isAllRunways()) {
                        result.addIssue(new ConversionIssue(Type.LOGICAL_ERROR,
                                "Wind shear reported both to all runways and at least one specific runway:" + match.getTACToken()));
                    } else {
                    	RunwayDirection rwd = new RunwayDirection(rw);
                    	rwd.setAssociatedAirportHeliport(msg.getAerodrome());
                        runways.add(rwd);
                    }
                }
                match = findNext(Identity.WIND_SHEAR, match, before);
            }
            if (!runways.isEmpty()) {
                ws.setRunwayDirections(runways);
            }
            msg.setWindShear(ws);
        });
    }

    private static void updateSeaState(final ConversionResult<METAR> result, final LexemeSequence lexed, final ConversionHints hints) {
        final METAR msg = result.getConvertedMessage();
        Lexeme.Identity[] before = { Identity.RUNWAY_STATE, Identity.COLOR_CODE, Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
        findNext(Identity.SEA_STATE, lexed.getFirstLexeme(), before, (match) -> {
            SeaState ss = new SeaStateImpl();
            Object[] values = match.getParsedValue(Lexeme.ParsedValueName.VALUE, Object[].class);
            if (values[0] instanceof Integer) {
                String tempUnit = match.getParsedValue(Lexeme.ParsedValueName.UNIT, String.class);
                ss.setSeaSurfaceTemperature(new NumericMeasureImpl((Integer) values[0], tempUnit));
            }
            if (values[1] instanceof fi.fmi.avi.converter.tac.lexer.impl.token.SeaState.SeaSurfaceState) {
                if (values[2] != null) {
                    result.addIssue(new ConversionIssue(Type.LOGICAL_ERROR,
                            "Sea state cannot contain both sea surface state and significant wave height:" + match.getTACToken()));
                } else {
                    switch ((fi.fmi.avi.converter.tac.lexer.impl.token.SeaState.SeaSurfaceState) values[1]) {
                        case CALM_GLASSY:
                            ss.setSeaSurfaceState(AviationCodeListUser.SeaSurfaceState.CALM_GLASSY);
                            break;
                        case CALM_RIPPLED:
                            ss.setSeaSurfaceState(AviationCodeListUser.SeaSurfaceState.CALM_RIPPLED);
                            break;
                        case SMOOTH_WAVELETS:
                            ss.setSeaSurfaceState(AviationCodeListUser.SeaSurfaceState.SMOOTH_WAVELETS);
                            break;
                        case SLIGHT:
                            ss.setSeaSurfaceState(AviationCodeListUser.SeaSurfaceState.SLIGHT);
                            break;
                        case MODERATE:
                            ss.setSeaSurfaceState(AviationCodeListUser.SeaSurfaceState.MODERATE);
                            break;
                        case ROUGH:
                            ss.setSeaSurfaceState(AviationCodeListUser.SeaSurfaceState.ROUGH);
                            break;
                        case VERY_ROUGH:
                            ss.setSeaSurfaceState(AviationCodeListUser.SeaSurfaceState.VERY_ROUGH);
                            break;
                        case HIGH:
                            ss.setSeaSurfaceState(AviationCodeListUser.SeaSurfaceState.HIGH);
                            break;
                        case VERY_HIGH:
                            ss.setSeaSurfaceState(AviationCodeListUser.SeaSurfaceState.VERY_HIGH);
                            break;
                        case PHENOMENAL:
                            ss.setSeaSurfaceState(AviationCodeListUser.SeaSurfaceState.PHENOMENAL);
                            break;
                        case MISSING:
                            ss.setSeaSurfaceState(AviationCodeListUser.SeaSurfaceState.MISSING_VALUE);
                            break;
                    }
                }
            }
            if (values[2] instanceof Number) {
            	if (values[1] != null) {
                    result.addIssue(new ConversionIssue(Type.LOGICAL_ERROR,
                            "Sea state cannot contain both sea surface state and significant wave height:" + match.getTACToken()));
                } else {
                    String heightUnit = match.getParsedValue(Lexeme.ParsedValueName.UNIT2, String.class);
                    ss.setSignificantWaveHeight(new NumericMeasureImpl( ((Number) values[2]).doubleValue(), heightUnit));
                }
            }
            msg.setSeaState(ss);
        });
    }

    private static void updateRunwayStates(final ConversionResult<METAR> result, final LexemeSequence lexed, final ConversionHints hints) {
        final METAR msg = result.getConvertedMessage();
        Lexeme.Identity[] before = { Identity.COLOR_CODE, Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
        findNext(Identity.RUNWAY_STATE, lexed.getFirstLexeme(), before, (match) -> {
        	List<RunwayState> states = new ArrayList<>();
        	while (match != null){
        		RunwayStateImpl rws = new RunwayStateImpl();
	        	@SuppressWarnings("unchecked")
				Map<RunwayStateReportType, Object> values = match.getParsedValue(ParsedValueName.VALUE, Map.class);
	        	Boolean repetition = (Boolean)values.get(RunwayStateReportType.REPETITION);
	        	Boolean allRunways = (Boolean)values.get(RunwayStateReportType.ALL_RUNWAYS);
	        	RunwayDirection runway = new RunwayDirection(match.getParsedValue(ParsedValueName.RUNWAY, String.class));
	        	runway.setAssociatedAirportHeliport(msg.getAerodrome());
	        	RunwayStateDeposit deposit = (RunwayStateDeposit)values.get(RunwayStateReportType.DEPOSITS);
	        	RunwayStateContamination contamination = (RunwayStateContamination)values.get(RunwayStateReportType.CONTAMINATION);
	        	Integer depthOfDeposit = (Integer)values.get(RunwayStateReportType.DEPTH_OF_DEPOSIT);
	        	String unitOfDeposit = (String)values.get(RunwayStateReportType.UNIT_OF_DEPOSIT);
	        	RunwayStateReportSpecialValue depthModifier = (RunwayStateReportSpecialValue)values.get(RunwayStateReportType.DEPTH_MODIFIER);
	        	Boolean cleared = (Boolean)values.get(RunwayStateReportType.CLEARED);
	        	
	        	Object breakingAction = values.get(RunwayStateReportType.BREAKING_ACTION);
	        	Object frictionCoefficient = values.get(RunwayStateReportType.FRICTION_COEFFICIENT);

                Boolean snowClosure = (Boolean) values.get(RunwayStateReportType.SNOW_CLOSURE);

                // Runway direction is missing if repetition, allRunways or SnoClo:
                if (repetition != null && repetition) {
                    rws.setRepetition(true);
	        	} else if (allRunways != null && allRunways) {
	        		rws.setAllRunways(true);
                } else if (snowClosure != null && snowClosure.booleanValue()) {
                    rws.setAllRunways(true);
                    rws.setSnowClosure(true);
                } else if (runway.getDesignator() != null) {
                    rws.setRunwayDirection(runway);
                } else {
                    result.addIssue(new ConversionIssue(Type.SYNTAX_ERROR, "No runway specified for runway state report: " + match.getTACToken()));
                }
	        	if (deposit != null) {
	        		AviationCodeListUser.RunwayDeposit value = fi.fmi.avi.converter.tac.lexer.impl.token.RunwayState.convertRunwayStateDepositToAPI(deposit);
	        		if (value != null) {
	        			rws.setDeposit(value);
	        		}
	        	}
	        	
	        	if (contamination != null) {
	        		AviationCodeListUser.RunwayContamination value = fi.fmi.avi.converter.tac.lexer.impl.token.RunwayState.convertRunwayStateContaminationToAPI(contamination);
	        		if (value != null) {
	        			rws.setContamination(value);
	        		}
	        	}
	        	
	        	if (depthOfDeposit != null) {
	        		if (deposit == null) {
                        result.addIssue(new ConversionIssue(Type.LOGICAL_ERROR, "Missing deposit kind but depth given for runway state: " + match.getTACToken()));
                    } else {
                        rws.setDepthOfDeposit(new NumericMeasureImpl(depthOfDeposit, unitOfDeposit));
                    }
                }
	        	
	        	if (depthModifier != null) {
	        		if (depthOfDeposit == null && depthModifier == RunwayStateReportSpecialValue.NOT_MEASURABLE) {
	        			rws.setDepthNotMeasurable(true);
	        			rws.setDepthOfDeposit(null);
                    } else if (depthOfDeposit == null && depthModifier != RunwayStateReportSpecialValue.RUNWAY_NOT_OPERATIONAL) {
                        result.addIssue(new ConversionIssue(Type.LOGICAL_ERROR,
                                "Missing deposit depth but depth modifier given for runway state: " + match.getTACToken()));
                    } else {
                        switch (depthModifier) {
                            case LESS_THAN_OR_EQUAL:
                                rws.setDepthOperator(AviationCodeListUser.RelationalOperator.BELOW);
                                break;
                            case MEASUREMENT_UNRELIABLE:
                            case NOT_MEASURABLE:
                                result.addIssue(
                                        new ConversionIssue(Type.SYNTAX_ERROR, "Illegal modifier for depth of deposit for runway state:" + match.getTACToken()));
                                break;
                            case MORE_THAN_OR_EQUAL:
                                rws.setDepthOperator(AviationCodeListUser.RelationalOperator.ABOVE);
                                break;
                            case RUNWAY_NOT_OPERATIONAL:
                                rws.setRunwayNotOperational(true);
                                break;
                        }
                    }
                }
	        	if (cleared != null && cleared) {
	        		if (deposit != null || contamination != null || depthOfDeposit != null) {
                        result.addIssue(new ConversionIssue(Type.LOGICAL_ERROR,
                                "Runway state cannot be both cleared and contain deposit or contamination info: " + match.getTACToken()));
                    } else {
                        rws.setCleared(true);
                    }
                }
		        	
				if (breakingAction instanceof fi.fmi.avi.converter.tac.lexer.impl.token.RunwayState.BreakingAction) {
					BreakingAction action = 
							fi.fmi.avi.converter.tac.lexer.impl.token.RunwayState.convertBreakingActionToAPI(
									(fi.fmi.avi.converter.tac.lexer.impl.token.RunwayState.BreakingAction)breakingAction);

					rws.setBreakingAction(action);
				} else if (breakingAction instanceof RunwayStateReportSpecialValue) {
					switch((RunwayStateReportSpecialValue)breakingAction) {
					case RUNWAY_NOT_OPERATIONAL:
						rws.setRunwayNotOperational(true);
						break;
					case MEASUREMENT_UNRELIABLE:
						rws.setEstimatedSurfaceFrictionUnreliable(true);
						break;
					case MORE_THAN_OR_EQUAL:
					case LESS_THAN_OR_EQUAL:
					case NOT_MEASURABLE:
						// TODO: no idea what we should do here
						break;
					}
				}
				
				if (frictionCoefficient != null && frictionCoefficient instanceof Number) {
					rws.setEstimatedSurfaceFriction(((Number)frictionCoefficient).doubleValue());
				} else if (frictionCoefficient == RunwayStateReportSpecialValue.MEASUREMENT_UNRELIABLE) {
					rws.setEstimatedSurfaceFrictionUnreliable(true);
				}
				
	        	states.add(rws);
	        	match = findNext(Identity.RUNWAY_STATE, match, before);
        	}
        	if (!states.isEmpty()) {
        		msg.setRunwayStates(states);
        	}
        });
    }

    private static void updateColorState(final ConversionResult<METAR> result, final LexemeSequence lexed, final ConversionHints hints) {
        final METAR msg = result.getConvertedMessage();
        Lexeme.Identity[] before = { Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START };
        findNext(Identity.COLOR_CODE, lexed.getFirstLexeme(), before, (colorToken) -> {
            ColorCode.ColorState code = colorToken.getParsedValue(ParsedValueName.VALUE, ColorCode.ColorState.class);
            for (AviationCodeListUser.ColorState state : AviationCodeListUser.ColorState.values()) {
                if (state.name().equalsIgnoreCase(code.getCode())) {
                    msg.setColorState(state);
                }
            }
            if (msg.getColorState() == null) {
                result.addIssue(new ConversionIssue(Type.SYNTAX_ERROR, "Unknown color state '" + code.getCode() + "'"));
            }
        });
    }

    private static void updateTrends(final ConversionResult<METAR> result, final LexemeSequence lexed, final ConversionHints hints) {
        final METAR msg = result.getConvertedMessage();
        Lexeme.Identity[] before = { Identity.REMARKS_START, Identity.END_TOKEN };
        final List<TrendForecast> trends = new ArrayList<>();
        findNext(Identity.FORECAST_CHANGE_INDICATOR, lexed.getFirstLexeme(), before, (changeFct) -> {
            //loop over change forecasts:
            Lexeme.Identity[] stopWithingGroup = { Identity.FORECAST_CHANGE_INDICATOR, Identity.REMARKS_START, Identity.END_TOKEN };
            while (changeFct != null) {
                TrendForecast fct = new TrendForecastImpl();
                ForecastChangeIndicator.ForecastChangeIndicatorType type = changeFct.getParsedValue(ParsedValueName.TYPE,
                        ForecastChangeIndicator.ForecastChangeIndicatorType.class);
                switch (type) {
                    case BECOMING:
                        fct.setChangeIndicator(BECOMING);
                        break;
                    case TEMPORARY_FLUCTUATIONS:
                        fct.setChangeIndicator(AviationCodeListUser.TrendForecastChangeIndicator.TEMPORARY_FLUCTUATIONS);
                        break;
                    case WITH_30_PCT_PROBABILITY:
                    case WITH_40_PCT_PROBABILITY:
                        result.addIssue(new ConversionIssue(Type.SYNTAX_ERROR, "PROB30/40 groups not allowed in METAR"));
                        break;
                    case NO_SIGNIFICANT_CHANGES:
                        fct.setChangeIndicator(AviationCodeListUser.TrendForecastChangeIndicator.NO_SIGNIFICANT_CHANGES);
                        break;
                    default:
                        break;
                }
                if (BECOMING == fct.getChangeIndicator() || TEMPORARY_FLUCTUATIONS == fct.getChangeIndicator()) {
                    //Check for the possibly following FM, TL and AT tokens:
                    Lexeme token = changeFct.getNext();
                    if (Identity.FORECAST_CHANGE_INDICATOR == token.getIdentity()) {
                        type = token.getParsedValue(ParsedValueName.TYPE, ForecastChangeIndicator.ForecastChangeIndicatorType.class);
                        if (type != null) {
                            TrendTimeGroups timeGroups = new TrendTimeGroupsImpl();
                            switch (type) {
                                case AT: {
                                    Integer fromHour = token.getParsedValue(ParsedValueName.HOUR1, Integer.class);
                                    Integer fromMinute = token.getParsedValue(ParsedValueName.MINUTE1, Integer.class);
                                    if (fromHour != null && fromMinute != null) {
                                        timeGroups.setPartialStartTime(fromHour, fromMinute);
                                    } else {
                                        result.addIssue(new ConversionIssue(Type.SYNTAX_ERROR, "Missing hour and/or minute from trend AT group " + token.getTACToken()));
                                    }
                                    timeGroups.setSingleInstance(true);
                                    fct.setTimeGroups(timeGroups);
                                    break;
                                }
                                case FROM: {
                                    Integer fromHour = token.getParsedValue(ParsedValueName.HOUR1, Integer.class);
                                    Integer fromMinute = token.getParsedValue(ParsedValueName.MINUTE1, Integer.class);
                                    if (fromHour != null && fromMinute != null) {
                                        timeGroups.setPartialStartTime(fromHour, fromMinute);
                                    } else {
                                        result.addIssue(new ConversionIssue(Type.SYNTAX_ERROR, "Missing hour and/or minute from trend FM group " + token.getTACToken()));
                                    }
                                    fct.setTimeGroups(timeGroups);
                                    break;
                                }
                                case UNTIL: {
                                    Integer toHour = token.getParsedValue(ParsedValueName.HOUR1, Integer.class);
                                    Integer toMinute = token.getParsedValue(ParsedValueName.MINUTE1, Integer.class);
                                    if (toHour != null && toMinute != null) {
                                        timeGroups.setPartialEndTime(toHour, toMinute);
                                    } else {
                                        result.addIssue(new ConversionIssue(Type.SYNTAX_ERROR, "Missing hour and/or minute from trend TL group " + token.getTACToken()));
                                    }
                                    fct.setTimeGroups(timeGroups);
                                    break;
                                }
                                default: {
                                    result.addIssue(new ConversionIssue(Type.SYNTAX_ERROR,
                                            "Illegal change group '" + token.getTACToken() + "' after '" + changeFct.getTACToken() + "'"));
                                    break;
                                }
                            }
                            changeFct = token;
                            token = findNext(token, stopWithingGroup);
                        }
                    }

                    //loop over change group tokens:
                    CloudForecast cloud = null;
                    List<fi.fmi.avi.model.CloudLayer> cloudLayers = null;
                    NumericMeasure prevailingVisibility = null;
                    AviationCodeListUser.RelationalOperator visibilityOperator = null;
                    TrendForecastSurfaceWind wind = null;
                    List<fi.fmi.avi.model.Weather> forecastWeather = null;
                    while (token != null) {
                    	Identity id = token.getIdentity();
                    	if (id != null) {
	                        switch (id) {
	                            case CAVOK:
	                                fct.setCeilingAndVisibilityOk(true);
	                                break;
	                            case CLOUD: {
	                                if (cloud == null) {
	                                    cloud = new CloudForecastImpl();
	                                }
	                                Object value = token.getParsedValue(Lexeme.ParsedValueName.VALUE, Object.class);
	                                String unit = token.getParsedValue(Lexeme.ParsedValueName.UNIT, String.class);
	                                CloudLayer.CloudCover cover = token.getParsedValue(Lexeme.ParsedValueName.COVER, CloudLayer.CloudCover.class);
	                                if (CloudLayer.CloudCover.SKY_OBSCURED == cover) {
	                                    if (value instanceof Integer) {
	                                        int height = ((Integer) value).intValue();
	                                        if ("hft".equals(unit)) {
	                                            height = height * 100;
	                                            unit = "[ft_i]";
	                                        }
	                                        cloud.setVerticalVisibility(new NumericMeasureImpl(height, unit));
	                                    } else {
	                                        result.addIssue(new ConversionIssue(Type.MISSING_DATA, "Missing value for vertical visibility"));
	                                    }
	                                } else if (CloudCover.NO_SIG_CLOUDS == cover) {
	                                	cloud.setNoSignificantCloud(true);
	                                } else {
	                                    fi.fmi.avi.model.CloudLayer layer = getCloudLayer(token);
	                                    if (layer != null) {
	                                        if (cloudLayers == null) {
	                                            cloudLayers = new ArrayList<>();
	                                        }
	                                        cloudLayers.add(layer);
	                                    } else {
	                                        result.addIssue(new ConversionIssue(Type.MISSING_DATA, "Missing base for cloud layer"));
	                                    }
	                                }
	                                break;
	                            }
	                            case HORIZONTAL_VISIBILITY: {
	                                if (prevailingVisibility == null) {
	                                    String unit = token.getParsedValue(Lexeme.ParsedValueName.UNIT, String.class);
	                                    Double value = token.getParsedValue(Lexeme.ParsedValueName.VALUE, Double.class);
	                                    RecognizingAviMessageTokenLexer.RelationalOperator operator = token.getParsedValue(
	                                            Lexeme.ParsedValueName.RELATIONAL_OPERATOR, RecognizingAviMessageTokenLexer.RelationalOperator.class);
	                                    prevailingVisibility = new NumericMeasureImpl(value, unit);
	                                    if (RecognizingAviMessageTokenLexer.RelationalOperator.LESS_THAN == operator) {
	                                        visibilityOperator = AviationCodeListUser.RelationalOperator.BELOW;
	                                    } else if (RecognizingAviMessageTokenLexer.RelationalOperator.MORE_THAN == operator) {
	                                        visibilityOperator = AviationCodeListUser.RelationalOperator.ABOVE;
	                                    }
	                                } else {
	                                    result.addIssue(new ConversionIssue(Type.LOGICAL_ERROR,
	                                            "More than one visibility token within a trend change group: " + token.getTACToken()));
	                                }
	                                break;
	                            }
	                            case SURFACE_WIND: {
	                                if (wind == null) {
	                                    wind = new TrendForecastSurfaceWindImpl();
	                                    Object direction = token.getParsedValue(Lexeme.ParsedValueName.DIRECTION, Integer.class);
	                                    Integer meanSpeed = token.getParsedValue(Lexeme.ParsedValueName.MEAN_VALUE, Integer.class);
	                                    Integer gust = token.getParsedValue(Lexeme.ParsedValueName.MAX_VALUE, Integer.class);
	                                    String unit = token.getParsedValue(Lexeme.ParsedValueName.UNIT, String.class);
	
	                                    if (direction == SurfaceWind.WindDirection.VARIABLE) {
	                                        result.addIssue(new ConversionIssue(Type.SYNTAX_ERROR, "Wind cannot be variable in trend: " + token.getTACToken()));
	                                    } else if (direction != null && direction instanceof Integer) {
	                                        wind.setMeanWindDirection(new NumericMeasureImpl((Integer) direction, "deg"));
	                                    } else {
	                                        result.addIssue(
	                                                new ConversionIssue(ConversionIssue.Type.MISSING_DATA, "Direction missing for surface wind:" + token.getTACToken()));
	                                    }
	
	                                    if (meanSpeed != null) {
	                                        wind.setMeanWindSpeed(new NumericMeasureImpl(meanSpeed, unit));
	                                    } else {
	                                        result.addIssue(
	                                                new ConversionIssue(ConversionIssue.Type.MISSING_DATA, "Mean speed missing for surface wind:" + token.getTACToken()));
	                                    }
	
	                                    if (gust != null) {
	                                        wind.setWindGust(new NumericMeasureImpl(gust, unit));
	                                    }
	                                } else {
	                                    result.addIssue(new ConversionIssue(Type.LOGICAL_ERROR, "More than one wind token within a trend change group"));
	                                }
	                                break;
	                            }
	                            case WEATHER: {
	                                if (forecastWeather == null) {
	                                    forecastWeather = new ArrayList<>();
	                                }
	                                String code = token.getParsedValue(Lexeme.ParsedValueName.VALUE, String.class);
	                                if (code != null) {
	                                    fi.fmi.avi.model.Weather weather = new WeatherImpl();
	                                    weather.setCode(code);
	                                    weather.setDescription(Weather.WEATHER_CODES.get(code));
	                                    forecastWeather.add(weather);
	                                } else {
	                                    result.addIssue(new ConversionIssue(Type.MISSING_DATA, "Weather code not found"));
	                                }
	                                break;
	                            }
	                            case NO_SIGNIFICANT_WEATHER:
	                                fct.setNoSignificantWeather(true);
	                                break;
	                            case COLOR_CODE: {
	                                ColorCode.ColorState code = token.getParsedValue(ParsedValueName.VALUE, ColorCode.ColorState.class);
	                                for (AviationCodeListUser.ColorState state : AviationCodeListUser.ColorState.values()) {
	                                    if (state.name().equalsIgnoreCase(code.getCode())) {
	                                        fct.setColorState(state);
	                                    }
	                                }
	                                if (fct.getColorState() == null) {
	                                    result.addIssue(new ConversionIssue(Type.SYNTAX_ERROR, "Unknown color state '" + code.getCode() + "'"));
	                                }
	                                break;
	                            }
	                            default:
	                                result.addIssue(
	                                        new ConversionIssue(Type.SYNTAX_ERROR, "Illegal token " + token.getTACToken() + " within the change forecast group"));
	                                break;
	                        }
                    	}
                        token = findNext(token, stopWithingGroup);
                    }
                    if (cloudLayers != null && !cloudLayers.isEmpty()) {
                        if (cloud.isNoSignificantCloud()) {
                            result.addIssue(new ConversionIssue(Type.LOGICAL_ERROR, "Cloud layers cannot co-exist with NSC in trend"));
                        } else if (cloud.getVerticalVisibility() != null) {
                            result.addIssue(new ConversionIssue(Type.LOGICAL_ERROR, "Cloud layers cannot co-exist with vertical visibility in trend"));
                        } else {
                            cloud.setLayers(cloudLayers);
                        }
                    }

                    fct.setSurfaceWind(wind);
                    if (fct.isCeilingAndVisibilityOk()) {
                        if (cloud != null || prevailingVisibility != null || forecastWeather != null || fct.isNoSignificantWeather()) {
                            result.addIssue(new ConversionIssue(Type.LOGICAL_ERROR,
                                    "CAVOK cannot co-exist with cloud, prevailing visibility, weather, NSW " + "in trend"));
                        }
                    } else {
                        fct.setCloud(cloud);
                        fct.setPrevailingVisibility(prevailingVisibility);
                        if (visibilityOperator != null) {
                            fct.setPrevailingVisibilityOperator(visibilityOperator);
                        }
                        if (fct.isNoSignificantWeather() && forecastWeather != null && !forecastWeather.isEmpty()) {
                            result.addIssue(new ConversionIssue(Type.LOGICAL_ERROR, "Forecast weather cannot co-exist with NSW in trend"));
                        }
                        fct.setForecastWeather(forecastWeather);
                    }
                }
                trends.add(fct);
                changeFct = findNext(Identity.FORECAST_CHANGE_INDICATOR, changeFct, before);
            }
        });
        if (!trends.isEmpty()) {
            msg.setTrends(trends);
        }
    }

}
