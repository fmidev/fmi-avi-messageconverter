package fi.fmi.avi.model.taf.impl;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.taf.TAFAirTemperatureForecast;
import fi.fmi.avi.model.taf.TAFBaseForecast;

/**
 * Created by rinne on 30/01/15.
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class TAFBaseForecastImpl extends TAFForecastImpl implements TAFBaseForecast {

    private List<TAFAirTemperatureForecast> temperatures;

    public TAFBaseForecastImpl(){
    }

    public TAFBaseForecastImpl(final TAFBaseForecast input) {
        super(input);
        if (input != null) {
        	if (input.getTemperatures() != null) {
				this.temperatures = new ArrayList<>();
				for (TAFAirTemperatureForecast temp: input.getTemperatures()) {
					this.temperatures.add(new TAFAirTemperatureForecastImpl(temp));
				}
			}
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
