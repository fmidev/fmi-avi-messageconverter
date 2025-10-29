package fi.fmi.avi.model.swx.amd79.immutable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.immutable.CircleByCenterPointImpl;
import fi.fmi.avi.model.immutable.CoordinateReferenceSystemImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.swx.amd79.AirspaceVolume;
import fi.fmi.avi.model.swx.amd79.SpaceWeatherRegion;
import fi.fmi.avi.util.SubSolarPointUtils;
import org.inferred.freebuilder.FreeBuilder;

import java.io.Serializable;
import java.time.Instant;
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

        /**
         * Sets the airspace volume's horizontal projection for
         * {@link fi.fmi.avi.model.swx.amd79.SpaceWeatherRegion.SpaceWeatherLocation#DAYLIGHT_SIDE}.
         * <p>
         * If the airspace volume already has a geometry, returns the builder without modifications.
         *
         * @param analysisTime analysis' time
         * @return builder instance
         */
        public Builder withComputedDaylightSideAirspaceVolume(final Instant analysisTime) {
            getLocationIndicator()
                    .filter(loc -> loc != SpaceWeatherLocation.DAYLIGHT_SIDE)
                    .ifPresent(loc -> {
                        throw new IllegalArgumentException("Location indicator is not DAYLIGHT SIDE: " + loc);
                    });
            setAirSpaceVolume(AirspaceVolumeImpl.builder()
                    .setHorizontalProjection(
                            CircleByCenterPointImpl.builder()
                                    .setCrs(CoordinateReferenceSystemImpl.wgs84())
                                    .setCenterPointCoordinates(
                                            SubSolarPointUtils.computeSubSolarPoint(analysisTime))
                                    .setRadius(NumericMeasureImpl.builder()
                                            .setUom("km")
                                            .setValue(SubSolarPointUtils.DAYLIGHT_SIDE_RADIUS_KM)
                                            .build())
                                    .build()
                    ).build());
            return this;
        }


        @Override
        @JsonDeserialize(as = AirspaceVolumeImpl.class)
        public Builder setAirSpaceVolume(final AirspaceVolume airSpaceVolume) {
            return super.setAirSpaceVolume(airSpaceVolume);
        }
    }
}
