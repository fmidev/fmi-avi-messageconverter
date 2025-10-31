package fi.fmi.avi.model.swx.amd79.immutable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.swx.VerticalLimits;
import fi.fmi.avi.model.swx.VerticalLimitsImpl;
import fi.fmi.avi.model.swx.amd79.AirspaceVolume;
import fi.fmi.avi.model.swx.amd79.SpaceWeatherRegion;
import org.inferred.freebuilder.FreeBuilder;

import javax.annotation.Nullable;
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
         * @param verticalLimits    vertical limit constraints
         * @return builder instance
         */
        public Builder withComputedAirspaceVolume(final SpaceWeatherLocation locationIndicator,
                                                  @Nullable final Instant analysisTime,
                                                  @Nullable final Double minLongitude,
                                                  @Nullable final Double maxLongitude,
                                                  @Nullable final VerticalLimits verticalLimits) {
            if (locationIndicator == SpaceWeatherLocation.DAYLIGHT_SIDE && analysisTime != null) {
                setAirSpaceVolume(AirspaceVolumeImpl.Builder.forDaylightSide(analysisTime));
            } else if (locationIndicator.getLatitudeBandMinCoordinate().isPresent()
                    && locationIndicator.getLatitudeBandMaxCoordinate().isPresent()) {
                setAirSpaceVolume(AirspaceVolumeImpl.Builder.fromBounds(
                        locationIndicator.getLatitudeBandMinCoordinate().get(),
                        minLongitude != null ? minLongitude : -180d,
                        locationIndicator.getLatitudeBandMaxCoordinate().get(),
                        maxLongitude != null ? maxLongitude : 180d,
                        verticalLimits != null ? verticalLimits : VerticalLimitsImpl.none()));
            }
            return this;
        }

        @Override
        @JsonDeserialize(as = AirspaceVolumeImpl.class)
        public Builder setAirSpaceVolume(final AirspaceVolume airSpaceVolume) {
            return super.setAirSpaceVolume(airSpaceVolume);
        }
    }
}
