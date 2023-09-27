package fi.fmi.avi.model.immutable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.CoordinateReferenceSystem;
import fi.fmi.avi.model.MultiPolygonGeometry;
import fi.fmi.avi.model.Winding;

@FreeBuilder
@JsonDeserialize(builder = MultiPolygonGeometryImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class MultiPolygonGeometryImpl implements MultiPolygonGeometry, Serializable {

    private static final long serialVersionUID = 4308464817438332280L;

    public static Builder builder() {
        return new Builder();
    }

    public static MultiPolygonGeometryImpl immutableCopyOf(final MultiPolygonGeometry polygon) {
        Objects.requireNonNull(polygon);
        if (polygon instanceof MultiPolygonGeometryImpl) {
            return (MultiPolygonGeometryImpl) polygon;
        } else {
            return Builder.from(polygon).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<MultiPolygonGeometryImpl> immutableCopyOf(final Optional<MultiPolygonGeometry> polygonsGeometry) {
        Objects.requireNonNull(polygonsGeometry);
        return polygonsGeometry.map(MultiPolygonGeometryImpl::immutableCopyOf);
    }

    @Override
    public List<List<Double>> getExteriorRingPositions(final Winding winding) {
        List<List<Double>> newPolygons = new ArrayList<>();
        for (List<Double> partPolygon: getExteriorRingPositions()) {
            List<Double> polygon = Winding.enforceWinding(partPolygon, winding);
            newPolygons.add(polygon);
        }
        return newPolygons;
    }

    public abstract Builder toBuilder();

    public static class Builder extends MultiPolygonGeometryImpl_Builder {

        Builder() {
        }

        public static Builder from(final MultiPolygonGeometry value) {
            if (value instanceof MultiPolygonGeometryImpl) {
                return ((MultiPolygonGeometryImpl) value).toBuilder();
            } else {
                return MultiPolygonGeometryImpl.builder()//
                        .setCrs(value.getCrs())//
                        .addAllExteriorRingPositions(value.getExteriorRingPositions());

            }
        }

        @JsonDeserialize(as = CoordinateReferenceSystemImpl.class)
        @Override
        public Builder setCrs(final CoordinateReferenceSystem crs) {
            return super.setCrs(crs);
        }

    }
}
