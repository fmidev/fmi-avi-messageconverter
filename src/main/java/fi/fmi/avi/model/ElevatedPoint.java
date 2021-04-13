package fi.fmi.avi.model;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("ElevatedPoint")
public interface ElevatedPoint extends PointGeometry {

    Optional<Double> getElevationValue();

    Optional<String> getElevationUom();

    Optional<String> getVerticalDatum();
}
