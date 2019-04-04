package fi.fmi.avi.model.sigmet.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.UnitPropertyGroup;
import fi.fmi.avi.model.VolcanoDescription;
import fi.fmi.avi.model.immutable.UnitPropertyGroupImpl;
import fi.fmi.avi.model.immutable.VolcanoDescriptionImpl;
import fi.fmi.avi.model.sigmet.VAInfo;

@FreeBuilder
    @JsonDeserialize(builder = VAInfoImpl.Builder.class)
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public abstract class VAInfoImpl implements VAInfo, Serializable {
        public static VAInfoImpl immutableCopyOf(final VAInfo vaInfo) {
            Objects.requireNonNull(vaInfo);
            if (vaInfo instanceof VAInfoImpl) {
                return (VAInfoImpl) vaInfo;
            } else {
                return Builder.from(vaInfo).build();
            }
        }

        public static Optional<VAInfoImpl> immutableCopyOf(final Optional<VAInfo> vaInfo) {
            Objects.requireNonNull(vaInfo);
            return vaInfo.map(VAInfoImpl::immutableCopyOf);
        }

        public abstract Builder toBuilder();

        public static class Builder extends VAInfoImpl_Builder {

            public static Builder from(final VAInfo value) {
                if (value instanceof VAInfoImpl) {
                    return ((VAInfoImpl) value).toBuilder();
                } else {
                    return new Builder();
                }
            }

            @Override
            @JsonDeserialize(as= VolcanoDescriptionImpl.class)
            public Builder setVolcano(final VolcanoDescription volcano) { return super.setVolcano(volcano);}

            @Override
            public Builder setNoVolcanicAshExpected(boolean noVolcanicAshExpected) { return super.setNoVolcanicAshExpected(noVolcanicAshExpected);}


            @Override
            @JsonDeserialize(as = UnitPropertyGroupImpl.class)
            public Builder setVolcanicAshMovedToFIR(final UnitPropertyGroup issuingAirTrafficServicesUnit) {
                return super.setVolcanicAshMovedToFIR(issuingAirTrafficServicesUnit);
            }
        }
}

