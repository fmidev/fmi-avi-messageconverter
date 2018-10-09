package fi.fmi.avi.model;

import java.util.Optional;


public interface GeoPosition {

    String getCoordinateReferenceSystemId();

    Double[] getCoordinates();

    Optional<Double> getElevationValue();

    Optional<String> getElevationUom();

}
