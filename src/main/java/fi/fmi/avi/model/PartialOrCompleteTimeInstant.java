package fi.fmi.avi.model;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;

/**
 * Created by rinne on 27/10/17.
 */
@FreeBuilder
@JsonDeserialize(builder = PartialOrCompleteTimeInstant.Builder.class)
public abstract class PartialOrCompleteTimeInstant {

    public static Pattern DAY_HOUR_MINUTE_TZ_PATTERN = Pattern.compile("^(FM)?(?<day>[0-9]{2})?(?<hour>[0-9]{2})(?<minute>[0-9]{2})(?<timezone>[A-Z]+)?$");
    public static Pattern DAY_HOUR_PATTERN = Pattern.compile("^(?<day>[0-9]{2})(?<hour>[0-9]{2})$");
    public static Pattern HOUR_PATTERN = Pattern.compile("^(?<hour>[0-9]{2})$");
    public static Pattern HOUR_MINUTE_PATTERN = Pattern.compile("^(?<hour>[0-9]{2})(?<minute>[0-9]{2})$");

    public static PartialOrCompleteTimeInstant createIssueTime(final String partialDateTime) {
        return new Builder().setPartialTimePattern(DAY_HOUR_MINUTE_TZ_PATTERN).setPartialTime(partialDateTime).build();
    }

    public static PartialOrCompleteTimeInstant createDayHourInstant(final String partialDateTime) {
        return new Builder().setPartialTimePattern(DAY_HOUR_PATTERN).setPartialTime(partialDateTime).build();
    }

    public static PartialOrCompleteTimeInstant createHourInstant(final String partialDateTime) {
        return new Builder().setPartialTimePattern(HOUR_PATTERN).setPartialTime(partialDateTime).build();
    }

    public static PartialOrCompleteTimeInstant createHourMinuteInstant(final String partialDateTime) {
        return new Builder().setPartialTimePattern(HOUR_MINUTE_PATTERN).setPartialTime(partialDateTime).build();
    }

    /**
     * Indicates the partial time pattern this time instance was created to handle.
     * This pattern is used to complete this time instance. The value must be one of
     * {@link #DAY_HOUR_MINUTE_TZ_PATTERN}, {@link #DAY_HOUR_PATTERN} or {@link #HOUR_PATTERN}.
     *
     * @return the time format pattern
     */
    public abstract Pattern getPartialTimePattern();

    /**
     * Returns the partial part of this time instance as String.
     * The consistency with {@link #getPartialTimePattern()} is enforced.
     *
     * @return the partial time instance as String.
     */
    public abstract String getPartialTime();

    /**
     * Returns the fully resolved time of this instance, if available.
     *
     * @return the full date time of this time instance
     */
    public abstract Optional<ZonedDateTime> getCompleteTime();

    /**
     * Returns the fully resolved date time of this time instance in format
     * {@link DateTimeFormatter#ISO_OFFSET_DATE_TIME}, if available.
     *
     * @return the full date time of this time instance in ISO offset data time format
     */
    public abstract Optional<String> getCompleteTimeAsISOString();

    /**
     * Indicates if this time instance was initialized as time '2400' indicating midnight
     * (the last instance of time of the day). As a time instance this is equal to '00:00' of the
     * next day, but it may have implications on how the time is serialized in TAC format.
     *
     * @return
     */
    public abstract boolean isMidnight24h();

    public int partialTimeMinute() {
        Pattern timePattern = getPartialTimePattern();
        if (timePattern.pattern().contains("?<minute>")) {
            Matcher m = timePattern.matcher(this.getPartialTime());
            Preconditions.checkState(m.matches(), "Partial time does not '" + getPartialTime() + "' does not match timePattern '" + timePattern + "'");
            return Integer.parseInt(m.group("minute"));
        } else {
            return -1;
        }
    }

    public int partialTimeHour() {
        Pattern timePattern = getPartialTimePattern();
        if (timePattern.pattern().contains("?<hour>")) {
            Matcher m = timePattern.matcher(getPartialTime());
            Preconditions.checkState(m.matches(), "Partial time does not '" + getPartialTime() + "' does not match timePattern '" + timePattern + "'");
            return Integer.parseInt(m.group("hour"));
        } else {
            return -1;
        }
    }

    public int partialTimeDay() {
        Pattern timePattern = getPartialTimePattern();
        if (timePattern.pattern().contains("?<day>")) {
            Matcher m = timePattern.matcher(getPartialTime());
            Preconditions.checkState(m.matches(), "Partial time does not '" + getPartialTime() + "' does not match timePattern '" + timePattern + "'");
            return Integer.parseInt(m.group("day"));
        } else {
            return -1;
        }
    }

    abstract Builder toBuilder();

    public static class Builder extends PartialOrCompleteTimeInstant_Builder {

        @Override
        public PartialOrCompleteTimeInstant build() {
            ensureMidnight24Updated();
            return super.build();
        }

        @Override
        public PartialOrCompleteTimeInstant.Builder setCompleteTime(final ZonedDateTime dateTime) {
            PartialOrCompleteTimeInstant.Builder retval = super.setCompleteTime(dateTime);
            String asISODateTime = dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            if (!getCompleteTimeAsISOString().isPresent() || !getCompleteTimeAsISOString().get().equals(asISODateTime)) {
                retval = retval.setCompleteTimeAsISOString(asISODateTime);
            }
            return retval;
        }

