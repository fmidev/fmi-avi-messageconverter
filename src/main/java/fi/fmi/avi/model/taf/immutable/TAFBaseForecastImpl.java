package fi.fmi.avi.model.taf.immutable;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.Weather;
import fi.fmi.avi.model.immutable.CloudForecastImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.WeatherImpl;
import fi.fmi.avi.model.taf.TAFAirTemperatureForecast;
import fi.fmi.avi.model.taf.TAFBaseForecast;
import fi.fmi.avi.model.taf.TAFForecast;
import fi.fmi.avi.model.taf.TAFSurfaceWind;

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

    public abstract Builder toBuilder();

    public static class Builder extends TAFBaseForecastImpl_Builder {

        public Builder() {
            setCeilingAndVisibilityOk(false);
            setNoSignificantWeather(false);
        }

        public static Builder from(final TAFBaseForecast value) {
            if (value instanceof TAFBaseForecastImpl) {
                return ((TAFBaseForecastImpl) value).toBuilder();
            } else {
                return new Builder().copyFrom(value);
            }
        }

        private static <T, I extends T> List<T> toImmutableList(final List<T> list, final Function<T, I> toImmutable) {
            return list.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(list.stream()//
                    .map(toImmutable)//
                    .collect(Collectors.toList()));
        }

        /**
         * Copies all property values from the given {@code TAFForecast}.
         * Properties specific to {@link TAFBaseForecast} are copied when {@code value} is an instance of {@code TAFBaseForecast}.
         *
         * @param value
         *         copy source
         *
         * @return this builder
         */
        public Builder copyFrom(final TAFForecast value) {
            requireNonNull(value, "value");
            if (value instanceof TAFBaseForecastImpl) {
                return clear().mergeFrom((TAFBaseForecastImpl) value);
            }
            this//
                    .setCeilingAndVisibilityOk(value.isCeilingAndVisibilityOk())
                    .setCloud(CloudForecastImpl.immutableCopyOf(value.getCloud()))
                    .setNoSignificantWeather(value.isNoSignificantWeather())
                    .setPrevailingVisibility(NumericMeasureImpl.immutableCopyOf(value.getPrevailingVisibility()))
                    .setPrevailingVisibilityOperator(value.getPrevailingVisibilityOperator())
                    .setSurfaceWind(TAFSurfaceWindImpl.immutableCopyOf(value.getSurfaceWind()))
                    .setForecastWeather(value.getForecastWeather()//
                            .map(list -> toImmutableList(list, WeatherImpl::immutableCopyOf)));
            if (value instanceof TAFBaseForecast) {
                final TAFBaseForecast otherBaseForecast = (TAFBaseForecast) value;
                setTemperatures(otherBaseForecast.getTemperatures()//
                        .map(list -> toImmutableList(list, TAFAirTemperatureForecastImpl::immutableCopyOf)));
            }

            return this;
        }

        /**
         * Merges the given {@code TAFForecast} into this builder.
         * All existing properties of {@code value} are copied into this builder and properties that are empty in {@code value} are left as is in this builder
         * with some exceptions depending on property values of {@code value}:
         *
         * <ul>
         * <li>
         * {@code prevailingVisibility}, {@code forecastWeather} and {@code cloudForecast} are always copied when {@code ceilingAndVisibilityOk == true}.
         * </li>
         * <li>{@code prevailingVisibilityOperator} is copied always whenever {@code prevailingVisibility} is copied.</li>
         * <li>{@code forecastWeather} is always copied when {@code noSignificantWeather == true}.</li>
         * <li>{@code noSignificantWeather} is always set to {@code false}</li>
         * </ul>
         *
         * @param value
         *         merge source
         *
         * @return this builder
         */
        public Builder mergeFromTAFForecast(final TAFForecast value) {
            requireNonNull(value, "value");
            this//
                    .mergeCeilingAndVisibilityOk(value)
                    .mergePrevailingVisibility(value)
                    .mergePrevailingVisibilityOperator(value)
                    .mergeSurfaceWind(value)
                    .mergeForecastWeather(value)
                    .mergeCloud(value);
            if (value instanceof TAFBaseForecast) {
                mergeTemperatures((TAFBaseForecast) value);
            }
            return this;
        }

        private Builder mergeCeilingAndVisibilityOk(final TAFForecast from) {
            return setCeilingAndVisibilityOk(from.isCeilingAndVisibilityOk());
        }

        private Builder mergePrevailingVisibility(final TAFForecast from) {
            if (from.isCeilingAndVisibilityOk()) {
                setPrevailingVisibility(from.getPrevailingVisibility().map(NumericMeasureImpl::immutableCopyOf));
            } else {
                from.getPrevailingVisibility()//
                        .map(NumericMeasureImpl::immutableCopyOf)//
                        .ifPresent(this::setPrevailingVisibility);
            }
            return this;
        }

        private Builder mergePrevailingVisibilityOperator(final TAFForecast from) {
            final Optional<AviationCodeListUser.RelationalOperator> prevailingVisibilityOperator = from.getPrevailingVisibilityOperator();
            if (prevailingVisibilityOperator.isPresent() || from.getPrevailingVisibility().isPresent()) {
                setPrevailingVisibilityOperator(prevailingVisibilityOperator);
            }
            return this;
        }

        private Builder mergeSurfaceWind(final TAFForecast from) {
            from.getSurfaceWind()//
                    .map(TAFSurfaceWindImpl::immutableCopyOf)//
                    .ifPresent(this::setSurfaceWind);
            return this;
        }

        private Builder mergeForecastWeather(final TAFForecast from) {
            if (from.isCeilingAndVisibilityOk() || from.isNoSignificantWeather()) {
                setForecastWeather(from.getForecastWeather()//
                        .map(list -> toImmutableList(list, WeatherImpl::immutableCopyOf)));
            } else {
                from.getForecastWeather()//
                        .map(list -> toImmutableList(list, WeatherImpl::immutableCopyOf))//
                        .ifPresent(this::setForecastWeather);
            }
            setNoSignificantWeather(false);
            return this;
        }

        private Builder mergeCloud(final TAFForecast from) {
            if (from.isCeilingAndVisibilityOk()) {
                setCloud(from.getCloud().map(CloudForecastImpl::immutableCopyOf));
            } else {
                from.getCloud()//
                        .map(CloudForecastImpl::immutableCopyOf)//
                        .ifPresent(this::setCloud);
            }
            return this;
        }

        private Builder mergeTemperatures(final TAFBaseForecast from) {
            from.getTemperatures()//
                    .map(list -> toImmutableList(list, TAFAirTemperatureForecastImpl::immutableCopyOf))//
                    .ifPresent(this::setTemperatures);
            return this;
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
