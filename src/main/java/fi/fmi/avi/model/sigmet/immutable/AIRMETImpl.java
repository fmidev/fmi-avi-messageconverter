package fi.fmi.avi.model.sigmet.immutable;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.AirTrafficServicesUnitWeatherMessageBuilderHelper;
import fi.fmi.avi.model.Airspace;
import fi.fmi.avi.model.AviationWeatherMessageBuilderHelper;
import fi.fmi.avi.model.BuilderHelper;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PhenomenonGeometryWithHeight;
import fi.fmi.avi.model.SIGMETAIRMETBuilderHelper;
import fi.fmi.avi.model.UnitPropertyGroup;
import fi.fmi.avi.model.immutable.AirspaceImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.PhenomenonGeometryWithHeightImpl;
import fi.fmi.avi.model.immutable.UnitPropertyGroupImpl;
import fi.fmi.avi.model.sigmet.AIRMET;
import fi.fmi.avi.model.sigmet.AirmetCloudLevels;
import fi.fmi.avi.model.sigmet.Reference;
import fi.fmi.avi.model.sigmet.AirmetWind;

@Value.Immutable
@JsonDeserialize(builder = AIRMETImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "reportStatus", "cancelMessage", "issuingAirTrafficServicesUnit", "meteorologicalWatchOffice",
        "sequenceNumber", "issueTime",
        "validityPeriod", "airspace", "analysisGeometries", "cancelledReport", "remarks", "permissibleUsage",
        "permissibleUsageReason",
        "permissibleUsageSupplementary", "translated", "translatedBulletinID", "translatedBulletinReceptionTime",
        "translationCentreDesignator",
        "translationCentreName", "translationTime", "translatedTAC" })
public abstract class AIRMETImpl implements AIRMET, Serializable {
    private static final long serialVersionUID = 4726279033666939216L;

    public static Builder builder() {
        return new Builder();
    }

    public static AIRMETImpl immutableCopyOf(final AIRMET airmet) {
        Objects.requireNonNull(airmet);
        if (airmet instanceof AIRMETImpl) {
            return (AIRMETImpl) airmet;
        } else {
            return Builder.copyOf(airmet).build();
        }
    }

    public static Optional<AIRMETImpl> immutableCopyOf(final Optional<AIRMET> airmet) {
        Objects.requireNonNull(airmet);
        return airmet.map(AIRMETImpl::immutableCopyOf);
    }

