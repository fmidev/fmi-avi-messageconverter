package fi.fmi.avi.model.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import fi.fmi.avi.model.PartialOrCompleteTimePeriod;

/**
 * Created by rinne on 27/10/17.
 */
public abstract class PartialOrCompleteTimePeriodImpl implements PartialOrCompleteTimePeriod {

    public static boolean timeOk(final int day, final int hour, final int minute) {
        if (day > 31) {
            return false;
        }
        if (hour > 24) {
            return false;
        }
        if (minute > 59) {
            return false;
        }
        if ((hour == 24) && (minute > 0)) {
            return false;
        }
        return true;
    }

    private int startDay = -1;
    private int startHour = -1;
    private int startMinute = -1;
    private int endDay = -1;
    private int endHour = -1;
    private int endMinute = -1;
    private boolean endHourIs24 = false;
    private ZonedDateTime from;
    private ZonedDateTime to;

    public PartialOrCompleteTimePeriodImpl() {
    }

    @Override
    @JsonIgnore
    public int getPartialStartTimeDay() {
        return this.startDay;
    }

    @Override
    @JsonIgnore
    public int getPartialStartTimeHour() {
        return this.startHour;
    }

    @Override
    @JsonIgnore
    public int getPartialStartTimeMinute() {
        return this.startMinute;
    }

    @JsonProperty("partialStartTime")
    public abstract String getPartialStartTime();

    @Override
    @JsonIgnore
    public ZonedDateTime getCompleteStartTime() {
        return this.from;
    }

    @JsonProperty("startTime")
    public String getCompleteStartTimeAsISOString() {
        if (this.from != null) {
            return this.from.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } else {
            return null;
        }
    }

    @Override
    @JsonIgnore
    public int getPartialEndTimeDay() {
        return this.endDay;
    }

    @Override
    @JsonIgnore
    public int getPartialEndTimeHour() {
        return this.endHour;
    }

    @Override
    @JsonIgnore
    public int getPartialEndTimeMinute() {
        return this.endMinute;
    }


    @JsonProperty("partialEndTime")
    public abstract String getPartialEndTime();

    @Override
    @JsonIgnore
    public ZonedDateTime getCompleteEndTime() {
        return this.to;
    }

    @JsonProperty("endTime")
    public String getCompleteEndTimeAsISOString() {
        if (this.to != null) {
            return this.to.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } else {
            return null;
        }
    }

    @Override
    @JsonIgnore
    public void setPartialStartTime(final int day, final int hour, final int minute) {
        if (timeOk(day, hour, minute)) {
            this.startDay = day;
            this.startHour = hour;
            this.startMinute = minute;
            this.from = null;
        } else {
            throw new IllegalArgumentException("Invalid day, hour and/or minute values");
        }
    }

    @Override
    @JsonProperty("partialStartTime")
    public void setPartialStartTime(final String time) {
        if (time == null) {
            this.startDay = -1;
            this.startHour = -1;
            this.startMinute = -1;
            this.from = null;
        } else if (this.matchesPartialTimePattern(time)) {
            int day = this.extractDayFromPartial(time);
            int hour = this.extractHourFromPartial(time);
            int minute = this.extractMinuteFromPartial(time);
            this.setPartialStartTime(day, hour, minute);
        } else {
            throw new IllegalArgumentException("Time '" + time + "' is not in the expected format '" + this.getPartialTimePattern() + "'");
        }

    }

    @Override
    @JsonIgnore
    public void setCompleteStartTime(int year, int monthOfYear, int dayOfMonth, int hour, int minute, ZoneId timeZone) {
        this.setCompleteStartTime(ZonedDateTime.of(LocalDateTime.of(year, monthOfYear, dayOfMonth, hour, minute), timeZone));
    }

    @Override
    @JsonIgnore
    public void setCompleteStartTime(ZonedDateTime time) {
        this.from = time;
        this.startDay = this.from.getDayOfMonth();
        this.startHour = this.from.getHour();
        this.startMinute = this.from.getMinute();
    }

    @JsonProperty("startTime")
    public void setCompleteStartTimeAsISOString(final String time) {
        this.setCompleteStartTime(ZonedDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(time)));
    }


    @Override
    @JsonIgnore
    public void setPartialEndTime(final int day, final int hour, final int minute) {
        if (timeOk(day, hour, minute)) {
            this.endDay = day;
            this.endHour = hour;
            this.endMinute = minute;
            if (this.endHour == 24 && this.endMinute == 0) {
                this.endHourIs24 = true;
            } else {
                this.endHourIs24 = false;
            }
            this.to = null;
        } else {
            throw new IllegalArgumentException("Invalid hour and/or minute values");
        }
    }

    @Override
    @JsonProperty("partialEndTime")
    public void setPartialEndTime(final String time) {
        if (time == null) {
            this.endDay = -1;
            this.endHour = -1;
            this.endMinute = -1;
            this.to = null;
        } else if (this.matchesPartialTimePattern(time)){
            int day = this.extractDayFromPartial(time);
            int hour = this.extractHourFromPartial(time);
            int minute = this.extractMinuteFromPartial(time);
            this.setPartialEndTime(day, hour,minute);
        } else {
            throw new IllegalArgumentException("Time '" + time + "' is not in the expected format '" + this.getPartialTimePattern() + "'");
        }

    }

    @Override
    @JsonIgnore
    public void setCompleteEndTime(int year, int monthOfYear, int dayOfMonth, int hour, int minute, ZoneId timeZone) {
        this.setCompleteEndTime(ZonedDateTime.of(LocalDateTime.of(year, monthOfYear, dayOfMonth, hour, minute), timeZone));
    }

    @Override
    @JsonIgnore
    public void setCompleteEndTime(ZonedDateTime time) {
        this.to = time;
        this.endDay = time.getDayOfMonth();
        int hour = time.getHour();
        int minute = time.getMinute();
        if (hour == 0 && minute == 0) {
            this.endHour = 24;
            this.endHourIs24 = true;
        } else {
            this.endHour = this.to.getHour();
            this.endHourIs24 = false;
        }
        this.endMinute = minute;
    }

    @JsonProperty("endTime")
    public void setCompleteEndTimeAsISOString(final String time) {
        this.setCompleteEndTime(ZonedDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(time)));
    }

    @Override
    @JsonIgnore
    public boolean endsAtMidnight() {
        return this.endHourIs24;
    }

    protected abstract boolean matchesPartialTimePattern(final String partialString);

    protected abstract Pattern getPartialTimePattern();

    protected abstract int extractDayFromPartial(final String partialString);

    protected abstract int extractHourFromPartial(final String partialString);

    protected abstract int extractMinuteFromPartial(final String partialString);

}
