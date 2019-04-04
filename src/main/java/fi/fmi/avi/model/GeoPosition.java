package fi.fmi.avi.model;

import java.util.List;
import java.util.Optional;


public interface GeoPosition {

    String getCoordinateReferenceSystemId();

    List<Double> getCoordinates();

    Optional<Double> getElevationValue();

    Optional<String> getElevationUom();

}
