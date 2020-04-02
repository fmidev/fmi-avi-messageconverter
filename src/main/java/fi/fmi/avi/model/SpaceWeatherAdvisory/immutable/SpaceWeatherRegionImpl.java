package fi.fmi.avi.model.SpaceWeatherAdvisory.immutable;

import java.io.Serializable;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.SpaceWeatherAdvisory.AirspaceVolume;
import fi.fmi.avi.model.SpaceWeatherAdvisory.SpaceWeatherRegion;

@FreeBuilder
@JsonDeserialize(builder = SpaceWeatherRegionImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class SpaceWeatherRegionImpl implements SpaceWeatherRegion, Serializable {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends SpaceWeatherRegionImpl_Builder {
        @Override
        @JsonDeserialize(as = AirspaceVolumeImpl.class)
        public Builder setAirSpaceVolume(final AirspaceVolume airSpaceVolume) {
            return super.setAirSpaceVolume(airSpaceVolume);
        }
    }
}
