package fi.fmi.avi.model.swx.amd79.immutable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.swx.VerticalLimits;
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

    public static SpaceWeatherRegionImpl fromLocationIndicator(
            final SpaceWeatherLocation locationIndicator,
            final VerticalLimits verticalLimits,
            @Nullable final Instant analysisTime,
            @Nullable final Double minLongitude,
            @Nullable final Double maxLongitude) {
        return builder()
                .setLocationIndicator(locationIndicator)
                .setAirSpaceVolume(AirspaceVolumeImpl.fromLocationIndicator(locationIndicator,
                        verticalLimits, analysisTime, minLongitude, maxLongitude))
                .setNullableLongitudeLimitMinimum(minLongitude)
                .setNullableLongitudeLimitMaximum(maxLongitude)
                .build();
    }

    public abstract Builder toBuilder();

    public static class Builder extends SpaceWeatherRegionImpl_Builder {

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
                    .setLocationIndicator(value.getLocationIndicator().map(Builder::locationIndicatorFromAmd82));
        }

        private static SpaceWeatherLocation locationIndicatorFromAmd82(
                final fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion.SpaceWeatherLocation amd82Location) {
            if (amd82Location == fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion.SpaceWeatherLocation.DAYSIDE) {
                return SpaceWeatherLocation.DAYLIGHT_SIDE;
            } else {
                // Let fail on NIGHTSIDE, there's no counterpart in Amd79.
                return SpaceWeatherLocation.valueOf(amd82Location.name());
            }
        }

        @Override
        @JsonDeserialize(as = AirspaceVolumeImpl.class)
        public Builder setAirSpaceVolume(final AirspaceVolume airSpaceVolume) {
            return super.setAirSpaceVolume(airSpaceVolume);
        }
    }
}
