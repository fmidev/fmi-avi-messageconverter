package fi.fmi.avi.data.taf.impl;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.data.taf.TAFAirTemperatureForecast;
import fi.fmi.avi.data.taf.TAFBaseForecast;

/**
 * Created by rinne on 30/01/15.
 */
public class TAFBaseForecastImpl extends TAFForecastImpl implements TAFBaseForecast {

    private List<TAFAirTemperatureForecast> temperatures;

    public TAFBaseForecastImpl(){
    }

    public TAFBaseForecastImpl(final TAFBaseForecast input) {
        super(input);
        this.setTemperatures(new ArrayList<TAFAirTemperatureForecast>());
        for (TAFAirTemperatureForecast airTemp:input.getTemperatures()) {
            this.getTemperatures().add(new TAFAirTemperatureForecastImpl(airTemp));
        }
    }

    @Override
    public List<TAFAirTemperatureForecast> getTemperatures() {
        return temperatures;
    }

    @Override
    @JsonDeserialize(contentAs = TAFAirTemperatureForecastImpl.class)
    public void setTemperatures(final List<TAFAirTemperatureForecast> temperatures) {
        this.temperatures = temperatures;
    }
}
