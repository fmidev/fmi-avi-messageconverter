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

    boolean isCeilingAndVisibilityOk();

    NumericMeasure getPrevailingVisibility();

    Optional<RelationalOperator> getPrevailingVisibilityOperator();

    TAFSurfaceWind getSurfaceWind();

    Optional<List<Weather>> getForecastWeather();

    boolean isNoSignificantWeather();

    List<String> getForecastWeatherCodes();

    CloudForecast getCloud();

    Optional<List<TAFAirTemperatureForecast>> getTemperatures();

    Builder toBuilder();

    class Builder extends TAFBaseForecast_Builder {
        public Builder() {
            setCeilingAndVisibilityOk(false);
            setNoSignificantWeather(false);
        }
    }
}
