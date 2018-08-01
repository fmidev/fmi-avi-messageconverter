package fi.fmi.avi.model.metar.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.metar.SeaState;

/**
 * Created by rinne on 13/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = SeaStateImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"seaSurfaceTemperature", "seaSurfaceState", "significantWaveHeight"})
public abstract class SeaStateImpl implements SeaState, Serializable {

    public static SeaStateImpl immutableCopyOf(final SeaState seaState) {
        Objects.nonNull(seaState);
        if (seaState instanceof SeaStateImpl) {
            return (SeaStateImpl) seaState;
        } else {
            return Builder.from(seaState).build();
        }
    }

    public static Optional<SeaStateImpl> immutableCopyOf(final Optional<SeaState> seaState) {
        Objects.nonNull(seaState);
        return seaState.map(SeaStateImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends SeaStateImpl_Builder {

        public static Builder from(final SeaState value) {
            return new SeaStateImpl.Builder().setSeaSurfaceState(value.getSeaSurfaceState())
                    .setSeaSurfaceTemperature(NumericMeasureImpl.immutableCopyOf(value.getSeaSurfaceTemperature()))
                    .setSignificantWaveHeight(NumericMeasureImpl.immutableCopyOf(value.getSignificantWaveHeight()));

        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setSeaSurfaceTemperature(final NumericMeasure seaSurfaceTemperature) {
            return super.setSeaSurfaceTemperature(seaSurfaceTemperature);
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setSignificantWaveHeight(final NumericMeasure significantWaveHeight) {
            return super.setSignificantWaveHeight(significantWaveHeight);
        }
    }
}
