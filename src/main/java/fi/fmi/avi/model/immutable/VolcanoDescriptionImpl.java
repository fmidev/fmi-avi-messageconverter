package fi.fmi.avi.model.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.ElevatedPoint;
import fi.fmi.avi.model.VolcanoDescription;

@FreeBuilder
@JsonDeserialize(builder = VolcanoDescriptionImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"volcanoPosition", "volcanoName"})
public abstract class VolcanoDescriptionImpl implements VolcanoDescription, Serializable {

     public static VolcanoDescriptionImpl immutableCopyOf(final VolcanoDescription volcanoDescription) {
        Objects.requireNonNull(volcanoDescription);
        if (volcanoDescription instanceof VolcanoDescriptionImpl) {
            return (VolcanoDescriptionImpl) volcanoDescription;
        } else {
            return Builder.from(volcanoDescription).build();
        }
    }

    public static Optional<VolcanoDescriptionImpl> immutableCopyOf(final Optional<VolcanoDescription> volcanoDescription) {
        return volcanoDescription.map(VolcanoDescriptionImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends VolcanoDescriptionImpl_Builder {

        public static Builder from(final VolcanoDescription value) {
            if (value instanceof VolcanoDescriptionImpl) {
                return ((VolcanoDescriptionImpl) value).toBuilder();
            } else {
                return new VolcanoDescriptionImpl.Builder().setVolcanoName(value.getVolcanoName())
                        .setVolcanoPosition(ElevatedPointImpl.immutableCopyOf(value.getVolcanoPosition()));
            }
        }

        @Override
        @JsonDeserialize(as = ElevatedPointImpl.class)
        public Builder setVolcanoPosition(final ElevatedPoint volcanoPosition) {
            return super.setVolcanoPosition(volcanoPosition);
        }

 /*       @Override
        public Builder setVolcanoName(String volcanoName) {
            return super.setVolcanoName(Optional.of(volcanoName));
        }

        public Builder setVolcanoName(Optional<String> volcanoName) {
            return super.setVolcanoName(volcanoName);
        }*/
    }

}
