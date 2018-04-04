package fi.fmi.avi.model.taf;

import java.util.List;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.Weather;

/**
 * Created by rinne on 30/01/15.
 */
@FreeBuilder
@JsonDeserialize(builder = TAFChangeForecast.Builder.class)
public interface TAFChangeForecast extends AviationCodeListUser {

    TAFChangeIndicator changeIndicator();

    PartialOrCompleteTimePeriod validityTime();

    boolean ceilingAndVisibilityOk();

    Optional<NumericMeasure> prevailingVisibility();

    Optional<RelationalOperator> prevailingVisibilityOperator();

    Optional<TAFSurfaceWind> surfaceWind();

    Optional<List<Weather>> forecastWeather();

    boolean noSignificantWeather();

    Optional<CloudForecast> cloud();

    Builder toBuilder();

    class Builder extends TAFChangeForecast_Builder {
    }

}
