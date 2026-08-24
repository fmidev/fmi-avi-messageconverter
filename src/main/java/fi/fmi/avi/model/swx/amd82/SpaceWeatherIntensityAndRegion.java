package fi.fmi.avi.model.swx.amd82;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.swx.amd82.immutable.SpaceWeatherIntensityAndRegionImpl;

/**
 * The class-level {@link JsonDeserialize} hint is needed because this interface can appear as the element type of a
 * {@code List} property (e.g. {@code List<SpaceWeatherIntensityAndRegion>}); a per-property {@code contentAs} hint
 * placed on an overridden {@code addAllX(...)} method is not reliably picked up by Jackson for builder-style
 * deserialization of such properties (see doc/immutables-migration.md).
 */
@JsonDeserialize(as = SpaceWeatherIntensityAndRegionImpl.class)
public interface SpaceWeatherIntensityAndRegion {
    Intensity getIntensity();

    List<SpaceWeatherRegion> getRegions();
}
