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
import org.immutables.value.Value;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;

import static java.util.Objects.requireNonNull;

@Value.Immutable
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
            return Builder.copyOf(sigmet).build();
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

    @Override
    @JsonDeserialize(as = UnitPropertyGroupImpl.class)
    public abstract UnitPropertyGroup getIssuingAirTrafficServicesUnit();

    @Override
    @JsonDeserialize(as = UnitPropertyGroupImpl.class)
    public abstract UnitPropertyGroup getMeteorologicalWatchOffice();

    @Override
    @JsonDeserialize(contentAs = SigmetReferenceImpl.class)
    public abstract Optional<Reference> getCancelledReference();

    @Override
    public abstract Optional<PartialOrCompleteTimeInstant> getIssueTime();

    @Override
    @JsonDeserialize(as = PartialOrCompleteTimePeriod.class)
    public abstract PartialOrCompleteTimePeriod getValidityPeriod();

    @Override
    @JsonDeserialize(as = AirspaceImpl.class)
    public abstract Airspace getAirspace();

    // NOTE: Immutables generates final setters for these properties, which cannot be overridden to carry
    // @JsonDeserialize/@JsonProperty hints (see doc/immutables-migration.md). The hints move onto these
    // abstract getter re-declarations instead; the package's passAnnotations style (see package-info.java)
    // propagates them onto the generated builder setters for Jackson's builder-based deserialization.
    // NOTE: no per-property @JsonDeserialize(contentAs=...) hint here: for an Optional<List<X>>-shaped property on a
    // non-detached builder, Immutables' passAnnotations propagates such a hint only onto the generated
    // setAnalysisGeometries(Optional<...>) overload (both overloads are `final`, so neither can be overridden to
    // relocate it), and Jackson then misapplies "contentAs" to the Optional's immediate content (List<X>) rather
    // than recursing into the list's own element type X, breaking deserialization. Instead, PhenomenonGeometryWithHeight
    // and PhenomenonGeometry carry their own class-level @JsonDeserialize(as=...) hints (see their javadoc and
    // doc/immutables-migration.md), which Jackson can apply regardless of how deeply nested the property is.
    @Override
    public abstract Optional<List<PhenomenonGeometryWithHeight>> getAnalysisGeometries();

    @Override
    public abstract Optional<List<PhenomenonGeometry>> getForecastGeometries();

    @Override
    @JsonDeserialize(contentAs = VAInfoImpl.class)
    @JsonProperty("VAInfo")
    public abstract Optional<VAInfo> getVAInfo();

    public static class Builder extends ImmutableSIGMETImpl.Builder {

        Builder() {
            this.setTranslated(false);
            this.setReportStatus(ReportStatus.NORMAL);
            this.setCancelMessage(false);
        }

        public static Builder copyOf(final SIGMET value) {
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
