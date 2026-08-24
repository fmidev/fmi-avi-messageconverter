package fi.fmi.avi.model.immutable;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.CoordinateReferenceSystem;
import fi.fmi.avi.model.ElevatedPoint;

/**
 * Created by rinne on 17/04/2018.
 */
@Value.Immutable
@JsonDeserialize(builder = ElevatedPointImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class ElevatedPointImpl implements ElevatedPoint, Serializable {

    private static final long serialVersionUID = -69857237712526561L;

    public static Builder builder() {
        return new Builder();
    }

    public static ElevatedPointImpl immutableCopyOf(final ElevatedPoint geoPosition) {
        Objects.requireNonNull(geoPosition);
        if (geoPosition instanceof ElevatedPointImpl) {
            return (ElevatedPointImpl) geoPosition;
        } else {
            return Builder.copyOf(geoPosition).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<ElevatedPointImpl> immutableCopyOf(final Optional<ElevatedPoint> geoPosition) {
        Objects.requireNonNull(geoPosition);
        return geoPosition.map(ElevatedPointImpl::immutableCopyOf);
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    @Override
    @JsonDeserialize(contentAs = CoordinateReferenceSystemImpl.class)
    public abstract Optional<CoordinateReferenceSystem> getCrs();

    public static class Builder extends ImmutableElevatedPointImpl.Builder {

        Builder() {
        }

        public static Builder copyOf(final ElevatedPoint value) {
            if (value instanceof ElevatedPointImpl) {
                return ((ElevatedPointImpl) value).toBuilder();
            } else {
                return ElevatedPointImpl.builder()//
                        .setCrs(value.getCrs())//
                        .addAllCoordinates(value.getCoordinates()).setElevationUom(value.getElevationUom()).setElevationValue(value.getElevationValue());
            }
        }
    }
}
