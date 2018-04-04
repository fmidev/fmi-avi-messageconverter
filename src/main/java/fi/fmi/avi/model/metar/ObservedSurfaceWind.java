package fi.fmi.avi.model.metar;

import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;

@FreeBuilder
@JsonDeserialize(builder = ObservedSurfaceWind.Builder.class)
public interface ObservedSurfaceWind extends AviationCodeListUser {

    boolean variableDirection();

    NumericMeasure meanWindDirection();

    NumericMeasure meanWindSpeed();

    Optional<NumericMeasure> windGust();

    Optional<NumericMeasure> extremeClockwiseWindDirection();

    Optional<NumericMeasure> extremeCounterClockwiseWindDirection();

    Builder toBuilder();

    class Builder extends ObservedSurfaceWind_Builder {
    }

}
