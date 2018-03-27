package fi.fmi.avi.model;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = RunwayDirection.Builder.class)
public interface RunwayDirection {

    String getDesignator();

    Double getTrueBearing();

    Double getElevationTDZMeters();

    Aerodrome getAssociatedAirportHeliport();

    boolean isResolved();

    Builder toBuilder();
    
    class Builder extends RunwayDirection_Builder {}
}