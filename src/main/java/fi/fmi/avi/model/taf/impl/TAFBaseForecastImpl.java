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
			this.setTemperatures(new ArrayList<TAFAirTemperatureForecast>());
			for (TAFAirTemperatureForecast airTemp : input.getTemperatures()) {
				this.getTemperatures().add(new TAFAirTemperatureForecastImpl(airTemp));
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

	@Override
	public void amendTimeReferences(ZonedDateTime referenceTime) {
		if (this.temperatures != null) {
			for (TAFAirTemperatureForecast fct:this.temperatures) {
				if (!fct.areTimeReferencesResolved()) {
					fct.amendTimeReferences(referenceTime);
				}
			}
		}
	}

	@Override
	public boolean areTimeReferencesResolved() {
		if (this.temperatures != null) {
			for (TAFAirTemperatureForecast fct:this.temperatures) {
				if (!fct.areTimeReferencesResolved()) {
					return false;
				}
			}
		}
		return true;
	}

}
