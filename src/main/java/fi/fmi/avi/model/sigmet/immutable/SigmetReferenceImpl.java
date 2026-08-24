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
@JsonDeserialize(builder = SigmetReferenceImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class SigmetReferenceImpl implements Reference, Serializable {
    private static final long serialVersionUID = -7590197694737728555L;

    public static SigmetReferenceImpl immutableCopyOf(final Reference sigmetReference) {
        Objects.requireNonNull(sigmetReference);
        if (sigmetReference instanceof SigmetReferenceImpl) {
            return (SigmetReferenceImpl) sigmetReference;
        } else {
            return Builder.copyOf(sigmetReference).build();
        }
    }

    public static Optional<SigmetReferenceImpl> immutableCopyOf(final Optional<Reference> sigmetReference) {
        Objects.requireNonNull(sigmetReference);
        return sigmetReference.map(SigmetReferenceImpl::immutableCopyOf);
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

    public static class Builder extends ImmutableSigmetReferenceImpl.Builder {

        public static Builder copyOf(final Reference value) {
            if (value instanceof SigmetReferenceImpl) {
                return ((SigmetReferenceImpl) value).toBuilder();
            } else {
                return new Builder();

            }
        }



    }
}
