package fi.fmi.avi.model.swx.immutable;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.swx.AirspaceVolume;

@FreeBuilder
@JsonDeserialize(builder = AirspaceVolumeImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class AirspaceVolumeImpl implements AirspaceVolume {
    public static Builder builder() {
        return new AirspaceVolumeImpl.Builder();
    }

    public abstract Builder toBuilder();

    public static class Builder extends AirspaceVolumeImpl_Builder {
        Builder() {
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setUpperLimit(final NumericMeasure upperLimit) {
            return super.setUpperLimit(upperLimit);
        }
    }
}
