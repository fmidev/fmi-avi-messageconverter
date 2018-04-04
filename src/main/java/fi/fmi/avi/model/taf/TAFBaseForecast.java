package fi.fmi.avi.model.taf;

import java.util.List;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.Weather;

/**
 * Created by rinne on 30/01/15.
 */
@FreeBuilder
@JsonDeserialize(builder = TAFBaseForecast.Builder.class)
public interface TAFBaseForecast extends AviationCodeListUser {

    boolean ceilingAndVisibilityOk();

    NumericMeasure prevailingVisibility();

    Optional<RelationalOperator> prevailingVisibilityOperator();

    TAFSurfaceWind surfaceWind();

    Optional<List<Weather>> forecastWeather();

    boolean noSignificantWeather();

    List<String> forecastWeatherCodes();

    CloudForecast cloud();

    Optional<List<TAFAirTemperatureForecast>> temperatures();

    Builder toBuilder();

    class Builder extends TAFBaseForecast_Builder {
        public Builder() {
            ceilingAndVisibilityOk(false);
            noSignificantWeather(false);
        }
    }
}
