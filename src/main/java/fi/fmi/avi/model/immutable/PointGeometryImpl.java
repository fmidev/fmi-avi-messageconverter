package fi.fmi.avi.model.immutable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.PointGeometry;

@FreeBuilder
@JsonDeserialize(builder = PointGeometryImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class PointGeometryImpl implements PointGeometry {

    public static Builder builder() {
        return new Builder();
    }

    public static PointGeometryImpl immutableCopyOf(final PointGeometry pointGeometry) {
        Objects.requireNonNull(pointGeometry);
        if (pointGeometry instanceof ElevatedPointImpl) {
            return (PointGeometryImpl) pointGeometry;
        } else {
            return Builder.from(pointGeometry).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<PointGeometryImpl> immutableCopyOf(final Optional<PointGeometry> pointGeometry) {
        Objects.requireNonNull(pointGeometry);
        return pointGeometry.map(PointGeometryImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends PointGeometryImpl_Builder {

        @Deprecated
        public Builder() {
        }

        public static Builder from(final PointGeometry value) {
            if (value instanceof PointGeometryImpl) {
                return ((PointGeometryImpl) value).toBuilder();
            } else {
                return PointGeometryImpl.builder().setSrsName(value.getSrsName())//
                        .setSrsDimension(value.getSrsDimension())//
                        .setAxisLabels(value.getAxisLabels())//
                        .addAllCoordinates(value.getCoordinates());
            }
        }

        public Builder setCoordinates(final List<Double> coordinates) {
            return this.clearCoordinates().addAllCoordinates(coordinates);
        }

    }
}
