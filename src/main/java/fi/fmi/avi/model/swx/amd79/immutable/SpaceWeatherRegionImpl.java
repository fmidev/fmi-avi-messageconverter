package fi.fmi.avi.model.swx.amd79.immutable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.Geometry;
import fi.fmi.avi.model.PolygonGeometry;
import fi.fmi.avi.model.immutable.CircleByCenterPointImpl;
import fi.fmi.avi.model.immutable.CoordinateReferenceSystemImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.PolygonGeometryImpl;
import fi.fmi.avi.model.swx.amd79.AirspaceVolume;
import fi.fmi.avi.model.swx.amd79.SpaceWeatherRegion;
import fi.fmi.avi.model.swx.amd79.VerticalLimits;
import fi.fmi.avi.util.SubSolarPointUtils;
import org.inferred.freebuilder.FreeBuilder;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@FreeBuilder
@JsonDeserialize(builder = SpaceWeatherRegionImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"airSpaceVolume", "locationIndicator", "longitudeLimitMinimum", "longitudeLimitMaximum"})
public abstract class SpaceWeatherRegionImpl implements SpaceWeatherRegion, Serializable {

    private static final long serialVersionUID = 207049872292188821L;

    public static Builder builder() {
        return new Builder();
    }

    public static SpaceWeatherRegionImpl immutableCopyOf(final SpaceWeatherRegion region) {
        Objects.requireNonNull(region);
        if (region instanceof SpaceWeatherRegionImpl) {
            return (SpaceWeatherRegionImpl) region;
        } else {
            return Builder.from(region).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<SpaceWeatherRegionImpl> immutableCopyOf(final Optional<SpaceWeatherRegion> region) {
        Objects.requireNonNull(region);
        return region.map(SpaceWeatherRegionImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends SpaceWeatherRegionImpl_Builder {
        @Deprecated
        Builder() {
        }

        public static Builder from(final SpaceWeatherRegion value) {
            if (value instanceof SpaceWeatherRegionImpl) {
                return ((SpaceWeatherRegionImpl) value).toBuilder();
            } else {
                return builder()//
                        .setAirSpaceVolume(AirspaceVolumeImpl.immutableCopyOf(value.getAirSpaceVolume()))//
                        .setLongitudeLimitMaximum(value.getLongitudeLimitMaximum())//
                        .setLongitudeLimitMinimum(value.getLongitudeLimitMinimum())//
                        .setLocationIndicator(value.getLocationIndicator());
            }
        }

        public static Builder fromAmd82(final fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion value) {
            return builder()
                    .setAirSpaceVolume(value.getAirSpaceVolume().map(airspaceVolume ->
                            AirspaceVolumeImpl.Builder.fromAmd82(airspaceVolume).build()))
                    .setLongitudeLimitMaximum(value.getLongitudeLimitMaximum())
                    .setLongitudeLimitMinimum(value.getLongitudeLimitMinimum())
                    .setLocationIndicator(value.getLocationIndicator().map(locationIndicator ->
                            SpaceWeatherLocation.valueOf(locationIndicator.name())));
        }

        private static Geometry buildGeometry(final double minLatitude, final double minLongitude, final double maxLatitude, final double maxLongitude) {
            final double absMinLongitude = Math.abs(minLongitude);
            final double absMaxLongitude = Math.abs(maxLongitude);
            return PolygonGeometryImpl.builder()
                    .setCrs(CoordinateReferenceSystemImpl.wgs84())
                    .mutateExteriorRingPositions(coordinates -> {
                        if (absMinLongitude == 180d && absMaxLongitude == 180d) {
                            addExteriorRingPositions(coordinates, minLatitude, -180d, maxLatitude, 180d);
                        } else if (absMinLongitude == 180d) {
                            addExteriorRingPositions(coordinates, minLatitude, -180d, maxLatitude, maxLongitude);
                        } else if (absMaxLongitude == 180d) {
                            addExteriorRingPositions(coordinates, minLatitude, minLongitude, maxLatitude, 180d);
                        } else {
                            addExteriorRingPositions(coordinates, minLatitude, minLongitude, maxLatitude, maxLongitude);
                        }
                    })
                    .build();
        }

        private static void addExteriorRingPositions(final List<Double> coordinates, final double minLat, final double minLon,
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

        private static AirspaceVolume buildAirspaceVolume(final Geometry geometry, final VerticalLimits verticalLimits) {
            return AirspaceVolumeImpl.builder()
                    .setHorizontalProjection(geometry)
                    .withVerticalLimits(verticalLimits)
                    .build();
        }

        private static AirspaceVolume buildDaylightSideAirspaceVolume(final Instant analysisTime) {
            return AirspaceVolumeImpl.builder()
                    .setHorizontalProjection(
                            CircleByCenterPointImpl.builder()
                                    .setCrs(CoordinateReferenceSystemImpl.wgs84())
                                    .setCenterPointCoordinates(SubSolarPointUtils.computeSubSolarPoint(analysisTime))
                                    .setRadius(NumericMeasureImpl.builder()
                                            .setUom("km")
                                            .setValue(SubSolarPointUtils.DAYSIDE_RADIUS_KM)
                                            .build())
                                    .build()
                    ).build();
        }

        /**
         * <p>
         * Builds and sets the airspace volume based on the location indicator and optional parameters.
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
         * @param verticalLimits    vertical limit constraints (lower limit, upper limit, and operator)
         * @return builder instance
         */
        public Builder withComputedAirspaceVolume(final SpaceWeatherLocation locationIndicator,
                                                  @Nullable final Instant analysisTime,
                                                  @Nullable final Double minLongitude,
                                                  @Nullable final Double maxLongitude,
                                                  @Nullable final VerticalLimits verticalLimits) {
            if (locationIndicator == SpaceWeatherLocation.DAYLIGHT_SIDE) {
                if (analysisTime == null) {
                    throw new IllegalArgumentException("analysisTime is required for DAYLIGHT_SIDE location indicator");
                }
                setAirSpaceVolume(buildDaylightSideAirspaceVolume(analysisTime));
            } else if (locationIndicator.getLatitudeBandMinCoordinate().isPresent()
                    && locationIndicator.getLatitudeBandMaxCoordinate().isPresent()) {
                final Geometry geometry = buildGeometry(
                        locationIndicator.getLatitudeBandMinCoordinate().get(),
                        minLongitude != null ? minLongitude : -180d,
                        locationIndicator.getLatitudeBandMaxCoordinate().get(),
                        maxLongitude != null ? maxLongitude : 180d);
                setAirSpaceVolume(buildAirspaceVolume(geometry,
                        verticalLimits != null ? verticalLimits : VerticalLimitsImpl.Builder.none()));
            }
            return this;
        }

        public Builder withAirspaceVolumeFromPolygon(final PolygonGeometry polygon, final VerticalLimits verticalLimits) {
            return setAirSpaceVolume(buildAirspaceVolume(polygon, verticalLimits));
        }

        public Builder withAirspaceVolumeFromBounds(final double minLatitude, final double minLongitude,
                                                    final double maxLatitude, final double maxLongitude,
                                                    final VerticalLimits verticalLimits) {
            final Geometry geometry = buildGeometry(minLatitude, minLongitude, maxLatitude, maxLongitude);
            return setAirSpaceVolume(buildAirspaceVolume(geometry, verticalLimits));
        }

        @Override
        @JsonDeserialize(as = AirspaceVolumeImpl.class)
        public Builder setAirSpaceVolume(final AirspaceVolume airSpaceVolume) {
            return super.setAirSpaceVolume(airSpaceVolume);
        }
    }
}
