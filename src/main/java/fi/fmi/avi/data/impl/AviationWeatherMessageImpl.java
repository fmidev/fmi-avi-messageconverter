package fi.fmi.avi.data.impl;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import fi.fmi.avi.data.AviationWeatherMessage;
import fi.fmi.avi.data.Weather;

/**
 * Created by rinne on 05/06/17.
 */
public abstract class AviationWeatherMessageImpl implements AviationWeatherMessage {

    protected static List<String> getAsWeatherCodes(final List<Weather> weatherList) {
        return getAsWeatherCodes(weatherList, null);
    }

    protected static List<String> getAsWeatherCodes(final List<Weather> weatherList, final String prefix) {
        List<String> retval = null;
        if (weatherList != null) {
            retval = new ArrayList<>(weatherList.size());
            for (final Weather w : weatherList) {
                if (prefix != null) {
                    retval.add(prefix + w.getCode());
                } else {
                    retval.add(w.getCode());
                }
            }
        }
        return retval;
    }

    private int issueDayOfMonth = -1;
    private int issueHour = -1;
    private int issueMinute = -1;
    private String timeZone;
    private List<String> remarks;

    public AviationWeatherMessageImpl() {
    }

    public AviationWeatherMessageImpl(AviationWeatherMessage input) {
        this.issueDayOfMonth = input.getIssueDayOfMonth();
        this.issueHour = input.getIssueHour();
        this.issueMinute = input.getIssueMinute();
        this.timeZone = input.getIssueTimeZone();
        this.remarks = input.getRemarks();
    }

    /* (non-Javadoc)
    * @see fi.fmi.avi.data.Metar#getIssueDayOfMonth()
    */
    @Override
    public int getIssueDayOfMonth() {
        return issueDayOfMonth;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#setIssueDayOfMonth(int)
     */
    @Override
    public void setIssueDayOfMonth(final int dayOfMonth) {
        this.issueDayOfMonth = dayOfMonth;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#getIssueHour()
     */
    @Override
    public int getIssueHour() {
        return issueHour;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#setIssueHour(int)
     */
    @Override
    public void setIssueHour(final int hour) {
        this.issueHour = hour;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#getIssueMinute()
     */
    @Override
    public int getIssueMinute() {
        return issueMinute;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#setIssueMinute(int)
     */
    @Override
    public void setIssueMinute(final int minute) {
        this.issueMinute = minute;
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#getTimeZone()
     */
    @Override
    public String getIssueTimeZone() {
        return timeZone;
    }

    @Override
    public ZonedDateTime getIssueTimeIn(final int month, final int year) throws DateTimeException {
        return ZonedDateTime.of(LocalDateTime.of(year, month, this.issueDayOfMonth, this.issueHour, this.issueMinute), ZoneId.of(this.timeZone));
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.Metar#setTimeZone(java.lang.String)
     */
    @Override
    public void setIssueTimeZone(final String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public List<String> getRemarks() {
        return this.remarks;
    }

    @Override
    public void setRemarks(final List<String> remarks) {
        this.remarks = remarks;
    }

}
