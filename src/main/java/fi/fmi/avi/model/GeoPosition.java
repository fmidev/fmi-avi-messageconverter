package fi.fmi.avi.model;

import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = GeoPosition.Builder.class)
public interface GeoPosition {

    String coordinateReferenceSystemId();

    Double[] coordinates();

    Optional<Double> elevationValue();

    Optional<String> elevationUom();


    Builder toBuilder();
    
    class Builder extends GeoPosition_Builder {}
}