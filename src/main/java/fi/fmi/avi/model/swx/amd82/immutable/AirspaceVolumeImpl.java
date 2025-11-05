package fi.fmi.avi.model.swx.amd82.immutable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.Geometry;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.PolygonGeometry;
import fi.fmi.avi.model.immutable.CircleByCenterPointImpl;
import fi.fmi.avi.model.immutable.CoordinateReferenceSystemImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.PolygonGeometryImpl;
import fi.fmi.avi.model.swx.VerticalLimits;
import fi.fmi.avi.model.swx.VerticalLimitsImpl;
import fi.fmi.avi.model.swx.amd82.AirspaceVolume;
import fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion.SpaceWeatherLocation;
import fi.fmi.avi.util.SubSolarPointUtils;
import org.inferred.freebuilder.FreeBuilder;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@FreeBuilder
@JsonDeserialize(builder = AirspaceVolumeImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"horizontalProjection", "upperLimit", "upperLimitReference", "lowerLimit", "lowerLimitReference", "maximumLimit", "maximumLimitReference",
        "minimumLimit", "minimumLimitReference", "width"})
public abstract class AirspaceVolumeImpl implements AirspaceVolume, Serializable {

    private static final long serialVersionUID = 3293242693002143947L;

    public static Builder builder() {
        return new Builder();
    }

