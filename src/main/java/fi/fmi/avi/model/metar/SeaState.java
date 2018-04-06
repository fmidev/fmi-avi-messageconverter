package fi.fmi.avi.model.metar;

import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;

@FreeBuilder
@JsonDeserialize(builder = SeaState.Builder.class)
public interface SeaState extends AviationCodeListUser {

    NumericMeasure getSeaSurfaceTemperature();

    Optional<NumericMeasure> getSignificantWaveHeight();

    Optional<SeaSurfaceState> getSeaSurfaceState();

    Builder toBuilder();

    class Builder extends SeaState_Builder {
    }

}
