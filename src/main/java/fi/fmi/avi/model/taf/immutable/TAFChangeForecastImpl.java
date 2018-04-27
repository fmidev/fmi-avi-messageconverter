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
import fi.fmi.avi.model.taf.TAFChangeForecast;

/**
 * Created by rinne on 18/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = TAFChangeForecastImpl.Builder.class)
public abstract class TAFChangeForecastImpl implements TAFChangeForecast, Serializable {

    public static TAFChangeForecastImpl immutableCopyOf(final TAFChangeForecast changeForecast) {
        checkNotNull(changeForecast);
        if (changeForecast instanceof TAFChangeForecastImpl) {
            return (TAFChangeForecastImpl) changeForecast;
        } else {
            return Builder.from(changeForecast).build();
        }
    }

    public static Optional<TAFChangeForecastImpl> immutableCopyOf(final Optional<TAFChangeForecast> changeForecast) {
        checkNotNull(changeForecast);
        return changeForecast.map(TAFChangeForecastImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends TAFChangeForecastImpl_Builder {

        public static Builder from(TAFChangeForecast value) {
            Builder retval = new Builder().setCeilingAndVisibilityOk(value.isCeilingAndVisibilityOk())
                    .setChangeIndicator(value.getChangeIndicator())
                    .setCloud(CloudForecastImpl.immutableCopyOf(value.getCloud()))
                    .setNoSignificantWeather(value.isNoSignificantWeather())
                    .setPrevailingVisibility(NumericMeasureImpl.immutableCopyOf(value.getPrevailingVisibility()))
                    .setPrevailingVisibilityOperator(value.getPrevailingVisibilityOperator())
                    .setSurfaceWind(TAFSurfaceWindImpl.immutableCopyOf(value.getSurfaceWind()))
                    .setValidityTime(value.getValidityTime());

            value.getForecastWeather()
                    .map(weather -> retval.setForecastWeather(
                            Collections.unmodifiableList(weather.stream().map(WeatherImpl::immutableCopyOf).collect(Collectors.toList()))));

            return retval;
        }
    }
}
