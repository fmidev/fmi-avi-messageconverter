package fi.fmi.avi.data.taf;

import fi.fmi.avi.data.AviationCodeListUser;
import fi.fmi.avi.data.NumericMeasure;

/**
 * Created by rinne on 30/01/15.
 */
public interface TAFAirTemperatureForecast extends AviationCodeListUser {

    NumericMeasure getMaxTemperature();

    int getMaxTemperatureDayOfMonth();

    int getMaxTemperatureHour();

    NumericMeasure getMinTemperature();

    int getMinTemperatureDayOfMonth();

    int getMinTemperatureHour();


    void setMaxTemperature(NumericMeasure maxTemperature);

    void setMaxTemperatureDayOfMonth(int maxTemperatureDayOfMonth);

    void setMaxTemperatureHour(int maxTemperatureHour);

    void setMinTemperature(NumericMeasure maxTemperature);

    void setMinTemperatureDayOfMonth(int maxTemperatureDayOfMonth);

    void setMinTemperatureHour(int maxTemperatureHour);

}
