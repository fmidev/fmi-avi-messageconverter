package fi.fmi.avi.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("MultiPolygon")
public interface MultiPolygonGeometry extends Geometry {
    public List<List<Double>> getExteriorRingPositions();
}
