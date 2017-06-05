package fi.fmi.avi.data;

import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * A generic interface for all aviation related weather reports and forecasts.
 *
 * Note that as TAC encoding does not contain the month and year
 * data, a fully resolved issue time can only be constructed by
 * providing this info externally using {@link #getIssueTimeIn(int, int)}.
 */
public interface AviationWeatherMessage {

    /**
     * Returns the day-of-month of the message issue time.
     *
     * @return the day-of-month for the issue time
     */
    int getIssueDayOfMonth();

    /**
     * Returns the hour-of-day of the message issue time.
     *
     * @return hour-of-day of the message issue time.
     *
     * @see #getIssueTimeIn(int, int)
     */
    int getIssueHour();

    /**
     * Returns the minute-of-hour of the message issue time.
     *
     * @see #getIssueTimeIn(int, int)
     * @return minute-of-hour of the message issue time.
     */
    int getIssueMinute();

    /**
     * Returns the time zone ID of the message issue time, 'Z' must be used by default.
     *
     * @see #getIssueTimeIn(int, int)
     * @return time zone ID
     */
    String getIssueTimeZone();

    /**
     * Returns a fully resolved {@link ZonedDateTime} using the given <code>month</code>
     * and <code>year</code> together with the issue time day-of-month, hour-of-day,
     * minute-of-hour and timezone.
     *
     * @param month
     *         month-of-year, 1-12
     * @param year
     *         the year
     *
     * @return the resolved date-time
     *
     * @throws DateTimeException
     *         if the date-time cannot be created using the given values
     */
    ZonedDateTime getIssueTimeIn(int month, int year) throws DateTimeException;

    /**
     * Returns the remarks, if included in the message.
     * in TAC remarks are provided at the end of the message
     * after the 'RMK' token.
     *
     * @return the remark tokens as-is
     */
    List<String> getRemarks();

    /**
     * Sets the issue time day-of-month.
     *
     * @param day issue time day-of-month
     */
    void setIssueDayOfMonth(final int day);

    /**
     *
     * Sets the issue time hour-of-day.
     *
     * @param hour issue time hour-of-day
     */
    void setIssueHour(final int hour);

    /**
     * Sets the issue time minute-of-hour.
     *
     * @param minute issue time minute-of-hour
     */
    void setIssueMinute(final int minute);

    /**
     * Sets the issue time time zone ID.
     * If not provided assumed to be 'Z'.
     *
     * @param timeZone the time zone ID
     */
    void setIssueTimeZone(String timeZone);

    /**
     * Sets the remarks as a List.
     *
     * @param remarks to set
     */
    void setRemarks(List<String> remarks);

}
