package fi.fmi.avi.model.swx.amd79.immutable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.swx.amd79.AirspaceVolume;
import org.inferred.freebuilder.FreeBuilder;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

@FreeBuilder
@JsonDeserialize(builder = AirspaceVolumeImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "horizontalProjection", "upperLimit", "upperLimitReference", "lowerLimit", "lowerLimitReference", "maximumLimit", "maximumLimitReference",
        "minimumLimit", "minimumLimitReference", "width" })
public abstract class AirspaceVolumeImpl implements AirspaceVolume, Serializable {

    private static final long serialVersionUID = 3293242693002143947L;

    public static Builder builder() {
        return new AirspaceVolumeImpl.Builder();
    }

    public static AirspaceVolumeImpl immutableCopyOf(final AirspaceVolume airspaceVolume) {
        Objects.requireNonNull(airspaceVolume);
        if (airspaceVolume instanceof AirspaceVolumeImpl) {
            return (AirspaceVolumeImpl) airspaceVolume;
        } else {
            return Builder.from(airspaceVolume).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<AirspaceVolumeImpl> immutableCopyOf(final Optional<AirspaceVolume> airspaceVolume) {
        Objects.requireNonNull(airspaceVolume);
        return airspaceVolume.map(AirspaceVolumeImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends AirspaceVolumeImpl_Builder {
        @Deprecated
        Builder() {
        }

        public static Builder from(final AirspaceVolume value) {
            if (value instanceof AirspaceVolumeImpl) {
                return ((AirspaceVolumeImpl) value).toBuilder();
            } else {
                return builder().setHorizontalProjection(value.getHorizontalProjection())
                        .setUpperLimit(value.getUpperLimit())
                        .setUpperLimitReference(value.getUpperLimitReference())
                        .setLowerLimit(value.getLowerLimit())
                        .setLowerLimitReference(value.getLowerLimitReference())
                        .setMaximumLimit(value.getMaximumLimit())
                        .setMaximumLimitReference(value.getMaximumLimitReference())
                        .setMinimumLimit(value.getMinimumLimit())
                        .setMinimumLimitReference(value.getMinimumLimitReference())
                        .setWidth(value.getWidth());
            }
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setUpperLimit(final NumericMeasure limit) {
            return super.setUpperLimit(limit);
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setLowerLimit(final NumericMeasure limit) {
            return super.setLowerLimit(limit);
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setMaximumLimit(final NumericMeasure limit) {
            return super.setMaximumLimit(limit);
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setMinimumLimit(final NumericMeasure limit) {
            return super.setMinimumLimit(limit);
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setWidth(final NumericMeasure width) {
            return super.setWidth(width);
        }
    }
}
