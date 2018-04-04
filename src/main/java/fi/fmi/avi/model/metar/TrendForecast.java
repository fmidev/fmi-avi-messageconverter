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

    PartialOrCompleteTimePeriod validityTime();

    boolean ceilingAndVisibilityOk();

    TrendForecastChangeIndicator changeIndicator();

    Optional<NumericMeasure> prevailingVisibility();

    Optional<RelationalOperator> prevailingVisibilityOperator();

    Optional<TrendForecastSurfaceWind> surfaceWind();

    Optional<List<Weather>> forecastWeather();

    boolean noSignificantWeather();

    Optional<CloudForecast> cloud();

    Optional<ColorState> colorState();

    Builder toBuilder();

    class Builder extends TrendForecast_Builder {
    }

}
