package fi.fmi.avi.model.SWX.immutable;

import java.io.Serializable;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.PhenomenonGeometryWithHeight;
import fi.fmi.avi.model.SWX.SWXAnalysis;
import fi.fmi.avi.model.immutable.PhenomenonGeometryWithHeightImpl;

@FreeBuilder
@JsonDeserialize(builder = SWXAnalysisImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class SWXAnalysisImpl implements SWXAnalysis, Serializable {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends SWXAnalysisImpl_Builder {
        @Override
        @JsonDeserialize(as = PhenomenonGeometryWithHeightImpl.class)
        public Builder setAnalysis(final PhenomenonGeometryWithHeight analysis) {
            return super.setAnalysis(analysis);
        }
    }
}
