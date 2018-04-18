package fi.fmi.avi.model.metar;

import java.util.Optional;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;

public interface ObservedSurfaceWind extends AviationCodeListUser {

    boolean isVariableDirection();

    NumericMeasure getMeanWindDirection();

    NumericMeasure getMeanWindSpeed();

    Optional<NumericMeasure> getWindGust();

    Optional<NumericMeasure> getExtremeClockwiseWindDirection();

    Optional<NumericMeasure> getExtremeCounterClockwiseWindDirection();

}
