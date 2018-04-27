package fi.fmi.avi.model.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.Aerodrome;

/**
 * Created by rinne on 13/04/2018.
 */

@FreeBuilder
@JsonDeserialize(builder = AerodromeImpl.Builder.class)
public abstract class AerodromeImpl implements Aerodrome, Serializable {

    public static AerodromeImpl immutableCopyOf(final Aerodrome aerodrome) {
        checkNotNull(aerodrome);
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
    }
}
