package fi.fmi.avi.model.impl;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import fi.fmi.avi.model.AviationCodeListUser.PermissibleUsage;
import fi.fmi.avi.model.AviationCodeListUser.PermissibleUsageReason;
import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.Weather;

/**
 * Created by rinne on 05/06/17.
 */

public abstract class AviationWeatherMessageImpl implements AviationWeatherMessage {

    private static final Pattern DAY_HOUR_MINUTE_PATTERN = Pattern.compile("([0-9]{2})([0-9]{2})([0-9]{2})([A-Z]+)");

    private int issueDayOfMonth = -1;
    private int issueHour = -1;
    private int issueMinute = -1;
    private ZoneId timeZone;
    private ZonedDateTime fullyResolvedIssueTime;
    private List<String> remarks;
    private PermissibleUsage permissibleUsage;
    private PermissibleUsageReason permissibleUsageReason;
    private String permissibleUsageSupplementary;
    private String translatedBulletinID;
    private ZonedDateTime translatedBulletinReceptionTime;
    private String translationCentreDesignator;
    private String translationCentreName;
    private ZonedDateTime translationTime;
    private String translatedTAC;
    private boolean translated;

    protected AviationWeatherMessageImpl() {
    }

    protected AviationWeatherMessageImpl(final AviationWeatherMessage input) {
        if (input != null) {
            if (input.getIssueTime() != null) {
                this.setIssueTime(input.getIssueTime());
            } else {
                this.fullyResolvedIssueTime = null;
                this.setPartialIssueTime(input.getPartialIssueTime());
            }
            if (input.getRemarks() != null) {
                this.remarks = new ArrayList<>(input.getRemarks());
            }
            this.permissibleUsage = input.getPermissibleUsage();
        }
    }

    public static List<String> getAsWeatherCodes(final List<Weather> weatherList) {
        return getAsWeatherCodes(weatherList, null);
    }

    public static List<String> getAsWeatherCodes(final List<Weather> weatherList, final String prefix) {
        List<String> retval = null;
        if (weatherList != null) {
            retval = new ArrayList<>(weatherList.size());
            for (final Weather w : weatherList) {
                if (prefix != null) {
                    retval.add(prefix + w.getCode());
                } else {
                    retval.add(w.getCode());
                }
            }
        }
        return retval;
    }

