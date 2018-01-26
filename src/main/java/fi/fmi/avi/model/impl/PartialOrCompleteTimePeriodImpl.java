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

    private int startDay = -1;
    private int startHour = -1;
    private int startMinute = -1;
    private int endDay = -1;
    private int endHour = -1;
    private int endMinute = -1;
    private boolean endHourIs24 = false;
    private ZonedDateTime from;
    private ZonedDateTime to;

    protected PartialOrCompleteTimePeriodImpl() {
    }

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

    @Override
    @JsonIgnore
    public int getStartTimeDay() {
        if (this.from != null) {
            return this.from.getDayOfMonth();
        } else {
            return this.startDay;
        }
    }

    @Override
    @JsonIgnore
    public int getStartTimeHour() {
        if (this.from != null) {
            return this.from.getHour();
        } else {
            return this.startHour;
        }
    }

    @Override
    @JsonIgnore
    public int getStartTimeMinute() {
        if (this.from != null) {
            return this.from.getMinute();
        } else {
            return this.startMinute;
        }
    }

    @Override
    @JsonProperty("partialStartTime")
    public abstract String getPartialStartTime();

    @Override
    @JsonProperty("partialStartTime")
    public void setPartialStartTime(final String time) {
        if (time == null) {
            this.startDay = -1;
            this.startHour = -1;
            this.startMinute = -1;
            this.from = null;
        } else if (this.matchesPartialTimePattern(time)) {
            final int day = this.extractDayFromPartial(time);
            final int hour = this.extractHourFromPartial(time);
            final int minute = this.extractMinuteFromPartial(time);
            this.setPartialStartTime(day, hour, minute);
        } else {
            throw new IllegalArgumentException("Time '" + time + "' is not in the expected format '" + this.getPartialTimePattern() + "'");
        }

    }

    @Override
    @JsonIgnore
    public ZonedDateTime getCompleteStartTime() {
        return this.from;
    }

    @Override
    @JsonIgnore
    public void setCompleteStartTime(final ZonedDateTime time) {
        this.from = time;
    }

    @Override
    @JsonProperty("startTime")
    public String getCompleteStartTimeAsISOString() {
        if (this.from != null) {
            return this.from.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } else {
            return null;
        }
    }

    @Override
    @JsonProperty("startTime")
    public void setCompleteStartTimeAsISOString(final String time) {
        if (time == null) {
            this.from = null;
        } else {
            this.setCompleteStartTime(ZonedDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(time)));
        }
    }

    @Override
    @JsonIgnore
    public int getEndTimeDay() {
        if (this.to != null) {
            return this.to.getDayOfMonth();
        } else {
            return this.endDay;
        }
    }

    @Override
    @JsonIgnore
    public int getEndTimeHour() {
        if (this.to != null) {
            return this.to.getHour();
        } else {
            return this.endHour;
        }
    }

    @Override
    @JsonIgnore
    public int getEndTimeMinute() {
        if (this.to != null) {
            return this.to.getMinute();
        } else {
            return this.endMinute;
        }
    }

    @Override
    @JsonProperty("partialEndTime")
    public abstract String getPartialEndTime();

    @Override
    @JsonProperty("partialEndTime")
    public void setPartialEndTime(final String time) {
        if (time == null) {
            this.endDay = -1;
            this.endHour = -1;
            this.endMinute = -1;
            this.to = null;
        } else if (this.matchesPartialTimePattern(time)) {
            final int day = this.extractDayFromPartial(time);
            final int hour = this.extractHourFromPartial(time);
            final int minute = this.extractMinuteFromPartial(time);
            this.setPartialEndTime(day, hour, minute);
        } else {
            throw new IllegalArgumentException("Time '" + time + "' is not in the expected format '" + this.getPartialTimePattern() + "'");
        }

    }

    @Override
    @JsonIgnore
    public ZonedDateTime getCompleteEndTime() {
        return this.to;
    }

    @Override
    @JsonIgnore
    public void setCompleteEndTime(final ZonedDateTime time) {
        this.to = time;
    }

    @Override
    @JsonProperty("endTime")
    public String getCompleteEndTimeAsISOString() {
        if (this.to != null) {
            return this.to.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } else {
            return null;
        }
    }

    @Override
    @JsonProperty("endTime")
    public void setCompleteEndTimeAsISOString(final String time) {
        if (time == null) {
            this.to = null;
        } else {
            this.setCompleteEndTime(ZonedDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(time)));
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
    @JsonIgnore
    public void setCompleteStartTime(final int year, final int monthOfYear, final int dayOfMonth, final int hour, final int minute, final ZoneId timeZone) {
        this.setCompleteStartTime(ZonedDateTime.of(LocalDateTime.of(year, monthOfYear, dayOfMonth, hour, minute), timeZone));
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
    @JsonIgnore
    public void setCompleteEndTime(final int year, final int monthOfYear, final int dayOfMonth, final int hour, final int minute, final ZoneId timeZone) {
        this.setCompleteEndTime(ZonedDateTime.of(LocalDateTime.of(year, monthOfYear, dayOfMonth, hour, minute), timeZone));
    }

    @Override
    public boolean isStartTimeComplete() {
        return this.from != null;
    }

    @Override
    public boolean isEndTimeComplete() {
        return this.to != null;
    }

    @Override
    @JsonIgnore
    public boolean endsAtMidnight() {
        return this.endHourIs24;
    }

    protected abstract boolean matchesPartialTimePattern(String partialString);

    protected abstract Pattern getPartialTimePattern();

    protected abstract int extractDayFromPartial(String partialString);

    protected abstract int extractHourFromPartial(String partialString);

    protected abstract int extractMinuteFromPartial(String partialString);

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (this.hasStartTime()) {
            sb.append("start: ");
            if (this.from != null) {
                sb.append("complete:");
                sb.append(this.getCompleteStartTimeAsISOString());
            } else {
                sb.append("partial, day:");
                if (this.startDay != -1) {
                    sb.append(this.startDay);
                } else {
                    sb.append("n/a");
                }
                sb.append(", hour:");
                if (this.startHour != -1) {
                    sb.append(this.startHour);
                } else {
                    sb.append("n/a");
                }
                sb.append(", minute:");
                if (this.startMinute != -1) {
                    sb.append(this.startMinute);
                } else {
                    sb.append("n/a");
                }
            }
        }
        if (this.hasEndTime()) {
            sb.append("; end: ");
            if (this.to != null) {
                sb.append("complete:");
                sb.append(this.getCompleteEndTimeAsISOString());
            } else {
                sb.append("partial, day:");
                if (this.endDay != -1) {
                    sb.append(this.endDay);
                } else {
                    sb.append("n/a");
                }
                sb.append(", hour:");
                if (this.endHour != -1) {
                    sb.append(this.endHour);
                } else {
                    sb.append("n/a");
                }
                sb.append(", minute:");
                if (this.endMinute != -1) {
                    sb.append(this.endMinute);
                } else {
                    sb.append("n/a");
                }
            }
        }
        return sb.toString();
    }

}
