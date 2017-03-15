package fi.fmi.avi.data.taf;

import fi.fmi.avi.data.metar.TrendForecastSurfaceWind;

/**
 * Created by rinne on 30/01/15.
 */
public interface TAFSurfaceWind extends TrendForecastSurfaceWind {

    boolean isVariableDirection();

    void setVariableDirection(boolean variableDirection);

}
