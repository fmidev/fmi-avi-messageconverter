package fi.fmi.avi.converter.tac;

import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity;

import fi.fmi.avi.converter.ConversionIssue;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.converter.ConversionIssue.Type;
import fi.fmi.avi.converter.tac.lexer.Lexeme;
import fi.fmi.avi.converter.tac.lexer.LexemeSequence;
import fi.fmi.avi.converter.tac.lexer.LexemeSequenceBuilder;
import fi.fmi.avi.converter.tac.lexer.SerializingException;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.Weather;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFAirTemperatureForecast;
import fi.fmi.avi.model.taf.TAFBaseForecast;
import fi.fmi.avi.model.taf.TAFChangeForecast;
import fi.fmi.avi.converter.ConversionHints;

/**
 * Created by rinne on 07/06/17.
 */
public class TAFTACSerializer extends AbstractTACSerializer<TAF> {

	@Override
    public ConversionResult<String> convertMessage(final TAF input, final ConversionHints hints) {
        ConversionResult<String> result = new ConversionResult<String>();
        try {
        	LexemeSequence seq = tokenizeMessage(input, hints);
        	result.setConvertedMessage(seq.getTAC());
        } catch (SerializingException se) {
        	result.addIssue(new ConversionIssue(Type.OTHER, se.getMessage()));
        }
    	return result;
    }

    @Override
    public LexemeSequence tokenizeMessage(final AviationWeatherMessage msg) throws SerializingException {
        return tokenizeMessage(msg, null);
    }

    @Override
    public LexemeSequence tokenizeMessage(final AviationWeatherMessage msg, final ConversionHints hints) throws SerializingException {
        if (!(msg instanceof TAF)) {
            throw new SerializingException("I can only tokenize TAFs!");
        }
        TAF input = (TAF) msg;
        LexemeSequenceBuilder retval = this.getLexingFactory().createLexemeSequenceBuilder();
        appendToken(retval, Identity.TAF_START, input, TAF.class, hints);
        appendToken(retval, Identity.AMENDMENT, input, TAF.class, hints);
        appendToken(retval, Identity.CORRECTION, input, TAF.class, hints);
        appendToken(retval, Identity.AERODROME_DESIGNATOR, input, TAF.class, hints);
        appendToken(retval, Identity.ISSUE_TIME, input, TAF.class, hints);

        if (AviationCodeListUser.TAFStatus.MISSING != input.getStatus()) {
            appendToken(retval, Identity.VALID_TIME, input, TAF.class, hints);
            appendToken(retval, Identity.CANCELLATION, input, TAF.class, hints);
            if (AviationCodeListUser.TAFStatus.CANCELLATION != input.getStatus()) {
                TAFBaseForecast baseFct = input.getBaseForecast();
                if (baseFct == null) {
                    throw new SerializingException("Missing base forecast");
                }
                appendToken(retval, Identity.SURFACE_WIND, input, TAF.class, hints, baseFct);
                appendToken(retval, Identity.CAVOK, input, TAF.class, hints, baseFct);
                appendToken(retval, Identity.HORIZONTAL_VISIBILITY, input, TAF.class, hints, baseFct);
                if (baseFct.getForecastWeather() != null) {
                    for (Weather weather : baseFct.getForecastWeather()) {
                        appendToken(retval, Identity.WEATHER, input, TAF.class, hints, baseFct, weather);
                    }
                }
                CloudForecast clouds = baseFct.getCloud();
                if (clouds != null) {
                    if (clouds.getVerticalVisibility() != null) {
                        this.appendToken(retval, Lexeme.Identity.CLOUD, input, TAF.class, hints, "VV", baseFct);
                    } else {
                        this.appendCloudLayers(retval, input, TAF.class, clouds.getLayers(), hints, baseFct);
                    }
                }
                if (baseFct.getTemperatures() != null) {
                    for (TAFAirTemperatureForecast tempFct : baseFct.getTemperatures()) {
                        appendToken(retval, Identity.MAX_TEMPERATURE, input, TAF.class, hints, baseFct, tempFct);
                        // No MIN_TEMPERATURE needed as they are produced together
                    }
                }

                if (input.getChangeForecasts() != null) {
                    for (TAFChangeForecast changeFct : input.getChangeForecasts()) {
                        appendToken(retval, Identity.FORECAST_CHANGE_INDICATOR, input, TAF.class, hints, changeFct);
                        appendToken(retval, Identity.CHANGE_FORECAST_TIME_GROUP, input, TAF.class, hints, changeFct);
                        appendToken(retval, Identity.SURFACE_WIND, input, TAF.class, hints, changeFct);
                        appendToken(retval, Identity.CAVOK, input, TAF.class, hints, changeFct);
                        appendToken(retval, Identity.HORIZONTAL_VISIBILITY, input, TAF.class, hints, changeFct);
                        appendToken(retval, Identity.NO_SIGNIFICANT_WEATHER, input, TAF.class, hints, changeFct);
                        if (changeFct.getForecastWeather() != null) {
                            for (Weather weather : changeFct.getForecastWeather()) {
                                appendToken(retval, Identity.WEATHER, input, TAF.class, hints, changeFct, weather);
                            }
                        }
                        clouds = changeFct.getCloud();
                        if (clouds != null) {
                            if (clouds.getVerticalVisibility() != null) {
                                this.appendToken(retval, Lexeme.Identity.CLOUD, input, TAF.class, hints, "VV", changeFct);
                            } else {
                                this.appendCloudLayers(retval, input, TAF.class, clouds.getLayers(), hints, changeFct);
                            }
                        }
                    }
                }
                if (input.getRemarks() != null && !input.getRemarks().isEmpty()) {
                    appendToken(retval, Identity.REMARKS_START, input, TAF.class, hints);
                    for (String remark : input.getRemarks()) {
                        this.appendToken(retval, Identity.REMARK, input, TAF.class, hints, remark);
                    }
                }
            }
        } else {
            appendToken(retval, Identity.NIL, input, TAF.class, hints);
        }
        appendToken(retval, Identity.END_TOKEN, input, TAF.class, hints);
        return retval.build();
    }
}
