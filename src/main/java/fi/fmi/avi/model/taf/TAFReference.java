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

    Optional<PartialOrCompleteTimeInstant> getIssueTime();

    Optional<PartialOrCompleteTimePeriod> getValidityTime();

    Optional<TAF.TAFStatus> getStatus();

}
