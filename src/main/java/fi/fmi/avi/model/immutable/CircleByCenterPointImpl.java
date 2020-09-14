package fi.fmi.avi.model.immutable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
                        .setSrsName(value.getSrsName())//
                        .setSrsDimension(value.getSrsDimension())//
                        .setAxisLabels(value.getAxisLabels())//
                        .addAllCenterPointCoordinates(value.getCenterPointCoordinates());
            }
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
