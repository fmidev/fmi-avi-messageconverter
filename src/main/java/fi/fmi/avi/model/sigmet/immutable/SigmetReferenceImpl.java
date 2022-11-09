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
@JsonDeserialize(builder = SigmetReferenceImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class SigmetReferenceImpl implements Reference, Serializable {
    private static final long serialVersionUID = -7590197694737728555L;

    public static SigmetReferenceImpl immutableCopyOf(final Reference sigmetReference) {
        Objects.requireNonNull(sigmetReference);
        if (sigmetReference instanceof SigmetReferenceImpl) {
            return (SigmetReferenceImpl) sigmetReference;
        } else {
            return Builder.from(sigmetReference).build();
        }
    }

    public static Optional<SigmetReferenceImpl> immutableCopyOf(final Optional<Reference> sigmetReference) {
        Objects.requireNonNull(sigmetReference);
        return sigmetReference.map(SigmetReferenceImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends SigmetReferenceImpl_Builder {

        public static Builder from(final Reference value) {
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
        @JsonDeserialize(as = PartialOrCompleteTimePeriod.class)
        public Builder setValidityPeriod(final PartialOrCompleteTimePeriod validityPeriod) {
            return super.setValidityPeriod(validityPeriod);
        }
    }
}
