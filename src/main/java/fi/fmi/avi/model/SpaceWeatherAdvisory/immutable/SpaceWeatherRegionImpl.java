package fi.fmi.avi.model.SpaceWeatherAdvisory.immutable;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.PhenomenonGeometryWithHeight;
import fi.fmi.avi.model.SpaceWeatherAdvisory.SpaceWeatherRegion;
import fi.fmi.avi.model.immutable.PhenomenonGeometryWithHeightImpl;

@FreeBuilder
@JsonDeserialize(builder = SpaceWeatherRegionImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class SpaceWeatherRegionImpl implements SpaceWeatherRegion {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends SpaceWeatherRegionImpl_Builder {

        @JsonDeserialize(as = PhenomenonGeometryWithHeightImpl.class)
        public Builder setGeographiclocation(final PhenomenonGeometryWithHeight geographiclocation) {
            return super.setGeographiclocation(geographiclocation);
        }
    }
}
