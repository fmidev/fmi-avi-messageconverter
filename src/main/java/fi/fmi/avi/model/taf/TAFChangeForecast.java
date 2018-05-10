package fi.fmi.avi.model.taf;

import fi.fmi.avi.model.PartialOrCompleteTimePeriod;

/**
 * Created by rinne on 30/01/15.
 */

public interface TAFChangeForecast extends TAFForecast {

    TAFChangeIndicator getChangeIndicator();

    PartialOrCompleteTimePeriod getPeriodOfChange();

}
