package fi.fmi.avi.model;

import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = Aerodrome.Builder.class)
public interface Aerodrome {

    String getDesignator();

    Optional<String> getName();

    Optional<String> getLocationIndicatorICAO();

    Optional<String> getDesignatorIATA();

    Optional<Double> getFieldElevationValue();

    Optional<GeoPosition> getReferencePoint();


    Builder toBuilder();
    
    class Builder extends Aerodrome_Builder {}
}