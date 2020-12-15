package fi.fmi.avi.model.taf;

import java.util.List;
import java.util.Optional;

import fi.fmi.avi.model.AerodromeWeatherMessage;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;

/**
 * Created by rinne on 30/01/15.
 */

public interface TAF extends AerodromeWeatherMessage, AviationCodeListUser {

    /**
     * Returns the TAF message status.
     *
     * @return the status of the TAF message
     * @deprecated migrate to using a combination of {@link AviationWeatherMessage#getReportStatus()}, {@link #isCancelMessage()} and
     * {@link #isMissingMessage()} instead
     */
    @Deprecated
    TAFStatus getStatus();

    Optional<PartialOrCompleteTimePeriod> getValidityTime();

    Optional<TAFBaseForecast> getBaseForecast();

    Optional<List<TAFChangeForecast>> getChangeForecasts();

    /**
     * Deprecated in favour of using the {@link #getReferredReportValidPeriod()} and {@link #getAerodrome()}. Links with other than cancellation amended
     * messages should be handled in the application code if necessary.
     *
     * @return the reference to the previously issued, amended message
     */
    @Deprecated
    Optional<TAFReference> getReferredReport();

    boolean isCancelMessage();

    default boolean isMissingMessage() {
        return !getBaseForecast().isPresent();
    }

    Optional<PartialOrCompleteTimePeriod> getReferredReportValidPeriod();

}
