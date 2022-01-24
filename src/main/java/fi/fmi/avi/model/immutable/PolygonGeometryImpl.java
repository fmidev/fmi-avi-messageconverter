package fi.fmi.avi.model.immutable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.CoordinateReferenceSystem;
import fi.fmi.avi.model.PolygonGeometry;
import fi.fmi.avi.util.geoutil.GeoUtils;

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

    public abstract Builder toBuilder();

    public static class Builder extends PolygonGeometryImpl_Builder {

        @Deprecated
        public Builder() {
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

        @JsonDeserialize(as = CoordinateReferenceSystemImpl.class)
        @Override
        public Builder setCrs(final CoordinateReferenceSystem crs) {
            return super.setCrs(crs);
        }

        @Override
        public List<Double> getExteriorRingPositions() {
            return getExteriorRingPositions(true);
        }

        public List<Double> getExteriorRingPositions(boolean CW) {
            //Make sure returned polygon has returned winding
            ArrayList<Double> points = new ArrayList<>(super.getExteriorRingPositions());
            if (CW) {
              if (GeoUtils.getWinding(getExteriorRingPositions())==GeoUtils.Winding.CCW) {
                Collections.reverse(points);
                return points;
              }
            } else {
              if (GeoUtils.getWinding(getExteriorRingPositions())==GeoUtils.Winding.CW) {
                Collections.reverse(points);
                return points;
              }
            }
            return points;
        }

    }
}
