package fi.fmi.avi.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("CircleByCenterPoint")
public interface CircleByCenterPoint extends Geometry {
    List<Double> getCoordinates();

    NumericMeasure getRadius();
}
