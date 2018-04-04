package fi.fmi.avi.model;

import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = RunwayDirection.Builder.class)
public interface RunwayDirection {

    String designator();

    Optional<Double> trueBearing();

    Optional<Double> elevationTDZMeters();

    Optional<Aerodrome> associatedAirportHeliport();


    Builder toBuilder();
    
    class Builder extends RunwayDirection_Builder {}
}