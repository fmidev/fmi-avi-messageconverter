package fi.fmi.avi.model;

import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = Aerodrome.Builder.class)
public interface Aerodrome {

    String designator();

    Optional<String> name();

    Optional<String> locationIndicatorICAO();

    Optional<String> designatorIATA();

    Optional<Double> fieldElevationValue();

    Optional<GeoPosition> referencePoint();


    Builder toBuilder();
    
    class Builder extends Aerodrome_Builder {}
}