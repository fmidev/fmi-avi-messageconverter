package fi.fmi.avi.model;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import fi.fmi.avi.model.AviationCodeListUser.PermissibleUsage;
import fi.fmi.avi.model.AviationCodeListUser.PermissibleUsageReason;

/**
 * A generic interface for all aviation related weather reports and forecasts.
 *
 * Note that as TAC encoding does not contain the month and year
 * data, a fully resolved issue time can only be constructed by
 * providing this info externally using {@link #amendTimeReferences(ZonedDateTime)}.
 */
public interface AviationWeatherMessage extends TimeReferenceAmendable {

    /**
     * Returns the partial issue time (day of month, hour & minute) 
     * of the message issue time in format ddHHmmz.
     * This should always be available regardless of the message 
     * source.
     *
     * @return the partial the issue time
     */
	String getPartialIssueTime();

    /**
     * Returns the fully-resolved issue time of the message.
     * This is only available if the complete issue time data
     * has been provided (not all formats contain 
     * day of month and year data).
     * 
     * @return the fully resolved issue time, or null if not available
     * 
     * @see #areTimeReferencesResolved()
     * @see #amendTimeReferences(ZonedDateTime)
     */
    ZonedDateTime getIssueTime();

    /**
     * Returns the remarks, if included in the message.
     * in TAC remarks are provided at the end of the message
     * after the 'RMK' token.
     *
     * @return the remark tokens as-is
     */
    List<String> getRemarks();

    /**
     * 
     * @return
     */
    PermissibleUsage getPermissibleUsage();
    
    /**
     * 
     * @return
     */
    PermissibleUsageReason getPermissibleUsageReason();
    
    /**
     * 
     * @return
     */
    String getPermissibleUsageSupplementary();
    
    /**
     * 
     * @return
     */
    boolean isTranslated();
    
    /**
     * 
     * @return
     */
    String getTranslatedBulletinID();

    /**
     * 
     * @return
     */
    ZonedDateTime getTranslatedBulletinReceptionTime();
    
    /**
     * 
     * @return
     */
    String getTranslationCentreDesignator();
    
    /**
     * 
     * @return
     */
    String getTranslationCentreName();
    
    /**
     * 
     * @return
     */
    ZonedDateTime getTranslationTime();
    
    /**
     * 
     * @return
     */
    String getTranslatedTAC();
    
    /**
     * Sets the partial issue time as a formatted String. 
     * To get a fully resolved issue time, 
     * the missing month of year and year data needs to be 
     * provided using {@link #amendTimeReferences}. 
     * 
     * @param time formatted as ddHHmmz (201004Z)
     * 
     * @see #getIssueTime()
     * @see #amendTimeReferences(ZonedDateTime)
     * @see #areTimeReferencesResolved()
     */
    void setPartialIssueTime(final String time);
   
    /**
     * Sets the partially resolved issue time in UTC. To get a fully resolved issue time, 
     * the missing month-of-year and year data needs to be provided using 
     * {@link #amendTimeReferences}. 
     *
     * @param dayOfMonth issue time day-of-month
     * @param hour issue time hour-of-day
     * @param minute issue time minute-of-hour
     * 
     * @see #getIssueTime()
     * @see #amendTimeReferences(ZonedDateTime)
     * @see #areTimeReferencesResolved()
     */
    void setPartialIssueTime(final int dayOfMonth, final int hour, final int minute);
    
    /**
     * 
     * @param dayOfMonth
     * @param hour
     * @param minute
     * @param timeZoneID
     */
    void setPartialIssueTime(final int dayOfMonth, final int hour, final int minute, final ZoneId timeZoneID);

    /**
     * 
     * @param year
     * @param monthOfYear
     * @param dayOfMonth
     * @param hour
     * @param minute
     * @param timeZoneID
     */
    void setIssueTime(final int year, final int monthOfYear, final int dayOfMonth, final int hour, final int minute, final ZoneId timeZoneID);

    /**
     * 
     * @param issueTime
     */
    void setIssueTime(final ZonedDateTime issueTime);
    
    
    /**
     * Sets the remarks as a List.
     *
     * @param remarks to set
     */
    void setRemarks(List<String> remarks);

    /**
     * 
     * @param usage
     */
    void setPermissibleUsage(PermissibleUsage usage);

    /**
     * 
     * @param reason
     */
    void setPermissibleUsageReason(PermissibleUsageReason reason);
    
    /**
     * 
     * @param text
     */
    void setPermissibleUsageSupplementary(String text);
    
    /**
     * 
     * @param translated
     */
    void setTranslated(boolean translated);
    
    /**
     * 
     * @param id
     */
    void setTranslatedBulletinID(String id);

    /**
     * 
     * @param time
     */
    void setTranslatedBulletinReceptionTime(ZonedDateTime time);
    
    /**
     * 
     * @param designator
     */
    void setTranslationCentreDesignator(String designator);
    
    /**
     * 
     * @param name
     */
    void setTranslationCentreName(String name);
    
    /**
     * 
     * @param time
     */
    void setTranslationTime(ZonedDateTime time);
    
    /**
     * 
     * @param originalTAC
     */
    void setTranslatedTAC(String originalTAC);
    
}
