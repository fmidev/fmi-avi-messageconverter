package fi.fmi.avi.model.metar;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import fi.fmi.avi.model.TimeReferenceAmendable;

/**
 * Created by rinne on 20/04/17.
 */
public interface TrendTimeGroups extends TimeReferenceAmendable {
    
	String getPartialStartTime();

    ZonedDateTime getStartTime();
    
    String getPartialEndTime();
    
    ZonedDateTime getEndTime();
    
    boolean isSingleInstance();


    void setPartialStartTime(final String time);
    
    void setPartialStartTime(final int hour, final int minute);
    
    void setStartTime(final int year, final int monthOfYear, final int dayOfMonth, final int hour, final int minute, final ZoneId timeZone);

    void setStartTime(final ZonedDateTime time);
    
    void setPartialEndTime(final String time);

    void setPartialEndTime(final int hour, final int minute);

    void setEndTime(final int year, final int monthOfYear, final int dayOfMonth, final int hour, final int minute, final ZoneId timeZone);
    
    void setEndTime(final ZonedDateTime time);
    
    void setSingleInstance(final boolean isInstance);

    public boolean hasStartTime();
	
    public boolean hasEndTime();
}
