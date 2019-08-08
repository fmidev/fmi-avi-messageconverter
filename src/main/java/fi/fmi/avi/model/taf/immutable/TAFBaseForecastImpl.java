package fi.fmi.avi.model.taf.immutable;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.SurfaceWind;
import fi.fmi.avi.model.Weather;
import fi.fmi.avi.model.immutable.CloudForecastImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.SurfaceWindImpl;
import fi.fmi.avi.model.immutable.WeatherImpl;
import fi.fmi.avi.model.taf.TAFAirTemperatureForecast;
import fi.fmi.avi.model.taf.TAFBaseForecast;
import fi.fmi.avi.model.taf.TAFForecast;
import fi.fmi.avi.model.taf.TAFForecastBuilderHelper;

/**
 * Created by rinne on 18/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = TAFBaseForecastImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "surfaceWind", "ceilingAndVisibilityOk", "prevailingVisibility", "prevailingVisibilityOperator", "forecastWeather", "noSignificantWeather",
        "cloud", "temperatures" })
public abstract class TAFBaseForecastImpl implements TAFBaseForecast, Serializable {
    private static final long serialVersionUID = -4188299349543187396L;

    public static Builder builder() {
        return new Builder();
    }

    public static TAFBaseForecastImpl immutableCopyOf(final TAFBaseForecast baseForecast) {
        requireNonNull(baseForecast);
        if (baseForecast instanceof TAFBaseForecastImpl) {
            return (TAFBaseForecastImpl) baseForecast;
        } else {
            return Builder.from(baseForecast).build();
        }
    }

    public static Optional<TAFBaseForecastImpl> immutableCopyOf(final Optional<TAFBaseForecast> baseForecast) {
        requireNonNull(baseForecast);
        return baseForecast.map(TAFBaseForecastImpl::immutableCopyOf);
    }

    @Override
    @JsonIgnore
    public boolean areAllTimeReferencesComplete() {
        if (!getTemperatures().isPresent()) {
            return true;
        }
        for (final TAFAirTemperatureForecast temperatureForecast : getTemperatures().get()) {
            if (!temperatureForecast.areAllTimeReferencesComplete()) {
                return false;
            }
        }
        return true;
    }

    public abstract Builder toBuilder();

    public static class Builder extends TAFBaseForecastImpl_Builder implements TAFForecast.Builder<TAFBaseForecastImpl, Builder> {

        @Deprecated
        public Builder() {
            setCeilingAndVisibilityOk(false);
            setNoSignificantWeather(false);
        }

        public static Builder from(final TAFBaseForecast value) {
            if (value instanceof TAFBaseForecastImpl) {
                return ((TAFBaseForecastImpl) value).toBuilder();
            }
            return new Builder().copyFrom(value);
        }

        @Override
        public Builder copyFrom(final TAFForecast value) {
            if (value instanceof TAFBaseForecastImpl) {
                return clear().mergeFrom((TAFBaseForecastImpl) value);
            }
            TAFForecastBuilderHelper.copyFrom(this, value);
            if (value instanceof TAFBaseForecast) {
                final TAFBaseForecast fromBaseForecast = (TAFBaseForecast) value;
                setTemperatures(fromBaseForecast.getTemperatures()//
                        .map(list -> TAFForecastBuilderHelper.toImmutableList(list, TAFAirTemperatureForecastImpl::immutableCopyOf)));
            }
            return this;
        }

        @Override
        public Builder mergeFromTAFForecast(final TAFForecast value) {
            TAFForecastBuilderHelper.mergeFromTAFForecast(this, value);
            if (value instanceof TAFBaseForecast) {
                final TAFBaseForecast fromBaseForecast = (TAFBaseForecast) value;
                fromBaseForecast.getTemperatures()//
                        .map(list -> TAFForecastBuilderHelper.toImmutableList(list, TAFAirTemperatureForecastImpl::immutableCopyOf))//
                        .ifPresent(this::setTemperatures);
            }
            return this;
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setPrevailingVisibility(final NumericMeasure prevailingVisibility) {
            return super.setPrevailingVisibility(prevailingVisibility);
        }

        @Override
        @JsonDeserialize(as = SurfaceWindImpl.class)
        public Builder setSurfaceWind(final SurfaceWind surfaceWind) {
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
