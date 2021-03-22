package fi.fmi.avi.model.taf;

import java.util.List;
import java.util.Optional;

import fi.fmi.avi.model.AerodromeWeatherMessage;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.immutable.AerodromeImpl;
import fi.fmi.avi.model.taf.immutable.TAFReferenceImpl;

/**
 * Created by rinne on 30/01/15.
 */

public interface TAF extends AerodromeWeatherMessage, AviationCodeListUser {

    /**
     * Returns the TAF message status.
     *
     * @return the status of the TAF message
     *
     * @deprecated migrate to using a combination of {@link AviationWeatherMessage#getReportStatus()}, {@link #isCancelMessage()} and
     * {@link #isMissingMessage()} instead
     */
    @Deprecated
    default TAFStatus getStatus() {
        return TAFStatus.fromReportStatus(getReportStatus(), isCancelMessage(), isMissingMessage());
    }

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
    default Optional<TAFReference> getReferredReport() {
        return getReferredReportValidPeriod().map(referredReportValidPeriod -> TAFReferenceImpl.builder()
                .setAerodrome(AerodromeImpl.immutableCopyOf(this.getAerodrome()))
                .setValidityTime(referredReportValidPeriod)
                .build());
    }

    boolean isCancelMessage();

    default boolean isMissingMessage() {
        return !isCancelMessage() && !getBaseForecast().isPresent();
    }

    Optional<PartialOrCompleteTimePeriod> getReferredReportValidPeriod();

}
