package fi.fmi.avi.model.metar;

import fi.fmi.avi.model.PartialOrCompleteTimePeriod;

/**
 * Created by rinne on 20/04/17.
 */
public interface TrendTimeGroups extends PartialOrCompleteTimePeriod {

    boolean isSingleInstance();
    
    void setSingleInstance(final boolean isInstance);

}
