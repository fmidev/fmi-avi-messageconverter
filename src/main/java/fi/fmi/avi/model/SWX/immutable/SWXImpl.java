package fi.fmi.avi.model.SWX.immutable;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.PhenomenonGeometryWithHeight;
import fi.fmi.avi.model.SWX.AdvisoryNumber;
import fi.fmi.avi.model.SWX.AdvisoryNumberImpl;
import fi.fmi.avi.model.SWX.NextAdvisory;
import fi.fmi.avi.model.SWX.NextAdvisoryImpl;
import fi.fmi.avi.model.SWX.SWX;
import fi.fmi.avi.model.immutable.PhenomenonGeometryWithHeightImpl;

@FreeBuilder
@JsonDeserialize(builder = SWXImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "issueTime", "status", "translationCentreName", "advisoryNumber", "replacementAdvisoryNumber", "weatherEffects", "observation",
        "forecasts", "remarks", "nextAdvisory" })
public abstract class SWXImpl implements SWX, Serializable {

    public static SWXImpl.Builder builder() {
        return new SWXImpl.Builder();
    }

    public abstract Builder toBuilder();

    @Override
    public boolean areAllTimeReferencesComplete() {
        return false;
    }

    public static class Builder extends SWXImpl_Builder {
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

        @Override
        @JsonDeserialize(as = PhenomenonGeometryWithHeightImpl.class)
        public Builder setObservation(final PhenomenonGeometryWithHeight observation) {
            if (observation == null) {
                return super.setObservation(Optional.empty());
            }

            return super.setObservation(observation);
        }

        @JsonDeserialize(contentAs = PhenomenonGeometryWithHeightImpl.class)
        public Builder addAllForecasts(final List<PhenomenonGeometryWithHeight> elements) {
            return super.addAllForecasts(elements);
        }
    }

}
