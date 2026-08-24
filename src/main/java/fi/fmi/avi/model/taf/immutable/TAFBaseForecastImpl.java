package fi.fmi.avi.model.taf.immutable;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.BuilderHelper;
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
 * See {@code METARImpl}'s javadoc (in {@code fi.fmi.avi.model.metar.immutable}) for the "detached
 * builder" pattern this class (and {@code Builder}) uses instead of extending an
 * Immutables-generated builder directly.
 */
@Value.Immutable
@Value.Style(init = "set*", get = { "is*", "get*" },
        passAnnotations = { com.fasterxml.jackson.databind.annotation.JsonDeserialize.class, com.fasterxml.jackson.annotation.JsonProperty.class },
        typeInnerBuilder = "InternalImmutableBuilder", builder = "internalBuilder")
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
            return Builder.copyOf(baseForecast).build();
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

    public Builder toBuilder() {
        return new Builder().mergeFrom(this);
    }

    public static class Builder extends AbstractTAFForecastBuilderImpl<TAFBaseForecastImpl, Builder> {

        @JsonIgnore
        private Optional<List<TAFAirTemperatureForecast>> temperatures = Optional.empty();

        Builder() {
            setCeilingAndVisibilityOk(false);
            setNoSignificantWeather(false);
        }

        public Optional<List<TAFAirTemperatureForecast>> getTemperatures() {
            return temperatures;
        }

        @JsonProperty("temperatures")
        @JsonDeserialize(contentAs = TAFAirTemperatureForecastImpl.class)
        public Builder setTemperatures(final List<TAFAirTemperatureForecast> temperatures) {
            this.temperatures = Optional.of(requireNonNull(temperatures, "temperatures"));
            return this;
        }

        public Builder setTemperatures(final Optional<? extends List<TAFAirTemperatureForecast>> temperatures) {
            requireNonNull(temperatures, "temperatures");
            this.temperatures = temperatures.isPresent() ? Optional.of(temperatures.get()) : Optional.empty();
            return this;
        }

        public static Builder copyOf(final TAFBaseForecast value) {
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
                        .map(list -> BuilderHelper.toImmutableList(list, TAFAirTemperatureForecastImpl::immutableCopyOf)));
            }
            return this;
        }

        @Override
        public Builder mergeFromTAFForecast(final TAFForecast value) {
            TAFForecastBuilderHelper.mergeFromTAFForecast(this, value);
            if (value instanceof TAFBaseForecast) {
                final TAFBaseForecast fromBaseForecast = (TAFBaseForecast) value;
                fromBaseForecast.getTemperatures()//
                        .map(list -> BuilderHelper.toImmutableList(list, TAFAirTemperatureForecastImpl::immutableCopyOf))//
                        .ifPresent(this::setTemperatures);
            }
            return this;
        }

        @Override
        public Builder mergeFrom(final TAFBaseForecastImpl template) {
            super.mergeFrom(template);
            this.temperatures = template.getTemperatures();
            return this;
        }

        @Override
        public Builder mergeFrom(final Builder template) {
            super.mergeFrom(template);
            this.temperatures = template.temperatures;
            return this;
        }

        @Override
        public Builder clear() {
            super.clear();
            this.temperatures = Optional.empty();
            return this;
        }

        @Override
        public ImmutableTAFBaseForecastImpl build() {
            final ImmutableTAFBaseForecastImpl.Builder delegate = ImmutableTAFBaseForecastImpl.internalBuilder()//
                    .setCeilingAndVisibilityOk(isCeilingAndVisibilityOk())//
                    .setNoSignificantWeather(isNoSignificantWeather());
            getPrevailingVisibility().ifPresent(delegate::setPrevailingVisibility);
            getPrevailingVisibilityOperator().ifPresent(delegate::setPrevailingVisibilityOperator);
            getSurfaceWind().ifPresent(delegate::setSurfaceWind);
            getForecastWeather().ifPresent(delegate::setForecastWeather);
            getCloud().ifPresent(delegate::setCloud);
            getTemperatures().ifPresent(delegate::setTemperatures);
            return delegate.build();
        }

        @Override
        @JsonProperty("prevailingVisibility")
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setPrevailingVisibility(final NumericMeasure prevailingVisibility) {
            return super.setPrevailingVisibility(prevailingVisibility);
        }

        @Override
        @JsonProperty("surfaceWind")
        @JsonDeserialize(as = SurfaceWindImpl.class)
        public Builder setSurfaceWind(final SurfaceWind surfaceWind) {
            return super.setSurfaceWind(surfaceWind);
        }

        @Override
        @JsonProperty("forecastWeather")
        @JsonDeserialize(contentAs = WeatherImpl.class)
        public Builder setForecastWeather(final List<Weather> forecastWeather) {
            return super.setForecastWeather(forecastWeather);
        }

        @Override
        @JsonProperty("cloud")
        @JsonDeserialize(as = CloudForecastImpl.class)
        public Builder setCloud(final CloudForecast cloud) {
            return super.setCloud(cloud);
        }
    }
}
