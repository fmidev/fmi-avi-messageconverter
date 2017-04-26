package fi.fmi.avi.data.taf;

import java.util.List;

import fi.fmi.avi.data.AviationCodeListUser;
import fi.fmi.avi.data.AviationWeatherMessage;

/**
 * Created by rinne on 30/01/15.
 */
public interface TAF extends AviationWeatherMessage, AviationCodeListUser {

    TAFStatus getStatus();

    String getAerodromeDesignator();

    int getValidityStartDayOfMonth();

    int getValidityStartHour();

    int getValidityEndDayOfMonth();

    int getValidityEndHour();

    TAFBaseForecast getBaseForecast();

    List<TAFChangeForecast> getChangeForecasts();

    TAF getReferredReport();

    void setStatus(TAFStatus status);

    void setAerodromeDesignator(String aerodromeDesignator);

    void setValidityStartDayOfMonth(int dayOfMonth);

    void setValidityStartHour(int hour);

    void setValidityEndDayOfMonth(int dayOfMonth);

    void setValidityEndHour(int hour);

    void setBaseForecast(TAFBaseForecast baseForecast);

    void setChangeForecasts(List<TAFChangeForecast> changeForecasts);

    void setReferredReport(TAF referredReport);


}
