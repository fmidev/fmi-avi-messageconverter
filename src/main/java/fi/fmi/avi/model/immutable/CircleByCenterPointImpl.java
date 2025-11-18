package fi.fmi.avi.model.immutable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.CircleByCenterPoint;
import fi.fmi.avi.model.CoordinateReferenceSystem;
import fi.fmi.avi.model.NumericMeasure;
import org.inferred.freebuilder.FreeBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@FreeBuilder
@JsonDeserialize(builder = CircleByCenterPointImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class CircleByCenterPointImpl implements CircleByCenterPoint, Serializable {

    private static final long serialVersionUID = 2380951223773931004L;

    public static Builder builder() {
        return new Builder();
    }

    public static CircleByCenterPointImpl immutableCopyOf(final CircleByCenterPoint geom) {
        Objects.requireNonNull(geom);
        if (geom instanceof CircleByCenterPointImpl) {
            return (CircleByCenterPointImpl) geom;
        } else {
            return CircleByCenterPointImpl.Builder.from(geom).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<CircleByCenterPointImpl> immutableCopyOf(final Optional<CircleByCenterPoint> geom) {
        Objects.requireNonNull(geom);
        return geom.map(CircleByCenterPointImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends CircleByCenterPointImpl_Builder {
        Builder() {
        }

        public static Builder from(final CircleByCenterPoint value) {
            if (value instanceof CircleByCenterPointImpl) {
                return ((CircleByCenterPointImpl) value).toBuilder();
            } else {
                return CircleByCenterPointImpl.builder()//
                        .setCrs(value.getCrs())//
                        .addAllCenterPointCoordinates(value.getCenterPointCoordinates());
            }
        }

        @JsonDeserialize(as = CoordinateReferenceSystemImpl.class)
        @Override
        public Builder setCrs(final CoordinateReferenceSystem crs) {
            return super.setCrs(crs);
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setRadius(final NumericMeasure radius) {
            return super.setRadius(radius);
        }

        public Builder setCenterPointCoordinates(final List<Double> coordinates) {
            return this.clearCenterPointCoordinates().addAllCenterPointCoordinates(coordinates);
        }
    }

}
