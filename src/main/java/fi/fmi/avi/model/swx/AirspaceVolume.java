package fi.fmi.avi.model.swx;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import fi.fmi.avi.model.Geometry;
import fi.fmi.avi.model.NumericMeasure;

public interface AirspaceVolume {
    Optional<BigInteger> getSrsDimension();

    Optional<String> getSrsName();

    Optional<List<String>> getAxisLabels();

    Optional<Geometry> getGeometry();

    Optional<NumericMeasure> getUpperLimit();

    Optional<String> getUpperLimitReference();
}
