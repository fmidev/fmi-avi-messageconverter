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
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.PhenomenonGeometry;
import fi.fmi.avi.model.PhenomenonGeometryWithHeight;
import fi.fmi.avi.model.SIGMETAIRMETBuilderHelper;
import fi.fmi.avi.model.UnitPropertyGroup;
import fi.fmi.avi.model.immutable.AirspaceImpl;
import fi.fmi.avi.model.immutable.PhenomenonGeometryImpl;
import fi.fmi.avi.model.immutable.PhenomenonGeometryWithHeightImpl;
import fi.fmi.avi.model.immutable.UnitPropertyGroupImpl;
import fi.fmi.avi.model.sigmet.SIGMET;
import fi.fmi.avi.model.sigmet.SigmetReference;
import fi.fmi.avi.model.sigmet.VAInfo;

@FreeBuilder
@JsonDeserialize(builder = SIGMETImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "reportStatus", "cancelMessage", "issuingAirTrafficServicesUnit", "meteorologicalWatchOffice", "sequenceNumber", "issueTime",
        "validityPeriod", "airspace", "analysisGeometries", "forecastGeometries", "volcano", "noVolcanicAshExpected",
        "volcanicAshMovedToFIR", "cancelledReport", "remarks", "permissibleUsage", "permissibleUsageReason", "permissibleUsageSupplementary", "translated",
        "translatedBulletinID", "translatedBulletinReceptionTime", "translationCentreDesignator", "translationCentreName", "translationTime", "translatedTAC" })
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
                if (geometryWithHeight.getTime().isPresent() && !geometryWithHeight.getTime().get().getCompleteTime().isPresent()) {
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
        return !this.getCancelledReference().isPresent() || (this.getCancelledReference().get().getValidityPeriod().isComplete());
    }

    public static class Builder extends SIGMETImpl_Builder {

        @Deprecated
        public Builder() {
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
                        .setSigmetPhenomenon(value.getSigmetPhenomenon())//
                        .setCancelledReference(SigmetReferenceImpl.immutableCopyOf(value.getCancelledReference()))//
                        .setAnalysisType(value.getAnalysisType())//
                        .setAnalysisGeometries(value.getAnalysisGeometries()//
                                .map(analysisGeometries -> BuilderHelper.toImmutableList(analysisGeometries,
                                        PhenomenonGeometryWithHeightImpl::immutableCopyOf)))//
                        .setForecastGeometries(value.getForecastGeometries()//
                                .map(forecastGeometries -> BuilderHelper.toImmutableList(forecastGeometries, PhenomenonGeometryImpl::immutableCopyOf)))//
                        .setNoVaExpected(value.getNoVaExpected())//
                        .setVAInfo(VAInfoImpl.immutableCopyOf(value.getVAInfo()));

 /*               if (value instanceof VASIGMET) {
                    //From VASigmet
                    VASIGMET va=(VASIGMET)value;
                    builder.setVolcano(VolcanoDescriptionImpl.immutableCopyOf((va.getVolcano())));
                    if (va.getNoVolcanicAshExpected().isPresent()) builder.setNoVolcanicAshExpected(va.getNoVolcanicAshExpected().get());
                    if (va.getVolcanicAshMovedToFIR().isPresent()) builder.setVolcanicAshMovedToFIR(va.getVolcanicAshMovedToFIR().get());
                }
*/
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
        public Builder setCancelledReference(final SigmetReference cancelledReference) {
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
        public Builder setVAInfo(final VAInfo vaInfo) {
            return super.setVAInfo(vaInfo);
        }

/*        @Override
        @JsonDeserialize(as = VAInfoImpl.class)
        public Builder setVAInfo(final Optional<? extends VAInfo> vaInfo) {
            return super.setVAInfo(vaInfo);
        }*/

        /*       @Override
               public Builder setTranslated(boolean translated) {
                   return super.setTranslated(translated);
               }
       */
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
