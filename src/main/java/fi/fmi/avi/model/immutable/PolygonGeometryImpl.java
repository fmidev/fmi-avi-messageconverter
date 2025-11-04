package fi.fmi.avi.model.immutable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.CoordinateReferenceSystem;
import fi.fmi.avi.model.PolygonGeometry;
import fi.fmi.avi.model.Winding;
import org.inferred.freebuilder.FreeBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@FreeBuilder
@JsonDeserialize(builder = PolygonGeometryImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class PolygonGeometryImpl implements PolygonGeometry, Serializable {

    private static final long serialVersionUID = 5468081316994649748L;

    public static Builder builder() {
        return new Builder();
    }

    public static PolygonGeometryImpl immutableCopyOf(final PolygonGeometry polygon) {
        Objects.requireNonNull(polygon);
        if (polygon instanceof PolygonGeometryImpl) {
            return (PolygonGeometryImpl) polygon;
        } else {
            return Builder.from(polygon).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<PolygonGeometryImpl> immutableCopyOf(final Optional<PolygonGeometry> polygonsGeometry) {
        Objects.requireNonNull(polygonsGeometry);
        return polygonsGeometry.map(PolygonGeometryImpl::immutableCopyOf);
    }

    @Override
    public Winding getExteriorRingWinding() {
        final List<Double> positions = getExteriorRingPositions();
        return Winding.getWinding(positions);
    }

    @Override
    public List<Double> getExteriorRingPositions(final Winding winding) {
        return Winding.enforceWinding(getExteriorRingPositions(), winding);
    }

    public abstract Builder toBuilder();

    public static class Builder extends PolygonGeometryImpl_Builder {

        Builder() {
        }

        public static Builder from(final PolygonGeometry value) {
            if (value instanceof PolygonGeometryImpl) {
                return ((PolygonGeometryImpl) value).toBuilder();
            } else {
                return PolygonGeometryImpl.builder()//
                        .setCrs(value.getCrs())//
                        .addAllExteriorRingPositions(value.getExteriorRingPositions());
            }
        }

        public Builder setExteriorRingPositions(final List<Double> positions) {
            return clearExteriorRingPositions().addAllExteriorRingPositions(positions);
        }

        @JsonDeserialize(as = CoordinateReferenceSystemImpl.class)
        @Override
        public Builder setCrs(final CoordinateReferenceSystem crs) {
            return super.setCrs(crs);
        }

    }
}
