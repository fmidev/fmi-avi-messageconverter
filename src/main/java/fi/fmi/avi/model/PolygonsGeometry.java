package fi.fmi.avi.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("Polygon")
public interface PolygonsGeometry extends Geometry {
    public List<List<Double>> getPolygons();
}
