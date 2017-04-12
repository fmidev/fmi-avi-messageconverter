package fi.fmi.avi.data.taf.impl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.data.NumericMeasure;
import fi.fmi.avi.data.impl.NumericMeasureImpl;
import fi.fmi.avi.data.taf.TAFAirTemperatureForecast;

/**
 * Created by rinne on 30/01/15.
 */
public class TAFAirTemperatureForecastImpl implements TAFAirTemperatureForecast {

    private NumericMeasure maxTemperature;
    private int maxTemperatureDayOfMonth = -1;
    private int maxTemperatureHour = -1;
    private NumericMeasure minTemperature;
    private int minTemperatureDayOfMonth = -1;
    private int minTemperatureHour = -1;

    public TAFAirTemperatureForecastImpl(){
    }

    public TAFAirTemperatureForecastImpl(final TAFAirTemperatureForecast input) {
        this.maxTemperature = new NumericMeasureImpl(input.getMaxTemperature());
        this.maxTemperatureDayOfMonth = input.getMaxTemperatureDayOfMonth();
        this.maxTemperatureHour = input.getMaxTemperatureHour();
        this.minTemperature = new NumericMeasureImpl(input.getMinTemperature());
        this.minTemperatureDayOfMonth = input.getMinTemperatureDayOfMonth();
        this.minTemperatureHour = input.getMinTemperatureHour();
    }

    @Override
    public NumericMeasure getMaxTemperature() {
        return maxTemperature;
    }

    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setMaxTemperature(final NumericMeasure maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    @Override
    public int getMaxTemperatureDayOfMonth() {
        return maxTemperatureDayOfMonth;
    }

    @Override
    public void setMaxTemperatureDayOfMonth(final int maxTemperatureDayOfMonth) {
        this.maxTemperatureDayOfMonth = maxTemperatureDayOfMonth;
    }

    @Override
    public int getMaxTemperatureHour() {
        return maxTemperatureHour;
    }

    @Override
    public void setMaxTemperatureHour(final int maxTemperatureHour) {
        this.maxTemperatureHour = maxTemperatureHour;
    }

    @Override
    public NumericMeasure getMinTemperature() {
        return minTemperature;
    }

    @Override
    @JsonDeserialize(as = NumericMeasureImpl.class)
    public void setMinTemperature(final NumericMeasure minTemperature) {
        this.minTemperature = minTemperature;
    }

    @Override
    public int getMinTemperatureDayOfMonth() {
        return minTemperatureDayOfMonth;
    }

    @Override
    public void setMinTemperatureDayOfMonth(final int minTemperatureDayOfMonth) {
        this.minTemperatureDayOfMonth = minTemperatureDayOfMonth;
    }

    @Override
    public int getMinTemperatureHour() {
        return minTemperatureHour;
    }

    @Override
    public void setMinTemperatureHour(final int minTemperatureHour) {
        this.minTemperatureHour = minTemperatureHour;
    }
}
