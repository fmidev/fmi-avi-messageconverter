package fi.fmi.avi.model;

import java.time.ZoneId;
import java.time.ZonedDateTime;


/**
 * Created by rinne on 27/10/17.
 */
public interface PartialOrCompleteTimePeriod {

    String getPartialStartTime();

    int getStartTimeDay();

    int getStartTimeHour();

    int getStartTimeMinute();

    ZonedDateTime getCompleteStartTime();

    String getCompleteStartTimeAsISOString();


    String getPartialEndTime();

    int getEndTimeDay();

    int getEndTimeHour();

    int getEndTimeMinute();

    ZonedDateTime getCompleteEndTime();

    String getCompleteEndTimeAsISOString();

    /**
     * Sets the partial start time using the implementation specific pattern.
     * Sets he complete start time to null.
     *
     * @param time the time as String
     */
    void setPartialStartTime(String time);

    /**
     * Sets the partial start time using day, hour and minute.
     *
     * Sets the complete start time to null.
     *
     * @param day the start day-of-month
     * @param hour the start hour-of-day
     * @param minute the start minute-of-hour
     */
    void setPartialStartTime(int day, int hour, int minute);


    void setCompleteStartTime(int year, int monthOfYear, int dayOfMonth, int hour, int minute, ZoneId timeZone);

    void setCompleteStartTime(ZonedDateTime time);

    void setCompleteStartTimeAsISOString(String isoDateTime);

    /**
     * Sets the partial start time using the implementation specific pattern.
     *
     * Sets the complete end time to null.
     *
     * @param time the time as String
     */
    void setPartialEndTime(String time);

    /**
     * Sets the complete end time to null.
     *
     * @param day the end day-of-month
     * @param hour the end hour-of-day
     * @param minute the end minute-of-hour
     */
    void setPartialEndTime(int day, int hour, int minute);

    void setCompleteEndTime(int year, int monthOfYear, int dayOfMonth, int hour, int minute, ZoneId timeZone);

    void setCompleteEndTime(ZonedDateTime time);

    void setCompleteEndTimeAsISOString(String isoDateTime);


    boolean hasStartTime();

    boolean hasEndTime();

    boolean isStartTimeComplete();

    boolean isEndTimeComplete();

    boolean endsAtMidnight();
}
