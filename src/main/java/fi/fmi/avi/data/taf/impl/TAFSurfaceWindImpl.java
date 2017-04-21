package fi.fmi.avi.data.taf.impl;

import com.fasterxml.jackson.annotation.JsonInclude;

import fi.fmi.avi.data.metar.impl.TrendForecastSurfaceWindImpl;
import fi.fmi.avi.data.taf.TAFSurfaceWind;

/**
 * Created by rinne on 30/01/15.
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class TAFSurfaceWindImpl extends TrendForecastSurfaceWindImpl implements TAFSurfaceWind {

    private boolean variableDirection;

    public TAFSurfaceWindImpl(){
    }

    public TAFSurfaceWindImpl(final TAFSurfaceWind input) {
        super(input);
        this.setVariableDirection(input.isVariableDirection());
    }

    @Override
    public boolean isVariableDirection() {
        return variableDirection;
    }

    @Override
    public void setVariableDirection(final boolean variableDirection) {
        this.variableDirection = variableDirection;
    }
}
