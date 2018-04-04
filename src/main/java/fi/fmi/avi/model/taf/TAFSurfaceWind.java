package fi.fmi.avi.model.taf;

import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.NumericMeasure;

/**
 * Created by rinne on 30/01/15.
 */
@FreeBuilder
@JsonDeserialize(builder = TAFSurfaceWind.Builder.class)
public interface TAFSurfaceWind {

    NumericMeasure meanWindDirection();

    NumericMeasure meanWindSpeed();

    Optional<NumericMeasure> windGust();

    boolean variableDirection();

    Builder toBuilder();

    class Builder extends TAFSurfaceWind_Builder {
        public Builder() {
            variableDirection(false);
        }
    }
}
