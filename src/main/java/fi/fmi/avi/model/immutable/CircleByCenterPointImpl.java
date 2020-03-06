package fi.fmi.avi.model.immutable;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.CircleByCenterPoint;
import fi.fmi.avi.model.NumericMeasure;

@FreeBuilder
@JsonDeserialize(builder = CircleByCenterPointImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class CircleByCenterPointImpl implements CircleByCenterPoint {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends CircleByCenterPointImpl_Builder {
        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setRadius(final NumericMeasure radius) {
            return super.setRadius(radius);
        }
    }

}
