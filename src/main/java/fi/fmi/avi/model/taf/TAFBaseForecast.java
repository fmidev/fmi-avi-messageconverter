package fi.fmi.avi.model.taf;

import java.util.List;

/**
 * Created by rinne on 30/01/15.
 */
public interface TAFBaseForecast extends TAFForecast {

    List<TAFAirTemperatureForecast> getTemperatures();

    void setTemperatures(List<TAFAirTemperatureForecast> temperatures);


}
