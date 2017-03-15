package fi.fmi.avi.data.metar;

import fi.fmi.avi.data.AviationCodeListUser;
import fi.fmi.avi.data.NumericMeasure;
import fi.fmi.avi.data.AviationCodeListUser.MissingReason;
import fi.fmi.avi.data.PossiblyMissingContent;

public interface TrendForecastSurfaceWind extends AviationCodeListUser, PossiblyMissingContent {

    NumericMeasure getMeanWindDirection();

    NumericMeasure getMeanWindSpeed();

    NumericMeasure getWindGust();


    void setMeanWindDirection(NumericMeasure meanWindDirection);

    void setMeanWindSpeed(NumericMeasure meanWindSpeed);

    void setWindGust(NumericMeasure windGust);

}
