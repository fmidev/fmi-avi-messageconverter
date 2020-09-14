package fi.fmi.avi.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeName;

import fi.fmi.avi.model.immutable.ElevatedPointImpl;

@JsonTypeName("Point")
@JsonSubTypes({ @JsonSubTypes.Type(value = ElevatedPointImpl.class, name = "ElevatedPoint") })
public interface PointGeometry extends Geometry {

    List<Double> getCoordinates();
}
