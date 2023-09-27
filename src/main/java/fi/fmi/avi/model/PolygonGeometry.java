package fi.fmi.avi.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("Polygon")
public interface PolygonGeometry extends Geometry {
    List<Double> getExteriorRingPositions();
    List<Double> getExteriorRingPositions(Winding winding);
    @JsonIgnore
    Winding getExteriorRingWinding();
}
