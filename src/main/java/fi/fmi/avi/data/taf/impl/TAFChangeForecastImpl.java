package fi.fmi.avi.data.taf.impl;

import fi.fmi.avi.data.taf.TAFChangeForecast;

/**
 * Created by rinne on 30/01/15.
 */
public class TAFChangeForecastImpl extends TAFForecastImpl implements TAFChangeForecast {

    private TAFChangeIndicator changeIndicator;
    private int validityStartDayOfMonth = -1;
    private int validityStartHour = -1;
    private int validityStartMinute = -1;
    private int validityEndDayOfMonth = -1;
    private int validityEndHour = -1;

    public TAFChangeForecastImpl(){
    }

    public TAFChangeForecastImpl(final TAFChangeForecast input) {
        super(input);
        if (MissingReason.NOT_MISSING.equals(this.getMissingReason())) {
            this.setChangeIndicator(input.getChangeIndicator());

            this.setValidityStartDayOfMonth(input.getValidityStartDayOfMonth());
            this.setValidityStartHour(input.getValidityStartHour());
            this.setValidityStartMinute(input.getValidityStartMinute());

            this.setValidityEndDayOfMonth(input.getValidityEndDayOfMonth());
            this.setValidityEndHour(input.getValidityEndHour());
        }
    }

    @Override
    public TAFChangeIndicator getChangeIndicator() {
        return changeIndicator;
    }

    @Override
    public void setChangeIndicator(final TAFChangeIndicator changeIndicator) {
        this.changeIndicator = changeIndicator;
    }


    @Override
    public int getValidityStartDayOfMonth() {
        return validityStartDayOfMonth;
    }

    @Override
    public void setValidityStartDayOfMonth(final int dayOfMonth) {
        this.validityStartDayOfMonth = dayOfMonth;
    }

    @Override
    public int getValidityStartHour() {
        return validityStartHour;
    }

    @Override
    public void setValidityStartHour(final int hour) {
        this.validityStartHour = hour;
    }

    @Override
    public int getValidityStartMinute() {
        return validityStartMinute;
    }

    @Override
    public void setValidityStartMinute(final int minute) {
        this.validityStartMinute = minute;
    }



    @Override
    public int getValidityEndDayOfMonth() {
        return validityEndDayOfMonth;
    }

    @Override
    public void setValidityEndDayOfMonth(final int dayOfMonth) {
        this.validityEndDayOfMonth = dayOfMonth;
    }

    @Override
    public int getValidityEndHour() {
        return validityEndHour;
    }

    @Override
    public void setValidityEndHour(final int hour) {
        this.validityEndHour = hour;
    }

}
