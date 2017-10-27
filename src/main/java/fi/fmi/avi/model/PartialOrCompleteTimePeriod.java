package fi.fmi.avi.model;

import java.time.ZoneId;
import java.time.ZonedDateTime;


/**
 * Created by rinne on 27/10/17.
 */
public interface PartialOrCompleteTimePeriod {

    String getPartialStartTime();

    int getPartialStartTimeDay();

    int getPartialStartTimeHour();

    int getPartialStartTimeMinute();

    ZonedDateTime getCompleteStartTime();

    String getCompleteStartTimeAsISOString();


    String getPartialEndTime();

    int getPartialEndTimeDay();

    int getPartialEndTimeHour();

    int getPartialEndTimeMinute();

    ZonedDateTime getCompleteEndTime();

    String getCompleteEndTimeAsISOString();

    /**
     * Sets the partial start time using the implementation specific pattern.
     * Sets he complete start time to null.
     *
     * @param time
     */
    void setPartialStartTime(String time);

    /**
     * Sets the complete start time to null.
     *
     * @param day
     * @param hour
     * @param minute
     */
    void setPartialStartTime(int day, int hour, int minute);


    void setCompleteStartTime(int year, int monthOfYear, int dayOfMonth, int hour, int minute, ZoneId timeZone);

    void setCompleteStartTime(ZonedDateTime time);

    void setCompleteStartTimeAsISOString(String isoDateTime);

    /**
     * Sets the complete end time to null.
     *
     * @param time
     */
    void setPartialEndTime(String time);

    /**
     * Sets the complete end time to null.
     *
     * @param day
     * @param hour
     * @param minute
     */
    void setPartialEndTime(int day, int hour, int minute);

    void setCompleteEndTime(int year, int monthOfYear, int dayOfMonth, int hour, int minute, ZoneId timeZone);

    void setCompleteEndTime(ZonedDateTime time);

    void setCompleteEndTimeAsISOString(String isoDateTime);


    boolean hasStartTime();

    boolean hasEndTime();

    boolean endsAtMidnight();
}
