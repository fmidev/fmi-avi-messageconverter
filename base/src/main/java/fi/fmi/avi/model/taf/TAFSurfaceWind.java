package fi.fmi.avi.model.taf;

import fi.fmi.avi.model.metar.TrendForecastSurfaceWind;

/**
 * Created by rinne on 30/01/15.
 */
public interface TAFSurfaceWind extends TrendForecastSurfaceWind {

    boolean isVariableDirection();

    void setVariableDirection(boolean variableDirection);

}