    @Override
    @JsonIgnore
    @Deprecated
    public SigmetAirmetReportStatus getStatus() {
        return AIRMET.super.getStatus();
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    @Override
    @JsonIgnore
    public boolean areAllTimeReferencesComplete() {
        if (!this.getValidityPeriod().isComplete()) {
            return false;
        }
        if (this.getAnalysisGeometries().isPresent()) {
            for (final PhenomenonGeometryWithHeight geometryWithHeight : this.getAnalysisGeometries().get()) {
                if (geometryWithHeight.getTime().isPresent()
                        && !geometryWithHeight.getTime().get().getCompleteTime().isPresent()) {
                    return false;
                }
            }
        }

        return !this.getCancelledReference().isPresent()
                || (this.getCancelledReference().get().getValidityPeriod().isComplete());
    }

    @Override
    @JsonDeserialize(as = UnitPropertyGroupImpl.class)
    public abstract UnitPropertyGroup getIssuingAirTrafficServicesUnit();

    @Override
    @JsonDeserialize(as = UnitPropertyGroupImpl.class)
    public abstract UnitPropertyGroup getMeteorologicalWatchOffice();

    @Override
    @JsonDeserialize(contentAs = AirmetReferenceImpl.class)
    public abstract Optional<Reference> getCancelledReference();

    @Override
    public abstract Optional<PartialOrCompleteTimeInstant> getIssueTime();

    @Override
    @JsonDeserialize(contentAs = AirmetCloudLevelsImpl.class)
    public abstract Optional<AirmetCloudLevels> getCloudLevels();

    @Override
    @JsonDeserialize(as = AirspaceImpl.class)
    public abstract Airspace getAirspace();

    @Override
    @JsonDeserialize(contentAs = NumericMeasureImpl.class)
    public abstract Optional<NumericMeasure> getVisibility();

    @Override
    @JsonDeserialize(contentAs = AirmetWindImpl.class)
    public abstract Optional<AirmetWind> getWind();

    // NOTE: Immutables generates a final setAnalysisGeometries(...) setter, which cannot be overridden to
    // carry @JsonDeserialize(contentAs = ...) (see docs/07-modernization-plan.md). The hint moves onto this
    // abstract getter re-declaration instead; the package's passAnnotations style (see package-info.java)
    // propagates it onto the generated builder setter for Jackson's builder-based deserialization.
    // NOTE: no per-property @JsonDeserialize(contentAs=...) hint here: see SIGMETImpl.getAnalysisGeometries() for why
    // (Optional<List<X>> on a non-detached builder). PhenomenonGeometryWithHeight instead carries its own
    // class-level @JsonDeserialize(as=...) hint (see docs/07-modernization-plan.md).
    @Override
    public abstract Optional<List<PhenomenonGeometryWithHeight>> getAnalysisGeometries();

    public static class Builder extends ImmutableAIRMETImpl.Builder {

        Builder() {
            setTranslated(false);
            this.setReportStatus(ReportStatus.NORMAL);
            this.setCancelMessage(false);
        }

        public static Builder copyOf(final AIRMET value) {
            if (value instanceof AIRMETImpl) {
                return ((AIRMETImpl) value).toBuilder();
            } else {
                final Builder builder = builder();
                AviationWeatherMessageBuilderHelper.copyFrom(builder, value, //
                        Builder::setReportStatus, //
                        Builder::setIssueTime, //
                        Builder::setRemarks, //
                        Builder::setPermissibleUsage, //
                        Builder::setPermissibleUsageReason, //
                        Builder::setPermissibleUsageSupplementary, //
                        Builder::setTranslated, //
                        Builder::setTranslatedBulletinID, //
                        Builder::setTranslatedBulletinReceptionTime, //
                        Builder::setTranslationCentreDesignator, //
                        Builder::setTranslationCentreName, //
                        Builder::setTranslationTime, //
                        Builder::setTranslatedTAC);
                AirTrafficServicesUnitWeatherMessageBuilderHelper.copyFrom(builder, value, //
                        Builder::setIssuingAirTrafficServicesUnit, //
                        Builder::setMeteorologicalWatchOffice);
                SIGMETAIRMETBuilderHelper.copyFrom(builder, value, //
                        Builder::setSequenceNumber, //
                        Builder::setValidityPeriod, //
                        Builder::setAirspace, //
                        Builder::setCancelMessage);
                return builder//
                        .setPhenomenon(value.getPhenomenon())//
                        .setCloudLevels(AirmetCloudLevelsImpl.immutableCopyOf(value.getCloudLevels()))//
                        .setWind(AirmetWindImpl.immutableCopyOf(value.getWind()))//
                        .setObscuration(value.getObscuration()//
                                .map(BuilderHelper::toImmutableList))//
                        .setVisibility(NumericMeasureImpl.immutableCopyOf(value.getVisibility()))//
                        .setCancelledReference(AirmetReferenceImpl.immutableCopyOf(value.getCancelledReference()))//
                        .setAnalysisGeometries(value.getAnalysisGeometries()//
                                .map(analysisGeometries -> BuilderHelper.toImmutableList(analysisGeometries,
                                        PhenomenonGeometryWithHeightImpl::immutableCopyOf))//
                        );
            }
        }

        /**
         * Sets the SIGMET-specific message status.
         *
         * Note, this method is provided for backward compatibility with previous
         * versions of the API. The <code>status</code> is no longer
         * explicitly stored. Instead, this method sets other property values with the
         * following logic:
         * <dl>
         * <dt>{@link fi.fmi.avi.model.AviationCodeListUser.SigmetAirmetReportStatus#CANCELLATION
         * CANCELLATION}</dt>
         * <dd>
         * <code>reportStatus = {@link fi.fmi.avi.model.AviationWeatherMessage.ReportStatus#NORMAL NORMAL}</code><br>
         * <code>cancelMessage = true</code><br>
         * </dd>
         *
         * <dt>{@link fi.fmi.avi.model.AviationCodeListUser.SigmetAirmetReportStatus#NORMAL
         * NORMAL}</dt>
         * <dd>
         * <code>reportStatus = {@link fi.fmi.avi.model.AviationWeatherMessage.ReportStatus#NORMAL NORMAL}</code><br>
         * <code>cancelMessage = false</code><br>
         * </dd>
         * </dl>
         *
         * @param status
         *               the status to set
         *
         * @return builder
         *
         * @deprecated migrate to using a combination of
         *             {@link #setReportStatus(ReportStatus)} and
         *             {@link #setCancelMessage(boolean)} instead
         */
        @Deprecated
        public Builder setStatus(final SigmetAirmetReportStatus status) {
            requireNonNull(status);
            return setReportStatus(status.getReportStatus())//
                    .setCancelMessage(status.isCancelMessage());
        }
    }
}
