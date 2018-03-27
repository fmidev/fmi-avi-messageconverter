package fi.fmi.avi.model;

import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.inferred.freebuilder.FreeBuilder;

import com.google.common.base.Preconditions;

import fi.fmi.avi.model.AviationCodeListUser.PermissibleUsage;
import fi.fmi.avi.model.AviationCodeListUser.PermissibleUsageReason;

/**
 * A generic interface for all aviation related weather reports and forecasts.
 *
 * Note that as TAC encoding does not contain the month and year
 * data, a fully resolved issue time can only be constructed by
 * providing this info externally using {@link #completeIssueTime(YearMonth)}
 */

@FreeBuilder
public interface AviationWeatherMessage {

    /**
     * Returns the partial issue time (day of month, hour and minute)
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
     * @return the fully resolved issue time if available
     * 
     * @see #isIssueTimeComplete()
     * @see #completeIssueTime(YearMonth)
     */
    Optional<ZonedDateTime> getIssueTime();

    /**
     * Returns the remarks, if included in the message.
     * in TAC remarks are provided at the end of the message
     * after the 'RMK' token.
     *
     * @return the remark tokens as-is
     */
    List<String> getRemarks();

    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @return permissible usage,
     */
    Optional<PermissibleUsage> getPermissibleUsage();
    
    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @return permissible usage reason,
     */
    Optional<PermissibleUsageReason> getPermissibleUsageReason();
    
    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @return permissible usage supplementary
     */
    Optional<String> getPermissibleUsageSupplementary();
    
    /**
     * Indication of the message has been created by automatic translation from another format.
     *
     * @return true if the message has been translated
     */
    boolean isTranslated();
    
    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @return bulleting id of the original message (if available),
     */
    Optional<String> getTranslatedBulletinID();

    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @return reception time of the original bulletin (if available)
     */
    Optional<ZonedDateTime> getTranslatedBulletinReceptionTime();
    
    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @return translation centre designator (if available)
     */
    Optional<String> getTranslationCentreDesignator();
    
    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @return translation centre name (if available)
     */
    Optional<String> getTranslationCentreName();
    
    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @return time the translation occurred
     */
    Optional<ZonedDateTime> getTranslationTime();
    
    /**
     * Returns the original TAC format message, if available.
     *
     * @return the original TAC message before translation
     */
    Optional<String> getTranslatedTAC();
    
    Builder toBuilder();
    
    class Builder extends AviationWeatherMessage_Builder {
        private static final Pattern DAY_HOUR_MINUTE_PATTERN = Pattern.compile("([0-9]{2})([0-9]{2})([0-9]{2})([A-Z]+)");

        @Override
        public Builder setPartialIssueTime(String partialIssueTime) {
            Preconditions.checkNotNull(partialIssueTime);
            if(DAY_HOUR_MINUTE_PATTERN.matcher(partialIssueTime).matches()){
                return super.setPartialIssueTime(partialIssueTime);
            } else {
                throw new IllegalArgumentException("Partial issue time must match pattern " + DAY_HOUR_MINUTE_PATTERN.toString());
            }
        }

        @Override
        public Builder setIssueTime(ZonedDateTime issueTime) {
            Builder b = super.setIssueTime(issueTime);
            this.setPartialIssueTime(issueTime.format(DateTimeFormatter.ofPattern("ddHHmmX")));
            return b;
        }
        
        public Builder setIssueTimeYearMonth(YearMonth yearMonth) {
            if (this.getPartialIssueTime() != null) {
                
            }
        }
        
    }
    /**
     * Sets the partial issue time as a formatted String. 
     * To get a fully resolved issue time, 
     * the missing month of year and year data needs to be 
     * provided using {@link #completeIssueTime(YearMonth)}.
     * 
     * @param time formatted as ddHHmmz (201004Z)
     * 
     * @see #getIssueTime()
     * @see #completeIssueTime(YearMonth)
     * @see #isIssueTimeComplete()
     */
    //void setPartialIssueTime(final String time);
   
