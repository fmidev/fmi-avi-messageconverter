package fi.fmi.avi.model.swx.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.swx.IssuingCenter;

@FreeBuilder
@JsonDeserialize(builder = IssuingCenterImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "designator", "name", "type" })
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
            return Builder.from(issuingCenter).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<IssuingCenterImpl> immutableCopyOf(final Optional<IssuingCenter> issuingCenter) {
        Objects.requireNonNull(issuingCenter);
        return issuingCenter.map(IssuingCenterImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends IssuingCenterImpl_Builder {
        @Deprecated
        Builder() {
        }

        public static Builder from(final IssuingCenter value) {
            if (value instanceof IssuingCenterImpl) {
                return ((IssuingCenterImpl) value).toBuilder();
            } else {
                return builder().setDesignator(value.getDesignator()).setName(value.getName()).setType(value.getType());
            }
        }
    }
}
