package fi.fmi.avi.model.taf.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fi.fmi.avi.model.impl.PartialOrCompleteTimePeriodImpl;

/**
 * Created by rinne on 22/11/17.
 */
public class TAFValidityTime extends PartialOrCompleteTimePeriodImpl {

    private static final long serialVersionUID = 8089894543673443539L;

    public TAFValidityTime() {
    }

    public TAFValidityTime(final TAFValidityTime source) {
        if (source != null) {
            if (source.isStartTimeComplete()) {
                this.setCompleteStartTime(source.getCompleteStartTime());
            } else {
                this.setPartialStartTime(source.getPartialStartTime());
            }

            if (source.isEndTimeComplete()) {
                this.setCompleteEndTime(source.getCompleteEndTime());
            } else {
                this.setPartialEndTime(source.getPartialEndTime());
            }
        }
    }

    @Override
    public String getPartialStartTime() {
        throw new UnsupportedOperationException("getPartialStartTime(...) not implemented");
    }

    @Override
    public String getPartialEndTime() {
        throw new UnsupportedOperationException("getPartialEndTime(...) not implemented");
    }

    @Override
    protected boolean matchesPartialTimePattern(final String partialString) {
        throw new UnsupportedOperationException("matchesPartialTimePattern(...) not implemented");
    }

    @Override
    protected Pattern getPartialTimePattern() {
        throw new UnsupportedOperationException("getPartialTimePattern(...) not implemented");
    }

    @Override
    protected int extractDayFromPartial(final String partialString) {
        throw new UnsupportedOperationException("extractDayFromPartial(...) not implemented");
    }

    @Override
    protected int extractHourFromPartial(final String partialString) {
        throw new UnsupportedOperationException("extractHourFromPartial(...) not implemented");
    }

    @Override
    protected int extractMinuteFromPartial(final String partialString) {
        throw new UnsupportedOperationException("extractMinuteFromPartial(...) not implemented");
    }

    @Override
    public boolean hasStartTime() {
        return this.getStartTimeDay() > -1 && this.getStartTimeHour() > -1;
    }

    @Override
    public boolean hasEndTime() {
        return this.getEndTimeHour() > -1;
    }

    public String getPartialValidityTimePeriod() {
        if (this.getStartTimeDay() > -1 && this.getStartTimeHour() > -1 && this.getEndTimeHour() > -1) {
            final StringBuilder sb = new StringBuilder();
            sb.append(String.format("%02d%02d", this.getStartTimeDay(), this.getStartTimeHour()));
            if (this.getEndTimeDay() > -1) {
                sb.append('/');
                sb.append(String.format("%02d%02d", this.getEndTimeDay(), this.getEndTimeHour()));
            } else {
                sb.append(String.format("%02d", this.getEndTimeHour()));
            }
            return sb.toString();
        } else {
            return null;
        }
    }

    public void setPartialValidityTimePeriod(final String time) {
        if (time == null) {
            this.setPartialValidityTimePeriod(-1, -1, -1, -1);
        } else {
            final Matcher m = TAFImpl.VALIDITY_PERIOD_PATTERN.matcher(time);
            if (m.matches()) {
                if (m.group(1) != null) {
                    //old 24h TAF, just one day field
                    final int day = Integer.parseInt(m.group(2));
                    final int fromHour = Integer.parseInt(m.group(3));
                    final int toHour = Integer.parseInt(m.group(4));
                    this.setPartialValidityTimePeriod(day, fromHour, toHour);
                } else {
                    //30h TAF
                    final int fromDay = Integer.parseInt(m.group(6));
                    final int fromHour = Integer.parseInt(m.group(7));
                    final int toDay = Integer.parseInt(m.group(8));
                    final int toHour = Integer.parseInt(m.group(9));
                    this.setPartialValidityTimePeriod(fromDay, toDay, fromHour, toHour);
                }
            } else {
                throw new IllegalArgumentException("Time period is not either 'ddHHHH' or 'ddHH/ddHH'");
            }
        }
    }

    public void setPartialValidityTimePeriod(final int day, final int startHour, final int endHour) {
        this.setPartialValidityTimePeriod(day, -1, startHour, endHour);
    }

    public void setPartialValidityTimePeriod(final int startDay, final int endDay, final int startHour, final int endHour) {
        if (TAFValidityTime.timeOk(startDay, startHour, 0) && TAFValidityTime.timeOk(endDay, endHour, 0)) {
            this.setPartialStartTime(startDay, startHour, 0);
            this.setPartialEndTime(endDay, endHour, 0);
        } else {
            throw new IllegalArgumentException("Start '" + startDay + "/" + startHour + "' and/or end time '" + endDay + "/" + endHour + "' is not allowed");
        }
    }

    public String getValidityStartTimeISO() {
        if (this.isStartTimeComplete()) {
            return this.getCompleteStartTimeAsISOString();
        } else {
            return null;
        }
    }

    public String getValidityEndTimeISO() {
        if (this.isEndTimeComplete()) {
            return this.getCompleteEndTimeAsISOString();
        } else {
            return null;
        }
    }

}
