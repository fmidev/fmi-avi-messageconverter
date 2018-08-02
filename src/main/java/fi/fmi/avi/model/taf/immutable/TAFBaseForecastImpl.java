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
import fi.fmi.avi.model.taf.TAFAirTemperatureForecast;
import fi.fmi.avi.model.taf.TAFBaseForecast;
import fi.fmi.avi.model.taf.TAFSurfaceWind;

/**
 * Created by rinne on 18/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = TAFBaseForecastImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"surfaceWind", "ceilingAndVisibilityOk", "prevailingVisibility", "prevailingVisibilityOperator",
        "forecastWeather", "noSignificantWeather", "cloud", "temperatures"})
public abstract class TAFBaseForecastImpl implements TAFBaseForecast, Serializable {

    public static TAFBaseForecastImpl immutableCopyOf(final TAFBaseForecast baseForecast) {
        Objects.requireNonNull(baseForecast);
        if (baseForecast instanceof TAFBaseForecastImpl) {
            return (TAFBaseForecastImpl) baseForecast;
        } else {
            return Builder.from(baseForecast).build();
        }
    }

    public static Optional<TAFBaseForecastImpl> immutableCopyOf(final Optional<TAFBaseForecast> baseForecast) {
        Objects.requireNonNull(baseForecast);
        return baseForecast.map(TAFBaseForecastImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

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

        @Override
        @JsonDeserialize(contentAs = TAFAirTemperatureForecastImpl.class)
        public Builder setTemperatures(final List<TAFAirTemperatureForecast> temperatures) {
            return super.setTemperatures(temperatures);
        }
    }
}
