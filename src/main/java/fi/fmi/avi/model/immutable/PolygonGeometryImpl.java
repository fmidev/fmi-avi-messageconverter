package fi.fmi.avi.model.immutable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.CoordinateReferenceSystem;
import fi.fmi.avi.model.PolygonGeometry;
import fi.fmi.avi.model.Winding;
import org.immutables.value.Value;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Value.Immutable
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
            return Builder.copyOf(polygon).build();
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

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    @Override
    @JsonDeserialize(contentAs = CoordinateReferenceSystemImpl.class)
    public abstract Optional<CoordinateReferenceSystem> getCrs();

    public static class Builder extends ImmutablePolygonGeometryImpl.Builder {

        Builder() {
        }

        public static Builder copyOf(final PolygonGeometry value) {
            if (value instanceof PolygonGeometryImpl) {
                return ((PolygonGeometryImpl) value).toBuilder();
            } else {
                return PolygonGeometryImpl.builder()//
                        .setCrs(value.getCrs())//
                        .addAllExteriorRingPositions(value.getExteriorRingPositions());
            }
        }

        // A hand-written setExteriorRingPositions(List<Double>) used to live here; Immutables' own
        // generated setExteriorRingPositions(Iterable<Double>) already replaces the whole
        // collection, so the wrapper was redundant once migrated - see docs/07-modernization-plan.md.
    }
}
