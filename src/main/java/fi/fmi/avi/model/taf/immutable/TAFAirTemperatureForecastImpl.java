package fi.fmi.avi.model.taf.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.taf.TAFAirTemperatureForecast;

/**
 * Created by rinne on 18/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = TAFAirTemperatureForecastImpl.Builder.class)
public abstract class TAFAirTemperatureForecastImpl implements TAFAirTemperatureForecast, Serializable {

    public static TAFAirTemperatureForecastImpl immutableCopyOf(final TAFAirTemperatureForecast airTemperatureForecast) {
        checkNotNull(airTemperatureForecast);
        if (airTemperatureForecast instanceof TAFAirTemperatureForecastImpl) {
            return (TAFAirTemperatureForecastImpl) airTemperatureForecast;
        } else {
            return Builder.from(airTemperatureForecast).build();
        }
    }

    public static Optional<TAFAirTemperatureForecastImpl> immutableCopyOf(final Optional<TAFAirTemperatureForecast> airTemperatureForecast) {
        checkNotNull(airTemperatureForecast);
        return airTemperatureForecast.map(TAFAirTemperatureForecastImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends TAFAirTemperatureForecastImpl_Builder {

        public static Builder from(final TAFAirTemperatureForecast value) {
            return new Builder().setMaxTemperature(NumericMeasureImpl.immutableCopyOf(value.getMaxTemperature()))
                    .setMinTemperature(NumericMeasureImpl.immutableCopyOf(value.getMinTemperature()))
                    .setMaxTemperatureTime(value.getMaxTemperatureTime())
                    .setMinTemperatureTime(value.getMinTemperatureTime());
        }
    }
}
