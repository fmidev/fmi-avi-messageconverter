package fi.fmi.avi.model.sigmet.immutable;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
@JsonPropertyOrder({ "status", "issuingAirTrafficServicesUnit", "meteorologicalWatchOffice", "sequenceNumber", "issueTime", "validityPeriod", "airspace",
        "movingDirection", "movingSpeed", "analysis", "cancelledReport", "remarks", "permissibleUsage", "permissibleUsageReason",
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

    public static Optional<AIRMETImpl> immutableCopyOf(final Optional<AIRMET> sigmet) {
        Objects.requireNonNull(sigmet);
        return sigmet.map(AIRMETImpl::immutableCopyOf);
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
        }

        public static Builder from(final AIRMET value) {
            if (value instanceof AIRMETImpl) {
                return ((AIRMETImpl) value).toBuilder();
            } else {
                final Builder builder = builder();
                AviationWeatherMessageBuilderHelper.copyFrom(builder, value,  //
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
                        Builder::setTranslatedTAC, //
                        Builder::setIssueTime, //
                        Builder::setReportStatus);
                AirTrafficServicesUnitWeatherMessageBuilderHelper.copyFrom(builder, value, //
                        Builder::setIssuingAirTrafficServicesUnit, //
                        Builder::setMeteorologicalWatchOffice);
                SIGMETAIRMETBuilderHelper.copyFrom(builder, value, //
                        Builder::setSequenceNumber, //
                        Builder::setValidityPeriod, //
                        Builder::setAirspace, //
                        Builder::setStatus);
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
    }
}
