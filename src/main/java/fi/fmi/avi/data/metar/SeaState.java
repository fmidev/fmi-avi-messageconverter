package fi.fmi.avi.data.metar;

import fi.fmi.avi.data.AviationCodeListUser;
import fi.fmi.avi.data.NumericMeasure;

public interface SeaState extends AviationCodeListUser {

    NumericMeasure getSeaSurfaceTemperature();

    NumericMeasure getSignificantWaveHeight();

    SeaSurfaceState getSeaSurfaceState();


    void setSeaSurfaceTemperature(NumericMeasure seaSurfaceTemperature);

    void setSignificantWaveHeight(NumericMeasure significantWaveHeight);

    void setSeaSurfaceState(SeaSurfaceState seaSurfaceState);

}
