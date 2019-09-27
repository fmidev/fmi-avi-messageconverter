package fi.fmi.avi.model;

import java.util.Optional;


public interface Aerodrome {

    String getDesignator();

    Optional<String> getName();

    Optional<String> getLocationIndicatorICAO();

    Optional<String> getDesignatorIATA();

    Optional<Double> getFieldElevationValue();

    Optional<String> getFieldElevationUom();

    Optional<GeoPosition> getReferencePoint();


}