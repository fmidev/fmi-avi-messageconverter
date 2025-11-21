package fi.fmi.avi.model.swx.amd82;

import fi.fmi.avi.model.Geometry;
import fi.fmi.avi.model.NumericMeasure;

import java.util.Optional;

public interface AirspaceVolume {

    Optional<Geometry> getHorizontalProjection();

    Optional<NumericMeasure> getUpperLimit();

    Optional<String> getUpperLimitReference();

    Optional<NumericMeasure> getLowerLimit();

    Optional<String> getLowerLimitReference();

    Optional<NumericMeasure> getMaximumLimit();

    Optional<String> getMaximumLimitReference();

    Optional<NumericMeasure> getMinimumLimit();

    Optional<String> getMinimumLimitReference();

    Optional<NumericMeasure> getWidth();

    //The AIXM AirspaceVolume also has "centreline" property (curve), however, we don't yet have a curve Geometry
    //Optional<Curve> getCentreline();

}
