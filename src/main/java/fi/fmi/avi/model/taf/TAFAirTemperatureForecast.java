package fi.fmi.avi.model.taf;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;

/**
 * Created by rinne on 30/01/15.
 */
public interface TAFAirTemperatureForecast extends AviationCodeListUser {

    NumericMeasure getMaxTemperature();

    int getMaxTemperatureDayOfMonth();

    int getMaxTemperatureHour();

    String getPartialMaxTemperatureTime();
    
    ZonedDateTime getMaxTemperatureTime();
    
    NumericMeasure getMinTemperature();

    int getMinTemperatureDayOfMonth();

    int getMinTemperatureHour();
    
    String getPartialMinTemperatureTime();
    
    ZonedDateTime getMinTemperatureTime();


    void setMinTemperature(NumericMeasure maxTemperature);
    
    void setPartialMinTemperatureTime(final String time);
    
    void setPartialMinTemperatureTime(final int hour);
    
    void setPartialMinTemperatureTime(final int day, final int hour);
    
    void setMinTemperatureTime(final int year, final int monthOfYear, final int dayOfMonth, final int hour, final ZoneId timeZone);

    void setMinTemperatureTime(final ZonedDateTime time);
    
    void setMaxTemperature(NumericMeasure maxTemperature);
    
    void setPartialMaxTemperatureTime(final String time);
    
    void setPartialMaxTemperatureTime(final int hour);
    
    void setPartialMaxTemperatureTime(final int day, final int hour);
    
    void setMaxTemperatureTime(final int year, final int monthOfYear, final int dayOfMonth, final int hour, final ZoneId timeZone);

    void setMaxTemperatureTime(final ZonedDateTime time);

}
