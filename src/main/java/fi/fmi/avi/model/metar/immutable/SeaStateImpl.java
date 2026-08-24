package fi.fmi.avi.model.metar.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.metar.SeaState;

/**
 * Created by rinne on 13/04/2018.
 */
@Value.Immutable
@JsonDeserialize(builder = SeaStateImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "seaSurfaceTemperature", "seaSurfaceTemperatureUnobservableByAutoSystem", "seaSurfaceState", "significantWaveHeight" })
public abstract class SeaStateImpl implements SeaState, Serializable {

    private static final long serialVersionUID = -2776254118856198495L;

    public static Builder builder() {
        return new Builder();
    }

    public static SeaStateImpl immutableCopyOf(final SeaState seaState) {
        Objects.requireNonNull(seaState);
        if (seaState instanceof SeaStateImpl) {
            return (SeaStateImpl) seaState;
        } else {
            return Builder.copyOf(seaState).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<SeaStateImpl> immutableCopyOf(final Optional<SeaState> seaState) {
        Objects.requireNonNull(seaState);
        return seaState.map(SeaStateImpl::immutableCopyOf);
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    @Override
    @JsonDeserialize(contentAs = NumericMeasureImpl.class)
    public abstract Optional<NumericMeasure> getSeaSurfaceTemperature();

    @Override
    @JsonDeserialize(contentAs = NumericMeasureImpl.class)
    public abstract Optional<NumericMeasure> getSignificantWaveHeight();

    public static class Builder extends ImmutableSeaStateImpl.Builder {

        Builder() {
        }

        public static Builder copyOf(final SeaState value) {
            if (value instanceof SeaStateImpl) {
                return ((SeaStateImpl) value).toBuilder();
            } else {
                return SeaStateImpl.builder()//
                        .setSeaSurfaceState(value.getSeaSurfaceState())//
                        .setSeaSurfaceTemperature(NumericMeasureImpl.immutableCopyOf(value.getSeaSurfaceTemperature()))//
                        .setSignificantWaveHeight(NumericMeasureImpl.immutableCopyOf(value.getSignificantWaveHeight()));
            }

        }


    }
}
