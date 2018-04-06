package fi.fmi.avi.model;

import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = GeoPosition.Builder.class)
public interface GeoPosition {

    String getCoordinateReferenceSystemId();

    Double[] getCoordinates();

    Optional<Double> getElevationValue();

    Optional<String> getElevationUom();


    Builder toBuilder();
    
    class Builder extends GeoPosition_Builder {}
}