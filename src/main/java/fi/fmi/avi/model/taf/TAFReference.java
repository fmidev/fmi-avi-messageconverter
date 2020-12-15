package fi.fmi.avi.model.taf;

import java.util.Optional;

import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;

/**
 * Created by rinne on 22/11/17.
 * <p>
 * Note: The entire interface is now deprecated, as the possible references to previous amended and/or cancelled aviation messages should
 * probably be handled in the application outside the avi-messageconverter library.
 */
@Deprecated
public interface TAFReference {

    @Deprecated
    Aerodrome getAerodrome();

    // Should not be used: cannot be parsed from TAC and information does not exist in IWXXM 2.1 or 3.0, not used in code either.
    @Deprecated
    Optional<PartialOrCompleteTimeInstant> getIssueTime();

    @Deprecated
    Optional<PartialOrCompleteTimePeriod> getValidityTime();

    // Should not be used: cannot be parsed from TAC and information does not exist in IWXXM 2.1 or 3.0, not used in code either.
    @Deprecated
    Optional<TAF.TAFStatus> getStatus();

    /**
     * Returns true if issue time and valid time references contained in this message are full ZonedDateTime instances.
     *
     * @return true if all time references are complete, false otherwise
     */
    @Deprecated
    boolean areAllTimeReferencesComplete();
}
