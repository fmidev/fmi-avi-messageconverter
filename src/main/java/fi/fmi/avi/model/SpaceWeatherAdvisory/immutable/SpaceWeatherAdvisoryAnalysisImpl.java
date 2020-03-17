package fi.fmi.avi.model.SpaceWeatherAdvisory.immutable;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.PhenomenonGeometryWithHeight;
import fi.fmi.avi.model.SpaceWeatherAdvisory.SpaceWeatherAdvisoryAnalysis;
import fi.fmi.avi.model.SpaceWeatherAdvisory.SpaceWeatherRegion;
import fi.fmi.avi.model.immutable.PhenomenonGeometryWithHeightImpl;

@FreeBuilder
@JsonDeserialize(builder = SpaceWeatherAdvisoryAnalysisImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class SpaceWeatherAdvisoryAnalysisImpl implements SpaceWeatherAdvisoryAnalysis, Serializable {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends SpaceWeatherAdvisoryAnalysisImpl_Builder {

        @Override
        @JsonDeserialize(contentAs = SpaceWeatherRegionImpl.class)
        public Builder setRegion(final List<SpaceWeatherRegion> region) {
            return super.setRegion(region);
        }

        /*
        @JsonDeserialize(as = PhenomenonGeometryWithHeightImpl.class)
        public Builder setAffectedArea(final PhenomenonGeometryWithHeight analysis) {
            return super.setAffectedArea(analysis);
        }*/
    }
}
