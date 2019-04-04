package fi.fmi.avi.model;

import java.util.Optional;

public interface SurfaceWind extends AviationCodeListUser {

    boolean isVariableDirection();

    Optional<NumericMeasure> getMeanWindDirection();

    NumericMeasure getMeanWindSpeed();

    Optional<RelationalOperator> getMeanWindSpeedOperator();

    Optional<NumericMeasure> getWindGust();

    Optional<RelationalOperator> getWindGustOperator();

}
