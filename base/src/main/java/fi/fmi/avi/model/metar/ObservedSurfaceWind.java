package fi.fmi.avi.model.metar;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;

public interface ObservedSurfaceWind extends AviationCodeListUser {

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
