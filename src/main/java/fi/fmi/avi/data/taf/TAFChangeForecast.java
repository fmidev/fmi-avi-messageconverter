package fi.fmi.avi.data.taf;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import fi.fmi.avi.data.TimeReferenceAmendable;

/**
 * Created by rinne on 30/01/15.
 */
public interface TAFChangeForecast extends TAFForecast, TimeReferenceAmendable {

    TAFChangeIndicator getChangeIndicator();

    int getValidityStartDayOfMonth();

    int getValidityStartHour();

    int getValidityStartMinute();

    String getPartialValidityTimePeriod();
    
    String getPartialValidityStartTime();
    
    ZonedDateTime getValidityStartTime();
    
    int getValidityEndDayOfMonth();

    int getValidityEndHour();
    
    ZonedDateTime getValidityEndTime();

    void setChangeIndicator(TAFChangeIndicator changeIndicator);

    void setPartialValidityTimePeriod(final String time);
    
    void setPartialValidityTimePeriod(int startHour, int endHour);
    
    void setPartialValidityTimePeriod(int startDay, int endDay, int startHour, int endHour);
    
    void setPartialValidityStartTime(final String time);
    
    void setPartialValidityStartTime(final int hour, final int minute);
    
    void setPartialValidityStartTime(final int day, final int hour, final int minute);
    
    void setValidityStartTime(final int year, final int monthOfYear, final int dayOfMonth, final int hour, final int minute, final ZoneId timeZone);

    void setValidityStartTime(final ZonedDateTime time);
    
    void setPartialValidityEndTime(final int day, final int hour);
    
    void setValidityEndTime(final int year, final int monthOfYear, final int dayOfMonth, final int hour, final int minute, final ZoneId timeZone);

    void setValidityEndTime(final ZonedDateTime time);
    

}