    public static AirspaceVolumeImpl immutableCopyOf(final AirspaceVolume airspaceVolume) {
        Objects.requireNonNull(airspaceVolume);
        if (airspaceVolume instanceof AirspaceVolumeImpl) {
            return (AirspaceVolumeImpl) airspaceVolume;
        } else {
            return Builder.from(airspaceVolume).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<AirspaceVolumeImpl> immutableCopyOf(final Optional<AirspaceVolume> airspaceVolume) {
        Objects.requireNonNull(airspaceVolume);
        return airspaceVolume.map(AirspaceVolumeImpl::immutableCopyOf);
    }

    private static Geometry buildPolygonGeometry(final double minLatitude, final double minLongitude,
                                                 final double maxLatitude, final double maxLongitude) {
        final double normalizedMinLongitude = Math.abs(minLongitude) == 180d ? -180d : minLongitude;
        final double normalizedMaxLongitude = Math.abs(maxLongitude) == 180d ? 180d : maxLongitude;
        return PolygonGeometryImpl.builder()
                .setCrs(CoordinateReferenceSystemImpl.wgs84())
                .mutateExteriorRingPositions(coordinates ->
                        addPolygonRingPositions(coordinates, minLatitude, normalizedMinLongitude, maxLatitude, normalizedMaxLongitude))
                .build();
    }

    private static void addPolygonRingPositions(final List<Double> coordinates, final double minLat, final double minLon,
                                                final double maxLat, final double maxLon) {
        // Upper left corner:
        coordinates.add(minLat);
        coordinates.add(minLon);

        // Lower left corner:
        coordinates.add(maxLat);
        coordinates.add(minLon);

        // Lower right corner:
        coordinates.add(maxLat);
        coordinates.add(maxLon);

        // Upper right corner:
        coordinates.add(minLat);
        coordinates.add(maxLon);

        // Upper left corner (again, to close the ring):
        coordinates.add(minLat);
        coordinates.add(minLon);
    }

    /**
     * Creates an airspace volume for the daylight side using the sub-solar point.
     *
     * @param analysisTime the time to compute the sub-solar point for
     * @return the built airspace volume
     */
    public static AirspaceVolumeImpl forDaylightSide(final Instant analysisTime) {
        return builder()
                .setHorizontalProjection(
                        CircleByCenterPointImpl.builder()
                                .setCrs(CoordinateReferenceSystemImpl.wgs84())
                                .setCenterPointCoordinates(SubSolarPointUtils.computeSubSolarPoint(analysisTime))
                                .setRadius(NumericMeasureImpl.builder()
                                        .setUom("km")
                                        .setValue(SubSolarPointUtils.DAYSIDE_RADIUS_KM)
                                        .build())
                                .build())
                .build();
    }

    /**
     * Creates an airspace volume from a polygon geometry and vertical limits.
     *
     * @param polygon        the polygon geometry
     * @param verticalLimits the vertical limits
     * @return the built airspace volume
     */
    public static AirspaceVolumeImpl fromPolygon(final PolygonGeometry polygon, final VerticalLimits verticalLimits) {
        return builder()
                .setHorizontalProjection(polygon)
                .withVerticalLimits(verticalLimits)
                .build();
    }

    /**
     * Creates an airspace volume from geographic bounds and vertical limits.
     *
     * @param minLatitude    minimum latitude
     * @param minLongitude   minimum longitude
     * @param maxLatitude    maximum latitude
     * @param maxLongitude   maximum longitude
     * @param verticalLimits the vertical limits
     * @return the built airspace volume
     */
    public static AirspaceVolumeImpl fromBounds(final double minLatitude, final double minLongitude,
                                                final double maxLatitude, final double maxLongitude,
                                                final VerticalLimits verticalLimits) {
        final Geometry geometry = buildPolygonGeometry(minLatitude, minLongitude, maxLatitude, maxLongitude);
        return builder()
                .setHorizontalProjection(geometry)
                .withVerticalLimits(verticalLimits)
                .build();
    }

    /**
     * <p>
     * Creates an airspace volume based on the location indicator and optional parameters.
     * </p>
     * <p>
     * For {@link fi.fmi.avi.model.swx.amd79.SpaceWeatherRegion.SpaceWeatherLocation#DAYLIGHT_SIDE},
     * computes the airspace volume using the sub-solar point and a static radius.
     * For other location indicators with latitude bands, builds a polygon geometry using the
     * location's latitude bounds and the provided longitude limits.
     * </p>
     *
     * @param locationIndicator the location indicator
     * @param analysisTime      analysis time (required only for DAYLIGHT_SIDE)
     * @param minLongitude      minimum longitude (optional, defaults to -180)
     * @param maxLongitude      maximum longitude (optional, defaults to 180)
     * @param verticalLimits    vertical limit constraints
     * @return the built airspace volume
     */
    public static AirspaceVolumeImpl fromLocationIndicator(final SpaceWeatherLocation locationIndicator,
                                                           @Nullable final Instant analysisTime,
                                                           @Nullable final Double minLongitude,
                                                           @Nullable final Double maxLongitude,
                                                           @Nullable final VerticalLimits verticalLimits) {
        if (locationIndicator == SpaceWeatherLocation.DAYSIDE && analysisTime != null) {
            return AirspaceVolumeImpl.forDaylightSide(analysisTime);
        } else if (locationIndicator == SpaceWeatherLocation.NIGHTSIDE && analysisTime != null) {
            // TODO
            return AirspaceVolumeImpl.builder().build();
        } else if (locationIndicator.getLatitudeBandMinCoordinate().isPresent()
                && locationIndicator.getLatitudeBandMaxCoordinate().isPresent()) {
            return AirspaceVolumeImpl.fromBounds(
                    locationIndicator.getLatitudeBandMinCoordinate().get(),
                    minLongitude != null ? minLongitude : -180d,
                    locationIndicator.getLatitudeBandMaxCoordinate().get(),
                    maxLongitude != null ? maxLongitude : 180d,
                    verticalLimits != null ? verticalLimits : VerticalLimitsImpl.none());
        }
        throw new IllegalArgumentException("Unable to create AirspaceVolume for location indicator: " + locationIndicator);
    }

    public abstract Builder toBuilder();

    public static class Builder extends AirspaceVolumeImpl_Builder {

        Builder() {
        }

        /**
         * <p>
         * Rounds polygon geometry coordinates to the nearest integers.
         * </p>
         * Only polygon coordinates are rounded, because space weather advisories use a polygon or a circle to
         * represent the airspace volume. The circles use a calculated center point, which we don't want to round.
         *
         * @param geometry geometry to process
         * @return geometry with rounded coordinates if it is a polygon, otherwise the original geometry
         */
        private static Geometry roundPolygonCoordinatesToIntegers(final Geometry geometry) {
            if (geometry instanceof PolygonGeometry) {
                final PolygonGeometry polygon = (PolygonGeometry) geometry;
                return PolygonGeometryImpl.Builder.from(polygon)
                        .setExteriorRingPositions(polygon.getExteriorRingPositions().stream()
                                .map(coordinate -> (double) Math.round(coordinate))
                                .collect(toList()))
                        .build();
            }
            return geometry;
        }

        public static Builder from(final AirspaceVolume value) {
            if (value instanceof AirspaceVolumeImpl) {
                return ((AirspaceVolumeImpl) value).toBuilder();
            } else {
                return builder().setHorizontalProjection(value.getHorizontalProjection())
                        .setUpperLimit(value.getUpperLimit())
                        .setUpperLimitReference(value.getUpperLimitReference())
                        .setLowerLimit(value.getLowerLimit())
                        .setLowerLimitReference(value.getLowerLimitReference())
                        .setMaximumLimit(value.getMaximumLimit())
                        .setMaximumLimitReference(value.getMaximumLimitReference())
                        .setMinimumLimit(value.getMinimumLimit())
                        .setMinimumLimitReference(value.getMinimumLimitReference())
                        .setWidth(value.getWidth());
            }
        }

        public static Builder fromAmd79(final fi.fmi.avi.model.swx.amd79.AirspaceVolume value) {
            return builder()
                    .setHorizontalProjection(value.getHorizontalProjection().map(Builder::roundPolygonCoordinatesToIntegers))
                    .setUpperLimit(value.getUpperLimit())
                    .setUpperLimitReference(value.getUpperLimitReference())
                    .setLowerLimit(value.getLowerLimit())
                    .setLowerLimitReference(value.getLowerLimitReference())
                    .setMaximumLimit(value.getMaximumLimit())
                    .setMaximumLimitReference(value.getMaximumLimitReference())
                    .setMinimumLimit(value.getMinimumLimit())
                    .setMinimumLimitReference(value.getMinimumLimitReference())
                    .setWidth(value.getWidth());
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setUpperLimit(final NumericMeasure limit) {
            return super.setUpperLimit(limit);
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setLowerLimit(final NumericMeasure limit) {
            return super.setLowerLimit(limit);
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setMaximumLimit(final NumericMeasure limit) {
            return super.setMaximumLimit(limit);
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setMinimumLimit(final NumericMeasure limit) {
            return super.setMinimumLimit(limit);
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setWidth(final NumericMeasure width) {
            return super.setWidth(width);
        }

        public Builder withVerticalLimits(final VerticalLimits verticalLimits) {
            final String verticalReference = verticalLimits.getVerticalReference();
            final Optional<NumericMeasure> lowerLimit = verticalLimits.getLowerLimit();
            final Optional<NumericMeasure> upperLimit = verticalLimits.getUpperLimit();
            final Optional<AviationCodeListUser.RelationalOperator> operator = verticalLimits.getOperator();

            if (lowerLimit.isPresent() && upperLimit.isPresent()) {
                setLowerLimitReference(verticalReference);
                setUpperLimitReference(verticalReference);
                setLowerLimit(lowerLimit.get());
                setUpperLimit(upperLimit.get());
            } else if (lowerLimit.isPresent() && operator.filter(op -> op == AviationCodeListUser.RelationalOperator.ABOVE).isPresent()) {
                setLowerLimitReference(verticalReference);
                setLowerLimit(lowerLimit.get());
            } else if (upperLimit.isPresent() && operator.filter(op -> op == AviationCodeListUser.RelationalOperator.BELOW).isPresent()) {
                setUpperLimitReference(verticalReference);
                setUpperLimit(upperLimit.get());
            }

            return this;
        }
    }
}
