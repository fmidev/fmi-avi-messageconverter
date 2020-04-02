package fi.fmi.avi.model.SpaceWeatherAdvisory.immutable;

import java.io.Serializable;
import java.util.List;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.SpaceWeatherAdvisory.SpaceWeatherAdvisoryAnalysis;
import fi.fmi.avi.model.SpaceWeatherAdvisory.SpaceWeatherRegion;

@FreeBuilder
@JsonDeserialize(builder = SpaceWeatherAdvisoryAnalysisImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class SpaceWeatherAdvisoryAnalysisImpl implements SpaceWeatherAdvisoryAnalysis, Serializable {

    public static Builder builder() {
        return new Builder();
    }

    public abstract Builder toBuilder();

    public static class Builder extends SpaceWeatherAdvisoryAnalysisImpl_Builder {
        public Builder() {
            setNoPhenomenaExpected(false);
            setNoInformationAvailable(false);
        }

        @Override
        @JsonDeserialize(contentAs = SpaceWeatherRegionImpl.class)
        public Builder setRegion(final List<SpaceWeatherRegion> region) {
            return super.setRegion(region);
        }

    }
}
