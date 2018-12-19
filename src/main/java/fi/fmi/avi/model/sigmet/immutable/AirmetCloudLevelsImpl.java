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
import fi.fmi.avi.model.sigmet.AirmetCloudLevels;
import fi.fmi.avi.model.sigmet.AirmetWind;

@FreeBuilder
@JsonDeserialize(builder = AirmetCloudLevelsImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"bottom", "top"})
public abstract class AirmetCloudLevelsImpl implements AirmetCloudLevels, Serializable {
    public static AirmetCloudLevelsImpl immutableCopyOf(final AirmetCloudLevels airmetCloudLevels) {
        Objects.requireNonNull(airmetCloudLevels);
        if (airmetCloudLevels instanceof AirmetCloudLevelsImpl) {
            return (AirmetCloudLevelsImpl) airmetCloudLevels;
        } else {
            return Builder.from(airmetCloudLevels).build();
        }
    }

    public static Optional<AirmetCloudLevelsImpl> immutableCopyOf(final Optional<AirmetCloudLevels> airmetWind) {
        return airmetWind.map(AirmetCloudLevelsImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends AirmetCloudLevelsImpl_Builder {

        public Builder() {
            this.setCloudBottom(NumericMeasureImpl.of(0,""));
            this.setCloudTop(NumericMeasureImpl.of(0, ""));
        }

        public static Builder from(final AirmetCloudLevels value) {
            if (value instanceof AirmetCloudLevelsImpl) {
                return ((AirmetCloudLevelsImpl) value).toBuilder();
            } else {
                AirmetCloudLevelsImpl.Builder builder=new AirmetCloudLevelsImpl.Builder();
                return new AirmetCloudLevelsImpl.Builder()
                        .setCloudBottom(value.getCloudBottom())
                        .setCloudTop(value.getCloudTop())
                ;
            }
        }

        @Override
        @JsonDeserialize(as=NumericMeasureImpl.class)
        public Builder setCloudBottom(NumericMeasure bottom) {
            return super.setCloudBottom(NumericMeasureImpl.immutableCopyOf(bottom));
        }

        @Override
        @JsonDeserialize(as=NumericMeasureImpl.class)
        public Builder setCloudTop(NumericMeasure top) {
            return super.setCloudTop(NumericMeasureImpl.immutableCopyOf(top));
        }
    }
}
