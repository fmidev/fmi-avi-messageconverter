package fi.fmi.avi.model.taf;

import java.util.Optional;

import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;

/**
 * Created by rinne on 22/11/17.
 */

public interface TAFReference {

    Aerodrome getAerodrome();

    // Should not be used: cannot be parsed from TAC and information does not exist in IWXXM 2.1 or 3.0, not used in code either.
    @Deprecated
    Optional<PartialOrCompleteTimeInstant> getIssueTime();

    Optional<PartialOrCompleteTimePeriod> getValidityTime();

    // Should not be used: cannot be parsed from TAC and information does not exist in IWXXM 2.1 or 3.0, not used in code either.
    @Deprecated
    Optional<TAF.TAFStatus> getStatus();

    /**
     * Returns true if issue time and valid time references contained in this message are full ZonedDateTime instances.
     *
     * @return true if all time references are complete, false otherwise
     */
    boolean areAllTimeReferencesComplete();
}
