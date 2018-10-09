package fi.fmi.avi.model.immutable;


import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.RunwayDirection;

/**
 * Created by rinne on 17/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = RunwayDirectionImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"designator", "elevationTDZ", "trueBearing", "associatedAirportHeliport"})
public abstract class RunwayDirectionImpl implements RunwayDirection, Serializable {

    public static RunwayDirectionImpl immutableCopyOf(final RunwayDirection runwayDirection) {
        Objects.requireNonNull(runwayDirection);
        if (runwayDirection instanceof RunwayDirectionImpl) {
            return (RunwayDirectionImpl) runwayDirection;
        } else {
            return Builder.from(runwayDirection).build();
        }
    }

    public static Optional<RunwayDirectionImpl> immutableCopyOf(final Optional<RunwayDirection> runwayDirection) {
        Objects.requireNonNull(runwayDirection);
        return runwayDirection.map(RunwayDirectionImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends RunwayDirectionImpl_Builder {

        public static Builder from(final RunwayDirection value) {
            if (value instanceof RunwayDirectionImpl) {
                return ((RunwayDirectionImpl) value).toBuilder();
            } else {
                return new RunwayDirectionImpl.Builder()//
                        .setDesignator(value.getDesignator())//
                        .setElevationTDZMeters(value.getElevationTDZMeters())//
                        .setTrueBearing(value.getTrueBearing())//
                        .setAssociatedAirportHeliport(AerodromeImpl.immutableCopyOf(value.getAssociatedAirportHeliport()));
            }
        }

        @Override
        @JsonDeserialize(as = AerodromeImpl.class)
        public Builder setAssociatedAirportHeliport(final Aerodrome associatedAirportHeliport) {
            return super.setAssociatedAirportHeliport(associatedAirportHeliport);
        }
    }
}
