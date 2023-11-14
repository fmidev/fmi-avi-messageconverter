package fi.fmi.avi.model.sigmet.immutable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.*;
import fi.fmi.avi.model.immutable.AirspaceImpl;
import fi.fmi.avi.model.immutable.PhenomenonGeometryImpl;
import fi.fmi.avi.model.immutable.PhenomenonGeometryWithHeightImpl;
import fi.fmi.avi.model.immutable.UnitPropertyGroupImpl;
import fi.fmi.avi.model.sigmet.Reference;
import fi.fmi.avi.model.sigmet.SIGMET;
import fi.fmi.avi.model.sigmet.VAInfo;
import org.inferred.freebuilder.FreeBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;

import static java.util.Objects.requireNonNull;

@FreeBuilder
@JsonDeserialize(builder = SIGMETImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "reportStatus", "cancelMessage", "issuingAirTrafficServicesUnit", "meteorologicalWatchOffice",
        "sequenceNumber", "issueTime",
        "validityPeriod", "airspace", "phenomenonType", "phenomenon", "analysisGeometries", "forecastGeometries", "volcano",
        "volcanicAshMovedToFIR", "cancelledReport", "remarks", "permissibleUsage", "permissibleUsageReason",
        "permissibleUsageSupplementary", "translated",
        "translatedBulletinID", "translatedBulletinReceptionTime", "translationCentreDesignator",
        "translationCentreName", "translationTime", "translatedTAC" })
public abstract class SIGMETImpl implements SIGMET, Serializable {
    private static final long serialVersionUID = -5959366555363410747L;

    public static Builder builder() {
        return new Builder();
    }

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

    @Override
    @JsonIgnore
    @Deprecated
    public SigmetAirmetReportStatus getStatus() {
        return SIGMET.super.getStatus();
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
                if (geometryWithHeight.getTime().isPresent()
                        && !geometryWithHeight.getTime().get().getCompleteTime().isPresent()) {
                    return false;
                }
            }
        }
        if (this.getForecastGeometries().isPresent()) {
            for (final PhenomenonGeometry geometry : this.getForecastGeometries().get()) {
                if (geometry.getTime().isPresent() && !geometry.getTime().get().getCompleteTime().isPresent()) {
                    return false;
                }
            }
        }
        return !this.getCancelledReference().isPresent()
                || (this.getCancelledReference().get().getValidityPeriod().isComplete());
    }

    public static class Builder extends SIGMETImpl_Builder {

        Builder() {
            this.setTranslated(false);
            this.setReportStatus(ReportStatus.NORMAL);
            this.setCancelMessage(false);
        }

        public static Builder from(final SIGMET value) {
            if (value instanceof SIGMETImpl) {
                return ((SIGMETImpl) value).toBuilder();
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
                        .setPhenomenonType(value.getPhenomenonType())//
                        .setPhenomenon(value.getPhenomenon())//
                        .setCancelledReference(SigmetReferenceImpl.immutableCopyOf(value.getCancelledReference()))//
                        .setAnalysisGeometries(value.getAnalysisGeometries()//
                                .map(analysisGeometries -> BuilderHelper.toImmutableList(analysisGeometries,
                                        PhenomenonGeometryWithHeightImpl::immutableCopyOf)))//
                        .setForecastGeometries(value.getForecastGeometries()//
                                .map(forecastGeometries -> BuilderHelper.toImmutableList(forecastGeometries,
                                        PhenomenonGeometryImpl::immutableCopyOf)))//
                        .setVAInfo(VAInfoImpl.immutableCopyOf(value.getVAInfo()));
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
        @JsonDeserialize(contentAs = PhenomenonGeometryImpl.class)
        public Builder setForecastGeometries(final List<PhenomenonGeometry> analysis) {
            return super.setForecastGeometries(analysis);
        }

        @Override
        @JsonDeserialize(as = UnitPropertyGroupImpl.class)
        public Builder setMeteorologicalWatchOffice(final UnitPropertyGroup meteorologicalWatchOffice) {
            return super.setMeteorologicalWatchOffice(meteorologicalWatchOffice);
        }

        @Override
        @JsonDeserialize(as = SigmetReferenceImpl.class)
        public Builder setCancelledReference(final Reference cancelledReference) {
            return super.setCancelledReference(cancelledReference);
        }

        @Override
        @JsonDeserialize(as = PartialOrCompleteTimeInstant.class)
        public Builder setIssueTime(final PartialOrCompleteTimeInstant issueTime) {
            return super.setIssueTime(issueTime);
        }

        @Override
        @JsonDeserialize(as = PartialOrCompleteTimePeriod.class)
        public Builder setValidityPeriod(final PartialOrCompleteTimePeriod validityPeriod) {
            return super.setValidityPeriod(validityPeriod);
        }

        @Override
        @JsonDeserialize(as = VAInfoImpl.class)
        @JsonProperty("VAInfo")
        public Builder setVAInfo(final VAInfo vaInfo) {
            return super.setVAInfo(vaInfo);
        }

        @Override
        @JsonIgnore
        @JsonDeserialize(as = VAInfoImpl.class)
        public Builder setVAInfo(final Optional<? extends VAInfo> vaInfo) {
            return super.setVAInfo(vaInfo);
        }

        @Override
        @JsonDeserialize(as = AirspaceImpl.class)
        public Builder setAirspace(final Airspace airspace) {
            return super.setAirspace(airspace);
        }

        @Deprecated
        public Builder mapStatus(final UnaryOperator<SigmetAirmetReportStatus> mapper) {
            requireNonNull(mapper, "mapper");
            return setStatus(mapper.apply(getStatus()));
        }

        /**
         * Provides the current builder value of the status property.
         *
         * Note, this method is provided for backward compatibility with previous
         * versions of the API. The <code>status</code> is no longer
         * explicitly stored. This implementation uses
         * {@link SigmetAirmetReportStatus#fromReportStatus(ReportStatus, boolean)}
         * instead to determine the
         * returned value on-the-fly.
         *
         * @return the message status
         *
         * @deprecated migrate to using a combination of {@link #getReportStatus()} and
         *             {@link #isCancelMessage()} instead
         */
        @Deprecated
        public SigmetAirmetReportStatus getStatus() {
            return SigmetAirmetReportStatus.fromReportStatus(getReportStatus(), isCancelMessage());
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
