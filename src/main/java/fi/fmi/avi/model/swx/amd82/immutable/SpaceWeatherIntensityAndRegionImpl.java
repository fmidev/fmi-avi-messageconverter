package fi.fmi.avi.model.swx.amd82.immutable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.swx.amd82.Intensity;
import fi.fmi.avi.model.swx.amd82.SpaceWeatherIntensityAndRegion;
import fi.fmi.avi.model.swx.amd82.SpaceWeatherRegion;
import org.inferred.freebuilder.FreeBuilder;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@FreeBuilder
@JsonDeserialize(builder = SpaceWeatherIntensityAndRegionImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"intensity", "regions"})
public abstract class SpaceWeatherIntensityAndRegionImpl implements SpaceWeatherIntensityAndRegion, Serializable {
    SpaceWeatherIntensityAndRegionImpl() {
    }

    public static SpaceWeatherIntensityAndRegionImpl immutableCopyOf(final SpaceWeatherIntensityAndRegion intensityAndRegion) {
        requireNonNull(intensityAndRegion);
        if (intensityAndRegion instanceof SpaceWeatherIntensityAndRegionImpl) {
            return (SpaceWeatherIntensityAndRegionImpl) intensityAndRegion;
        } else {
            return SpaceWeatherIntensityAndRegionImpl.Builder.from(intensityAndRegion).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<SpaceWeatherIntensityAndRegionImpl> immutableCopyOf(final Optional<SpaceWeatherIntensityAndRegion> intensityAndRegion) {
        requireNonNull(intensityAndRegion);
        return intensityAndRegion.map(SpaceWeatherIntensityAndRegionImpl::immutableCopyOf);
    }

    public static Builder builder() {
        return new Builder();
    }

    public abstract Builder toBuilder();

    public static class Builder extends SpaceWeatherIntensityAndRegionImpl_Builder {
        Builder() {
        }

        public static Builder from(final SpaceWeatherIntensityAndRegion value) {
            if (value instanceof SpaceWeatherIntensityAndRegionImpl) {
                return ((SpaceWeatherIntensityAndRegionImpl) value).toBuilder();
            }
            return builder()
                    .setIntensity(value.getIntensity())
                    .addAllRegions(value.getRegions());
        }

        public static Builder fromAmd79(
                final Intensity intensity, final Collection<? extends fi.fmi.avi.model.swx.amd79.SpaceWeatherRegion> values) {
            return builder()
                    .setIntensity(intensity)
                    .addAllRegions(values.stream()
                            .map(value -> SpaceWeatherRegionImpl.Builder.fromAmd79(value).build()));
        }

        @JsonDeserialize(contentAs = SpaceWeatherRegionImpl.class)
        public Builder addAllRegions(final List<SpaceWeatherRegion> elements) {
            return super.addAllRegions(elements);
        }
    }
}
