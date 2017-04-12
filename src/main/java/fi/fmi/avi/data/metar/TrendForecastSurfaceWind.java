package fi.fmi.avi.data.metar;

import fi.fmi.avi.data.AviationCodeListUser;
import fi.fmi.avi.data.NumericMeasure;

public interface TrendForecastSurfaceWind extends AviationCodeListUser {

    NumericMeasure getMeanWindDirection();

    NumericMeasure getMeanWindSpeed();

    NumericMeasure getWindGust();


    void setMeanWindDirection(NumericMeasure meanWindDirection);

    void setMeanWindSpeed(NumericMeasure meanWindSpeed);

    void setWindGust(NumericMeasure windGust);

}
