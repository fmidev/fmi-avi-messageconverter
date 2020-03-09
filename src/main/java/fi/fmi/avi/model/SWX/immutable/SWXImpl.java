package fi.fmi.avi.model.SWX.immutable;

import java.io.Serializable;
import java.util.List;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.SWX.AdvisoryNumber;
import fi.fmi.avi.model.SWX.NextAdvisory;
import fi.fmi.avi.model.SWX.SWX;
import fi.fmi.avi.model.SWX.SWXAnalysis;

@FreeBuilder
@JsonDeserialize(builder = SWXImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "issueTime", "status", "issuingCenterName", "advisoryNumber", "replacementAdvisoryNumber", "phenomena", "analyses", "remarks",
        "nextAdvisory" })
public abstract class SWXImpl implements SWX, Serializable {

    public static SWXImpl.Builder builder() {
        return new Builder();
    }

    public abstract Builder toBuilder();

    @Override
    public boolean areAllTimeReferencesComplete() {
        return false;
    }

    public static class Builder extends SWXImpl_Builder {
        public Builder() {
            this.setTranslated(false);
        }

        @Override
        @JsonDeserialize(as = AdvisoryNumberImpl.class)
        public Builder setAdvisoryNumber(final AdvisoryNumber advisoryNumber) {
            return super.setAdvisoryNumber(advisoryNumber);
        }

        @Override
        @JsonDeserialize(as = NextAdvisoryImpl.class)
        public Builder setNextAdvisory(final NextAdvisory nextAdvisory) {
            return super.setNextAdvisory(nextAdvisory);
        }

        @JsonDeserialize(contentAs = SWXAnalysisImpl.class)
        public Builder addAllAnalyses(final List<SWXAnalysis> elements) {
            return super.addAllAnalyses(elements);
        }
    }
}
