package fi.fmi.avi.data.taf;

import java.util.List;

import fi.fmi.avi.data.TimeReferenceAmendable;

/**
 * Created by rinne on 30/01/15.
 */
public interface TAFBaseForecast extends TAFForecast, TimeReferenceAmendable {

    List<TAFAirTemperatureForecast> getTemperatures();

    void setTemperatures(List<TAFAirTemperatureForecast> temperatures);


}
