package fi.fmi.avi.model.sigmet.immutable;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.AviationCodeListUser.AeronauticalSignificantWeatherPhenomenon;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.UnitPropertyGroup;
import fi.fmi.avi.model.immutable.UnitPropertyGroupImpl;
import fi.fmi.avi.model.sigmet.SigmetReference;
import org.inferred.freebuilder.FreeBuilder;

import java.io.Serializable;
import java.util.Optional;

@FreeBuilder
@JsonDeserialize(builder = SigmetReferenceImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class SigmetReferenceImpl implements SigmetReference, Serializable {
    public static SigmetReferenceImpl immutableCopyOf(final SigmetReference sigmetReference) {
        Objects.requireNonNull(sigmetReference);
        if (sigmetReference instanceof SigmetReferenceImpl) {
            return (SigmetReferenceImpl) sigmetReference;
        } else {
            return Builder.from(sigmetReference).build();
        }
    }

    public static Optional<SigmetReferenceImpl> immutableCopyOf(final Optional<SigmetReference> sigmetReference) {
        Objects.requireNonNull(sigmetReference);
        return sigmetReference.map(SigmetReferenceImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends SigmetReferenceImpl_Builder {

        public static Builder from(final SigmetReference value) {
            if (value instanceof SigmetReferenceImpl) {
                return ((SigmetReferenceImpl) value).toBuilder();
            } else {
                return new Builder();

            }
        }

        @Override
        @JsonDeserialize(as = UnitPropertyGroupImpl.class)
        public Builder setIssuingAirTrafficServicesUnit(final UnitPropertyGroup issuingAirTrafficServicesUnit) {
            return super.setIssuingAirTrafficServicesUnit(issuingAirTrafficServicesUnit);
        }

        @Override
        @JsonDeserialize(as = UnitPropertyGroupImpl.class)
        public Builder setMeteorologicalWatchOffice(final UnitPropertyGroup meteorologicalWatchOffice) {
            return super.setMeteorologicalWatchOffice(meteorologicalWatchOffice);
        }

        @Override
        @JsonDeserialize(as = AeronauticalSignificantWeatherPhenomenon.class)
        public Builder setPhenomenon(final AeronauticalSignificantWeatherPhenomenon sigmetPhenomenon) {
            return super.setPhenomenon(sigmetPhenomenon);
        }

        @Override
        @JsonDeserialize(as = PartialOrCompleteTimePeriod.class)
        public Builder setValidityPeriod(final PartialOrCompleteTimePeriod validityPeriod) {
            return super.setValidityPeriod(validityPeriod);
        }
    }
}
