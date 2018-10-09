package fi.fmi.avi.model.sigmet.immutable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.UnitPropertyGroup;
import fi.fmi.avi.model.immutable.UnitPropertyGroupImpl;
import fi.fmi.avi.model.sigmet.SIGMET;
import fi.fmi.avi.model.sigmet.SigmetReference;
import fi.fmi.avi.model.sigmet.SigmetAnalysis;
import org.inferred.freebuilder.FreeBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@FreeBuilder
@JsonDeserialize(builder = SIGMETImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"status", "issuingAirTrafficServicesUnit", "meteorologicalWatchOffice", "sequenceNumber", "issueTime", "validityPeriod", "analysis", "forecastPositionAnalysis", "cancelledReport", "remarks", "permissibleUsage",
        "permissibleUsageReason", "permissibleUsageSupplementary", "translated", "translatedBulletinID", "translatedBulletinReceptionTime",
        "translationCentreDesignator", "translationCentreName", "translationTime", "translatedTAC"})

public abstract class SIGMETImpl implements SIGMET, Serializable {

    public static SIGMETImpl immutableCopyOf(final SIGMET sigmet) {
        Objects.requireNonNull(sigmet);
        if (sigmet instanceof SIGMETImpl) {
            return (SIGMETImpl) sigmet;
        } else {
            return Builder.from(sigmet).build();
        }
    }

    public static Optional<SIGMETImpl> immutableCopyOf(final Optional<SIGMET> sigmet) {
        Objects.requireNonNull(sigmet);
        return sigmet.map(SIGMETImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    @Override
    @JsonIgnore
    public boolean areAllTimeReferencesComplete() {
        return true;
    }

    @Override
    @JsonIgnore
    public SIGMET createCancelSigmet() {
        return null;
    }

    public static class Builder extends SIGMETImpl_Builder {

        public static Builder from(final SIGMET value) {
            if (value instanceof SIGMETImpl) {
                return ((SIGMETImpl) value).toBuilder();
            } else {
                return new Builder()
                        .setIssuingAirTrafficServicesUnit(value.getIssuingAirTrafficServicesUnit())
                        .setMeteorologicalWatchOffice(value.getMeteorologicalWatchOffice())
                        .setAnalysis(value.getAnalysis())
                        .setIssueTime(value.getIssueTime())
                        .setStatus(value.getStatus())
                        .setVolcanicAshMovedToFIR(value.getVolcanicAshMovedToFIR())
                        .setCancelledReference(value.getCancelledReference())
                        .setSigmetPhenomenon(value.getSigmetPhenomenon())
                        .setValidityPeriod(value.getValidityPeriod())
                        .setSequenceNumber(value.getSequenceNumber())
                        .setCancelledReference(value.getCancelledReference())
                        ;
            }
        }


        @Override
        @JsonDeserialize(as = UnitPropertyGroupImpl.class)
        public Builder setIssuingAirTrafficServicesUnit(final UnitPropertyGroup issuingAirTrafficServicesUnit) {
            return super.setIssuingAirTrafficServicesUnit(issuingAirTrafficServicesUnit);
        }

        @Override
        @JsonDeserialize(contentAs = SigmetAnalysisImpl.class)
        public Builder setAnalysis(final List<SigmetAnalysis> analysis) {
            return super.setAnalysis(analysis);
        }

        @Override
        @JsonDeserialize(as = UnitPropertyGroupImpl.class)
        public Builder setMeteorologicalWatchOffice(final UnitPropertyGroup meteorologicalWatchOffice) {
            return super.setMeteorologicalWatchOffice(meteorologicalWatchOffice);
        }

        @Override
        @JsonDeserialize(as = SigmetReference.class)
        public Builder setCancelledReference(final SigmetReference cancelledReference) {
            return super.setCancelledReference(cancelledReference);
        }

        @Override
        @JsonDeserialize(as = PartialOrCompleteTimeInstant.class)
        public Builder setIssueTime(PartialOrCompleteTimeInstant issueTime) {
            return super.setIssueTime(issueTime);
        }

    }
}
