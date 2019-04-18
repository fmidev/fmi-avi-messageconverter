package fi.fmi.avi.model.sigmet.immutable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.Airspace;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.UnitPropertyGroup;
import fi.fmi.avi.model.immutable.AirspaceImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.UnitPropertyGroupImpl;
import fi.fmi.avi.model.sigmet.PhenomenonGeometry;
import fi.fmi.avi.model.sigmet.PhenomenonGeometryWithHeight;
import fi.fmi.avi.model.sigmet.SIGMET;
import fi.fmi.avi.model.sigmet.SigmetReference;
import fi.fmi.avi.model.sigmet.VAInfo;

@FreeBuilder
@JsonDeserialize(builder = SIGMETImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "status", "issuingAirTrafficServicesUnit", "meteorologicalWatchOffice", "sequenceNumber", "issueTime", "validityPeriod", "airspace",
        "analysisGeometries", "forecastGeometries",
        "movingSpeed", "movingDirection",
        "volcano", "noVolcanicAshExpected", "volcanicAshMovedToFIR",
        "cancelledReport", "remarks", "permissibleUsage", "permissibleUsageReason", "permissibleUsageSupplementary", "translated",
        "translatedBulletinID", "translatedBulletinReceptionTime", "translationCentreDesignator", "translationCentreName", "translationTime", "translatedTAC" })
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

    public static Builder builder() {
        return new Builder();
    }

    public abstract Builder toBuilder();

    @Override
    @JsonIgnore
    public boolean areAllTimeReferencesComplete() {
        if (!this.getValidityPeriod().isComplete()) {
            return false;
        }
        if (this.getAnalysisGeometries().isPresent()) {
            for (PhenomenonGeometryWithHeight geometryWithHeight : this.getAnalysisGeometries().get()) {
                if (geometryWithHeight.getTime().isPresent() && !geometryWithHeight.getTime().get().getCompleteTime().isPresent()) {
                    return false;
                }
            }
        }
        if (this.getForecastGeometries().isPresent()) {
            for (PhenomenonGeometry geometry: this.getForecastGeometries().get()) {
                if (geometry.getTime().isPresent() && !geometry.getTime().get().getCompleteTime().isPresent()) {
                    return false;
                }
            }
        }
        if (this.getCancelledReference().isPresent() && (!this.getCancelledReference().get().getValidityPeriod().isComplete())) {
            return false;
        }
        return true;
    }

    public static class Builder extends SIGMETImpl_Builder {

        public Builder() {
          this.setTranslated(false);
        }

        public static Builder from(final SIGMET value) {
            if (value instanceof SIGMETImpl) {
                return ((SIGMETImpl) value).toBuilder();
            } else {
                //From AviationWeatherMessage
                Builder retval = new Builder()//
                        .setIssueTime(value.getIssueTime())
                        .setPermissibleUsage(value.getPermissibleUsage())
                        .setPermissibleUsageReason(value.getPermissibleUsageReason())
                        .setPermissibleUsageSupplementary(value.getPermissibleUsageSupplementary())
                        .setAnalysisType(value.getAnalysisType())
                        .setTranslated(value.isTranslated())
                        .setTranslatedBulletinID(value.getTranslatedBulletinID())
                        .setTranslatedBulletinReceptionTime(value.getTranslatedBulletinReceptionTime())
                        .setTranslationCentreDesignator(value.getTranslationCentreDesignator())
                        .setTranslationCentreName(value.getTranslationCentreName())
                        .setTranslationTime(value.getTranslationTime())
                        .setTranslatedTAC(value.getTranslatedTAC());

                value.getRemarks().map(remarks -> retval.setRemarks(Collections.unmodifiableList(new ArrayList<>(remarks))));

                //From AirTrafficServicesUnitWeatherMessage
                retval.setIssuingAirTrafficServicesUnit(UnitPropertyGroupImpl.immutableCopyOf(value.getIssuingAirTrafficServicesUnit()))
                        .setMeteorologicalWatchOffice(UnitPropertyGroupImpl.immutableCopyOf(value.getMeteorologicalWatchOffice()));

                //From SigmetAirmet
                retval.setAirspace(AirspaceImpl.immutableCopyOf(value.getAirspace()))
                    .setStatus(value.getStatus())
                    .setSequenceNumber(value.getSequenceNumber())
                    .setValidityPeriod(value.getValidityPeriod());

                //From Sigmet
                retval.setSigmetPhenomenon(value.getSigmetPhenomenon())
                        .setCancelledReference(SigmetReferenceImpl.immutableCopyOf(value.getCancelledReference()))
                        .setMovingDirection(NumericMeasureImpl.immutableCopyOf(value.getMovingDirection()))
                        .setMovingSpeed(NumericMeasureImpl.immutableCopyOf(value.getMovingSpeed()));

                value.getAnalysisGeometries().map(an -> retval.setAnalysisGeometries(
                        (Collections.unmodifiableList(an.stream().map(PhenomenonGeometryWithHeightImpl::immutableCopyOf).collect(Collectors.toList())))));

                value.getForecastGeometries().map(an -> retval.setForecastGeometries(
                        (Collections.unmodifiableList(an.stream().map(PhenomenonGeometryImpl::immutableCopyOf).collect(Collectors.toList())))));

                retval.setVAInfo(value.getVAInfo());
 /*               if (value instanceof VASIGMET) {
                    //From VASigmet
                    VASIGMET va=(VASIGMET)value;
                    retval.setVolcano(VolcanoDescriptionImpl.immutableCopyOf((va.getVolcano())));
                    if (va.getNoVolcanicAshExpected().isPresent()) retval.setNoVolcanicAshExpected(va.getNoVolcanicAshExpected().get());
                    if (va.getVolcanicAshMovedToFIR().isPresent()) retval.setVolcanicAshMovedToFIR(va.getVolcanicAshMovedToFIR().get());
                }
*/
                return retval;
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
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setMovingSpeed(NumericMeasure speed) { return super.setMovingSpeed(speed);}

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setMovingDirection(NumericMeasure direction) { return super.setMovingDirection(direction);}

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
        @JsonDeserialize( as = AirspaceImpl.class)
        public Builder setAirspace(Airspace airspace) { return super.setAirspace(airspace);}
    }
}
