package fi.fmi.avi.model.taf.impl;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.impl.NumericMeasureImpl;
import fi.fmi.avi.model.taf.TAFAirTemperatureForecast;

/**
 * Created by rinne on 30/01/15.
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class TAFAirTemperatureForecastImpl implements TAFAirTemperatureForecast, Serializable {

    private static final long serialVersionUID = 9211887065072755362L;
    private static final Pattern DAY_HOUR_PATTERN = Pattern.compile("([0-9]{2})?([0-9]{2})([A-Z]*)");

    private NumericMeasure maxTemperature;
    private int maxTemperatureDayOfMonth = -1;
    private int maxTemperatureHour = -1;
    private ZonedDateTime maxTemperatureTime = null;
    private NumericMeasure minTemperature;
    private int minTemperatureDayOfMonth = -1;
    private int minTemperatureHour = -1;
    private ZonedDateTime minTemperatureTime = null;

    public TAFAirTemperatureForecastImpl() {
    }

    public TAFAirTemperatureForecastImpl(final TAFAirTemperatureForecast input) {
        if (input != null) {
            if (input.getMaxTemperature() != null) {
                this.maxTemperature = new NumericMeasureImpl(input.getMaxTemperature());
            }
            if (input.getMaxTemperatureTime() != null) {
                this.setMaxTemperatureTime(input.getMaxTemperatureTime());
            } else {
                this.setPartialMaxTemperatureTime(input.getPartialMaxTemperatureTime());
            }
            if (input.getMinTemperatureTime() != null) {
                this.setMinTemperatureTime(input.getMinTemperatureTime());
            } else {
                this.setPartialMinTemperatureTime(input.getPartialMinTemperatureTime());
            }
            if (input.getMinTemperature() != null) {
                this.minTemperature = new NumericMeasureImpl(input.getMinTemperature());
            }
        }
    }

    private static boolean timeOk(final int day, final int hour) {
        if (day > 31) {
            return false;
        }
        if (hour > 24) {
            return false;
        }
        return true;
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
        if (this.maxTemperatureTime != null) {
            return maxTemperatureTime.getDayOfMonth();
        } else {
            return maxTemperatureDayOfMonth;
        }
    }

    @Override
    public int getMaxTemperatureHour() {
        if (this.maxTemperatureTime != null) {
            return this.maxTemperatureTime.getHour();
        } else {
            return maxTemperatureHour;
        }
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
        if (this.minTemperatureTime != null) {
            return this.minTemperatureTime.getDayOfMonth();
        } else {
            return minTemperatureDayOfMonth;
        }
    }

    @Override
    public int getMinTemperatureHour() {
        if (this.minTemperatureTime != null) {
            return this.minTemperatureTime.getHour();
        } else {
            return minTemperatureHour;
        }
    }

    @Override
    public String getPartialMaxTemperatureTime() {
        if (this.getMaxTemperatureHour() > -1) {
            if (this.getMaxTemperatureDayOfMonth() > -1) {
                return String.format("%02d%02dZ", this.getMaxTemperatureDayOfMonth(), this.getMaxTemperatureHour());
            } else {
                return String.format("%02dZ", this.getMaxTemperatureHour());
            }
        } else {
            return null;
        }
    }

    @Override
    public void setPartialMaxTemperatureTime(final int hour) {
        this.setPartialMaxTemperatureTime(-1, hour);
    }

    @Override
    @JsonProperty("partialMaxTemperatureTime")
    public void setPartialMaxTemperatureTime(final String time) {
        if (time == null) {
            this.maxTemperatureDayOfMonth = -1;
            this.maxTemperatureHour = -1;
            this.maxTemperatureTime = null;
        } else {
            final Matcher m = DAY_HOUR_PATTERN.matcher(time);
            if (m.matches()) {
                int day = -1;
                if (m.group(1) != null) {
                    day = Integer.parseInt(m.group(1));
                }
                final int hour = Integer.parseInt(m.group(2));
                if (timeOk(day, hour)) {
                    this.maxTemperatureDayOfMonth = day;
                    this.maxTemperatureHour = hour;
                } else {
                    throw new IllegalArgumentException("Invalid day and/or hour values in '" + time + "'");
                }
                this.maxTemperatureTime = null;
            } else {
                throw new IllegalArgumentException("Time '" + time + "' is not in format 'HHZ' or 'ddHHZ'");
            }
        }

    }

    @Override
    public ZonedDateTime getMaxTemperatureTime() {
        return this.maxTemperatureTime;
    }

    @Override
    public void setMaxTemperatureTime(final ZonedDateTime time) {
        this.maxTemperatureTime = time;

    }

    @Override
    public String getPartialMinTemperatureTime() {
        if (this.getMinTemperatureHour() > -1) {
            if (this.getMinTemperatureDayOfMonth() > -1) {
                return String.format("%02d%02dZ", this.getMinTemperatureDayOfMonth(), this.getMinTemperatureHour());
            } else {
                return String.format("%02dZ", this.getMinTemperatureHour());
            }
        } else {
            return null;
        }
    }

    @Override
    public void setPartialMinTemperatureTime(final int hour) {
        this.setPartialMinTemperatureTime(-1, hour);
    }

    @Override
    @JsonProperty("partialMinTemperatureTime")
    public void setPartialMinTemperatureTime(final String time) {
        if (time == null) {
            this.minTemperatureDayOfMonth = -1;
            this.minTemperatureHour = -1;
            this.minTemperatureTime = null;
        } else {
            final Matcher m = DAY_HOUR_PATTERN.matcher(time);
            if (m.matches()) {
                int day = -1;
                if (m.group(1) != null) {
                    day = Integer.parseInt(m.group(1));
                }
                final int hour = Integer.parseInt(m.group(2));
                if (timeOk(day, hour)) {
                    this.minTemperatureDayOfMonth = day;
                    this.minTemperatureHour = hour;
                } else {
                    throw new IllegalArgumentException("Invalid day and/or hour values in '" + time + "'");
                }
                this.minTemperatureTime = null;
            } else {
                throw new IllegalArgumentException("Time '" + time + "' is not in format 'HHZ' or 'ddHHZ'");
            }
        }
    }

    @Override
    public ZonedDateTime getMinTemperatureTime() {
        return this.minTemperatureTime;
    }

    @Override
    public void setMinTemperatureTime(final ZonedDateTime time) {
        this.minTemperatureTime = time;
    }

    @Override
    public void completeForecastTimeReferences(final ZonedDateTime approximateIssueTime) {
        if (approximateIssueTime != null) {
            if (this.minTemperatureHour > -1) {
                if (this.minTemperatureDayOfMonth > -1) {
                    if (this.minTemperatureDayOfMonth < approximateIssueTime.getDayOfMonth()) {
                        //Assume the next month
                        final ZonedDateTime oneMonthAfterIssue = approximateIssueTime.plusMonths(1);
                        this.setMinTemperatureTime(ZonedDateTime.of(
                                LocalDateTime.of(oneMonthAfterIssue.getYear(), oneMonthAfterIssue.getMonth(), this.minTemperatureDayOfMonth,
                                        this.minTemperatureHour, 0), oneMonthAfterIssue.getZone()));
                    } else {
                        this.setMinTemperatureTime(ZonedDateTime.of(
                                LocalDateTime.of(approximateIssueTime.getYear(), approximateIssueTime.getMonth(), this.minTemperatureDayOfMonth,
                                        this.minTemperatureHour, 0), approximateIssueTime.getZone()));
                    }
                } else {
                    this.minTemperatureTime = ZonedDateTime.of(
                            LocalDateTime.of(approximateIssueTime.getYear(), approximateIssueTime.getMonth(), approximateIssueTime.getDayOfMonth(),
                                    this.minTemperatureHour, 0), approximateIssueTime.getZone());
                }
            }

            if (this.maxTemperatureHour > -1) {
                if (this.maxTemperatureDayOfMonth > -1) {
                    if (this.maxTemperatureDayOfMonth < approximateIssueTime.getDayOfMonth()) {
                        //Assume the next month
                        final ZonedDateTime oneMonthAfterIssue = approximateIssueTime.plusMonths(1);
                        this.setMaxTemperatureTime(ZonedDateTime.of(
                                LocalDateTime.of(oneMonthAfterIssue.getYear(), oneMonthAfterIssue.getMonth(), this.maxTemperatureDayOfMonth,
                                        this.maxTemperatureHour, 0), oneMonthAfterIssue.getZone()));
                    } else {
                        this.setMaxTemperatureTime(ZonedDateTime.of(
                                LocalDateTime.of(approximateIssueTime.getYear(), approximateIssueTime.getMonth(), this.maxTemperatureDayOfMonth,
                                        this.maxTemperatureHour, 0), approximateIssueTime.getZone()));
                    }
                } else {
                    this.maxTemperatureTime = ZonedDateTime.of(
                            LocalDateTime.of(approximateIssueTime.getYear(), approximateIssueTime.getMonth(), approximateIssueTime.getDayOfMonth(),
                                    this.maxTemperatureHour, 0), approximateIssueTime.getZone());
                }
            }
        }
    }

    @Override
    public void resetForecastTimeReferences() {
        this.minTemperatureTime = null;
        this.maxTemperatureTime = null;
    }

    @Override
    public boolean areForecastTimeReferencesComplete() {
        return this.minTemperatureTime != null && this.maxTemperatureTime != null;
    }

    @Override
    public void setPartialMinTemperatureTime(final int day, final int hour) {
        if (timeOk(day, hour)) {
            this.minTemperatureDayOfMonth = day;
            this.minTemperatureHour = hour;
            this.minTemperatureTime = null;
        }
    }

    @Override
    public void setMinTemperatureTime(final int year, final int monthOfYear, final int dayOfMonth, final int hour, final ZoneId timeZone) {
        this.setMinTemperatureTime(ZonedDateTime.of(LocalDateTime.of(year, monthOfYear, dayOfMonth, hour, 0), timeZone));
    }

    @Override
    public void setPartialMaxTemperatureTime(final int day, final int hour) {
        if (timeOk(day, hour)) {
            this.maxTemperatureDayOfMonth = day;
            this.maxTemperatureHour = hour;
            this.maxTemperatureTime = null;
        }

    }

    @Override
    public void setMaxTemperatureTime(final int year, final int monthOfYear, final int dayOfMonth, final int hour, final ZoneId timeZone) {
        this.setMaxTemperatureTime(ZonedDateTime.of(LocalDateTime.of(year, monthOfYear, dayOfMonth, hour, 0), timeZone));
    }
}
