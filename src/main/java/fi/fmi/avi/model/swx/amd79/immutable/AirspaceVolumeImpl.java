package fi.fmi.avi.model.swx.amd79.immutable;

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
import fi.fmi.avi.model.swx.amd79.AirspaceVolume;
import fi.fmi.avi.model.swx.amd79.SpaceWeatherRegion;
import fi.fmi.avi.util.SubSolarPointUtils;
import org.inferred.freebuilder.FreeBuilder;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    public static AirspaceVolumeImpl fromLocationIndicator(final SpaceWeatherRegion.SpaceWeatherLocation locationIndicator,
                                                           @Nullable final Instant analysisTime,
                                                           @Nullable final Double minLongitude,
                                                           @Nullable final Double maxLongitude,
                                                           @Nullable final VerticalLimits verticalLimits) {
        if (locationIndicator == SpaceWeatherRegion.SpaceWeatherLocation.DAYLIGHT_SIDE && analysisTime != null) {
            return AirspaceVolumeImpl.forDaylightSide(analysisTime);
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
         * Rounds polygon geometry coordinates to AMD 79 resolution.
         * </p>
         * <p>
         * AMD 79 coordinate resolution:
         * <ul>
         *   <li>Latitude: 10 degrees precision</li>
         *   <li>Longitude: 15 degrees precision</li>
         * </ul>
         * <p>
         * Only polygon coordinates are rounded, because space weather advisories use a polygon or a circle to
         * represent the airspace volume. The circles use a calculated center point, which we don't want to round.
         * </p>
         * <p>
         * Duplicate consecutive coordinate pairs resulting from rounding are removed to maintain a valid polygon
         * geometry.
         * </p>
         *
         * @param geometry geometry to process
         * @return geometry with rounded coordinates if it is a polygon, otherwise the original geometry
         */
        private static Geometry roundPolygonCoordinatesToAmd79Precision(final Geometry geometry) {
            if (geometry instanceof PolygonGeometry) {
                final PolygonGeometry polygon = (PolygonGeometry) geometry;
                final List<Double> positions = polygon.getExteriorRingPositions();
                if (positions.size() % 2 != 0 || positions.isEmpty()) {
                    return geometry;
                }

                final List<Double> roundedPositions = new ArrayList<>();
                for (int i = 0; i < positions.size(); i += 2) {
                    final double lat = positions.get(i);
                    final double lon = positions.get(i + 1);
                    final double roundedLat = Math.round(lat / 10.0) * 10.0;
                    final double roundedLon = Math.round(lon / 15.0) * 15.0;

                    if (roundedPositions.isEmpty() ||
                            roundedPositions.get(roundedPositions.size() - 2) != roundedLat ||
                            roundedPositions.get(roundedPositions.size() - 1) != roundedLon) {
                        roundedPositions.add(roundedLat);
                        roundedPositions.add(roundedLon);
                    }
                }

                return PolygonGeometryImpl.Builder.from(polygon)
                        .setExteriorRingPositions(roundedPositions)
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

        public static Builder fromAmd82(final fi.fmi.avi.model.swx.amd82.AirspaceVolume value) {
            return builder()
                    .setHorizontalProjection(value.getHorizontalProjection().map(Builder::roundPolygonCoordinatesToAmd79Precision))
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
