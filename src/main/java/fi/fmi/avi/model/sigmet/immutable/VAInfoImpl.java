package fi.fmi.avi.model.sigmet.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.UnitPropertyGroup;
import fi.fmi.avi.model.VolcanoDescription;
import fi.fmi.avi.model.immutable.UnitPropertyGroupImpl;
import fi.fmi.avi.model.immutable.VolcanoDescriptionImpl;
import fi.fmi.avi.model.sigmet.VAInfo;

@Value.Immutable
@JsonDeserialize(builder = VAInfoImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class VAInfoImpl implements VAInfo, Serializable {
    private static final long serialVersionUID = 7039565463496071014L;

    public static VAInfoImpl immutableCopyOf(final VAInfo vaInfo) {
        Objects.requireNonNull(vaInfo);
        if (vaInfo instanceof VAInfoImpl) {
            return (VAInfoImpl) vaInfo;
        } else {
            return Builder.copyOf(vaInfo).build();
        }
    }

    public static Optional<VAInfoImpl> immutableCopyOf(final Optional<VAInfo> vaInfo) {
        Objects.requireNonNull(vaInfo);
        return vaInfo.map(VAInfoImpl::immutableCopyOf);
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    @Override
    @JsonDeserialize(contentAs = VolcanoDescriptionImpl.class)
    public abstract Optional<VolcanoDescription> getVolcano();

    @Override
    @JsonDeserialize(contentAs = UnitPropertyGroupImpl.class)
    public abstract Optional<UnitPropertyGroup> getVolcanicAshMovedToFIR();

    public static class Builder extends ImmutableVAInfoImpl.Builder {

        public static Builder copyOf(final VAInfo value) {
            if (value instanceof VAInfoImpl) {
                return ((VAInfoImpl) value).toBuilder();
            } else {
                return new Builder();
            }
        }


    }
}

