package fi.fmi.avi.model.taf.immutable;


import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.Weather;
import fi.fmi.avi.model.immutable.CloudForecastImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.WeatherImpl;
import fi.fmi.avi.model.taf.TAFChangeForecast;
import fi.fmi.avi.model.taf.TAFSurfaceWind;

/**
 * Created by rinne on 18/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = TAFChangeForecastImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"changeIndicator", "periodOfChange", "surfaceWind", "ceilingAndVisibilityOk",
        "prevailingVisibility", "prevailingVisibilityOperator", "forecastWeather", "noSignificantWeather", "cloud"})
public abstract class TAFChangeForecastImpl implements TAFChangeForecast, Serializable {

    public static TAFChangeForecastImpl immutableCopyOf(final TAFChangeForecast changeForecast) {
        Objects.nonNull(changeForecast);
        if (changeForecast instanceof TAFChangeForecastImpl) {
            return (TAFChangeForecastImpl) changeForecast;
        } else {
            return Builder.from(changeForecast).build();
        }
    }

    public static Optional<TAFChangeForecastImpl> immutableCopyOf(final Optional<TAFChangeForecast> changeForecast) {
        Objects.nonNull(changeForecast);
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
                    .setPeriodOfChange(value.getPeriodOfChange());

            value.getForecastWeather()
                    .map(weather -> retval.setForecastWeather(
                            Collections.unmodifiableList(weather.stream().map(WeatherImpl::immutableCopyOf).collect(Collectors.toList()))));

            return retval;
        }

        public Builder() {
            setCeilingAndVisibilityOk(false);
            setNoSignificantWeather(false);
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setPrevailingVisibility(final NumericMeasure prevailingVisibility) {
            return super.setPrevailingVisibility(prevailingVisibility);
        }

        @Override
        @JsonDeserialize(as = TAFSurfaceWindImpl.class)
        public Builder setSurfaceWind(final TAFSurfaceWind surfaceWind) {
            return super.setSurfaceWind(surfaceWind);
        }

        @Override
        @JsonDeserialize(contentAs = WeatherImpl.class)
        public Builder setForecastWeather(final List<Weather> forecastWeather) {
            return super.setForecastWeather(forecastWeather);
        }

        @Override
        @JsonDeserialize(as = CloudForecastImpl.class)
        public Builder setCloud(final CloudForecast cloud) {
            return super.setCloud(cloud);
        }
    }
}
