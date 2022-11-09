package fi.fmi.avi.model.sigmet.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.UnitPropertyGroup;
import fi.fmi.avi.model.immutable.UnitPropertyGroupImpl;
import fi.fmi.avi.model.sigmet.Reference;

@FreeBuilder
@JsonDeserialize(builder = AirmetReferenceImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class AirmetReferenceImpl implements Reference, Serializable {
    private static final long serialVersionUID = 2988230428861993266L;

    public static AirmetReferenceImpl immutableCopyOf(final Reference airmetReference) {
        Objects.requireNonNull(airmetReference);
        if (airmetReference instanceof AirmetReferenceImpl) {
            return (AirmetReferenceImpl) airmetReference;
        } else {
            return Builder.from(airmetReference).build();
        }
    }

    public static Optional<AirmetReferenceImpl> immutableCopyOf(final Optional<Reference> airmetReference) {
        Objects.requireNonNull(airmetReference);
        return airmetReference.map(AirmetReferenceImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends AirmetReferenceImpl_Builder {

        public static Builder from(final Reference value) {
            if (value instanceof AirmetReferenceImpl) {
                return ((AirmetReferenceImpl) value).toBuilder();
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
        @JsonDeserialize(as = PartialOrCompleteTimePeriod.class)
        public Builder setValidityPeriod(final PartialOrCompleteTimePeriod validityPeriod) {
            return super.setValidityPeriod(validityPeriod);
        }
    }
}
