package fi.fmi.avi.model.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.ElevatedPoint;

/**
 * Created by rinne on 13/04/2018.
 */

@FreeBuilder
@JsonDeserialize(builder = AerodromeImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "designator", "name", "locationIndicatorICAO", "fieldElevationValue", "fieldElevationUom", "referencePoint" })
public abstract class AerodromeImpl implements Aerodrome, Serializable {

    private static final long serialVersionUID = -6822087279546133445L;

    public static Builder builder() {
        return new Builder();
    }

    public static AerodromeImpl immutableCopyOf(final Aerodrome aerodrome) {
        Objects.requireNonNull(aerodrome);
        if (aerodrome instanceof AerodromeImpl) {
            return (AerodromeImpl) aerodrome;
        } else {
            return Builder.from(aerodrome).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<AerodromeImpl> immutableCopyOf(final Optional<Aerodrome> aerodrome) {
        return aerodrome.map(AerodromeImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends AerodromeImpl_Builder {

        @Deprecated
        public Builder() {
        }

        public static Builder from(final Aerodrome value) {
            if (value instanceof AerodromeImpl) {
                return ((AerodromeImpl) value).toBuilder();
            } else {
                return AerodromeImpl.builder()//
                        .setDesignator(value.getDesignator())
                        .setDesignatorIATA(value.getDesignatorIATA())
                        .setFieldElevationValue(value.getFieldElevationValue())
                        .setFieldElevationUom(value.getFieldElevationUom())
                        .setLocationIndicatorICAO(value.getLocationIndicatorICAO())
                        .setName(value.getName())
                        .setReferencePoint(ElevatedPointImpl.immutableCopyOf(value.getReferencePoint()));
            }
        }

        @Override
        @JsonDeserialize(as = ElevatedPointImpl.class)
        public Builder setReferencePoint(final ElevatedPoint referencePoint) {
            return super.setReferencePoint(referencePoint);
        }
    }
}
