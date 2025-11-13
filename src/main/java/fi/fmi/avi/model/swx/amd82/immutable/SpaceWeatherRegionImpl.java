package fi.fmi.avi.model.swx.amd82.immutable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.swx.VerticalLimits;
import fi.fmi.avi.model.swx.amd82.AirspaceVolume;
import fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion;
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
            @Nullable final Instant analysisTime,
            @Nullable final Double minLongitude,
            @Nullable final Double maxLongitude,
            @Nullable final VerticalLimits verticalLimits) {
        return builder()
                .setLocationIndicator(locationIndicator)
                .setAirSpaceVolume(AirspaceVolumeImpl.fromLocationIndicator(locationIndicator,
                        analysisTime, minLongitude, maxLongitude, verticalLimits))
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

        public static Builder fromAmd79(final fi.fmi.avi.model.swx.amd79.SpaceWeatherRegion value) {
            return builder()
                    .setAirSpaceVolume(value.getAirSpaceVolume().map(airspaceVolume ->
                            AirspaceVolumeImpl.Builder.fromAmd79(airspaceVolume).build()))
                    .setLongitudeLimitMaximum(value.getLongitudeLimitMaximum())
                    .setLongitudeLimitMinimum(value.getLongitudeLimitMinimum())
                    .setLocationIndicator(value.getLocationIndicator().map(Builder::locationIndicatorFromAmd79));
        }

        private static SpaceWeatherLocation locationIndicatorFromAmd79(
                final fi.fmi.avi.model.swx.amd79.SpaceWeatherRegion.SpaceWeatherLocation amd79Location) {
            if (amd79Location == fi.fmi.avi.model.swx.amd79.SpaceWeatherRegion.SpaceWeatherLocation.DAYLIGHT_SIDE) {
                return SpaceWeatherLocation.DAYSIDE;
            } else {
                return SpaceWeatherLocation.valueOf(amd79Location.name());
            }
        }

        @Override
        @JsonDeserialize(as = AirspaceVolumeImpl.class)
        public Builder setAirSpaceVolume(final AirspaceVolume airSpaceVolume) {
            return super.setAirSpaceVolume(airSpaceVolume);
        }
    }
}
