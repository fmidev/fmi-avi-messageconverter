package fi.fmi.avi.model.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.GeoPosition;

/**
 * Created by rinne on 13/04/2018.
 */

@FreeBuilder
@JsonDeserialize(builder = AerodromeImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"designator", "name", "locationIndicatorICAO", "fieldElevation", "referencePoint"})
public abstract class AerodromeImpl implements Aerodrome, Serializable {

    public static AerodromeImpl immutableCopyOf(final Aerodrome aerodrome) {
        Objects.requireNonNull(aerodrome);
        if (aerodrome instanceof AerodromeImpl) {
            return (AerodromeImpl) aerodrome;
        } else {
            return Builder.from(aerodrome).build();
        }
    }

    public static Optional<AerodromeImpl> immutableCopyOf(final Optional<Aerodrome> aerodrome) {
        return aerodrome.map(AerodromeImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends AerodromeImpl_Builder {

        public static Builder from(final Aerodrome value) {
            AerodromeImpl.Builder retval = new AerodromeImpl.Builder().setDesignator(value.getDesignator())
                    .setDesignatorIATA(value.getDesignatorIATA())
                    .setFieldElevationValue(value.getFieldElevationValue())
                    .setLocationIndicatorICAO(value.getLocationIndicatorICAO())
                    .setName(value.getName())
                    .setReferencePoint(GeoPositionImpl.immutableCopyOf(value.getReferencePoint()));

            return retval;
        }

        @Override
        @JsonDeserialize(as = GeoPositionImpl.class)
        public Builder setReferencePoint(final GeoPosition referencePoint) {
            return super.setReferencePoint(referencePoint);
        }
    }
}
