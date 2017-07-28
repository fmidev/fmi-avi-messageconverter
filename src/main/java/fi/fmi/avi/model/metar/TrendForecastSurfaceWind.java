package fi.fmi.avi.model.metar;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;

public interface TrendForecastSurfaceWind extends AviationCodeListUser {

    NumericMeasure getMeanWindDirection();

    NumericMeasure getMeanWindSpeed();

    NumericMeasure getWindGust();


    void setMeanWindDirection(NumericMeasure meanWindDirection);

    void setMeanWindSpeed(NumericMeasure meanWindSpeed);

    void setWindGust(NumericMeasure windGust);

}
