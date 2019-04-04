package fi.fmi.avi.model.metar;

import java.util.Optional;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.SurfaceWind;

public interface ObservedSurfaceWind extends SurfaceWind {

    Optional<NumericMeasure> getExtremeClockwiseWindDirection();

    Optional<NumericMeasure> getExtremeCounterClockwiseWindDirection();

}
