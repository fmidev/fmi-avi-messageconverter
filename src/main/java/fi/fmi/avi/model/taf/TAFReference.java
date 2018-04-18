package fi.fmi.avi.model.taf;

import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;

/**
 * Created by rinne on 22/11/17.
 */

public interface TAFReference {

    Aerodrome getAerodrome();

    PartialOrCompleteTimeInstant getIssueTime();

    PartialOrCompleteTimePeriod getValidityTime();

    TAF.TAFStatus getStatus();

}