    public static void completePartialTimeReferenceList(final List<? extends PartialOrCompleteTimePeriod> references, final ZonedDateTime referenceTime) {
        if (references != null) {
            //Assumption: the start times come in chronological order, but the periods may be (partly) overlapping
            LocalDateTime ref = LocalDateTime.from(referenceTime);
            for (final PartialOrCompleteTimePeriod period : references) {
                if (period != null) {
                    if (period.hasStartTime()) {
                        if (period.getStartTimeDay() == -1) {
                            if (period.getStartTimeHour() < ref.getHour()) {
                                //Roll over to the next day
                                ref = ref.plusDays(1);
                            }
                        } else {
                            if (period.getStartTimeDay() < ref.getDayOfMonth()) {
                                //Roll over to the next month
                                ref = ref.plusMonths(1);
                            }
                            ref = ref.withDayOfMonth(period.getStartTimeDay());
                        }
                        ref = ref.withHour(period.getStartTimeHour()).withMinute(period.getStartTimeMinute());
                        period.setCompleteStartTime(ZonedDateTime.of(ref, referenceTime.getZone()));

                        if (period.hasEndTime()) {
                            LocalDateTime endTime = LocalDateTime.from(period.getCompleteStartTime());
                            if (period.getEndTimeDay() == -1) {
                                if (period.getEndTimeHour() <= endTime.getHour()) {
                                    endTime = endTime.plusDays(1);
                                }
                            } else {
                                //We know the day
                                if (period.getEndTimeDay() < period.getCompleteStartTime().getDayOfMonth()) {
                                    //Roll over to the next month
                                    endTime = endTime.plusMonths(1);
                                }
                                endTime = endTime.withDayOfMonth(period.getEndTimeDay());
                            }
                            if (period.endsAtMidnight()) {
                                endTime = endTime.plusDays(1).withHour(0).withMinute(0);
                            } else {
                                endTime = endTime.withHour(period.getEndTimeHour()).withMinute(period.getEndTimeMinute());
                            }
                            period.setCompleteEndTime(ZonedDateTime.of(endTime, referenceTime.getZone()));
                        }
                    } else if (period.hasEndTime()) {
                        if (period.getEndTimeDay() == -1) {
                            if (period.endsAtMidnight() || period.getEndTimeHour() <= ref.getHour()) {
                                ref = ref.plusDays(1);
                            }
                        } else {
                            //We know the day
                            if (period.getEndTimeDay() < period.getCompleteStartTime().getDayOfMonth()) {
                                //Roll over to the next month
                                ref = ref.plusMonths(1);
                            }
                            ref = ref.withDayOfMonth(period.getEndTimeDay());
                        }
                        if (period.endsAtMidnight()) {
                            ref = ref.withHour(0).withMinute(0);
                        } else {
                            ref = ref.withHour(period.getEndTimeHour()).withMinute(period.getEndTimeMinute());
                        }
                        period.setCompleteEndTime(ZonedDateTime.of(ref, referenceTime.getZone()));
                    }
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#setIssueTime()
     */
    @Override
    public void setPartialIssueTime(final int dayOfMonth, final int hour, final int minute) {
        this.setPartialIssueTime(dayOfMonth, hour, minute, ZoneId.of("Z"));
    }

    /* (non-Javadoc)
     * @see fi.fmi.avi.data.METAR#setIssueTime(int, int, int, String)
     */
    @Override
    public void setPartialIssueTime(final int dayOfMonth, final int hour, final int minute, final ZoneId timeZoneID) {
        if (timeOk(dayOfMonth, hour, minute)) {
            this.issueDayOfMonth = dayOfMonth;
            this.issueHour = hour;
            this.issueMinute = minute;
            this.timeZone = timeZoneID;
            if (this.fullyResolvedIssueTime != null) {
                if (this.fullyResolvedIssueTime.getDayOfMonth() != dayOfMonth || this.fullyResolvedIssueTime.getHour() != hour
                        || this.fullyResolvedIssueTime.getMinute() != minute || !this.fullyResolvedIssueTime.getZone().equals(timeZoneID)) {
                    this.fullyResolvedIssueTime = null;
                }
            }

        } else {
            throw new IllegalArgumentException("Invalid day, hour and/or minute values");
        }
    }

    @Override
    public void setIssueTime(final int year, final int monthOfYear, final int dayOfMonth, final int hour, final int minute, final ZoneId timeZoneID) {
        this.setIssueTime(ZonedDateTime.of(LocalDateTime.of(year, monthOfYear, dayOfMonth, hour, minute), timeZoneID));
    }

    @JsonProperty("issueTime")
    public String getIssueTimeISO() {
        if (this.fullyResolvedIssueTime != null) {
            return this.fullyResolvedIssueTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } else {
            return null;
        }
    }

    @JsonProperty("issueTime")
    public void setIssueTimeISO(final String time) {
        this.setIssueTime(ZonedDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(time)));
    }

    @Override
    public String getPartialIssueTime() {
        if (this.issueDayOfMonth > -1 && this.issueHour > -1 && this.issueMinute > -1 && this.timeZone != null) {
            return String.format("%02d%02d%02d%s", this.issueDayOfMonth, this.issueHour, this.issueMinute, this.timeZone);
        } else {
            return null;
        }
    }

    @Override
    public void setPartialIssueTime(final String time) {
        if (time == null) {
            this.setPartialIssueTime(-1, -1, -1, null);
        } else {
            final Matcher m = DAY_HOUR_MINUTE_PATTERN.matcher(time);
            if (m.matches()) {
                final int day = Integer.parseInt(m.group(1));
                final int hour = Integer.parseInt(m.group(2));
                final int minute = Integer.parseInt(m.group(3));
                try {
                    final ZoneId tz = ZoneId.of(m.group(4));
                    this.setPartialIssueTime(day, hour, minute, tz);
                } catch (final DateTimeException dte) {
                    throw new IllegalArgumentException("Time zone id '" + m.group(4) + "' is unknown");
                }
            } else {
                throw new IllegalArgumentException("Time '" + time + "' is not in format 'ddHHmmz'");
            }
        }
    }

    @Override
    @JsonIgnore
    public ZonedDateTime getIssueTime() {
        return this.fullyResolvedIssueTime;
    }

    @Override
    public void setIssueTime(final ZonedDateTime issueTime) {
        this.fullyResolvedIssueTime = issueTime;
        this.issueDayOfMonth = this.fullyResolvedIssueTime.getDayOfMonth();
        this.issueHour = this.fullyResolvedIssueTime.getHour();
        this.issueMinute = this.fullyResolvedIssueTime.getMinute();
        this.timeZone = issueTime.getZone();
    }

    @Override
    public List<String> getRemarks() {
        return this.remarks;
    }

    @Override
    public void setRemarks(final List<String> remarks) {
        this.remarks = remarks;
    }

    @Override
    public PermissibleUsage getPermissibleUsage() {
        return permissibleUsage;
    }

    @Override
    public void setPermissibleUsage(final PermissibleUsage permissibleUsage) {
        this.permissibleUsage = permissibleUsage;
    }

    @Override
    public boolean isTranslated() {
        return this.translated;
    }

    @Override
    public void setTranslated(final boolean translated) {
        this.translated = translated;
    }

    @Override
    public PermissibleUsageReason getPermissibleUsageReason() {
        return this.permissibleUsageReason;
    }

    @Override
    public void setPermissibleUsageReason(final PermissibleUsageReason reason) {
        this.permissibleUsageReason = reason;
    }

    @Override
    public String getPermissibleUsageSupplementary() {
        return this.permissibleUsageSupplementary;
    }

    @Override
    public void setPermissibleUsageSupplementary(final String text) {
        this.permissibleUsageSupplementary = text;
    }

    @Override
    public String getTranslatedBulletinID() {
        return this.translatedBulletinID;
    }

    @Override
    public void setTranslatedBulletinID(final String id) {
        this.translatedBulletinID = id;
    }

    @Override
    @JsonIgnore
    public ZonedDateTime getTranslatedBulletinReceptionTime() {
        return this.translatedBulletinReceptionTime;
    }

    @Override
    @JsonIgnore
    public void setTranslatedBulletinReceptionTime(final ZonedDateTime time) {
        this.translatedBulletinReceptionTime = time;
    }

    @JsonProperty("translatedBulletinReceptionTime")
    public String getTranslatedBulletinReceptionTimeISO() {
        if (this.translatedBulletinReceptionTime != null) {
            return this.translatedBulletinReceptionTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } else {
            return null;
        }
    }

    @JsonProperty("translatedBulletinReceptionTime")
    public void setTranslatedBulletinReceptionTimeISO(final String time) {
        this.setTranslatedBulletinReceptionTime(ZonedDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(time)));
    }

    @Override
    public String getTranslationCentreDesignator() {
        return this.translationCentreDesignator;
    }

    @Override
    public void setTranslationCentreDesignator(final String designator) {
        this.translationCentreDesignator = designator;
    }

    @Override
    public String getTranslationCentreName() {
        return this.translationCentreName;
    }

    @Override
    public void setTranslationCentreName(final String name) {
        this.translationCentreName = name;
    }

    @Override
    @JsonIgnore
    public ZonedDateTime getTranslationTime() {
        return this.translationTime;
    }

    @Override
    @JsonIgnore
    public void setTranslationTime(final ZonedDateTime time) {
        this.translationTime = time;
    }

    @JsonProperty("translationTime")
    public String getTranslationTimeISO() {
        if (this.translationTime != null) {
            return this.translationTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } else {
            return null;
        }
    }

    @JsonProperty("translationTime")
    public void setTranslationTimeISO(final String time) {
        this.setTranslationTime(ZonedDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(time)));
    }

    @Override
    @JsonIgnore
    public String getTranslatedTAC() {
        return this.translatedTAC;
    }

    @Override
    @JsonIgnore
    public void setTranslatedTAC(final String originalTAC) {
        this.translatedTAC = originalTAC;
    }

    @Override
    public void completeIssueTime(final YearMonth referenceTime) {
        if (this.issueDayOfMonth > -1 && this.issueHour > -1 && this.issueMinute > -1 && this.timeZone != null) {
            try {
                if (this.issueHour == 24 && this.issueMinute == 0) {
                    this.setIssueTime(
                            ZonedDateTime.of(LocalDateTime.of(referenceTime.getYear(), referenceTime.getMonth(), this.issueDayOfMonth, 0, 0), this.timeZone)
                                    .plusDays(1));
                } else {
                    this.setIssueTime(ZonedDateTime.of(
                            LocalDateTime.of(referenceTime.getYear(), referenceTime.getMonth(), this.issueDayOfMonth, this.issueHour, this.issueMinute),
                            this.timeZone));
                }
            } catch (final DateTimeException dte) {
                throw new IllegalArgumentException(
                        "Issue time with day of month '" + this.issueDayOfMonth + "' cannot be completed with month '" + referenceTime.getMonth() + "'", dte);
            }
        }
    }

    @Override
    public boolean isIssueTimeComplete() {
        return this.fullyResolvedIssueTime != null;
    }

    private boolean timeOk(final int day, final int hour, final int minute) {
        if (day > 31) {
            return false;
        }
        if (hour > 24) {
            return false;
        } else if (hour == 24 && minute != 0) {
            return false;
        }
        if (minute > 59) {
            return false;
        }
        return true;
    }

}
