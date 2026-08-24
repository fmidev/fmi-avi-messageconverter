package fi.fmi.avi.model.sigmet.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.sigmet.AirmetWind;

@Value.Immutable
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
            return Builder.copyOf(airmetWind).build();
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

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public abstract NumericMeasure getSpeed();

    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public abstract NumericMeasure getDirection();

    public static class Builder extends ImmutableAirmetWindImpl.Builder {

        public static Builder copyOf(final AirmetWind value) {
            if (value instanceof AirmetWindImpl) {
                return ((AirmetWindImpl) value).toBuilder();
            } else {
                return new AirmetWindImpl.Builder()//
                        .setSpeed(NumericMeasureImpl.immutableCopyOf(value.getSpeed())).setDirection(NumericMeasureImpl.immutableCopyOf(value.getDirection()));
            }
        }



    }
}
