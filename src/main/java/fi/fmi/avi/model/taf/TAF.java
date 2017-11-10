package fi.fmi.avi.model.taf;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import fi.fmi.avi.model.AerodromeWeatherMessage;
import fi.fmi.avi.model.AviationCodeListUser;

/**
 * Created by rinne on 30/01/15.
 */
public interface TAF extends AerodromeWeatherMessage, AviationCodeListUser {

    TAFStatus getStatus();

    String getPartialValidityTimePeriod();
    
    
    int getValidityStartDayOfMonth();

    int getValidityStartHour();
    
    ZonedDateTime getValidityStartTime();
    

    int getValidityEndDayOfMonth();

    int getValidityEndHour();
    
    ZonedDateTime getValidityEndTime();

    
    TAFBaseForecast getBaseForecast();

    List<TAFChangeForecast> getChangeForecasts();

    TAF getReferredReport();


    void setStatus(TAFStatus status);

    
    void setPartialValidityTimePeriod(String time);
    
    void setPartialValidityTimePeriod(int day, int startHour, int endHour);
    
    void setPartialValidityTimePeriod(int startDay, int endDay, int startHour, int endHour);
    
    
    void setValidityStartTime(int year, int monthOfYear, int dayOfMonth, int hour, int minute, ZoneId timeZone);

    void setValidityStartTime(ZonedDateTime time);
    
    void setValidityEndTime(int year, int monthOfYear, int dayOfMonth, int hour, int minute, ZoneId timeZone);

    void setValidityEndTime(ZonedDateTime time);
    
    
    void setBaseForecast(TAFBaseForecast baseForecast);

    void setChangeForecasts(List<TAFChangeForecast> changeForecasts);

    void setReferredReport(TAF referredReport);

    /**
     * Completes the partial forecast start and end times by providing the missing year and month information.
     * The given year, month, day-of-month and hour-of-day values should match the corresponding
     * values of the expected TAF issue time.
     *
     * @param issueYear the (expected or known) year of the message issue time.
     * @param issueMonth the (expected or known) month (1-12) of message issue time.
     * @param issueDay the (expected or known) day-of-month (1-31) of the message issue time.
     * @param issueHour the (expected or known) hour-of-day (0-23) of the message issue time.
     * @param tz timezone
     *
     * @throws IllegalArgumentException when the time references cannot be completed by combining the existing partial times and the provided additional
     * information.
     */
    void completeForecastTimeReferences(int issueYear, int issueMonth, int issueDay, int issueHour, ZoneId tz);

    /**
     * Resets the fully-qualified forecast time references.
     *
     * If partial time values have been set previously,and those have been completed
     * using {@link #completeForecastTimeReferences(int, int, int, int, ZoneId)}, the complete
     * time references must no longer be available after this call. Also the methods
     * returning partial time values must return the times based on the originally
     * provided partial time components.
     */
    void uncompleteForecastTimeReferences();

    /**
     * Indicates whether there are partial forecast time references in this TAF.
     *
     * @return true, if the all time references are complete or there are no time references given, false otherwise.
     */
    boolean areForecastTimeReferencesComplete();


}
