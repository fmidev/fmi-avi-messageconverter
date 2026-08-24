package fi.fmi.avi.model.swx.amd79.immutable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.swx.amd79.IssuingCenter;
import org.immutables.value.Value;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

@Value.Immutable
@JsonDeserialize(builder = IssuingCenterImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"designator", "name", "type"})
public abstract class IssuingCenterImpl implements IssuingCenter, Serializable {

    private static final long serialVersionUID = -7092051001055467810L;

    public static Builder builder() {
        return new Builder();
    }

    public static IssuingCenterImpl immutableCopyOf(final IssuingCenter issuingCenter) {
        Objects.requireNonNull(issuingCenter);
        if (issuingCenter instanceof IssuingCenterImpl) {
            return (IssuingCenterImpl) issuingCenter;
        } else {
            return Builder.copyOf(issuingCenter).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<IssuingCenterImpl> immutableCopyOf(final Optional<IssuingCenter> issuingCenter) {
        Objects.requireNonNull(issuingCenter);
        return issuingCenter.map(IssuingCenterImpl::immutableCopyOf);
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    public static class Builder extends ImmutableIssuingCenterImpl.Builder {
        @Deprecated
        Builder() {
        }

        public static Builder copyOf(final IssuingCenter value) {
            if (value instanceof IssuingCenterImpl) {
                return ((IssuingCenterImpl) value).toBuilder();
            } else {
                return builder().setDesignator(value.getDesignator()).setName(value.getName()).setType(value.getType());
            }
        }

        public static Builder fromAmd82(final fi.fmi.avi.model.swx.amd82.IssuingCenter value) {
            return builder().setDesignator(value.getDesignator()).setName(value.getName()).setType(value.getType());
        }
    }
}
