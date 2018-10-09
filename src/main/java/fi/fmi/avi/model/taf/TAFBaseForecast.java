package fi.fmi.avi.model.taf;

import java.util.List;
import java.util.Optional;

/**
 * Created by rinne on 30/01/15.
 */

public interface TAFBaseForecast extends TAFForecast {

    Optional<List<TAFAirTemperatureForecast>> getTemperatures();

}
