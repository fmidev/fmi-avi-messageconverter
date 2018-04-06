package fi.fmi.avi.model;

import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = RunwayDirection.Builder.class)
public interface RunwayDirection {

    String getDesignator();

    Optional<Double> getTrueBearing();

    Optional<Double> getElevationTDZMeters();

    Optional<Aerodrome> getAssociatedAirportHeliport();


    Builder toBuilder();
    
    class Builder extends RunwayDirection_Builder {}
}