    /**
     * Sets the partially resolved issue time in UTC. To get a fully resolved issue time, 
     * the missing month-of-year and year data needs to be provided using 
     * {@link #completeIssueTime(YearMonth)}.
     *
     * @param dayOfMonth issue time day-of-month
     * @param hour issue time hour-of-day
     * @param minute issue time minute-of-hour
     * 
     * @see #getIssueTime()
     * @see #completeIssueTime(YearMonth)
     * @see #isIssueTimeComplete()
     */
    //void setPartialIssueTime(final int dayOfMonth, final int hour, final int minute);

    /**
     * Sets the partially resolved issue time in UTC. To get a fully resolved issue time,
     * the missing month-of-year and year data needs to be provided using
     * {@link #completeIssueTime(YearMonth)}.
     *
     * @param dayOfMonth issue time day-of-month
     * @param hour issue time hour-of-day
     * @param minute issue time minute-of-hour
     * @param timeZoneID the timezone
     *
     * @see #getIssueTime()
     * @see #completeIssueTime(YearMonth)
     * @see #isIssueTimeComplete()
     */
    //void setPartialIssueTime(final int dayOfMonth, final int hour, final int minute, final ZoneId timeZoneID);

    /**
     * Sets the complete issue time of the message.
     *
     * @param year issue time year
     * @param monthOfYear issue time month-of-year
     * @param dayOfMonth issue time day-of-month
     * @param hour issue time hour-of-day
     * @param minute issue time minute-of-hour
     * @param timeZoneID the timezone
     */
    //void setIssueTime(final int year, final int monthOfYear, final int dayOfMonth, final int hour, final int minute, final ZoneId timeZoneID);

    /**
     * Sets the complete issue time.
     *
     * @param issueTime the time of issue
     */
    //void setIssueTime(final ZonedDateTime issueTime);
    
    
    /**
     * Sets the remarks as a List.
     *
     * @param remarks to set
     */
    //void setRemarks(List<String> remarks);

    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @param usage the usage
     */
    //void setPermissibleUsage(PermissibleUsage usage);

    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @param reason description of the reason for usage restriction
     */
    //void setPermissibleUsageReason(PermissibleUsageReason reason);
    
    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @param text free text to describe the permissible usage in more detail
     */
    //void setPermissibleUsageSupplementary(String text);
    
    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @param translated true to set as translated
     */
    //void setTranslated(boolean translated);
    
    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @param id the bulletin ID
     */
    //void setTranslatedBulletinID(String id);

    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @param time time of reception
     */
    //void setTranslatedBulletinReceptionTime(ZonedDateTime time);
    
    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     * @param designator centre designator
     */
    //void setTranslationCentreDesignator(String designator);
    
    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @param name name the centre which has done the translation
     */
    //void setTranslationCentreName(String name);
    
    /**
     * See https://schemas.wmo.int/iwxxm/2.1/common.xsd
     *
     * @param time time of the actual translation
     */
    //void setTranslationTime(ZonedDateTime time);
    
    /**
     * The original message in TAC format before translation.
     *
     * @param originalTAC the TAC message
     */
    //void setTranslatedTAC(String originalTAC);

    /**
     * Completes the partial message issue time by providing the missing year and month information.
     *
     * @param reference the year and month for the issue time
     * @throws IllegalArgumentException when the issue time cannot be completed by combining the existing partial issue time and the provided additional
     * information.
     */
    //void completeIssueTime(YearMonth reference) throws IllegalArgumentException;

    /**
     * Indicates whether the message issue time is partial (year and month missing) or complete.
     * Only when this method returns true, the {@link #getIssueTime()} is guaranteed to return a non-null result.
     *
     * @return true, if the issue time is complete, false otherwise.
     */
    //boolean isIssueTimeComplete();
    
}
