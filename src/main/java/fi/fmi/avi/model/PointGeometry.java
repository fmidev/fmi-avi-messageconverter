package fi.fmi.avi.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("Point")
public interface PointGeometry extends Geometry {
    List<Double> getPoint();
}
