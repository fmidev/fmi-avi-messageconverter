package fi.fmi.avi.model.sigmet.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.UnitPropertyGroup;
import fi.fmi.avi.model.immutable.UnitPropertyGroupImpl;
import fi.fmi.avi.model.sigmet.Reference;

@Value.Immutable
@JsonDeserialize(builder = AirmetReferenceImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class AirmetReferenceImpl implements Reference, Serializable {
    private static final long serialVersionUID = 2988230428861993266L;

    public static AirmetReferenceImpl immutableCopyOf(final Reference airmetReference) {
        Objects.requireNonNull(airmetReference);
        if (airmetReference instanceof AirmetReferenceImpl) {
            return (AirmetReferenceImpl) airmetReference;
        } else {
            return Builder.copyOf(airmetReference).build();
        }
    }

    public static Optional<AirmetReferenceImpl> immutableCopyOf(final Optional<Reference> airmetReference) {
        Objects.requireNonNull(airmetReference);
        return airmetReference.map(AirmetReferenceImpl::immutableCopyOf);
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    @Override
    @JsonDeserialize(as = UnitPropertyGroupImpl.class)
    public abstract UnitPropertyGroup getIssuingAirTrafficServicesUnit();

    @Override
    @JsonDeserialize(as = UnitPropertyGroupImpl.class)
    public abstract UnitPropertyGroup getMeteorologicalWatchOffice();

    @Override
    @JsonDeserialize(as = PartialOrCompleteTimePeriod.class)
    public abstract PartialOrCompleteTimePeriod getValidityPeriod();

    public static class Builder extends ImmutableAirmetReferenceImpl.Builder {

        public static Builder copyOf(final Reference value) {
            if (value instanceof AirmetReferenceImpl) {
                return ((AirmetReferenceImpl) value).toBuilder();
            } else {
                return new Builder();

            }
        }



    }
}
