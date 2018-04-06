package fi.fmi.avi.model.metar;

import java.util.List;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.Weather;

@FreeBuilder
@JsonDeserialize(builder = TrendForecast.Builder.class)
public interface TrendForecast extends AviationCodeListUser {

    PartialOrCompleteTimePeriod getValidityTime();

    boolean isCeilingAndVisibilityOk();

    TrendForecastChangeIndicator getChangeIndicator();

    Optional<NumericMeasure> getPrevailingVisibility();

    Optional<RelationalOperator> getPrevailingVisibilityOperator();

    Optional<TrendForecastSurfaceWind> getSurfaceWind();

    Optional<List<Weather>> getForecastWeather();

    boolean isNoSignificantWeather();

    Optional<CloudForecast> getCloud();

    Optional<ColorState> getColorState();

    Builder toBuilder();

    class Builder extends TrendForecast_Builder {
    }

}
