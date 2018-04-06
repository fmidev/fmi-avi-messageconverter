package fi.fmi.avi.model.metar;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = SPECI.Builder.class)
public interface SPECI extends MeteorologicalTerminalAirReport {

    Builder toBuilder();

    class Builder extends SPECI_Builder {

    }

}
