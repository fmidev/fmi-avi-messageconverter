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
import fi.fmi.avi.model.sigmet.AirmetCloudLevels;

@Value.Immutable
@JsonDeserialize(builder = AirmetCloudLevelsImpl.Builder.class)
@JsonInclude(JsonInclude.Include.USE_DEFAULTS)
@JsonPropertyOrder({ "base", "top", "topabove" })
public abstract class AirmetCloudLevelsImpl implements AirmetCloudLevels, Serializable {
    private static final long serialVersionUID = 9141069296330300504L;

    public static Builder builder() {
        return new Builder();
    }

    public static AirmetCloudLevelsImpl immutableCopyOf(final AirmetCloudLevels airmetCloudLevels) {
        Objects.requireNonNull(airmetCloudLevels);
        if (airmetCloudLevels instanceof AirmetCloudLevelsImpl) {
            return (AirmetCloudLevelsImpl) airmetCloudLevels;
        } else {
            return Builder.copyOf(airmetCloudLevels).build();
        }
    }

    public static Optional<AirmetCloudLevelsImpl> immutableCopyOf(final Optional<AirmetCloudLevels> airmetWind) {
        return airmetWind.map(AirmetCloudLevelsImpl::immutableCopyOf);
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public abstract NumericMeasure getCloudBase();

    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public abstract NumericMeasure getCloudTop();

    public static class Builder extends ImmutableAirmetCloudLevelsImpl.Builder {

        Builder() {
            this.setCloudBase(NumericMeasureImpl.of(0, ""));
            this.setCloudTop(NumericMeasureImpl.of(0, ""));
        }

        public static Builder copyOf(final AirmetCloudLevels value) {
            if (value instanceof AirmetCloudLevelsImpl) {
                return ((AirmetCloudLevelsImpl) value).toBuilder();
            } else {
                return new AirmetCloudLevelsImpl.Builder()//
                        .setCloudBase(NumericMeasureImpl.immutableCopyOf(value.getCloudBase()))//
                        .setCloudTop(NumericMeasureImpl.immutableCopyOf(value.getCloudTop()))//
                        .setTopAbove(value.getTopAbove());
            }
        }
    }
}