        @Override
        public PartialOrCompleteTimeInstant.Builder setCompleteTimeAsISOString(final String dateTime) {
            ZonedDateTime parsed = ZonedDateTime.parse(dateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            PartialOrCompleteTimeInstant.Builder retval = super.setCompleteTimeAsISOString(dateTime);
            if (!getCompleteTime().isPresent() || !getCompleteTime().get().equals(parsed)) {
                retval = setCompleteTime(parsed);
            }
            return retval;
        }

        public PartialOrCompleteTimeInstant.Builder completedWithIssueYearMonth(final YearMonth issueYearMonth) throws IllegalArgumentException {
            Pattern timePattern = getPartialTimePattern();
            Preconditions.checkState(timePattern.pattern().contains("?<day>"),
                    "The current timePattern " + timePattern + " does not match dayOfMonth, day " + "must be given to complete. Use method "
                            + "completeWithYearMonthDay(YearMonth, int) or fix the timePattern");
            Matcher m = getPartialTimePattern().matcher(getPartialTime());
            Preconditions.checkState(m.matches(), "Partial time does not '" + getPartialTime() + "' does not match timePattern '" + timePattern + "'");
            Preconditions.checkState(m.group("day") != null,
                    "Partial time does not contain dayOfMonth, use method " + "completeWithYearMonthDay(YearMonth, int)");
            return completedWithIssueYearMonthDay(issueYearMonth, Integer.parseInt(m.group("day")));
        }

        public PartialOrCompleteTimeInstant.Builder completedWithIssueYearMonthDay(final YearMonth issueYearMonth, int issueDayOfMonth)
                throws IllegalArgumentException {
            PartialOrCompleteTimeInstant.Builder retval = this;
            String partialTime = getPartialTime();
            Pattern timePattern = getPartialTimePattern();
            Preconditions.checkArgument(issueYearMonth != null, "issueYearMonth must not be null");
            Preconditions.checkArgument(issueDayOfMonth >= 1 && issueDayOfMonth <= 31, "issueDayOfMonth must be between 1 and 31");
            Preconditions.checkState(partialTime != null, "Partial time is null, cannot complete");
            Preconditions.checkState(timePattern.pattern().contains("?<hour>"), "The current timePattern " + timePattern + " does not match hour");
            Matcher m = timePattern.matcher(partialTime);
            Preconditions.checkState(m.matches(), "Partial time '" + partialTime + "' does not match timePattern '" + timePattern + "'");
            ensureMidnight24Updated();

            ZoneId tzId;
            int minute;
            int day;

            if (timePattern.pattern().contains("?<day>")) {
                String ds = m.group("day");
                if (ds != null) {
                    day = Integer.parseInt(ds);
                } else {
                    day = issueDayOfMonth;
                }
            } else {
                day = issueDayOfMonth;
            }

            int hour = Integer.parseInt(m.group("hour"));

            if (timePattern.pattern().contains("?<minute>") && m.group("minute") != null) {
                minute = Integer.parseInt(m.group("minute"));
            } else {
                minute = 0;
            }

            if (timePattern.pattern().contains("?<timezone>") && m.group("timezone") != null) {
                tzId = ZoneId.of(m.group("timezone"));
            } else {
                tzId = ZoneId.of("Z");
            }
            ZonedDateTime completeTime;
            try {
                if (isMidnight24h()) {
                    completeTime = ZonedDateTime.of(LocalDateTime.of(issueYearMonth.getYear(), issueYearMonth.getMonth(), day, 0, 0), tzId);
                    completeTime.plusDays(1);
                } else {
                    completeTime = ZonedDateTime.of(LocalDateTime.of(issueYearMonth.getYear(), issueYearMonth.getMonth(), day, hour, minute), tzId);
                }
                if (day < issueDayOfMonth) {
                    //issue day > my current day, assume next month:
                    completeTime = completeTime.plusMonths(1);
                }
                return retval.setCompleteTime(completeTime);
            } catch (DateTimeException dte) {
                throw new IllegalArgumentException("Unable to complete date with given arguments", dte);
            }
        }

        private void ensureMidnight24Updated() {
            Pattern timePattern = getPartialTimePattern();
            String partialTime = getPartialTime();
            Preconditions.checkNotNull(timePattern, "partialTimePattern must be set at this point");
            Preconditions.checkNotNull(partialTime, "partialTime must be set at this point");
            Preconditions.checkState(timePattern.pattern().contains("?<hour>"), "The current timePattern " + timePattern + " does not match hour");
            Matcher m = timePattern.matcher(partialTime);
            Preconditions.checkState(m.matches(), "Partial time '" + partialTime + "' does not match timePattern '" + timePattern + "'");
            int hour = Integer.parseInt(m.group("hour"));
            int minute;
            if (timePattern.pattern().contains("?<minute>") && m.group("minute") != null) {
                minute = Integer.parseInt(m.group("minute"));
            } else {
                minute = 0;
            }
            setMidnight24h(hour == 24 && minute == 0);
        }

    }
}
