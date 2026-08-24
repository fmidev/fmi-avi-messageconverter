package fi.fmi.avi.model.taf.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.taf.TAFAirTemperatureForecast;

/**
 * Created by rinne on 18/04/2018.
 */
@Value.Immutable
@JsonDeserialize(builder = TAFAirTemperatureForecastImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "maxTemperature", "maxTemperatureTime", "minTemperature", "minTemperatureTime" })
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
            return Builder.copyOf(airTemperatureForecast).build();
        }
    }

    public static Optional<TAFAirTemperatureForecastImpl> immutableCopyOf(final Optional<TAFAirTemperatureForecast> airTemperatureForecast) {
        Objects.requireNonNull(airTemperatureForecast);
        return airTemperatureForecast.map(TAFAirTemperatureForecastImpl::immutableCopyOf);
    }

    @Override
    @JsonIgnore
    public boolean areAllTimeReferencesComplete() {
        return getMinTemperatureTime().getCompleteTime().isPresent() && getMaxTemperatureTime().getCompleteTime().isPresent();
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public abstract NumericMeasure getMaxTemperature();

    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public abstract NumericMeasure getMinTemperature();

    public static class Builder extends ImmutableTAFAirTemperatureForecastImpl.Builder {

        Builder() {
        }

        public static Builder copyOf(final TAFAirTemperatureForecast value) {
            if (value instanceof TAFAirTemperatureForecastImpl) {
                return ((TAFAirTemperatureForecastImpl) value).toBuilder();
            } else {
                return builder()//
                        .setMaxTemperature(NumericMeasureImpl.immutableCopyOf(value.getMaxTemperature()))
                        .setMinTemperature(NumericMeasureImpl.immutableCopyOf(value.getMinTemperature()))
                        .setMaxTemperatureTime(value.getMaxTemperatureTime())
                        .setMinTemperatureTime(value.getMinTemperatureTime());
            }
        }


    }
}
