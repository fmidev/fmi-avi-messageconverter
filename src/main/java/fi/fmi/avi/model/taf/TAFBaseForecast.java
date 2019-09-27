package fi.fmi.avi.model.taf;

import java.util.List;
import java.util.Optional;

/**
 * Created by rinne on 30/01/15.
 */

public interface TAFBaseForecast extends TAFForecast {

    Optional<List<TAFAirTemperatureForecast>> getTemperatures();

    /**
     * Returns true if min and max temperature time references contained in all of air temperature forecasts are full ZonedDateTime instances.
     *
     * @return true if all time references are complete, false otherwise
     */
    boolean areAllTimeReferencesComplete();

}
