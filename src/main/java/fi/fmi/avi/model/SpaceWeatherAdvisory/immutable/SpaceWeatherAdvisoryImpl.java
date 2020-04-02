package fi.fmi.avi.model.SpaceWeatherAdvisory.immutable;

import java.io.Serializable;
import java.util.List;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.SpaceWeatherAdvisory.AdvisoryNumber;
import fi.fmi.avi.model.SpaceWeatherAdvisory.NextAdvisory;
import fi.fmi.avi.model.SpaceWeatherAdvisory.SpaceWeatherAdvisory;
import fi.fmi.avi.model.SpaceWeatherAdvisory.SpaceWeatherAdvisoryAnalysis;

@FreeBuilder
@JsonDeserialize(builder = SpaceWeatherAdvisoryImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "issueTime", "status", "issuingCenterName", "advisoryNumber", "replacementAdvisoryNumber", "phenomena", "analyses", "remarks",
        "nextAdvisory" })
public abstract class SpaceWeatherAdvisoryImpl implements SpaceWeatherAdvisory, Serializable {

    public static Builder builder() {
        return new Builder();
    }

    public abstract Builder toBuilder();

    @Override
    public boolean areAllTimeReferencesComplete() {
        return false;
    }

    public static class Builder extends SpaceWeatherAdvisoryImpl_Builder {
        public Builder() {
            this.setTranslated(false);
        }

        @Override
        @JsonDeserialize(as = AdvisoryNumberImpl.class)
        public Builder setAdvisoryNumber(final AdvisoryNumber advisoryNumber) {
            return super.setAdvisoryNumber(advisoryNumber);
        }

        @Override
        @JsonDeserialize(as = AdvisoryNumberImpl.class)
        public Builder setReplaceAdvisoryNumber(final AdvisoryNumber replaceAdvisoryNumber) {
            return super.setReplaceAdvisoryNumber(replaceAdvisoryNumber);
        }

        @Override
        @JsonDeserialize(as = NextAdvisoryImpl.class)
        public Builder setNextAdvisory(final NextAdvisory nextAdvisory) {
            return super.setNextAdvisory(nextAdvisory);
        }

        @JsonDeserialize(contentAs = SpaceWeatherAdvisoryAnalysisImpl.class)
        public Builder addAllAnalyses(final List<SpaceWeatherAdvisoryAnalysis> elements) {
            return super.addAllAnalyses(elements);
        }
    }
}
