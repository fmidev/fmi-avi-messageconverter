package fi.fmi.avi.model.sigmet.immutable;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;

import org.inferred.freebuilder.FreeBuilder;

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
import fi.fmi.avi.model.sigmet.AirmetReference;
import fi.fmi.avi.model.sigmet.AirmetWind;

@FreeBuilder
@JsonDeserialize(builder = AIRMETImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "reportStatus", "cancelMessage", "issuingAirTrafficServicesUnit", "meteorologicalWatchOffice", "sequenceNumber", "issueTime",
        "validityPeriod", "airspace", "movingDirection", "movingSpeed", "analysis", "cancelledReport", "remarks", "permissibleUsage", "permissibleUsageReason",
        "permissibleUsageSupplementary", "translated", "translatedBulletinID", "translatedBulletinReceptionTime", "translationCentreDesignator",
        "translationCentreName", "translationTime", "translatedTAC" })
public abstract class AIRMETImpl implements AIRMET, Serializable {
    public static Builder builder() {
        return new Builder();
    }

    public static AIRMETImpl immutableCopyOf(final AIRMET airmet) {
        Objects.requireNonNull(airmet);
        if (airmet instanceof AIRMETImpl) {
            return (AIRMETImpl) airmet;
        } else {
            return Builder.from(airmet).build();
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

    public abstract Builder toBuilder();

    @Override
    @JsonIgnore
    public boolean areAllTimeReferencesComplete() {
        if (!this.getValidityPeriod().isComplete()) {
            return false;
        }
        if (this.getAnalysisGeometries().isPresent()) {
            for (final PhenomenonGeometryWithHeight geometryWithHeight : this.getAnalysisGeometries().get()) {
                if (geometryWithHeight.getTime().isPresent() && !geometryWithHeight.getTime().get().getCompleteTime().isPresent()) {
                    return false;
                }
            }
        }

        return !this.getCancelledReference().isPresent() || (this.getCancelledReference().get().getValidityPeriod().isComplete());
    }

    public static class Builder extends AIRMETImpl_Builder {

        @Deprecated
        public Builder() {
            setTranslated(false);
            this.setReportStatus(ReportStatus.NORMAL);
            this.setCancelMessage(false);
        }

        public static Builder from(final AIRMET value) {
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
                        .setAirmetPhenomenon(value.getAirmetPhenomenon())//
                        .setCloudLevels(AirmetCloudLevelsImpl.immutableCopyOf(value.getCloudLevels()))//
                        .setWind(AirmetWindImpl.immutableCopyOf(value.getWind()))//
                        .setObscuration(value.getObscuration()//
                                .map(BuilderHelper::toImmutableList))//
                        .setVisibility(NumericMeasureImpl.immutableCopyOf(value.getVisibility()))//
                        .setCancelledReference(AirmetReferenceImpl.immutableCopyOf(value.getCancelledReference()))//
                        .setAnalysisType(value.getAnalysisType())//
                        .setAnalysisGeometries(value.getAnalysisGeometries()//
                                .map(analysisGeometries -> BuilderHelper.toImmutableList(analysisGeometries,
                                        PhenomenonGeometryWithHeightImpl::immutableCopyOf)))//
                        .setMovingSpeed(NumericMeasureImpl.immutableCopyOf(value.getMovingSpeed()))//
                        .setMovingDirection(NumericMeasureImpl.immutableCopyOf(value.getMovingDirection()))//
                        .setIntensityChange(value.getIntensityChange());
            }
        }

        @Override
        @JsonDeserialize(as = UnitPropertyGroupImpl.class)
        public Builder setIssuingAirTrafficServicesUnit(final UnitPropertyGroup issuingAirTrafficServicesUnit) {
            return super.setIssuingAirTrafficServicesUnit(issuingAirTrafficServicesUnit);
        }

        @Override
        @JsonDeserialize(contentAs = PhenomenonGeometryWithHeightImpl.class)
        public Builder setAnalysisGeometries(final List<PhenomenonGeometryWithHeight> analysis) {
            return super.setAnalysisGeometries(analysis);
        }

        @Override
        @JsonDeserialize(as = UnitPropertyGroupImpl.class)
        public Builder setMeteorologicalWatchOffice(final UnitPropertyGroup meteorologicalWatchOffice) {
            return super.setMeteorologicalWatchOffice(meteorologicalWatchOffice);
        }

        @Override
        @JsonDeserialize(as = AirmetReference.class)
        public Builder setCancelledReference(final AirmetReference cancelledReference) {
            return super.setCancelledReference(cancelledReference);
        }

        @Override
        @JsonDeserialize(as = PartialOrCompleteTimeInstant.class)
        public Builder setIssueTime(final PartialOrCompleteTimeInstant issueTime) {
            return super.setIssueTime(issueTime);
        }

        @Override
        @JsonDeserialize(as = AirmetCloudLevelsImpl.class)
        public Builder setCloudLevels(final AirmetCloudLevels cloudLevels) {
            return super.setCloudLevels(cloudLevels);
        }

        @Override
        @JsonDeserialize(as = AirspaceImpl.class)
        public Builder setAirspace(final Airspace airspace) {
            return super.setAirspace(airspace);
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setMovingSpeed(final NumericMeasure speed) {
            return super.setMovingSpeed(speed);
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setMovingDirection(final NumericMeasure direction) {
            return super.setMovingDirection(direction);
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setVisibility(final NumericMeasure visibility) {
            return super.setVisibility(visibility);
        }

        @Override
        @JsonDeserialize(as = AirmetWindImpl.class)
        public Builder setWind(final AirmetWind windInfo) {
            return super.setWind(AirmetWindImpl.immutableCopyOf(windInfo));
        }

        @Deprecated
        public Builder mapStatus(final UnaryOperator<SigmetAirmetReportStatus> mapper) {
            requireNonNull(mapper, "mapper");
            return setStatus(mapper.apply(getStatus()));
        }

        /**
         * Provides the current builder value of the status property.
         *
         * Note, this method is provided for backward compatibility with previous versions of the API. The <code>status</code> is no longer
         * explicitly stored. This implementation uses {@link SigmetAirmetReportStatus#fromReportStatus(ReportStatus, boolean)} instead to determine the
         * returned value on-the-fly.
         *
         * @return the message status
         *
         * @deprecated migrate to using a combination of {@link #getReportStatus()} and {@link #isCancelMessage()} instead
         */
        @Deprecated
        public SigmetAirmetReportStatus getStatus() {
            return SigmetAirmetReportStatus.fromReportStatus(getReportStatus(), isCancelMessage());
        }

        /**
         * Sets the SIGMET-specific message status.
         *
         * Note, this method is provided for backward compatibility with previous versions of the API. The <code>status</code> is no longer
         * explicitly stored. Instead, this method sets other property values with the following logic:
         * <dl>
         *     <dt>{@link fi.fmi.avi.model.AviationCodeListUser.SigmetAirmetReportStatus#CANCELLATION CANCELLATION}</dt>
         *     <dd>
         *         <code>reportStatus = {@link fi.fmi.avi.model.AviationWeatherMessage.ReportStatus#NORMAL NORMAL}</code><br>
         *         <code>cancelMessage = true</code><br>
         *     </dd>
         *
         *     <dt>{@link fi.fmi.avi.model.AviationCodeListUser.SigmetAirmetReportStatus#NORMAL NORMAL}</dt>
         *     <dd>
         *         <code>reportStatus = {@link fi.fmi.avi.model.AviationWeatherMessage.ReportStatus#NORMAL NORMAL}</code><br>
         *         <code>cancelMessage = false</code><br>
         *     </dd>
         * </dl>
         *
         * @param status
         *         the status to set
         *
         * @return builder
         *
         * @deprecated migrate to using a combination of {@link #setReportStatus(ReportStatus)} and {@link #setCancelMessage(boolean)} instead
         */
        @Deprecated
        public Builder setStatus(final SigmetAirmetReportStatus status) {
            requireNonNull(status);
            return setReportStatus(status.getReportStatus())//
                    .setCancelMessage(status.isCancelMessage());
        }
    }
}
