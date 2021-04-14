package fi.fmi.avi.model.sigmet.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.sigmet.AirmetWind;

@FreeBuilder
@JsonDeserialize(builder = AirmetWindImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "speed", "direction" })
public abstract class AirmetWindImpl implements AirmetWind, Serializable {
    private static final long serialVersionUID = -7056053439475425396L;

    public static AirmetWindImpl immutableCopyOf(final AirmetWind airmetWind) {
        Objects.requireNonNull(airmetWind);
        if (airmetWind instanceof AirmetWindImpl) {
            return (AirmetWindImpl) airmetWind;
        } else {
            return Builder.from(airmetWind).build();
        }
    }

    public static Optional<AirmetWindImpl> immutableCopyOf(final Optional<AirmetWind> airmetWind) {
        return airmetWind.map(AirmetWindImpl::immutableCopyOf);
    }

    /*
    public static Optional<UnitPropertyGroupImpl> immutableCopyOf(final Optional<UnitPropertyGroup> UnitPropertyGroup) {
        return UnitPropertyGroup.map(UnitPropertyGroupImpl::immutableCopyOf);
    }
    */

    public abstract Builder toBuilder();

    public static class Builder extends AirmetWindImpl_Builder {

        public static Builder from(final AirmetWind value) {
            if (value instanceof AirmetWindImpl) {
                return ((AirmetWindImpl) value).toBuilder();
            } else {
                return new AirmetWindImpl.Builder()//
                        .setSpeed(NumericMeasureImpl.immutableCopyOf(value.getSpeed())).setDirection(NumericMeasureImpl.immutableCopyOf(value.getDirection()));
            }
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setSpeed(final NumericMeasure speed) {
            return super.setSpeed(NumericMeasureImpl.immutableCopyOf(speed));
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setDirection(final NumericMeasure direction) {
            return super.setDirection(NumericMeasureImpl.immutableCopyOf(direction));
        }

    }
}
