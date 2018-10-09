package fi.fmi.avi.model.taf;

import java.util.Optional;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;

/**
 * Created by rinne on 30/01/15.
 */

public interface TAFSurfaceWind {

    Optional<NumericMeasure> getMeanWindDirection();

    NumericMeasure getMeanWindSpeed();

    Optional<AviationCodeListUser.RelationalOperator> getMeanWindSpeedOperator();

    Optional<NumericMeasure> getWindGust();

    Optional<AviationCodeListUser.RelationalOperator> getWindGustOperator();

    boolean isVariableDirection();


}
