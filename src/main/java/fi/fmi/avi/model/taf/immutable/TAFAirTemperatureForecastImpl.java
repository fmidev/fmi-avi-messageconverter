package fi.fmi.avi.model.taf.immutable;


import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.taf.TAFAirTemperatureForecast;

/**
 * Created by rinne on 18/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = TAFAirTemperatureForecastImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"maxTemperature", "maxTemperatureTime", "minTemperature", "minTemperatureTime"})
public abstract class TAFAirTemperatureForecastImpl implements TAFAirTemperatureForecast, Serializable {

    private static final long serialVersionUID = 4723016643452217407L;

    public static Builder builder() {
        return new Builder();
    }

    public static TAFAirTemperatureForecastImpl immutableCopyOf(final TAFAirTemperatureForecast airTemperatureForecast) {
        Objects.requireNonNull(airTemperatureForecast);
        if (airTemperatureForecast instanceof TAFAirTemperatureForecastImpl) {
            return (TAFAirTemperatureForecastImpl) airTemperatureForecast;
        } else {
            return Builder.from(airTemperatureForecast).build();
        }
    }

    public static Optional<TAFAirTemperatureForecastImpl> immutableCopyOf(final Optional<TAFAirTemperatureForecast> airTemperatureForecast) {
        Objects.requireNonNull(airTemperatureForecast);
        return airTemperatureForecast.map(TAFAirTemperatureForecastImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends TAFAirTemperatureForecastImpl_Builder {

        @Deprecated
        public Builder() {
        }

        public static Builder from(final TAFAirTemperatureForecast value) {
            if (value instanceof TAFAirTemperatureForecastImpl) {
                return ((TAFAirTemperatureForecastImpl) value).toBuilder();
            } else {
                return new Builder()//
                        .setMaxTemperature(NumericMeasureImpl.immutableCopyOf(value.getMaxTemperature()))
                        .setMinTemperature(NumericMeasureImpl.immutableCopyOf(value.getMinTemperature()))
                        .setMaxTemperatureTime(value.getMaxTemperatureTime())
                        .setMinTemperatureTime(value.getMinTemperatureTime());
            }
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setMaxTemperature(final NumericMeasure maxTemperature) {
            return super.setMaxTemperature(maxTemperature);
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setMinTemperature(final NumericMeasure minTemperature) {
            return super.setMinTemperature(minTemperature);
        }
    }
}
