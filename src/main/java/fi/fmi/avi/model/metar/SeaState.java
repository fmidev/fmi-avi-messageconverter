package fi.fmi.avi.model.metar;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;

public interface SeaState extends AviationCodeListUser {

    NumericMeasure getSeaSurfaceTemperature();

    NumericMeasure getSignificantWaveHeight();

    SeaSurfaceState getSeaSurfaceState();


    void setSeaSurfaceTemperature(NumericMeasure seaSurfaceTemperature);

    void setSignificantWaveHeight(NumericMeasure significantWaveHeight);

    void setSeaSurfaceState(SeaSurfaceState seaSurfaceState);

}
