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


}
