package fi.fmi.avi.data.metar;

import fi.fmi.avi.data.AviationCodeListUser;
import fi.fmi.avi.data.NumericMeasure;
import fi.fmi.avi.data.AviationCodeListUser.MissingReason;
import fi.fmi.avi.data.PossiblyMissingContent;

public interface ObservedSurfaceWind extends AviationCodeListUser, PossiblyMissingContent {

    boolean isVariableDirection();

    NumericMeasure getMeanWindDirection();

    NumericMeasure getMeanWindSpeed();

    NumericMeasure getWindGust();

    NumericMeasure getExtremeClockwiseWindDirection();

    NumericMeasure getExtremeCounterClockwiseWindDirection();


    void setVariableDirection(boolean variableDirection);

    void setMeanWindDirection(NumericMeasure meanWindDirection);

    void setMeanWindSpeed(NumericMeasure meanWindSpeed);

    void setWindGust(NumericMeasure windGust);

    void setExtremeClockwiseWindDirection(NumericMeasure extremeClockwiseWindDirection);

    void setExtremeCounterClockwiseWindDirection(NumericMeasure extremeCounterClockwiseWindDirection);

}
