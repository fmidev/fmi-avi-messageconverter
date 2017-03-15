package fi.fmi.avi.data.taf;

/**
 * Created by rinne on 30/01/15.
 */
public interface TAFChangeForecast extends TAFForecast {

    TAFChangeIndicator getChangeIndicator();

    int getValidityStartDayOfMonth();

    int getValidityStartHour();

    int getValidityStartMinute();

    int getValidityEndDayOfMonth();

    int getValidityEndHour();


    void setChangeIndicator(TAFChangeIndicator changeIndicator);

    void setValidityStartDayOfMonth(int dayOfMonth);

    void setValidityStartHour(int hour);

    void setValidityStartMinute(int minute);

    void setValidityEndDayOfMonth(int dayOfMonth);

    void setValidityEndHour(int hour);

}
