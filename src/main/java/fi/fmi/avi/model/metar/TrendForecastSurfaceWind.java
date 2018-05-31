package fi.fmi.avi.model.metar;

import java.util.Optional;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;

public interface TrendForecastSurfaceWind extends AviationCodeListUser {

    NumericMeasure getMeanWindDirection();

    NumericMeasure getMeanWindSpeed();

    Optional<RelationalOperator> getMeanWindSpeedOperator();

    Optional<NumericMeasure> getWindGust();

    Optional<RelationalOperator> getWindGustOperator();
}
