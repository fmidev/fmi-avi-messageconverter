package fi.fmi.avi.model.taf;

import java.util.List;
import java.util.Optional;

import fi.fmi.avi.model.AerodromeWeatherMessage;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;

/**
 * Created by rinne on 30/01/15.
 */

public interface TAF extends AerodromeWeatherMessage, AviationCodeListUser {

    TAFStatus getStatus();

    Optional<PartialOrCompleteTimePeriod> getValidityTime();

    Optional<TAFBaseForecast> getBaseForecast();

    Optional<List<TAFChangeForecast>> getChangeForecasts();

    Optional<TAFReference> getReferredReport();

}
