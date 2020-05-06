package fi.fmi.avi.model;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("CircleByCenterPoint")
public interface CircleByCenterPoint extends Geometry {
    List<Double> getCoordinates();

    NumericMeasure getRadius();

    Optional<BigInteger> getNumarc();
}
