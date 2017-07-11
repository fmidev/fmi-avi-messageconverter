package fi.fmi.avi.data.taf;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import fi.fmi.avi.data.AerodromeWeatherMessage;
import fi.fmi.avi.data.AviationCodeListUser;

/**
 * Created by rinne on 30/01/15.
 */
public interface TAF extends AerodromeWeatherMessage, AviationCodeListUser {

    TAFStatus getStatus();

    int getValidityStartDayOfMonth();

    int getValidityStartHour();
    
    String getPartialValidityStartTime();
    
    ZonedDateTime getValidityStartTime();

    int getValidityEndDayOfMonth();

    int getValidityEndHour();
    
    String getPartialValidityEndTime();
    
    ZonedDateTime getValidityEndTime();

    TAFBaseForecast getBaseForecast();

    List<TAFChangeForecast> getChangeForecasts();

    TAF getReferredReport();


    void setStatus(TAFStatus status);

    void setPartialValidityStartTime(final String time);
    
    void setPartialValidityStartTime(final int day, final int hour);
    
    void setValidityStartTime(final int year, final int monthOfYear, final int dayOfMonth, final int hour, final int minute, final ZoneId timeZone);

    void setValidityStartTime(final ZonedDateTime time);
    
    void setPartialValidityEndTime(final String time);
    
    void setPartialValidityEndTime(final int day, final int hour);
    
    void setValidityEndTime(final int year, final int monthOfYear, final int dayOfMonth, final int hour, final int minute, final ZoneId timeZone);

    void setValidityEndTime(final ZonedDateTime time);
    
    void setBaseForecast(TAFBaseForecast baseForecast);

    void setChangeForecasts(List<TAFChangeForecast> changeForecasts);

    void setReferredReport(TAF referredReport);


}
