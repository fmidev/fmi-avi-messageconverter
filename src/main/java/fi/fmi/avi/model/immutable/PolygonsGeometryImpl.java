package fi.fmi.avi.model.immutable;

import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.PolygonsGeometry;

@FreeBuilder
@JsonDeserialize(builder = PolygonsGeometryImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class PolygonsGeometryImpl implements PolygonsGeometry {

    public static Builder builder() {
        return new Builder();
    }

    public static PolygonsGeometryImpl immutableCopyOf(final PolygonsGeometry polygonsGeometry) {
        Objects.requireNonNull(polygonsGeometry);
        if (polygonsGeometry instanceof PolygonsGeometryImpl) {
            return (PolygonsGeometryImpl) polygonsGeometry;
        } else {
            return Builder.from(polygonsGeometry).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<PolygonsGeometryImpl> immutableCopyOf(final Optional<PolygonsGeometry> polygonsGeometry) {
        Objects.requireNonNull(polygonsGeometry);
        return polygonsGeometry.map(PolygonsGeometryImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends PolygonsGeometryImpl_Builder {

        @Deprecated
        public Builder() {
        }

        public static Builder from(final PolygonsGeometry value) {
            if (value instanceof PolygonsGeometryImpl) {
                return ((PolygonsGeometryImpl) value).toBuilder();
            } else {
                return PolygonsGeometryImpl.builder()//
                .setPolygons(value.getPolygons());
            }
        }


        @Override
        public final Builder setPolygons(final Double[][][]points){
            return super.setPolygons(points);
        }
    }
}
