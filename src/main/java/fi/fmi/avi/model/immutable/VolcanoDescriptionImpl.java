package fi.fmi.avi.model.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.ElevatedPoint;
import fi.fmi.avi.model.VolcanoDescription;

@Value.Immutable
@JsonDeserialize(builder = VolcanoDescriptionImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "volcanoPosition", "volcanoName" })
public abstract class VolcanoDescriptionImpl implements VolcanoDescription, Serializable {
    private static final long serialVersionUID = -8358165880683740153L;

    public static VolcanoDescriptionImpl immutableCopyOf(final VolcanoDescription volcanoDescription) {
        Objects.requireNonNull(volcanoDescription);
        if (volcanoDescription instanceof VolcanoDescriptionImpl) {
            return (VolcanoDescriptionImpl) volcanoDescription;
        } else {
            return Builder.copyOf(volcanoDescription).build();
        }
    }

    public static Optional<VolcanoDescriptionImpl> immutableCopyOf(final Optional<VolcanoDescription> volcanoDescription) {
        return volcanoDescription.map(VolcanoDescriptionImpl::immutableCopyOf);
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    @Override
    @JsonDeserialize(contentAs = ElevatedPointImpl.class)
    public abstract Optional<ElevatedPoint> getVolcanoPosition();

    public static class Builder extends ImmutableVolcanoDescriptionImpl.Builder {

        public static Builder copyOf(final VolcanoDescription value) {
            if (value instanceof VolcanoDescriptionImpl) {
                return ((VolcanoDescriptionImpl) value).toBuilder();
            } else {
                return new VolcanoDescriptionImpl.Builder().setVolcanoName(value.getVolcanoName())
                        .setVolcanoPosition(ElevatedPointImpl.immutableCopyOf(value.getVolcanoPosition()));
            }
        }

    }

}
