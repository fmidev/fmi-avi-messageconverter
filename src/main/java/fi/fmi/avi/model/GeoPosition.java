package fi.fmi.avi.model;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = GeoPosition.Builder.class)
public interface GeoPosition {

    String getCoordinateReferenceSystemId();

    Double[] getCoordinates();

    Double getElevationValue();

    String getElevationUom();

    Builder toBuilder();
    
    class Builder extends GeoPosition_Builder {}
}