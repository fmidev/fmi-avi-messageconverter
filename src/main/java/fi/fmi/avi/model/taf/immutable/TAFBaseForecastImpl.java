package fi.fmi.avi.model.taf.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.immutable.CloudForecastImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.WeatherImpl;
import fi.fmi.avi.model.taf.TAFBaseForecast;

/**
 * Created by rinne on 18/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = TAFBaseForecastImpl.Builder.class)
public abstract class TAFBaseForecastImpl implements TAFBaseForecast, Serializable {

    public static TAFBaseForecastImpl immutableCopyOf(final TAFBaseForecast baseForecast) {
        checkNotNull(baseForecast);
        if (baseForecast instanceof TAFBaseForecastImpl) {
            return (TAFBaseForecastImpl) baseForecast;
        } else {
            return Builder.from(baseForecast).build();
        }
    }

    public static Optional<TAFBaseForecastImpl> immutableCopyOf(final Optional<TAFBaseForecast> baseForecast) {
        checkNotNull(baseForecast);
        return baseForecast.map(TAFBaseForecastImpl::immutableCopyOf);
    }

    abstract Builder toBuilder();

    public static class Builder extends TAFBaseForecastImpl_Builder {

        public static Builder from(final TAFBaseForecast value) {
            Builder retval = new Builder().setCeilingAndVisibilityOk(value.isCeilingAndVisibilityOk())
                    .setCloud(CloudForecastImpl.immutableCopyOf(value.getCloud()))
                    .setNoSignificantWeather(value.isNoSignificantWeather())
                    .setPrevailingVisibility(NumericMeasureImpl.immutableCopyOf(value.getPrevailingVisibility()))
                    .setPrevailingVisibilityOperator(value.getPrevailingVisibilityOperator())
                    .setSurfaceWind(TAFSurfaceWindImpl.immutableCopyOf(value.getSurfaceWind()));

            value.getForecastWeather()
                    .map(weather -> retval.setForecastWeather(
                            Collections.unmodifiableList(weather.stream().map(WeatherImpl::immutableCopyOf).collect(Collectors.toList()))));

            value.getTemperatures()
                    .map(temps -> retval.setTemperatures(
                            Collections.unmodifiableList(temps.stream().map(TAFAirTemperatureForecastImpl::immutableCopyOf).collect(Collectors.toList()))));
            return retval;
        }
    }
}
