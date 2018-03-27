package fi.fmi.avi.model;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = Aerodrome.Builder.class)
public interface Aerodrome {

    String getDesignator();

    String getName();

    String getLocationIndicatorICAO();

    String getDesignatorIATA();

    Double getFieldElevationValue();

    GeoPosition getReferencePoint();
    
    boolean isResolved();

    Builder toBuilder();
    
    class Builder extends Aerodrome_Builder {}
}